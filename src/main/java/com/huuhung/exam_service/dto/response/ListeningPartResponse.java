/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;    // 🌟 1. THÊM ĐƯỜNG IMPORT NÀY
import lombok.AllArgsConstructor; // 🌟 2. THÊM ĐƯỜNG IMPORT NÀY

@Data
@Builder
@NoArgsConstructor    // 🌟 3. CẮM CÔNG TẮC NÀY
@AllArgsConstructor   // 🌟 4. CẮM CÔNG TẮC NÀY
public class ListeningPartResponse {
    private Long id;
    private Integer partNumber;
    private String title;
    private long totalQuestions; // Để hiện "40 câu" ở UI cho đẹp
}
