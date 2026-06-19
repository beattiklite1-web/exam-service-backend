/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
public class QuizResultResponse {
    private String score; // Định dạng "Số câu đúng / Tổng số câu"
    private List<QuizCheckDetailResponse> details; // Chi tiết từng câu hiển thị lời giải
}
