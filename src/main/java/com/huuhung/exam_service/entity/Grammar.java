/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grammars")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Grammar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grammar_id") // Đổi tên cột Khóa chính trong DB thành grammar_id luôn cho thuận tiện
    private Long grammarId; // Đây là ID gốc, tự tăng duy nhất của hệ thống Database

    @Column(nullable = false, length = 255)
    private String title; // Lưu "title": "BÀI 10: 어제 도서관에서..."

    @Column(columnDefinition = "text")
    private String description; // Lưu "description": "Ngày tháng và Địa điểm & Hành động..."

    // Mối quan hệ một bài học lớn có nhiều phần lý thuyết ngữ pháp
    @OneToMany(mappedBy = "grammar", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GrammarSection> grammarSections = new ArrayList<>();

    // Mối quan hệ một bài học lớn chứa danh sách câu hỏi thực hành
    @OneToMany(mappedBy = "grammar", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GrammarQuiz> grammarQuizzes = new ArrayList<>();
}