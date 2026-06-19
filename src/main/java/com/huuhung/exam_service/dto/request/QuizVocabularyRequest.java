/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import lombok.Data;

/**
 *
 * @author qnam0
 */
@Data
public class QuizVocabularyRequest {
    private Long partId;          // ID của Part muốn luyện tập
    private String mode;          // Chế độ: "K_V" hoặc "V_K"
    private boolean isShuffle;    // Có muốn xáo trộn thứ tự các câu hỏi không
}
