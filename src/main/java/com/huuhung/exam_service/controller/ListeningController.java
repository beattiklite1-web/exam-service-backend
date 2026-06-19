/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;



import com.huuhung.exam_service.dto.response.ListeningPartResponse;
import com.huuhung.exam_service.dto.response.ListeningResponse;
import com.huuhung.exam_service.dto.response.PartProgressResponse;
import com.huuhung.exam_service.service.ListeningService;
import com.huuhung.exam_service.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/listening")
@RequiredArgsConstructor
public class ListeningController {

    private final ListeningService listeningService;

    /**
     * 1. TRANG SẢNH (LOBBY)
     * Hiển thị tất cả các Part nghe để User lựa chọn.
     * Vì không làm phần tiến độ riêng cho Listening, 
     * ta có thể dùng chung cấu trúc PartProgressResponse nhưng % progress sẽ luôn là 0 hoặc null.
     */
    @GetMapping("/lobby")
    public ResponseEntity<List<ListeningPartResponse>> getLobby() {
        // Không cần Principal vì danh sách Part là chung cho mọi User
        return ResponseEntity.ok(listeningService.getAllParts());
    }

    /**
     * 2. CHI TIẾT PART (HỌC)
     * Khi User chọn 1 Part từ Lobby, Next.js sẽ gọi API này để lấy toàn bộ câu hỏi.
     */
    @GetMapping("/part/{partId}")
    public ResponseEntity<List<ListeningResponse>> getListeningsInPart(@PathVariable Long partId) {
        return ResponseEntity.ok(listeningService.getListenings(partId));
    }

    /**
     * 3. LÀM TRẮC NGHIỆM (QUIZ)
     * Trả về danh sách câu hỏi đã được đảo ngẫu nhiên (Shuffle) trên RAM.
     */
    @GetMapping("/quiz/{partId}")
    public ResponseEntity<List<ListeningResponse>> getListeningQuiz(@PathVariable Long partId) {
        return ResponseEntity.ok(listeningService.generateQuiz(partId));
    }
}
