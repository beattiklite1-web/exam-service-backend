/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.response;

import lombok.*;


@Data
@Builder
public class GrammarLobbyResponse {
    private Long grammarId;
    private String title;
    private String description;
    //private boolean isPassed; // Trạng thái hoàn thành hiển thị tích xanh ở Lobby
}
