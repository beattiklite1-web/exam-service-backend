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
public class VocabProgressRequest {
    private Long vocabId;
    private Boolean isMastered;
    private Boolean isIncrementListen; // Nếu true thì tăng listenCount
}
