/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "listening")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String audioUrl; // Link file .mp3

    /**
     * Dùng List<Map> để hứng trọn data từ JSON:
     * [{"number": 1, "text": "야외", "is_correct": true}, ...]
     */
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> options;

    private Integer lessonTag; // Phân loại theo bài học (Lesson 1, 2...)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private ListeningPart part;
}
