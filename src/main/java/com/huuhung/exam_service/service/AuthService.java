/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void changePassword(String username, ChangePasswordRequest request);
    UserResponse getProfile(String username);
    void verifyEmail(VerifyRequest request);
}
