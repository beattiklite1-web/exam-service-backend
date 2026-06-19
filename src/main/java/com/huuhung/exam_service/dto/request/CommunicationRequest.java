/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import com.huuhung.exam_service.model.GrammarCommunicationItem;
import com.huuhung.exam_service.model.VocabularyCommunicationItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationRequest {

    private LessonDTO lesson;
    private List<DialogueDTO> dialogue;
    private KnowledgeDTO knowledge;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonDTO {
        private String title;
        private String language_pair; // Đồng bộ snake_case chuẩn file JSON
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DialogueDTO {
        private String speaker;
        private String role;
        private String text_kr;       // Đồng bộ snake_case chuẩn file JSON
        private String text_vi;       // Đồng bộ snake_case chuẩn file JSON
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeDTO {
        private List<VocabularyCommunicationItem> vocabulary;
        private List<GrammarCommunicationItem> grammar;
    }
}