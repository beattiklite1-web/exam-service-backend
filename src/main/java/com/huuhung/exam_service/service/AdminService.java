/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;
import java.util.List;

public interface AdminService {
    // === QUẢN LÝ USER ===
    List<UserAdminResponse> searchUsers(String keyword);
    void toggleUserStatus(Long userId);
    void extendUserTime(String username, int hours);
    void deleteUser(Long id);

    // === QUẢN LÝ ĐỀ THI ===
    List<ExamResponse> getAllExams();
    void updateExam(Long id, ExamUpdateRequest request);
    void deleteExam(Long id);
    void importExam(ExamRequest request);
    public ExamResponse getExamById(Long id);

    // === QUẢN LÝ CÂU HỎI LẺ ===
    void updateQuestion(Long id, QuestionUpdateRequest request);

    // === THỐNG KÊ ===
    AdminStatsResponse getStats();
}