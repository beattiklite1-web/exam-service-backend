/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminResponse {
    private Long id;
    private String username;
    private String email;
    
    // Admin cần xem danh sách tất cả các quyền (Set) thay vì chỉ 1 quyền lẻ
    private Set<String> roles;
    
    private String expiryDate;
    private boolean enabled;
    
    // Hùng có thể thêm trường này để Admin biết ai là người mới đăng ký
    // private String createdAt; 
}