/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class GrammarAdminDetailResponse {
    private Long grammarId;
    private String title;
    private String description;
    private List<Map<String, Object>> grammarSections;
    private List<QuizAdminResponse> questions; // Trả về mảng câu hỏi phiên bản FULL quyền lực
}