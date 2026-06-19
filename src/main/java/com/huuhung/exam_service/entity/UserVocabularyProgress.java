/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_vocab_progress", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "vocab_id"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserVocabularyProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Giả định ông đã có lớp User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isMastered = false; // Đã thuộc hay chưa

    @Builder.Default
    private Integer listenCount = 0; // Số lần click nghe

    private LocalDateTime lastStudied;
}