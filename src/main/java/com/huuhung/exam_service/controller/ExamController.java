/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.request.ExamRequest;
import com.huuhung.exam_service.dto.response.ExamResponse;
import com.huuhung.exam_service.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Import này nếu dùng Principal

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor

public class ExamController {

    private final ExamService examService;

    // Admin nạp đề (JSON + Markdown)
    @PostMapping("/import")
    public ResponseEntity<String> importExam(@RequestBody ExamRequest request) {
        examService.importFullExam(request);
        return ResponseEntity.ok("Nạp đề thi và khớp đáp án vào bảng ExamAnswer thành công!");
    }

    // User lấy đề random 
    // Mình đổi qua dùng Principal để lấy username an toàn hơn từ Security Context
    @GetMapping("/random")
    public ResponseEntity<ExamResponse> getExam(Principal principal) {
        String username = principal.getName(); 
        return ResponseEntity.ok(examService.getRandomExam(username));
    }

    // Đánh dấu hoàn thành để lần sau không random trùng
    @PostMapping("/{id}/complete")
    public ResponseEntity<String> complete(Principal principal, @PathVariable Long id) {
        String username = principal.getName();
        examService.completeExam(username, id);
        return ResponseEntity.ok("Đã ghi nhận bài thi " + id + " vào danh sách hoàn thành.");
    }
}
