/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.request.ChangePasswordRequest;
import com.huuhung.exam_service.dto.response.UserResponse;
import com.huuhung.exam_service.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Lấy thông tin cá nhân dựa trên Token đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Principal principal) {
        // Spring sẽ tự động nạp username từ Token vào principal.getName()
        return ResponseEntity.ok(userService.getMyProfile(principal.getName()));
    }


    // Đổi mật khẩu
    @PutMapping("/me/change-password")
    public ResponseEntity<String> changePassword(
            Principal principal, 
            @RequestBody ChangePasswordRequest request) {
        
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Thay đổi mật khẩu thành công!");
    }
}
