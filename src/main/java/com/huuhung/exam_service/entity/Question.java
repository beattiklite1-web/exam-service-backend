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
@Table(name = "questions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID tự tăng của Database (Primary Key)

    @Column(name = "question_id")
    private Integer questionId; // Ánh xạ từ trường "id" trong file JSON (1, 2, 3...)

    private String type; // reading, listening...
    private String code; // Mã câu hỏi (ví dụ: MA-DENC-17-1)
    
    @Column(columnDefinition = "text")
    private String questionText; // Nội dung câu hỏi
    
    private String image; // Link ảnh (nếu có)
    private String audio; // Link audio (nếu có)

    // LƯU TRỮ JSONB: Dùng để lưu mảng options [ {number, text, code}, ... ]
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> options;

    // MỐI QUAN HỆ: Nhiều câu hỏi thuộc về một Đề thi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;
}