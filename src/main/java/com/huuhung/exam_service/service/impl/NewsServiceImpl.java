/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.NewsRequest;
import com.huuhung.exam_service.dto.response.NewsResponse;
import com.huuhung.exam_service.dto.response.NewsDetailResponse;
import com.huuhung.exam_service.entity.News;
import com.huuhung.exam_service.exception.NewsLimitException; // File exception ông đã tạo
import com.huuhung.exam_service.repository.NewsRepository;
import com.huuhung.exam_service.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    // --- 1. DÀNH CHO USER ---

    @Override
    public List<NewsResponse> getHomeHotNews() {
        // Lấy danh sách bài Admin chủ động bật Hot
        List<News> hotNews = newsRepository.findByIsHotTrueAndIsPublishedTrueOrderByCreatedAtDesc();
        
        // Nếu thiếu bài Hot (nhỏ hơn 3), tiến hành Auto-fill
        if (hotNews.size() < 3) {
            int needToFill = 3 - hotNews.size();
            // Lấy thêm những bài mới nhất chưa được set Hot
            List<News> fillItems = newsRepository.findLatestNotHot(PageRequest.of(0, needToFill));
            
            // Dùng List mới để tránh lỗi Immutable nếu có
            List<News> resultList = new ArrayList<>(hotNews);
            resultList.addAll(fillItems);
            
            // Trả về đúng 3 bài đầu tiên
            return resultList.stream()
                    .limit(3)
                    .map(this::mapToLobbyResponse)
                    .collect(Collectors.toList());
        }

        // Nếu đã đủ hoặc dư thì lấy 3 bài Hot nhất
        return hotNews.stream()
                .limit(3)
                .map(this::mapToLobbyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsResponse> getLobbyNews(String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") 
                    ? Sort.by("createdAt").ascending() 
                    : Sort.by("createdAt").descending();
        
        return newsRepository.findAllByIsPublishedTrue(sort).stream()
                .map(this::mapToLobbyResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NewsDetailResponse getNewsDetail(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        
        // Tăng lượt xem mỗi khi truy cập
        newsRepository.incrementViewCount(id);
        
        return NewsDetailResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .imageUrl(news.getImageUrl())
                .isHot(news.getIsHot())
                .viewCount(news.getViewCount() + 1) // +1 để UI hiển thị lượt xem mới nhất ngay
                .createdAt(news.getCreatedAt())
                .build();
    }

    // --- 2. DÀNH CHO ADMIN ---

    @Override
    @Transactional
    public void createNews(NewsRequest request) {
        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : true)
                .build();
        newsRepository.save(news);
    }

    @Override
    @Transactional
    public void updateNews(Long id, NewsRequest request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        news.setIsPublished(request.getIsPublished());
        
        newsRepository.save(news);
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bài viết để xóa");
        }
        newsRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleHotStatus(Long id, boolean status) {
        // Chỉ kiểm tra khi Admin muốn BẬT trạng thái Hot
        if (status) {
            long currentHotCount = newsRepository.countByIsHotTrue();
            if (currentHotCount >= 3) {
                throw new NewsLimitException("Đã đạt giới hạn 3 bài Hot. Hãy tắt một bài cũ trước.");
            }
        }
        
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        news.setIsHot(status);
        newsRepository.save(news);
    }

    @Override
    @Transactional
    public void updatePublishStatus(Long id, boolean isPublished) {
        News news = newsRepository.findById(id).orElseThrow();
        news.setIsPublished(isPublished);
        newsRepository.save(news);
    }

    @Override
    public Page<NewsResponse> searchNewsAdmin(String title, Pageable pageable) {
        // Vì pageable đã bao gồm cả Sort bên trong nó rồi, 
        // ông chỉ cần truyền thẳng đối tượng pageable vào repository là xong.
        return newsRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::mapToLobbyResponse); 
    }

    @Override
    public List<NewsResponse> getTrendingNews() {
        return newsRepository.findTop5ByIsPublishedTrueOrderByViewCountDesc().stream()
                .map(this::mapToLobbyResponse)
                .collect(Collectors.toList());
    }

    // --- HELPER METHOD ---
    private NewsResponse mapToLobbyResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .imageUrl(news.getImageUrl())
                .isHot(news.getIsHot())
                .createdAt(news.getCreatedAt())
                .build();
    }
}
