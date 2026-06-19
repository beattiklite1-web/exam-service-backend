/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.ListeningPartRequest;
import com.huuhung.exam_service.dto.request.ListeningPartUpdateRequest;
import com.huuhung.exam_service.dto.request.ListeningRequest;
import com.huuhung.exam_service.dto.response.ListeningPartResponse;
import com.huuhung.exam_service.dto.response.ListeningResponse;
import com.huuhung.exam_service.entity.Listening;
import com.huuhung.exam_service.entity.ListeningPart;
import com.huuhung.exam_service.exception.InsufficientDataException;
import com.huuhung.exam_service.repository.ListeningPartRepository;
import com.huuhung.exam_service.repository.ListeningRepository;
import com.huuhung.exam_service.service.ListeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListeningServiceImpl implements ListeningService {

    private final ListeningPartRepository partRepository;
    private final ListeningRepository listeningRepository;

    // ==========================================
    // 1. NHÓM QUẢN TRỊ (ADMIN)
    // ==========================================

    @Override
    @Transactional
    public void importFullListeningPart(ListeningPartRequest request) {
        // 1. Kiểm tra tồn tại trước khi xử lý
        boolean isExisting = partRepository.existsByPartNumber(request.getPartNumber());

        // 2. Tìm hoặc tạo mới ListeningPart
        ListeningPart part = partRepository.findByPartNumber(request.getPartNumber())
                .orElseGet(() -> ListeningPart.builder()
                        .partNumber(request.getPartNumber())
                        .build());

        part.setTitle(request.getTitle());
        ListeningPart savedPart = partRepository.save(part);

        // 3. BƯỚC QUAN TRỌNG: Nếu đã tồn tại, dọn dẹp câu hỏi cũ
        if (isExisting) {
            // Xóa sạch để tránh việc 40 câu cũ cộng dồn với 40 câu mới thành 80 câu
            listeningRepository.deleteByPartId(savedPart.getId());
            // Đẩy lệnh xóa xuống DB ngay lập tức trước khi thực hiện lưu mới
            listeningRepository.flush(); 
        }

        // 4. Chuyển đổi danh sách câu hỏi (Giữ nguyên code của ông)
        List<Listening> listenings = request.getQuestions().stream().map(q -> 
            Listening.builder()
                    .audioUrl(q.getAudio())
                    .options(q.getOptions()) 
                    .lessonTag(q.getLessonTag())
                    .part(savedPart)
                    .build()
        ).collect(Collectors.toList());

        // 5. Lưu toàn bộ câu hỏi
        listeningRepository.saveAll(listenings);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ListeningPartResponse> getAllParts() {
        // 🌟 THẦN CHÚ DIỆT N+1: Gọi thẳng hàm gộp Object Constructor của Repository
        // Dữ liệu đếm số câu hỏi đã được Postgres đóng gói sẵn vào DTO, trả thẳng về luôn!
        return partRepository.getPartsWithQuestionsCountSingleQuery();
    }

    @Override
    @Transactional
    public void updateListeningPart(Long id, ListeningPartUpdateRequest request) {
        ListeningPart part = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bộ đề nghe ID: " + id));
        
        part.setTitle(request.getTitle());
        part.setPartNumber(request.getPartNumber());
        partRepository.save(part);
    }

    @Override
    @Transactional
    public void updateListening(Long id, ListeningRequest request) {
        Listening listening = listeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi nghe ID: " + id));

        listening.setAudioUrl(request.getAudio());
        listening.setOptions(request.getOptions());
        listening.setLessonTag(request.getLessonTag());

        listeningRepository.save(listening);
    }

    @Override
    @Transactional
    public void deleteListening(Long id) {
        if (!listeningRepository.existsById(id)) {
            throw new RuntimeException("Câu hỏi không tồn tại để xóa.");
        }
        listeningRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deletePart(Long partId) {
        if (!partRepository.existsById(partId)) {
            throw new RuntimeException("Bộ đề không tồn tại để xóa.");
        }
        // Nhờ CascadeType.ALL và orphanRemoval=true, các câu hỏi con sẽ tự động bị xóa
        partRepository.deleteById(partId);
    }

    // ==========================================
    // 2. NHÓM NGƯỜI DÙNG (USER)
    // ==========================================

    @Override
    public List<ListeningResponse> getListenings(Long partId) {
        List<Listening> data = listeningRepository.findByPartId(partId);
        return data.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ListeningResponse> generateQuiz(Long partId) {
        List<Listening> questions = listeningRepository.findByPartId(partId);

        if (questions.isEmpty()) {
            throw new InsufficientDataException("Bộ đề này chưa có dữ liệu câu hỏi!");
        }

        // 1. Đảo thứ tự danh sách câu hỏi
        Collections.shuffle(questions);

        return questions.stream().map(q -> {
            ListeningResponse res = convertToResponse(q);
            
            // 2. Đảo thứ tự các đáp án bên trong JSONB để tránh học vẹt vị trí
            // Cần tạo ArrayList mới vì q.getOptions() có thể là UnmodifiableList
            List<java.util.Map<String, Object>> shuffledOptions = new ArrayList<>(res.getOptions());
            Collections.shuffle(shuffledOptions);
            res.setOptions(shuffledOptions);
            
            return res;
        }).collect(Collectors.toList());
    }

    /**
     * Hàm hỗ trợ map từ Entity sang Response DTO
     */
    private ListeningResponse convertToResponse(Listening listening) {
        return ListeningResponse.builder()
                .id(listening.getId())
                .audioUrl(listening.getAudioUrl())
                .options(listening.getOptions()) // Jackson sẽ lo việc parse JSON
                .lessonTag(listening.getLessonTag())
                .build();
    }
}
