/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.exception;

import ch.qos.logback.core.model.processor.ProcessorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. Nhóm lỗi KHÔNG TÌM THẤY (404 Not Found)
     */
    @ExceptionHandler({
        UserNotFoundException.class, 
        ExamNotFoundException.class,
        VocabularyNotFoundException.class,
        ResourceNotFoundException.class,
        GrammarNotFoundException.class,
        CommunicationNotFoundException.class,
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", e.getMessage());
    }

    /**
     * 2. Nhóm lỗi XUNG ĐỘT DỮ LIỆU (409 Conflict)
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(UserAlreadyExistsException e) {
        return buildResponse(HttpStatus.CONFLICT, "DATA_CONFLICT", e.getMessage());
    }

    /**
     * 3. Nhóm lỗi HẾT HẠN SỬ DỤNG (402 Payment Required hoặc 403 Forbidden)
     * Dùng mã 402 để Frontend dễ nhận diện đây là lỗi cần nạp thêm giờ
     */
    @ExceptionHandler(ExpiryDateException.class)
    public ResponseEntity<Map<String, Object>> handleExpiry(ExpiryDateException e) {
        return buildResponse(HttpStatus.PAYMENT_REQUIRED, "SUBSCRIPTION_EXPIRED", e.getMessage());
    }

    /**
     * 4. Nhóm lỗi LOGIC / DỮ LIỆU SAI (400 Bad Request)
     * Áp dụng cho: Sai mật khẩu, Sai OTP, Hết lượt thi, Trạng thái User không hợp lệ
     */
    @ExceptionHandler({
        InvalidPasswordException.class,
        InvalidOTPException.class,
        NoMoreExamsException.class,
        InvalidUserStateException.class, // Đã cập nhật theo ảnh của Hùng
        VocabFileProcessException.class,
        ProcessorException.class,
        NewsLimitException.class,
        InsufficientDataException.class,
        CommunicationFileProcessException.class,

    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST_LOGIC", e.getMessage());
    }

    /**
     * 5. Bắt lỗi VALIDATION (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "VALIDATION_FAILED");
        body.put("details", fieldErrors);
        body.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 6. Bắt các lỗi hệ thống chung (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception e) {
        // Log lỗi thực tế ra console để Hùng debug
        e.printStackTrace(); 
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_ERROR", "Đã có lỗi hệ thống xảy ra!");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String errorKey, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", errorKey);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}