/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import com.huuhung.exam_service.model.GrammarCommunicationItem;
import com.huuhung.exam_service.model.VocabularyCommunicationItem;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CommunicationUserDetailResponse {
    private LessonInfo lesson;
    private List<DialogueCommunicationResponse> dialogue;
    private KnowledgeInfo knowledge;

    @Data
    @Builder
    public static class LessonInfo {
        private String title;
        private String language_pair;
    }

    @Data
    @Builder
    public static class KnowledgeInfo {
        private List<VocabularyCommunicationItem> vocabulary;
        private List<GrammarCommunicationItem> grammar;
    }
}