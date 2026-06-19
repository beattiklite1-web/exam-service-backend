/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import lombok.Data;

@Data
public class VocabularyRequest {
    private String korean;
    private String meaning;
    private String audioUrl;
    private Integer wordGroup;
    private Integer lessonTag;
}
