/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.service.GrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grammar")
@RequiredArgsConstructor
public class GrammarController {

    private final GrammarService grammarService;

    // Học viên lấy danh sách bài học hiển thị ở Lobby (trang chính)
    @GetMapping("/lobby")
    public ResponseEntity<List<GrammarLobbyResponse>> getGrammarLobby(Principal principal) {
        String username = principal.getName(); // Lấy username an toàn từ Security Context
        return ResponseEntity.ok(grammarService.getGrammarLobby(username));
    }

    // Học viên bấm click vào bài học cụ thể - Lấy nội dung lý thuyết + 20 câu hỏi (Đã giấu đáp án chống cheat)
    @GetMapping("/{grammarId}/detail")
    public ResponseEntity<GrammarDetailResponse> getGrammarDetail(@PathVariable Long grammarId) {
        return ResponseEntity.ok(grammarService.getGrammarDetail(grammarId));
    }

    // Học viên hoàn thành bài tập, gửi cụm câu trả lời lên chấm điểm, lấy lời giải chi tiết
    @PostMapping("/{grammarId}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            Principal principal,
            @PathVariable Long grammarId,
            @RequestBody Map<Integer, String> studentAnswers) {
        String username = principal.getName(); // Lấy username an toàn để xác thực thông tin
        return ResponseEntity.ok(grammarService.submitGrammarQuiz(grammarId, username, studentAnswers));
    }
}