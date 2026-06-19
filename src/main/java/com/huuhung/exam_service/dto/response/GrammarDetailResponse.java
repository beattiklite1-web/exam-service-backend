/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;



import java.util.List;
import java.util.Map;

import lombok.*;

@Data
@Builder
public class GrammarDetailResponse {
    private Long grammarId;
    private String title;
    private String description;
    private List<Map<String, Object>> grammarSections;
    private List<QuizSecureResponse> questions; // Danh sách câu hỏi đã ẩn đáp án bảo mật
}
