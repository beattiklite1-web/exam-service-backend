/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.ExamRequest;
import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.entity.*;
import com.huuhung.exam_service.exception.*;
import com.huuhung.exam_service.repository.*;
import com.huuhung.exam_service.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamAnswerRepository examAnswerRepository; // Cần tạo Repo này
    private final UserRepository userRepository;

    // 🌟 THẦN CHÚ SỬA LỖI CACHE BYPASS: Tự tiêm chính mình thông qua Proxy
    private ExamService selfProxy;

    @org.springframework.beans.factory.annotation.Autowired
    public void setSelfProxy(@org.springframework.context.annotation.Lazy ExamService selfProxy) {
        this.selfProxy = selfProxy;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void importFullExam(ExamRequest request) {
        // 1. Lưu Exam trước
        Exam exam = Exam.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .isPublished(true)
                .showAnswer(true)
                .build();
        exam = examRepository.save(exam);

        // 2. Lưu danh sách Questions từ JSON
        List<Question> questions = new ArrayList<>();
        for (Map<String, Object> node : request.getQuestionsJson()) {
            questions.add(Question.builder()
                    .exam(exam)
                    .questionId((Integer) node.get("id"))
                    .type((String) node.get("type"))
                    .code((String) node.get("code"))
                    .questionText((String) node.get("question_text"))
                    .image((String) node.get("image"))
                    .audio((String) node.get("audio"))
                    .options((List<Map<String, Object>>) node.get("options"))
                    .build());
        }
        // Lưu để có ID thực tế của Question
        List<Question> savedQuestions = questionRepository.saveAll(questions);

        // 3. Bóc tách MD và lưu vào bảng ExamAnswer
        List<ExamAnswer> examAnswers = new ArrayList<>();
        
        // 1. Lấy chuỗi từ Request
        String rawData = request.getRawMdAnswers();

        // 2. LÀM SẠCH: Xóa hết dấu \ (đây là cách ông nên chọn)
        String cleanData = rawData.replace("\\", "");
        
        Pattern pattern = Pattern.compile("#(\\d+)_([A-Z])");
        Matcher matcher = pattern.matcher(cleanData);
        
        int order = 1;
        while (matcher.find()) {
            String qIdInMd = matcher.group(1); // Số 60
            String letter = matcher.group(2);   // Chữ A
            
            // Tìm QuestionId thực tế trong DB để map vào bảng Answer (nếu cần)
            Long realQuestionId = null;
            if (order <= savedQuestions.size()) {
                realQuestionId = savedQuestions.get(order - 1).getId();
            }

            examAnswers.add(ExamAnswer.builder()
                    .exam(exam)
                    .questionOrder(order)
                    .correctOptionCode(qIdInMd + ":" + letter) // Lưu "60:A"
                    .questionId(realQuestionId)
                    .build());
            order++;
        }
        //examAnswerRepository.saveAll(examAnswers);
        if (!examAnswers.isEmpty()) {
            examAnswerRepository.saveAll(examAnswers);
            System.out.println("✅ THÀNH CÔNG: Đã nạp " + examAnswers.size() + " đáp án vào database.");
        } else {
            System.out.println("❌ LỖI: Không tìm thấy đáp án nào trong chuỗi MD!");
            System.out.println("Chuỗi nhận được: " + request.getRawMdAnswers());
        }
    }

// 🌟 HÀM TỔNG 1: Xử lý logic Random chọn Mã Đề chưa làm (Hàm này chạy nhanh, KHÔNG cache)
    @Override
    @Transactional
    public ExamResponse getRandomExam(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        if (user.getExpiryDate() == null || user.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new ExpiryDateException("Thời gian thi thử của bạn đã hết hạn. Hãy gia hạn để tiếp tục!");
        }
        
        List<Long> completedIds = user.getCompletedExamIds();
        Long selectedExamId = null;
        java.util.Random random = new java.util.Random();

        if (completedIds.isEmpty()) {
            List<Long> allIds = examRepository.findAllExamIds();
            if (!allIds.isEmpty()) {
                selectedExamId = allIds.get(random.nextInt(allIds.size()));
            }
        } else {
            List<Long> availableIds = examRepository.findExamIdsExcluding(completedIds);
            if (!availableIds.isEmpty()) {
                selectedExamId = availableIds.get(random.nextInt(availableIds.size()));
            }
        }

        // Tự động RESET kho đề khi học viên đã cày hết sạch PDF
        if (selectedExamId == null) {
            user.getCompletedExamIds().clear();
            // Dùng save thông thường để Hibernate tự tối ưu Batch Update thay vì ép Flush gây lock nghẽn DB
            userRepository.save(user); 
            
            List<Long> allIds = examRepository.findAllExamIds();
            if (!allIds.isEmpty()) {
                selectedExamId = allIds.get(random.nextInt(allIds.size()));
            }
        }

        if (selectedExamId == null) {
            throw new ExamNotFoundException("No exams available trong hệ thống");
        }

        // 🌟 GỌI HÀM SỐ 2 ĐỂ BỐC NỘI DUNG ĐỀ QUA KÉN CACHE REDIS SIÊU TỐC
        return selfProxy.getExamDetailWithCache(selectedExamId);
    }

    // 🌟 HÀM TỔNG 2: Bốc nội dung 40 câu hỏi tĩnh cố định từ RAM Redis (BẢO VỆ DATABASE TUYỆT ĐỐI)
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "exam_cache_records", key = "#examId") // 👑 THẦN CHÚ CHIẾN THẮNG 1000 USERS
    public ExamResponse getExamDetailWithCache(Long examId) {
        System.out.println("⚠️ CACHE MISS! Đang lội xuống Postgres quét ổ cứng gom 40 câu của mã đề ID: " + examId);
        
        // 1. Fetch mảng Questions ra trước
        Exam exam = examRepository.findExamWithQuestionsById(examId)
                .orElseThrow(() -> new ExamNotFoundException("Exam not found with id: " + examId));
        
        // 2. Kích hoạt nạp nốt mảng Answers một cách mượt mà (Hibernate tự tối ưu câu lệnh số 2 rất nhẹ)
        int answerSize = exam.getAnswers().size(); 

        // 3. Mapping trả về dữ liệu phẳng chuẩn khít thứ tự 1-40 lên mạng RAM Redis
        return ExamResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .duration(exam.getDuration())
                .isPublished(exam.getIsPublished())
                .questions(exam.getQuestions().stream()
                    .sorted(Comparator.comparing(Question::getQuestionId))   
                    .map(q -> 
                    QuestionResponse.builder()
                        .id(q.getId())
                        .questionId(q.getQuestionId())
                        .type(q.getType())
                        .code(q.getCode())
                        .questionText(q.getQuestionText())
                        .image(q.getImage())
                        .audio(q.getAudio())
                        .options(q.getOptions())
                        .build()
                ).collect(Collectors.toList()))
                .answers(exam.getAnswers().stream()
                    .sorted(Comparator.comparing(ExamAnswer::getQuestionOrder))
                    .map(a -> 
                    AnswerResponse.builder()
                        .questionOrder(a.getQuestionOrder())
                        .correctOptionCode(a.getCorrectOptionCode())
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void completeExam(String username, Long examId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User không tồn tại"));
        if (!user.getCompletedExamIds().contains(examId)) {
            user.getCompletedExamIds().add(examId);
            userRepository.save(user);
        }
    }
}