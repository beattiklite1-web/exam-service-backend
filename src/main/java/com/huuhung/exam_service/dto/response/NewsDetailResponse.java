/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class NewsDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Boolean isHot;
    private Integer viewCount;
    private LocalDateTime createdAt;
}
