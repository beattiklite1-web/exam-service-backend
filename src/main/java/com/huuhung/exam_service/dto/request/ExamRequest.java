/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExamRequest {
    private String title;
    private String description;
    private Integer duration;
    private String rawMdAnswers; // Chuỗi thô: #60_A:#183_D...
    private List<Map<String, Object>> questionsJson; // Mảng chứa 40 câu hỏi từ JSON
}