package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.service.AuthService;
import jakarta.servlet.http.HttpServletResponse; // Thêm import này
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders; // Thêm import này
import org.springframework.http.ResponseCookie; // Thêm import này
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @Value("${app.cookie.secure}")
    private boolean isCookieSecure;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Quá trình Đăng ký đăng diễn ra ! bạn chờ trong giây phút để xác thực email.");
    }

    // --- SỬA LẠI HÀM LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        // 1. Lấy dữ liệu auth từ Service
        AuthResponse authResponse = authService.login(request);

        // 2. Tạo Cookie HttpOnly để chứa Token
        ResponseCookie cookie = ResponseCookie.from("token", authResponse.getToken())
                .httpOnly(true)                // Bảo mật: JS không đọc được (Chống XSS)
                .secure(isCookieSecure)                // Để false nếu chạy localhost (http)
                .path("/")                    // Có hiệu lực cho toàn trang
                .maxAge(86400)                // Hết hạn sau 24h
                .sameSite("Lax")              // Chống CSRF cơ bản
                .build();

        // 3. Thêm Cookie vào Header của Response
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Xóa token trong body trước khi trả về để đảm bảo an toàn tuyệt đối
        authResponse.setToken(null); 

        return ResponseEntity.ok(authResponse);
    }

    // --- THÊM ENDPOINT LOGOUT ĐỂ XÓA COOKIE ---
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // Xóa ngay lập tức
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Đã đăng xuất thành công!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        // 1. Kiểm tra nếu chưa đăng nhập
        if (principal == null) {
            // Trả về 401 kèm body là null hoặc message object
            // Việc trả về 401 giúp Axios bên Frontend nhảy vào khối "catch" ngay lập tức
            return ResponseEntity.status(401).body(null); 
        }

        // 2. Nếu đã đăng nhập, lấy dữ liệu và trả về
        UserResponse response = authService.getProfile(principal.getName());
        return ResponseEntity.ok(response);
    }

    // Hùng nhớ thêm cái verify nữa nãy mình có nhắc nhé
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok("Xác thực email thành công!");
    }
}