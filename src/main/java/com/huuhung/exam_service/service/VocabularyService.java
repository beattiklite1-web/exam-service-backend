/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;



import com.huuhung.exam_service.dto.request.QuizVocabularyRequest;
import com.huuhung.exam_service.dto.request.VocabularyPartRequest;
import com.huuhung.exam_service.dto.request.VocabularyRequest;
import com.huuhung.exam_service.dto.request.VocabProgressRequest;
import com.huuhung.exam_service.dto.response.PartProgressResponse;
import com.huuhung.exam_service.dto.response.QuizVocabularyResponse;
import com.huuhung.exam_service.dto.response.VocabularyResponse;

import java.util.List;

public interface VocabularyService {

    // ==========================================
    // 1. NHÓM QUẢN TRỊ (ADMIN)
    // ==========================================

    /**
     * Nạp toàn bộ dữ liệu từ JSON (Giống cách nạp Exam)
     */
    void importFullVocabPart(VocabularyPartRequest request);

    /**
     * Cập nhật thông tin từ vựng đơn lẻ (Hàn, Việt, Audio)
     */
    VocabularyResponse updateWord(Long id, VocabularyRequest request);

    /**
     * Xóa một từ vựng
     */
    void deleteWord(Long id);

    /**
     * Xóa nguyên một Part (Xóa cả tập bài học và từ vựng liên quan)
     */
    void deletePart(Long partId);


    // ==========================================
    // 2. NHÓM NGƯỜI DÙNG (USER & ADMIN)
    // ==========================================

    

    /**
     * Lấy danh sách từ vựng theo Part (có thể lọc theo bài lẻ 1-60)
     */
    List<VocabularyResponse> getWords(Long partId, Integer lesson);


    // ==========================================
    // 3. NHÓM TIẾN ĐỘ HỌC TẬP (PROGRESS)
    // ==========================================

    /**
     * Đánh dấu từ đã thuộc hoặc tăng số lần nghe audio
     */
    void updateProgress(Long userId, VocabProgressRequest request);
    void updateProgressByUsername(String username, VocabProgressRequest request);
    
    List<PartProgressResponse> getAllPartsWithProgress(Long userId);
    List<PartProgressResponse> getAllPartsWithProgressByUsername(String username);
    
    List<Long> getMasteredVocabIds(Long userId);
    List<Long> getMasteredVocabIdsByUsername(String username);
    
    void resetPartProgress(Long userId, Long partId);
    void resetPartProgressByUsername(String username, Long partId);
    
    List<QuizVocabularyResponse> generateQuiz(QuizVocabularyRequest request);
}