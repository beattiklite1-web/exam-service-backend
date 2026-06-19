/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class QuizSecureResponse {
    private Integer questionOrder;
    private String type;
    private String title;
    private List<Map<String, Object>> options;
    private String hint;
}