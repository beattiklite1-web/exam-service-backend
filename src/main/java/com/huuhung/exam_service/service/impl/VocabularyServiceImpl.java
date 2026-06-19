/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.QuizVocabularyRequest;
import com.huuhung.exam_service.dto.request.VocabProgressRequest;
import com.huuhung.exam_service.dto.request.VocabularyPartRequest;
import com.huuhung.exam_service.dto.request.VocabularyRequest;
import com.huuhung.exam_service.dto.response.PartProgressProjectionReponse;
import com.huuhung.exam_service.dto.response.PartProgressResponse;
import com.huuhung.exam_service.dto.response.QuizVocabularyResponse;
import com.huuhung.exam_service.dto.response.VocabularyResponse;
import com.huuhung.exam_service.entity.*;
import com.huuhung.exam_service.exception.VocabFileProcessException;
import com.huuhung.exam_service.exception.VocabularyNotFoundException;
import com.huuhung.exam_service.repository.*;
import com.huuhung.exam_service.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections; // Để dùng hàm shuffle
import com.huuhung.exam_service.exception.InsufficientDataException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyPartRepository partRepository;
    private final VocabularyRepository vocabRepository;
    private final UserVocabularyProgressRepository progressRepository;
    private final UserRepository userRepository;

    // ==========================================
    // 1. NHÓM QUẢN TRỊ (ADMIN)
    // ==========================================

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void importFullVocabPart(VocabularyPartRequest request) {
        try {
            // Tìm hoặc tạo Part mới
            VocabularyPart part = partRepository.findByPartNumber(request.getPartNumber())
                    .orElseGet(() -> VocabularyPart.builder()
                            .partNumber(request.getPartNumber())
                            .build());
            
            part.setTitle(request.getTitle());
            partRepository.save(part);

            List<Vocabulary> vocabList = new ArrayList<>();
            
            // Xử lý JSON lồng nhau (Group -> Words)
            for (Map<String, Object> groupObj : request.getWordsJson()) {
                Integer groupIdx = (Integer) groupObj.get("group");
                List<Map<String, String>> words = (List<Map<String, String>>) groupObj.get("words");

                for (Map<String, String> w : words) {
                    vocabList.add(Vocabulary.builder()
                            .korean(w.get("korean"))
                            .meaning(w.get("meaning"))
                            .audioUrl(w.get("audio_url"))
                            .wordGroup(groupIdx)
                            // Công thức tính Lesson: (Part-1)*10 + (Group-1)/10 + 1
                            .lessonTag(((request.getPartNumber() - 1) * 10) + ((groupIdx - 1) / 10 + 1))
                            .part(part)
                            .build());
                }
            }
            vocabRepository.saveAll(vocabList);
        } catch (Exception e) {
            throw new VocabFileProcessException("Lỗi định dạng dữ liệu JSON: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public VocabularyResponse updateWord(Long id, VocabularyRequest request) {
        Vocabulary v = vocabRepository.findById(id)
                .orElseThrow(() -> new VocabularyNotFoundException("Không tìm thấy từ vựng ID: " + id));
        
        v.setKorean(request.getKorean());
        v.setMeaning(request.getMeaning());
        v.setAudioUrl(request.getAudioUrl());
        
        Vocabulary updated = vocabRepository.save(v);
        return VocabularyResponse.builder()
                .id(updated.getId())
                .korean(updated.getKorean())
                .meaning(updated.getMeaning())
                .audioUrl(updated.getAudioUrl())
                .build();
    }

    @Override
    @Transactional
    public void deleteWord(Long id) {
        if (!vocabRepository.existsById(id)) {
            throw new VocabularyNotFoundException("ID từ vựng không tồn tại để xóa.");
        }
        vocabRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deletePart(Long partId) {
        VocabularyPart part = partRepository.findById(partId)
                .orElseThrow(() -> new VocabularyNotFoundException("Không tìm thấy Part ID: " + partId));
        // Xóa Part sẽ xóa luôn toàn bộ Vocab trong đó nhờ CascadeType.ALL
        partRepository.delete(part);
    }

    // ==========================================
    // 2. NHÓM NGƯỜI DÙNG (USER)
    // ==========================================



    @Override
    public List<PartProgressResponse> getAllPartsWithProgress(Long userId) {
        // 🌟 KHÔNG CÒN VÒNG LẶP N+1: Bắn đúng 1 câu SQL duy nhất xử lý gọn gàng cho 1000 users cùng lúc!
        List<PartProgressProjectionReponse> stats = 
                partRepository.getPartsProgressWithSingleQuery(userId);
        
        return stats.stream().map(stat -> {
            long total = stat.getTotalCount();
            long mastered = stat.getMasteredCount();
            // Tính toán tỷ lệ phần trăm hoàn thành bài học
            double percent = (total == 0) ? 0 : (double) mastered / total * 100;

            return PartProgressResponse.builder()
                    .partId(stat.getPartId())
                    .partNumber(stat.getPartNumber())
                    .title(stat.getTitle())
                    .totalCount(total)
                    .masteredCount(mastered)
                    .completionPercentage(Math.round(percent * 10.0) / 10.0) // Làm tròn 1 chữ số thập phân
                    .build();
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<PartProgressResponse> getAllPartsWithProgressByUsername(String username) {
        // 1. Tìm User dựa trên username lấy từ Principal
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // 2. Gọi lại chính hàm cũ của ông bằng user.getId()
        return getAllPartsWithProgress(user.getId());
    }

    @Override
    public List<VocabularyResponse> getWords(Long partId, Integer lesson) {
        List<Vocabulary> words = (lesson != null)
                ? vocabRepository.findByPartIdAndLessonTag(partId, lesson)
                : vocabRepository.findByPartId(partId);

        return words.stream().map(v -> VocabularyResponse.builder()
                .id(v.getId())
                .korean(v.getKorean())
                .meaning(v.getMeaning())
                .audioUrl(v.getAudioUrl())
                .wordGroup(v.getWordGroup())
                .lessonTag(v.getLessonTag())
                .build()).collect(Collectors.toList());
    }

    // ==========================================
    // 3. TIẾN ĐỘ HỌC TẬP (PROGRESS)
    // ==========================================

    @Override
    @Transactional
    public void updateProgress(Long userId, VocabProgressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Vocabulary vocab = vocabRepository.findById(request.getVocabId())
                .orElseThrow(() -> new VocabularyNotFoundException("Từ vựng không tồn tại"));

        UserVocabularyProgress progress = progressRepository
                .findByUserIdAndVocabularyId(userId, request.getVocabId())
                .orElse(UserVocabularyProgress.builder()
                        .user(user)
                        .vocabulary(vocab)
                        .listenCount(0)
                        .isMastered(false)
                        .build());

        // Cập nhật trạng thái đã thuộc
        if (request.getIsMastered() != null) {
            progress.setIsMastered(request.getIsMastered());
        }
        
        // Tăng số lần nghe nếu Frontend yêu cầu
        if (Boolean.TRUE.equals(request.getIsIncrementListen())) {
            progress.setListenCount(progress.getListenCount() + 1);
        }

        progress.setLastStudied(LocalDateTime.now());
        progressRepository.save(progress);
    }
    
    // --- HÀM MỚI: Cập nhật tiến độ qua Username ---
    @Override
    @Transactional
    public void updateProgressByUsername(String username, VocabProgressRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
        this.updateProgress(user.getId(), request);
    }
    
    @Override
    public List<Long> getMasteredVocabIds(Long userId) {
        return progressRepository.getMasteredVocabIds(userId);
    }
    @Override
    public List<Long> getMasteredVocabIdsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
        return getMasteredVocabIds(user.getId());
    }

    @Override
    public void resetPartProgress(Long userId, Long partId) {
        progressRepository.deleteByUserIdAndPartId(userId, partId);
    }
    @Override
    @Transactional
    public void resetPartProgressByUsername(String username, Long partId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
        this.resetPartProgress(user.getId(), partId);
    }

    @Override
    public List<QuizVocabularyResponse> generateQuiz(QuizVocabularyRequest request) {
    List<Vocabulary> allVocabs = vocabRepository.findByPartId(request.getPartId());
        
        // Check bảo mật: Nếu Part quá ít từ thì không làm trắc nghiệm 4 đáp án được
        if (allVocabs.size() < 4) {
            throw new InsufficientDataException("Part này cần ít nhất 4 từ để tạo trắc nghiệm!");
        }

        // 2. Đảo thứ tự các câu hỏi nếu request yêu cầu
        
            Collections.shuffle(allVocabs);
        

        // 3. Chuyển đổi sang List Response
        return allVocabs.stream().map(vocab -> {
            
            // Xác định câu hỏi và đáp án đúng dựa trên Mode (K_V hoặc V_K)
            String question = "K_V".equals(request.getMode()) ? vocab.getKorean() : vocab.getMeaning();
            String correct = "K_V".equals(request.getMode()) ? vocab.getMeaning() : vocab.getKorean();

            // 4. Logic bốc đáp án nhiễu ngay trên RAM
            List<String> options = allVocabs.stream()
                .map(v -> "K_V".equals(request.getMode()) ? v.getMeaning() : v.getKorean())
                .filter(m -> !m.equals(correct)) // Loại bỏ đáp án đúng ra khỏi danh sách nhiễu
                .distinct() 
                .collect(Collectors.toList());
            
            Collections.shuffle(options); // Đảo danh sách nhiễu
            
            // Lấy 3 cái đầu tiên và thêm đáp án đúng vào
            List<String> finalOptions = new ArrayList<>(options.subList(0, 3));
            finalOptions.add(correct);
            Collections.shuffle(finalOptions); // Đảo lại lần nữa để vị trí A,B,C,D là ngẫu nhiên

            return QuizVocabularyResponse.builder()
                    .vocabId(vocab.getId())
                    .question(question)
                    .correctAnswer(correct)
                    .options(finalOptions)
                    .type(request.getMode())
                    .build();
        }).collect(Collectors.toList());    
    }
    
    
}
