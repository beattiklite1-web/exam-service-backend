package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.entity.*;
import com.huuhung.exam_service.exception.*;
import com.huuhung.exam_service.repository.*;
import com.huuhung.exam_service.service.AdminService;
import com.huuhung.exam_service.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict; // 🌟 IMPORT ĐỂ TRỤC XUẤT CACHE
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.CacheManager; // 🌟 IMPORT CHUẨN ĐƯỜNG DẪN NÀY
import org.springframework.cache.Cache;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamService examService;
    private final CacheManager cacheManager;

    // --- QUẢN LÝ USER ---
    @Override
    public List<UserAdminResponse> searchUsers(String keyword) {
        List<User> users = (keyword == null || keyword.isEmpty()) 
                ? userRepository.findAll() 
                : userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        
        return users.stream().map(this::mapToUserAdminResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void extendUserTime(String username, int hours) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy user: " + username));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentExpiry = user.getExpiryDate();
        
        user.setExpiryDate((currentExpiry == null || currentExpiry.isBefore(now)) 
                ? now.plusHours(hours) : currentExpiry.plusHours(hours));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User không tồn tại"));
        user.setEnabled(!user.isEnabled());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new UserNotFoundException("User không tồn tại");
        userRepository.deleteById(id);
    }

    // --- QUẢN LÝ ĐỀ THI (KẾT NỐI REDIS BẢO MẬT) ---
    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream().map(this::mapToExamResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    // 🌟 VŨ KHÍ TRỤC XUẤT: Admin sửa đề nào, xóa ngay kén dữ liệu thiu của đề đó trên RAM Redis lập tức!
    @CacheEvict(value = "exam_cache_records", key = "#id") 
    public void updateExam(Long id, ExamUpdateRequest request) {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new ExamNotFoundException("Đề thi không tồn tại"));
        if (request.getTitle() != null) exam.setTitle(request.getTitle());
        if (request.getDescription() != null) exam.setDescription(request.getDescription());
        if (request.getDuration() != null) exam.setDuration(request.getDuration());
        if (request.getIsPublished() != null) exam.setIsPublished(request.getIsPublished());
        
        System.out.println("🔄 Admin cập nhật đề ID " + id + " -> Đã hạ lệnh xóa Cache cũ.");
    }

    @Override
    @Transactional
    // 🌟 VŨ KHÍ DIỆT TẬN GỐC: Xóa đề dưới DB song song bốc hơi luôn cục bộ nhớ đệm rác trên RAM Redis
    @CacheEvict(value = "exam_cache_records", key = "#id") 
    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ExamNotFoundException("Đề thi không tồn tại"));

        examRepository.delete(exam); 
        System.out.println("🔥 Admin xóa đề ID " + id + " -> Đã quét sạch kén ma trên RAM Redis.");
    }

    @Override
    public void importExam(ExamRequest request) {
        examService.importFullExam(request);
    }

    // --- QUẢN LÝ CÂU HỎI LẺ ---
    @Override
    @Transactional
    // 🌟 VÁ LỖI LOGIC NÂNG CAO: Sửa 1 câu hỏi lẻ thì cái Đề chứa câu hỏi đó cũng phải bị xóa Cache để cập nhật chữ mới!
    public void updateQuestion(Long id, QuestionUpdateRequest request) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + id));
        if (request.getQuestionText() != null) q.setQuestionText(request.getQuestionText());
        if (request.getImage() != null) q.setImage(request.getImage());
        if (request.getAudio() != null) q.setAudio(request.getAudio());
        
        questionRepository.save(q);

        // 🌟 2. VÁ LỖI KHÔNG TỒN TẠI PACKAGE: Dùng CacheManager chuẩn của Spring để dọn cache
        if (q.getExam() != null && cacheManager != null) {
            Cache cache = cacheManager.getCache("exam_cache_records");
            if (cache != null) {
                cache.evict(q.getExam().getId()); // Trục xuất cục cache cũ của đề cha ngay lập tức
                System.out.println("⚡ Đã làm tươi (Clear Cache) đề cha ID " + q.getExam().getId() + " bằng CacheManager chuẩn.");
            }
        }
    }

    @Override
    public AdminStatsResponse getStats() {
        return AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalExams(examRepository.count())
                .totalQuestions(questionRepository.count())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id) {
        // 🌟 VÁ LỖI N+1 TRANG ADMIN: Thay vì dùng findById thô, ta gọi hàm JOIN FETCH sạch câu hỏi ra trong 1 câu SQL
        Exam exam = examRepository.findExamWithQuestionsById(id)
                .orElseThrow(() -> new ExamNotFoundException("Không tìm thấy đề thi ID: " + id));

        // Ép Hibernate nạp trước mảng Answers để tránh lỗi Lazy Initialization
        int answerSize = exam.getAnswers().size();

        return mapToExamResponseWithQuestions(exam);
    }
    
    private ExamResponse mapToExamResponseWithQuestions(Exam e) {
        Map<Integer, String> answerMap = e.getAnswers().stream()
                .collect(Collectors.toMap(
                    ExamAnswer::getQuestionOrder, 
                    ExamAnswer::getCorrectOptionCode,
                    (existing, replacement) -> existing 
                ));

        return ExamResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .duration(e.getDuration())
                .isPublished(e.getIsPublished())
                .questions(e.getQuestions().stream()
                    .sorted(Comparator.comparing(Question::getQuestionId))
                    .map(q -> {
                        String correctAnswer = answerMap.getOrDefault(q.getQuestionId(), "");

                        return QuestionResponse.builder()
                                .id(q.getId())               
                                .questionId(q.getQuestionId()) 
                                .questionText(q.getQuestionText())
                                .image(q.getImage())
                                .audio(q.getAudio())
                                .options(q.getOptions())
                                .correctAnswer(correctAnswer) 
                                .build();
                    })
                    .collect(Collectors.toList()))
                .build();
    }

    private UserAdminResponse mapToUserAdminResponse(User u) {
        return UserAdminResponse.builder()
                .id(u.getId()).username(u.getUsername()).email(u.getEmail())
                .enabled(u.isEnabled()).roles(u.getRoles())
                .expiryDate(u.getExpiryDate() != null ? u.getExpiryDate().toString() : "N/A")
                .build();
    }

    private ExamResponse mapToExamResponse(Exam e) {
        return ExamResponse.builder()
                .id(e.getId()).title(e.getTitle()).description(e.getDescription())
                .duration(e.getDuration()).isPublished(e.getIsPublished())
                .build();
    }
}