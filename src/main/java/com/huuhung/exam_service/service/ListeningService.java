/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.ListeningPartRequest;
import com.huuhung.exam_service.dto.request.ListeningPartUpdateRequest;
import com.huuhung.exam_service.dto.request.ListeningRequest;
import com.huuhung.exam_service.dto.response.ListeningPartResponse;
import com.huuhung.exam_service.dto.response.ListeningResponse;
import java.util.List;

public interface ListeningService {

    // ==========================================
    // 1. NHÓM QUẢN TRỊ (ADMIN)
    // ==========================================

    /**
     * Nạp toàn bộ dữ liệu bộ đề nghe từ JSON (Part + Questions)
     */
    void importFullListeningPart(ListeningPartRequest request);

    
    List<ListeningPartResponse> getAllParts();
    /**
     * Cập nhật thông tin chung của Part (Tiêu đề, số tập)
     */
    void updateListeningPart(Long id, ListeningPartUpdateRequest request);

    /**
     * Cập nhật một câu hỏi nghe đơn lẻ (Audio, Options, Tag)
     */
    void updateListening(Long id, ListeningRequest request);

    /**
     * Xóa một câu hỏi nghe
     */
    void deleteListening(Long id);

    /**
     * Xóa toàn bộ một Part (Xóa cả bộ đề và các câu hỏi bên trong)
     */
    void deletePart(Long partId);


    // ==========================================
    // 2. NHÓM NGƯỜI DÙNG (USER)
    // ==========================================

    /**
     * Lấy danh sách câu hỏi theo Part để hiển thị hoặc quản lý
     */
    List<ListeningResponse> getListenings(Long partId);

    /**
     * Tạo bài trắc nghiệm nghe (Tự động đảo thứ tự câu hỏi và đáp án)
     */
    List<ListeningResponse> generateQuiz(Long partId);
}