/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.entity;

import com.huuhung.exam_service.model.GrammarCommunicationItem;
import com.huuhung.exam_service.model.VocabularyCommunicationItem;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "communications")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Communication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "communication_id")
    private Long communicationId;

    @Column(nullable = false, length = 255)
    private String title; // Lưu "Hội Thoại: Xin Nghỉ Việc (퇴사)"

    @Column(name = "language_pair", length = 10, nullable = false)
    private String languagePair; // Lưu "ko-vi"

    // Một bài học giao tiếp có nhiều câu thoại hiển thị theo thứ tự trong chatbox
    @OneToMany(mappedBy = "communication", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dialogueOrder ASC") // Đảm bảo các câu thoại luôn load ra đúng thứ tự
    @Builder.Default
    private List<DialogueCommunication> dialogues = new ArrayList<>();

    // Lưu mảng từ vựng dưới dạng jsonb với kiểu dữ liệu rõ ràng, an toàn
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<VocabularyCommunicationItem> vocabulary; 

    // Lưu mảng ngữ pháp dưới dạng jsonb với kiểu dữ liệu rõ ràng, an toàn
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<GrammarCommunicationItem> grammar; 
}