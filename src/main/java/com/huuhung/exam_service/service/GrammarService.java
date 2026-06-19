/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.GrammarInfoUpdateRequest;
import com.huuhung.exam_service.dto.request.GrammarRequest;
import com.huuhung.exam_service.dto.response.*;

import java.util.List;
import java.util.Map;

public interface GrammarService {
    void importFullGrammar(GrammarRequest request);
    // Thêm hàm lấy chi tiết độc quyền cho Admin
    GrammarAdminDetailResponse getGrammarDetailForAdmin(Long grammarId);
    List<GrammarLobbyResponse> getGrammarLobby(String username);
    GrammarDetailResponse getGrammarDetail(Long grammarId);
    void updateGrammarInfo(Long grammarId, GrammarInfoUpdateRequest request);
    void deleteGrammar(Long grammarId);
    QuizResultResponse submitGrammarQuiz(Long grammarId, String username, Map<Integer, String> studentAnswers);
}