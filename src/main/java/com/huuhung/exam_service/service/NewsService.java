/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.NewsRequest;
import com.huuhung.exam_service.dto.response.NewsResponse;
import com.huuhung.exam_service.dto.response.NewsDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService {

    // --- 1. DÀNH CHO NGƯỜI DÙNG (USER) ---

    /**
     * Lấy 3 bài viết cho trang chủ. 
     * Ưu tiên bài được set 'isHot', nếu thiếu tự động bù bài mới nhất.
     */
    List<NewsResponse> getHomeHotNews();

    /**
     * Lấy danh sách tin tức cho trang Lobby.
     * @param direction: "desc" (mới nhất) hoặc "asc" (cũ nhất)
     */
    List<NewsResponse> getLobbyNews(String direction);

    /**
     * Xem chi tiết bài viết và tự động tăng viewCount.
     */
    NewsDetailResponse getNewsDetail(Long id);


    // --- 2. QUẢN TRỊ VIÊN (ADMIN) ---

    /**
     * Thêm mới tin tức.
     */
    void createNews(NewsRequest request);

    /**
     * Cập nhật thông tin bài viết.
     */
    void updateNews(Long id, NewsRequest request);

    /**
     * Xóa bài viết.
     */
    void deleteNews(Long id);

    /**
     * Bật/Tắt trạng thái Hot thủ công. 
     * Phải kiểm tra không cho phép quá 3 bài.
     */
    void toggleHotStatus(Long id, boolean status);

    /**
     * Thay đổi trạng thái ẩn/hiện bài viết nhanh.
     */
    void updatePublishStatus(Long id, boolean isPublished);

    /**
     * Tìm kiếm bài viết theo tiêu đề (Dành cho trang quản lý Admin).
     */
    Page<NewsResponse> searchNewsAdmin(String title, Pageable pageable);


    // --- 3. THỐNG KÊ (DÀNH CHO TRANG CHỦ HOẶC SIDEBAR) ---

    /**
     * Lấy danh sách tin tức đang dẫn đầu về lượt xem (Trending).
     */
    List<NewsResponse> getTrendingNews();
}
