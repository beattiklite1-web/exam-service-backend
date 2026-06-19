/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "eps_page", indexes = {
    @Index(name = "idx_book_page", columnList = "book_id, pageNumber")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageEps {
    @Id
    // Dùng String ID kiểu "book1_page18" để đảm bảo không bao giờ trùng giữa 2 tập
    private String compositeId; 

    private Integer pageNumber; // ID gốc trong JS (1 -> 395)
    
    private String image; // Tên file: "18.jpg"
    
    private String audio; // Tên file: "0442512357.mp3" (Nếu có)

    @Builder.Default
    private Boolean hasQuiz = false; // Đánh dấu trang có bài tập

    @Column(name = "book_id")
    private Integer bookId;
}
