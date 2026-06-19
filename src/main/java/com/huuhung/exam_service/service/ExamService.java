/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.ExamRequest;
import com.huuhung.exam_service.dto.response.ExamResponse;


public interface ExamService {
    void importFullExam(ExamRequest request);
    ExamResponse getRandomExam(String username);
    void completeExam(String username, Long examId);
    
    ExamResponse getExamDetailWithCache(Long examId);
}
