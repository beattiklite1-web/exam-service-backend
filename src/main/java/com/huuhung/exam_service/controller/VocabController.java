/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.request.QuizVocabularyRequest;
import com.huuhung.exam_service.dto.request.VocabProgressRequest;
import com.huuhung.exam_service.dto.response.PartProgressResponse;
import com.huuhung.exam_service.dto.response.QuizVocabularyResponse;
import com.huuhung.exam_service.dto.response.VocabularyResponse;
import com.huuhung.exam_service.service.VocabularyService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vocab")
@RequiredArgsConstructor
public class VocabController {

    private final VocabularyService vocabService;

    // 1. Lấy danh sách Part kèm % tiến độ của User đang đăng nhập
    @GetMapping("/lobby")
    public ResponseEntity<List<PartProgressResponse>> getLobby(Principal principal) {
        // Lấy username an toàn từ Token và gọi hàm ByUsername
        return ResponseEntity.ok(vocabService.getAllPartsWithProgressByUsername(principal.getName()));
    }

    // 2. Lấy danh sách từ (Click vào Part hiện hết từ, hoặc lọc theo Lesson)
    @GetMapping("/part/{partId}")
    public ResponseEntity<List<VocabularyResponse>> getWordsInPart(
            @PathVariable Long partId,
            @RequestParam(required = false) Integer lesson) {
        return ResponseEntity.ok(vocabService.getWords(partId, lesson));
    }

    // 3. Cập nhật tiến độ (Đánh dấu thuộc/Tăng lượt nghe)
    @PostMapping("/progress")
    public ResponseEntity<String> updateProgress(
            Principal principal,
            @RequestBody VocabProgressRequest request) {
        // Bảo mật tuyệt đối: Không ai có thể cập nhật tiến độ cho người khác
        vocabService.updateProgressByUsername(principal.getName(), request);
        return ResponseEntity.ok("Tiến độ đã được ghi nhận!");
    }
    
    
    // 4. Lấy danh sách ID các từ đã thuộc để highlight ✅ ở Frontend
    @GetMapping("/mastered")
    public ResponseEntity<List<Long>> getMasteredVocabIds(Principal principal) {
        return ResponseEntity.ok(vocabService.getMasteredVocabIdsByUsername(principal.getName()));
    }
    
    @DeleteMapping("/progress/reset")
    public ResponseEntity<?> resetProgress(Principal principal, @RequestParam Long partId) {
        vocabService.resetPartProgressByUsername(principal.getName(), partId);
        return ResponseEntity.ok("Đã xóa sạch tiến độ tập " + partId);
    }
    
    // 5. API Trắc nghiệm đảo từ (Pro Mode)
    // Dùng POST vì QuizVocabularyRequest có chứa nhiều tham số (partId, mode, isShuffle)
    @PostMapping("/quiz")
    public ResponseEntity<List<QuizVocabularyResponse>> getQuiz(@RequestBody QuizVocabularyRequest request) {
        // Service sẽ lo việc shuffle và tạo đáp án nhiễu trên RAM như đã bàn
        return ResponseEntity.ok(vocabService.generateQuiz(request));
    }
}