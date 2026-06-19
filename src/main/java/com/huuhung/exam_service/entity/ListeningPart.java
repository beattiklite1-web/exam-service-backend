/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "listening_part")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer partNumber; // Số thứ tự tập (Ví dụ: 1, 2, 3...)

    private String title; // Tiêu đề tập nghe

    // Cascade ALL để khi xóa Part thì tự động xóa hết các câu hỏi bên trong
    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Listening> listenings = new ArrayList<>();
}
