/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListeningResponse {
    private Long id;
    private String audioUrl;
    private List<Map<String, Object>> options;
    private Integer lessonTag;
}
