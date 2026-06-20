/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor   
@AllArgsConstructor
@Data
@Builder
public class QuestionResponse {
    private Long id;
    private Integer questionId;
    private String type;
    private String code;
    private String questionText;
    private String image;
    private String audio;
    private List<Map<String, Object>> options; // Mảng options [ {number, text, code},... ]
    private String correctAnswer; // Mã đáp án đúng (ví dụ: 60:A) để FE tự chấm
}
