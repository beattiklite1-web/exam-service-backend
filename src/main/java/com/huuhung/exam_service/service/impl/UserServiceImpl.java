/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;



import com.huuhung.exam_service.dto.request.ChangePasswordRequest;
import com.huuhung.exam_service.dto.request.UserUpdateRequest;
import com.huuhung.exam_service.dto.response.UserResponse;
import com.huuhung.exam_service.entity.User;
import com.huuhung.exam_service.exception.InvalidPasswordException;
import com.huuhung.exam_service.exception.UserNotFoundException;
import com.huuhung.exam_service.repository.UserRepository;
import com.huuhung.exam_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng: " + username));
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoles().iterator().next())
                .expiryDate(user.getExpiryDate() != null ? user.getExpiryDate().toString() : "N/A")
                .enabled(user.isEnabled())
                .build();
    }


    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        // Sử dụng Exception riêng thay vì RuntimeException chung chung
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Mật khẩu cũ không chính xác!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
