/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.dto.request;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class EpsImportRequest {
    private Integer bookId;
    private List<Map<String, Object>> structure;
    private List<Map<String, Object>> pages;
}
