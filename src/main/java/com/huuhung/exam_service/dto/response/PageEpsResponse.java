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
public class PageEpsResponse {
    private String compositeId;
    private Integer bookId;
    private Integer pageNumber;
    private String image;
    private String audio;
    private Boolean hasQuiz;
}