/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title; // Tiêu đề bài viết

    @Column(name = "image_url")
    private String imageUrl; // Ảnh đại diện bài viết (Thumbnail)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung chi tiết (Có thể lưu HTML từ CKEditor/Quill)

    @Column(name = "is_hot", nullable = false)
    @Builder.Default
    private Boolean isHot = false; // Trạng thái Admin bật thủ công (Tối đa 3 bài)

    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private Boolean isPublished = true; // Trạng thái ẩn/hiện bài viết

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0; // Lượt xem bài viết (Dùng để sau này lọc bài xem nhiều)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- TỰ ĐỘNG CẬP NHẬT THỜI GIAN ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
