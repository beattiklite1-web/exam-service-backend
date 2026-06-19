/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;
import java.util.List;

public interface EpsService {
// --- ADMIN (Dùng Principal để bảo mật) ---
    void importFullEpsBook(EpsImportRequest request, String adminUsername);
    void deleteBook(Integer bookId, String adminUsername);
    void updatePageContent(String compositeId, EpsPageUpdateRequest request, String adminUsername);

    // --- HIỂN THỊ (Dùng chung cho Admin/User) ---
    List<TopicEpsResponse> getBookMenu(Integer bookId);
    List<PageEpsResponse> getLessonPages(Long lessonId);
    PageEpsResponse getSinglePage(Integer bookId, Integer pageNumber);
}
