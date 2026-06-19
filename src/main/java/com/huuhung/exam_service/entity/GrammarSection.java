package com.huuhung.exam_service.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "grammar_sections")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class GrammarSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa chính tự tăng riêng của bảng con

    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder; // Lưu thứ tự bài học (1, 2) từ JSON

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String structure;

    @Column(columnDefinition = "text")
    private String usage;

    // SỬ DỤNG PHONG CÁCH CŨ: Đồng bộ 100% với các class khác trong dự án
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> examples; // Lưu mảng ví dụ [ {korean, vietnamese}, ... ] dưới dạng jsonb

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grammar_id", nullable = false) // Khóa ngoại trỏ thẳng về cột grammar_id của bảng cha
    private Grammar grammar;
}