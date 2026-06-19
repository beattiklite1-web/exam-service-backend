/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GrammarRequest {
    private String title;
    private String description;
    private List<Map<String, Object>> grammarSectionsJson; // Hứng mảng "grammarSection" từ file JSON
    private List<Map<String, Object>> grammarQuizzesJson;   // Hứng mảng "quizSection" hoặc "questions" từ file JSON
}