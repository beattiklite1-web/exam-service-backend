/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartProgressResponse {
    private Long partId;
    private Integer partNumber;
    private String title;
    private Double completionPercentage; // Ví dụ: 65.5 (%)
    private Long masteredCount;
    private Long totalCount;
}
