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
import java.util.Map;

@Data
@Builder
public class CommunicationAdminDetailResponse {
    private Long communicationId;
    private String title;
    private String language_pair;
    private List<Map<String, Object>> dialogues; // Trả về dạng map phẳng hóa thứ tự để admin quản lý
    private List<VocabularyCommunicationItem> vocabulary;
    private List<GrammarCommunicationItem> grammar;
}
