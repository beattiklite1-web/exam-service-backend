        /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import lombok.Data;

@Data
public class EpsPageUpdateRequest {
    private String image; // Tên file ảnh mới: "18_v2.jpg"
    private String audio; // Tên file audio mới: "04425_new.mp3"
    private Boolean hasQuiz;
}