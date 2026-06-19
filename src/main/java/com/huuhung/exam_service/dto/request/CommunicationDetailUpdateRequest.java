/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import com.huuhung.exam_service.model.GrammarCommunicationItem;
import com.huuhung.exam_service.model.VocabularyCommunicationItem;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CommunicationDetailUpdateRequest {
    private String title;
    private String language_pair; // Đã sửa từ languagePair sang language_pair để thống nhất logic bóc mảng với client
    private List<Map<String, Object>> dialogues; 
    private List<VocabularyCommunicationItem> vocabulary;
    private List<GrammarCommunicationItem> grammar;
}