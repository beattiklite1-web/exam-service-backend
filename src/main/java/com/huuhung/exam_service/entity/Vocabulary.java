/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

// 1. JPA & Persistence (Quản lý thực thể và quan hệ DB)
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;

// 2. Lombok (Hỗ trợ generate code tự động)
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "vocabularies")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String korean;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaning;

    private String audioUrl;

    @Column(name = "word_group")
    private Integer wordGroup; // Group 1, 2, 3... trong JSON

    private Integer lessonTag; // Ví dụ: 1, 2, 3... 60

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private VocabularyPart part; 
    
    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Tránh lỗi vòng lặp vô tận khi render JSON
    private List<UserVocabularyProgress> progressList;
}