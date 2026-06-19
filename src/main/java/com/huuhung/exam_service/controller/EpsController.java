/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.response.PageEpsResponse;
import com.huuhung.exam_service.dto.response.TopicEpsResponse;
import com.huuhung.exam_service.service.EpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eps") // Path riêng biệt cho User
@RequiredArgsConstructor
public class EpsController {

    private final EpsService epsService;

    /**
     * 1. Lấy danh mục bài học của Tập 1 hoặc Tập 2
     * Frontend dùng cái này để vẽ Sidebar hoặc Menu chọn bài.
     */
    @GetMapping("/menu/{bookId}")
    public ResponseEntity<List<TopicEpsResponse>> getMenu(@PathVariable Integer bookId) {
        return ResponseEntity.ok(epsService.getBookMenu(bookId));
    }

    /**
     * 2. Lấy toàn bộ các trang của một bài học (Lesson)
     * Khi sinh viên bấm vào "Bài 6: Xin chào", Frontend gọi API này để lấy 
     * danh sách ảnh/audio từ trang bắt đầu đến trang kết thúc.
     */
    @GetMapping("/lesson/{lessonId}/pages")
    public ResponseEntity<List<PageEpsResponse>> getPagesByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(epsService.getLessonPages(lessonId));
    }

    /**
     * 3. Lấy thông tin một trang lẻ
     * Dùng cho tính năng "Nhảy trang" hoặc xem lại một trang cụ thể qua số trang.
     */
    @GetMapping("/book/{bookId}/page/{pageNumber}")
    public ResponseEntity<PageEpsResponse> getPage(
            @PathVariable Integer bookId, 
            @PathVariable Integer pageNumber) {
        return ResponseEntity.ok(epsService.getSinglePage(bookId, pageNumber));
    }
}