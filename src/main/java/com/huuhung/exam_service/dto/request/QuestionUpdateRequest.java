/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;


import lombok.Data;

@Data
public class QuestionUpdateRequest {
    private String questionText;
    private String image;
    private String audio;
}
