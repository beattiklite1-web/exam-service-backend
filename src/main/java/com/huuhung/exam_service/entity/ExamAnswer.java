/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_answers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với đề thi (Exam)
    // Dùng LAZY để tối ưu hiệu năng khi không cần load thông tin Exam kèm theo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    // Thứ tự câu hỏi trong đề thi (1, 2, 3, 4, ..., 40)
    // Dùng để khớp với thứ tự các câu hỏi trong file JSON
    @Column(name = "question_order")
    private Integer questionOrder;

    // Mã đáp án đúng bóc tách từ file .md
    // Ví dụ: "#60_A" trong file .md sẽ được chuẩn hóa thành "60:A" để khớp với JSON
    @Column(name = "correct_option_code")
    private String correctOptionCode;

    // Bạn có thể thêm trường này nếu muốn lưu ID câu hỏi thực tế sau khi đã map thành công
    @Column(name = "question_id")
    private Long questionId;
}