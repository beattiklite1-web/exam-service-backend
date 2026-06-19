package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

// 1. Topic: Đại diện cho các Chương lớn (Ví dụ: Sinh hoạt cơ bản, Lao động...)
@Entity
@Table(name = "eps_topic")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicEps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Lấy ID gốc từ file JS (ví dụ: 12, 23)

    @Column(nullable = false)
    private String name;

    @Column(name = "book_id", nullable = false)
    private Integer bookId; // Để phân biệt Tập 1 và Tập 2

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude // ATTT: Tránh lỗi vòng lặp khi log dữ liệu
    private List<LessonEps> lessons = new java.util.ArrayList<>();
}