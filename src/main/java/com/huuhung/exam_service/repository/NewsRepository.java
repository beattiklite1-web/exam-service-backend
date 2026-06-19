package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.News;
import org.springframework.data.domain.Page; // Quan trọng: Dùng Page cho phân trang
import org.springframework.data.domain.Pageable; // Quan trọng: Nhận Pageable
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // --- 1. LOGIC LOBBY ---
    List<News> findAllByIsPublishedTrue(Sort sort);

    // --- 2. LOGIC HOT NEWS ---
    List<News> findByIsHotTrueAndIsPublishedTrueOrderByCreatedAtDesc();

    @Query("SELECT n FROM News n WHERE n.isHot = false AND n.isPublished = true ORDER BY n.createdAt DESC")
    List<News> findLatestNotHot(Pageable pageable);

    long countByIsHotTrue();

    // --- 3. LOGIC ADMIN & THỐNG KÊ (ĐÃ SỬA TẠI ĐÂY) ---

    // Chuyển sang Page để dùng được hàm .map() trong Service và hỗ trợ phân trang trang Admin
    Page<News> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void incrementViewCount(@Param("id") Long id);

    List<News> findTop5ByIsPublishedTrueOrderByViewCountDesc();

    // --- 4. LOGIC XÓA & TRẠNG THÁI ---
    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.isPublished = :status WHERE n.id IN :ids")
    void updatePublishStatus(@Param("ids") List<Long> ids, @Param("status") Boolean status);
}