/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.CommunicationDetailUpdateRequest;
import com.huuhung.exam_service.dto.request.CommunicationLobbyUpdateRequest;
import com.huuhung.exam_service.dto.request.CommunicationRequest;
import com.huuhung.exam_service.dto.response.CommunicationAdminDetailResponse;
import com.huuhung.exam_service.dto.response.CommunicationLobbyResponse;
import com.huuhung.exam_service.dto.response.CommunicationUserDetailResponse;

import java.util.List;

public interface CommunicationService {

    // ==========================================
    // 1. NHÓM QUẢN TRỊ (ADMIN)
    // ==========================================

    /**
     * Nạp toàn bộ dữ liệu bài học giao tiếp + hội thoại phân vai từ đối tượng Request (JSON)
     */
    void importFullCommunication(CommunicationRequest request);

    /**
     * Lấy danh sách toàn bộ bài học giao tiếp tại trang Lobby quản trị của Admin
     */
    List<CommunicationLobbyResponse> getLobbyForAdmin();

    /**
     * Lấy chi tiết cấu trúc nội dung bài học phục vụ giao diện chỉnh sửa của Admin
     */
    CommunicationAdminDetailResponse getDetailForAdmin(Long id);

    /**
     * Cập nhật nhanh thông tin cơ bản (Tiêu đề) của bài học giao tiếp ngoài sảnh Lobby
     */
    void updateLobby(Long id, CommunicationLobbyUpdateRequest request);

    /**
     * Cập nhật chuyên sâu toàn bộ nội dung chi tiết (Hội thoại, từ vựng, ngữ pháp) của bài học
     */
    void updateDetail(Long id, CommunicationDetailUpdateRequest request);

    /**
     * Xóa vĩnh viễn bài học giao tiếp (Tự động dọn sạch câu thoại bảng con và khối JSONB nhờ Cascade)
     */
    void deleteCommunication(Long id);


    // ==========================================
    // 2. NHÓM NGƯỜI DÙNG (USER)
    // ==========================================

    /**
     * Lấy danh sách bài học giao tiếp hiển thị ngoài trang sảnh dành cho học viên
     */
    List<CommunicationLobbyResponse> getLobbyForUser(String username);

    /**
     * Lấy chi tiết nội dung hộp thoại chatbox theo đúng thứ tự và khối kiến thức cho học viên học
     */
    CommunicationUserDetailResponse getDetailForUser(Long id, String username);
}