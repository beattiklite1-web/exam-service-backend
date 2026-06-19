/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author qnam0
 */
@Data
@Builder
public class QuizVocabularyResponse {
    private Long vocabId;         // ID từ vựng gốc
    private String question;      // Từ vựng (Ví dụ: "나이")
    private String correctAnswer; // Nghĩa đúng (Ví dụ: "tuổi")
    private List<String> options; // Mảng 4 chuỗi đáp án đã được xáo trộn
    private String type;          // "K_V" (Hàn-Việt) hoặc "V_K" (Việt-Hàn)
}
