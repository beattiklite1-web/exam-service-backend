/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.entity.User;
import com.huuhung.exam_service.exception.InvalidOTPException;
import com.huuhung.exam_service.exception.InvalidPasswordException;
import com.huuhung.exam_service.exception.InvalidUserStateException;
import com.huuhung.exam_service.exception.UserAlreadyExistsException;
import com.huuhung.exam_service.exception.UserNotFoundException;
import com.huuhung.exam_service.repository.UserRepository;
import com.huuhung.exam_service.security.JwtUtils;
import com.huuhung.exam_service.service.AuthService;
import com.huuhung.exam_service.service.EmailService; // Import thêm EmailService
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService; // Tiêm vào để gửi OTP

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email đã được sử dụng!");
        }

        // 1. Sinh mã OTP 6 số
        String otpCode = String.valueOf((int)((Math.random() * 900000) + 100000));

        // 2. Lưu trạng thái chờ (enabled = false, expiry = null)
        User user = User.builder()
                .fullName(request.getFullname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("USER"))
                .expiryDate(null) // Chưa cho giờ
                .enabled(false)   // Chưa kích hoạt
                .verificationToken(otpCode)
                .completedExamIds(new ArrayList<>()) // Khởi tạo list rỗng để tránh Null
                .build();
        userRepository.save(user);

        // 3. Gửi mail thật
        emailService.sendSimpleEmail(user.getEmail(), "Mã xác thực tài khoản", "Mã của bạn là: " + otpCode);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Tìm user trước để kiểm tra trạng thái
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Tài khoản không tồn tại!"));

        // 2. Kiểm tra xem đã kích hoạt OTP chưa (Tránh lỗi Principal null ở Frontend)
        if (!user.isEnabled()) {
            throw new InvalidUserStateException("Tài khoản chưa được kích hoạt OTP!");
        }

        // 3. Thực hiện xác thực password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new InvalidPasswordException("Mật khẩu không chính xác!");
        }

        // 4. Nếu pass hết thì mới tạo Token
        String role = user.getRoles().iterator().next();
        String token = jwtUtils.generateToken(user.getUsername(), role);

        String expiryStr = (user.getExpiryDate() != null) ? user.getExpiryDate().toString() : "N/A";

        return new AuthResponse(token, user.getUsername(), role, expiryStr, user.isEnabled());
    }
    
    @Override
    @Transactional
    public void verifyEmail(VerifyRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User không tồn tại"));

        if (user.isEnabled()) {
            throw new InvalidUserStateException("Tài khoản này đã được xác thực rồi!");
        }

        if (!user.getVerificationToken().equals(request.getCode())) {
            throw new InvalidOTPException("Mã xác thực không chính xác!");
        }

        // Xác thực thành công -> Kích hoạt và tặng 5 giờ
        user.setEnabled(true);
        user.setExpiryDate(LocalDateTime.now().plusHours(5));
        user.setVerificationToken(null); // Xóa mã OTP sau khi dùng
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User không tồn tại"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Mật khẩu cũ không chính xác!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User không tồn tại"));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoles().iterator().next())
                .expiryDate(user.getExpiryDate() != null ? user.getExpiryDate().toString() : "N/A")
                .enabled(user.isEnabled())
                .build();
    }
}