package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "exams")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "text")
    private String description;

    private Integer duration;      
    private Boolean showAnswer;    
    private Boolean isPublished;   

    // Đổi sang List và dùng OrderBy để khóa cứng thứ tự đáp án từ 1 đến 40
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC") 
    @Builder.Default
    private List<ExamAnswer> answers = new ArrayList<>();
    
    // Đổi sang List và dùng OrderBy để khóa cứng thứ tự câu hỏi từ 1 đến 40
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionId ASC") 
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
}