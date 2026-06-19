/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VocabularyResponse {
    private Long id;
    private String korean;
    private String meaning;
    private String audioUrl;
    private Integer wordGroup;
    private Integer lessonTag;
}
