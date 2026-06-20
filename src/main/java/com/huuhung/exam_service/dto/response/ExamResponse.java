/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   
@AllArgsConstructor
public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
// Danh sách câu hỏi để hiển thị
    private List<QuestionResponse> questions;
    
    @JsonProperty("isPublished")
    private Boolean isPublished;
    // Danh sách đáp án chuẩn để Frontend tự chấm điểm
    private List<AnswerResponse> answers;
}