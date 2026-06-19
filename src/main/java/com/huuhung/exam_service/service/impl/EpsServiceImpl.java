/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.entity.*;
import com.huuhung.exam_service.exception.*;
import com.huuhung.exam_service.repository.*;
import com.huuhung.exam_service.service.EpsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpsServiceImpl implements EpsService {

    private final TopicEpsRepository topicRepo;
    private final PageEpsRepository pageRepo;
    private final LessonEpsRepository lessonRepo;
    private final UserRepository userRepo;

@Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void importFullEpsBook(EpsImportRequest request, String adminUsername) {
        // 1. Kiểm tra Admin qua Principal (ATTT)
        userRepo.findByUsername(adminUsername)
                .orElseThrow(() -> new UserNotFoundException("Admin không hợp lệ: " + adminUsername));

        try {
            Integer bookId = request.getBookId();
            
            // 2. CHUYÊN NGHIỆP: Xóa sạch dữ liệu cũ của tập này trước khi nạp mới
            // Đảm bảo không còn Lesson/Topic rác nếu cấu trình thay đổi
            this.deleteBook(bookId, adminUsername);

            // 3. Lưu Pages (Dùng compositeId để chống trùng giữa 2 tập)
            if (request.getPages() != null) {
                List<PageEps> pages = request.getPages().stream().map(p -> PageEps.builder()
                        .compositeId(bookId + "_" + p.get("id"))
                        .pageNumber(Integer.valueOf(p.get("id").toString())) // Convert an toàn
                        .image((String) p.get("image"))
                        .audio(p.get("audio") != null ? (String) p.get("audio") : null)
                        .hasQuiz(p.get("quiz") != null && (Boolean) p.get("quiz"))
                        .bookId(bookId)
                        .build()).collect(Collectors.toList());
                pageRepo.saveAll(pages);
            }

            // 4. Lưu Structure (Topic -> Lessons)
            if (request.getStructure() != null) {
                for (Map<String, Object> tObj : request.getStructure()) {
                    TopicEps topic = TopicEps.builder()
//                            .id(Integer.valueOf(tObj.get("id").toString())) 
                            .name((String) tObj.get("name"))
                            .bookId(bookId)
                            .build();

                    List<Map<String, Object>> lessonsJson = (List<Map<String, Object>>) tObj.get("lessons");
                    if (lessonsJson != null) {
                        List<LessonEps> lessons = lessonsJson.stream().map(l -> LessonEps.builder()
                                .title((String) l.get("title"))
                                .startPage(Integer.valueOf(l.get("start_page").toString()))
                                .endPage(Integer.valueOf(l.get("end_page").toString()))
                                .topic(topic)
                                .build()).collect(Collectors.toList());
                        topic.setLessons(lessons);
                    }
                    
                    topicRepo.save(topic); // Cascade lưu Lessons
                }
            }
            log.info("Admin [{}] nạp thành công Tập {}. Hệ thống đã tự động dọn dẹp dữ liệu cũ.", adminUsername, bookId);
        } catch (Exception e) {
            log.error("Lỗi Import EPS của Admin {}: {}", adminUsername, e.getMessage());
            throw new VocabFileProcessException("Lỗi bóc tách JSON EPS: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public void deleteBook(Integer bookId, String adminUsername) {
        validateAdmin(adminUsername);
        log.warn("Admin {} yêu cầu xóa sạch Tập {}", adminUsername, bookId);
        
        // Xóa sạch 3 tầng theo thứ tự ràng buộc
        lessonRepo.deleteByBookId(bookId);
        topicRepo.deleteByBookId(bookId);
        pageRepo.deleteByBookId(bookId);
    }

    @Override
    @Transactional
    public void updatePageContent(String compositeId, EpsPageUpdateRequest request, String adminUsername) {
        validateAdmin(adminUsername);
        PageEps page = pageRepo.findById(compositeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không thấy trang: " + compositeId));
        
        page.setImage(request.getImage());
        page.setAudio(request.getAudio());
        page.setHasQuiz(request.getHasQuiz());
        pageRepo.save(page);
    }

    // --- Nhóm Hiển thị ---
    @Override
    @Transactional(readOnly = true)
    public List<TopicEpsResponse> getBookMenu(Integer bookId) {
        // 🌟 KHÓA CHẶT N+1: Sử dụng hàm FETCH JOIN để lội DB gom toàn bộ Lessons lên RAM cùng Topic trong 1 hit độc nhất!
        List<TopicEps> topics = topicRepo.findByBookIdWithLessonsFetchAsync(bookId);

        return topics.stream().map(topic -> {
            // Không sử dụng hàm helper mapToTopicResponse cũ để tránh trigger Lazy Loading lặp dữ liệu
            List<LessonEpsResponse> lessonDtos = topic.getLessons().stream()
                    .map(l -> LessonEpsResponse.builder()
                            .id(l.getId())
                            .title(l.getTitle())
                            .startPage(l.getStartPage())
                            .endPage(l.getEndPage())
                            .build())
                    .collect(Collectors.toList());

            return TopicEpsResponse.builder()
                    .id(topic.getId()) // Convert an toàn từ Integer sang Long cho khớp DTO Response
                    .name(topic.getName())
                    .bookId(topic.getBookId())
                    .lessons(lessonDtos)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<PageEpsResponse> getLessonPages(Long lessonId) {
        LessonEps lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học ID " + lessonId + " không tồn tại trong hệ thống."));

        return pageRepo.findByBookIdAndPageNumberBetween(
                lesson.getTopic().getBookId(), lesson.getStartPage(), lesson.getEndPage()
        ).stream().map(this::mapToPageResponse).collect(Collectors.toList());
    }

    @Override
    public PageEpsResponse getSinglePage(Integer bookId, Integer pageNumber) {
        String compositeId = bookId + "_" + pageNumber;
        return pageRepo.findById(compositeId)
                .map(this::mapToPageResponse)
                .orElseThrow(() ->  new ResourceNotFoundException("Trang số " + pageNumber + " của tập " + bookId + " chưa có dữ liệu."));
    }

    // --- Helpers ---
    private void validateAdmin(String username) {
        userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Phiên làm việc admin đã hết hạn: " + username));
    }

    private TopicEpsResponse mapToTopicResponse(TopicEps t) {
        return TopicEpsResponse.builder()
                .id(t.getId()).name(t.getName()).bookId(t.getBookId())
                .lessons(t.getLessons().stream().map(l -> LessonEpsResponse.builder()
                        .id(l.getId()).title(l.getTitle())
                        .startPage(l.getStartPage()).endPage(l.getEndPage())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private PageEpsResponse mapToPageResponse(PageEps p) {
        return PageEpsResponse.builder()
                .compositeId(p.getCompositeId()).bookId(p.getBookId())
                .pageNumber(p.getPageNumber()).image(p.getImage())
                .audio(p.getAudio()).hasQuiz(p.getHasQuiz())
                .build();
    }
}