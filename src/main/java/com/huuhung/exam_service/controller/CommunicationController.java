/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.response.CommunicationLobbyResponse;
import com.huuhung.exam_service.dto.response.CommunicationUserDetailResponse;
import com.huuhung.exam_service.service.CommunicationService;
import com.huuhung.exam_service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/communications")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;

    /**
     * Lấy danh sách toàn bộ bài học giao tiếp hiển thị ngoài Lobby của học viên
     */
    @GetMapping
    public ResponseEntity<List<CommunicationLobbyResponse>> getLobbyForUser(Principal principal) {
        if (principal == null) {
            throw new UserNotFoundException("Yêu cầu không hợp lệ. Bạn chưa đăng nhập hệ thống!");
        }
        return ResponseEntity.ok(communicationService.getLobbyForUser(principal.getName()));
    }

    /**
     * Lấy chi tiết nội dung hộp thoại chatbox theo bài học cụ thể cho học viên học
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommunicationUserDetailResponse> getDetailForUser(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            throw new UserNotFoundException("Yêu cầu không hợp lệ. Bạn chưa đăng nhập hệ thống!");
        }
        return ResponseEntity.ok(communicationService.getDetailForUser(id, principal.getName()));
    }
}