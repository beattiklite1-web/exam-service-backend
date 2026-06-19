/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author qnam0
 */
@Data @Builder
public class AnswerResponse {
    private Integer questionOrder;
    private String correctOptionCode; // Ví dụ: "60:A"
}
