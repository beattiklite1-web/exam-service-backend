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
public class QuizAdminResponse {
    private Integer questionOrder;
    private String type;
    private String title;
    private List<Map<String, Object>> options;
    private String hint;
    private String correctAnswer;       // Hiện rõ đáp án trắc nghiệm
    private List<Object> acceptedAnswers; // Hiện rõ mảng đáp án Hàn-Việt
    private String exactMatchNoSpace;   // Hiện rõ chuỗi so khớp Việt-Hàn
    private String explanation;         // Hiện rõ lời giải thích chi tiết
}
