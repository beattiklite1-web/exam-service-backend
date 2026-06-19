/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.response.NewsResponse;
import com.huuhung.exam_service.dto.response.NewsDetailResponse;
import com.huuhung.exam_service.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    // Lấy 3 bài viết cho trang chủ (Logic Auto-fill đủ 3 bài)
    @GetMapping("/hot")
    public ResponseEntity<List<NewsResponse>> getHomeHot() {
        return ResponseEntity.ok(newsService.getHomeHotNews());
    }

    // Lấy danh sách bài viết cho trang Lobby
    // Ví dụ: /api/news/lobby?sort=asc hoặc /api/news/lobby?sort=desc
    @GetMapping("/lobby")
    public ResponseEntity<List<NewsResponse>> getLobby(
            @RequestParam(defaultValue = "desc") String sort) {
        return ResponseEntity.ok(newsService.getLobbyNews(sort));
    }

    // Lấy chi tiết bài viết (Tự động tăng lượt xem)
    @GetMapping("/{id}")
    public ResponseEntity<NewsDetailResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsDetail(id));
    }

    // Lấy top bài viết xem nhiều (Trending)
    @GetMapping("/trending")
    public ResponseEntity<List<NewsResponse>> getTrending() {
        return ResponseEntity.ok(newsService.getTrendingNews());
    }
}
