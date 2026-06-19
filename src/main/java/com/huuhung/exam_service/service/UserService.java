/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.ChangePasswordRequest;
import com.huuhung.exam_service.dto.request.UserUpdateRequest;
import com.huuhung.exam_service.dto.response.UserResponse;

public interface UserService {
    UserResponse getMyProfile(String username);

    void changePassword(String username, ChangePasswordRequest request);
}
