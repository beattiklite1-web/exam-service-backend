/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String email;

    // QUẢN LÝ THỜI HẠN SỬ DỤNG (Subscription)
    // User chỉ được vào thi nếu LocalDateTime.now() < expiryDate
    private LocalDateTime expiryDate;

    // DANH SÁCH ID CÁC ĐỀ ĐÃ THI
    // Dùng để logic Random đề không bị lặp lại những đề này
    @ElementCollection
    @CollectionTable(name = "user_completed_exams", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "exam_id")
    @Builder.Default // Đã có - Giữ nguyên để tránh Null khi add đề đã thi
    private List<Long> completedExamIds = new java.util.ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default // Đã có - Giữ nguyên để tránh Null khi gán Role
    private Set<String> roles = new java.util.HashSet<>();

    // Cần thêm @Builder.Default ở đây
    @Builder.Default 
    private boolean enabled = true;
    
    private String verificationToken;
}