/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.*;

@Data
@Builder
public class QuizCheckDetailResponse {
    private Integer questionOrder;
    private boolean isCorrect;
    private String correctAnswer; // Chỉ khi nộp bài mới trả đáp án chuẩn về
    private String explanation;   // Trả lời giải chi tiết hiển thị dưới giao diện
}
