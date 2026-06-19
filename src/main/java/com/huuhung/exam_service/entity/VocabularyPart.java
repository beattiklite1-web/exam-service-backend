/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

// 1. JPA & Persistence (Quản lý Database)
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

// 2. Lombok (Giúp code sạch hơn)
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

// 3. Java Util (Cho danh sách words)
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "vocabulary_parts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VocabularyPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer partNumber; // Chỉ chạy từ 1 đến 6

    private String title; // Ví dụ: "Từ vựng Bài 1 - 10"

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Vocabulary> words = new ArrayList<>();
}
