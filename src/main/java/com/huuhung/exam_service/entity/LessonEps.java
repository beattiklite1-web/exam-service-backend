/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "eps_lesson")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonEps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Tên bài: "6. 저는 투안입니다"

    private Integer startPage; // Trang bắt đầu (Dùng để query BETWEEN)
    private Integer endPage;   // Trang kết thúc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @ToString.Exclude
    private TopicEps topic;
}