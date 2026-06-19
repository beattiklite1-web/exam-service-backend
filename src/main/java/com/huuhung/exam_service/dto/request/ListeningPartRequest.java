/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class ListeningPartRequest {
    private Integer partNumber;
    private String title;
    private List<ListeningRequest> questions;
}
