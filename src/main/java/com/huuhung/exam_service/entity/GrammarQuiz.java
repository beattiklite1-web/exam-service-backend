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
@Table(name = "grammar_quizzes")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class GrammarQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa chính tự tăng riêng của bảng con

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder; // Lưu thứ tự câu (1 đến 20) từ JSON

    @Column(nullable = false, length = 50)
    private String type; // multiple_choice, translate_ko_vi, translate_vi_ko

    @Column(columnDefinition = "text", nullable = false)
    private String title; // Nội dung đề bài câu hỏi

    // SỬ DỤNG PHONG CÁCH CŨ: Đồng bộ mảng options trắc nghiệm dạng jsonb không cần class con
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> options;

    @Column(name = "correct_answer")
    private String correctAnswer;

    // SỬ DỤNG PHONG CÁCH CŨ: Lưu mảng chuỗi đáp án tự luận Hàn - Việt được chấp nhận
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Object> acceptedAnswers;

    @Column(name = "exact_match_no_space")
    private String exactMatchNoSpace; // Chuỗi viết liền để Front-end dễ so khớp tự luận

    private String hint; // Gợi ý từ vựng

    @Column(columnDefinition = "text")
    private String explanation; // Lời giải thích chi tiết

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grammar_id", nullable = false) // Khóa ngoại trỏ thẳng về cột grammar_id của bảng cha
    private Grammar grammar;
}
