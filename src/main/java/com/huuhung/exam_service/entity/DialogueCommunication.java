/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dialogue_communications")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class DialogueCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dialogue_order", nullable = false)
    private Integer dialogueOrder; // Thứ tự xuất hiện trong hội thoại chatbox (1, 2, 3...)

    @Column(nullable = false, length = 50)
    private String speaker; // "Minu", "Sếp"

    @Column(length = 50)
    private String role; // "employee", "manager" để Frontend chia bên trái/phải và màu sắc

    @Column(name = "text_kr", columnDefinition = "text", nullable = false)
    private String textKr; // Đoạn thoại tiếng Hàn gốc

    @Column(name = "text_vi", columnDefinition = "text", nullable = false)
    private String textVi; // Đoạn thoại dịch nghĩa tiếng Việt

    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communication_id", nullable = false)
    private Communication communication;
}