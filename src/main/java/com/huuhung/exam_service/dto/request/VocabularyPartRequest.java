/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class VocabularyPartRequest {
    private Integer partNumber;
    private String title;
    private List<Map<String, Object>> wordsJson; // Mảng chứa các group và words từ JSON
}
