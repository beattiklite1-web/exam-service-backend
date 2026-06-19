/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.CommunicationDetailUpdateRequest;
import com.huuhung.exam_service.dto.request.CommunicationLobbyUpdateRequest;
import com.huuhung.exam_service.dto.request.CommunicationRequest;
import com.huuhung.exam_service.dto.response.CommunicationAdminDetailResponse;
import com.huuhung.exam_service.dto.response.CommunicationLobbyResponse;
import com.huuhung.exam_service.dto.response.CommunicationUserDetailResponse;
import com.huuhung.exam_service.dto.response.DialogueCommunicationResponse;
import com.huuhung.exam_service.entity.Communication;
import com.huuhung.exam_service.entity.DialogueCommunication;
import com.huuhung.exam_service.exception.CommunicationFileProcessException;
import com.huuhung.exam_service.exception.CommunicationNotFoundException;
import com.huuhung.exam_service.exception.UserNotFoundException;
import com.huuhung.exam_service.repository.CommunicationRepository;
import com.huuhung.exam_service.repository.DialogueCommunicationRepository;
import com.huuhung.exam_service.repository.UserRepository;
import com.huuhung.exam_service.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunicationServiceImpl implements CommunicationService {

    private final CommunicationRepository communicationRepository;
    private final UserRepository userRepository;
    private final DialogueCommunicationRepository dialogueCommunicationRepository;

    // ==========================================
    // 1. NHÓM QUẢN TRỊ (ADMIN)
    // ==========================================

    @Override
    @Transactional
    public void importFullCommunication(CommunicationRequest request) {
        try {
            String title = request.getLesson().getTitle();

            // Kiểm tra bài học đã tồn tại theo tiêu đề chưa để cập nhật đè (giống phong cách ListeningPart của ông giáo)
            Optional<Communication> existingCommOpt = communicationRepository.findAll().stream()
                    .filter(c -> title.equals(c.getTitle()))
                    .findFirst();

            boolean isExisting = existingCommOpt.isPresent();
            Communication communication;

            if (isExisting) {
                communication = existingCommOpt.get();
                communication.setLanguagePair(request.getLesson().getLanguage_pair());
                communication.setVocabulary(request.getKnowledge().getVocabulary());
                communication.setGrammar(request.getKnowledge().getGrammar());
                log.info("Bài học giao tiếp [{}] đã tồn tại. Tiến hành ghi đè cập nhật nội dung mới.", title);
            } else {
                communication = Communication.builder()
                        .title(title)
                        .languagePair(request.getLesson().getLanguage_pair())
                        .vocabulary(request.getKnowledge().getVocabulary())
                        .grammar(request.getKnowledge().getGrammar())
                        .dialogues(new ArrayList<>())
                        .build();
            }

            Communication savedCommunication = communicationRepository.save(communication);

            // BIỆN PHÁP CHỐNG CỘNG DỒN CÂU THOẠI: Dọn sạch mảng bằng 1 câu xoá duy nhất chống Lock DB
            if (isExisting) {
                communicationRepository.save(savedCommunication); // Đẩy info xuống DB trước
                // 🌟 GỠ BOM: Xoá bằng @Modifying thay vì .clear() trên mảng để tránh N+1 delete
                dialogueCommunicationRepository.deleteByCommunicationId(savedCommunication.getCommunicationId());
                savedCommunication.getDialogues().clear(); // Dọn trên RAM đồng bộ
            }

            // Xử lý mảng hội thoại Type-Safe động không giới hạn số lượng câu hỏi từ file JSON truyền vào
            List<CommunicationRequest.DialogueDTO> dialogueDtos = request.getDialogue();
            if (dialogueDtos != null && !dialogueDtos.isEmpty()) {
                for (int i = 0; i < dialogueDtos.size(); i++) {
                    CommunicationRequest.DialogueDTO dDto = dialogueDtos.get(i);

                    DialogueCommunication dialogue = DialogueCommunication.builder()
                            .communication(savedCommunication)
                            .dialogueOrder(i + 1) // Tự động đánh số thứ tự từ 1 đến hết mảng thực tế của file JSON
                            .speaker(dDto.getSpeaker())
                            .role(dDto.getRole())
                            .textKr(dDto.getText_kr())
                            .textVi(dDto.getText_vi())
                            .build();

                    savedCommunication.getDialogues().add(dialogue);
                }
                communicationRepository.save(savedCommunication);
                log.info("Nạp dữ liệu thành công [{}] câu thoại vào bài học giao tiếp.", dialogueDtos.size());
            }
        } catch (Exception e) {
            log.error("Lỗi xảy ra trong quá trình import dữ liệu giao tiếp: {}", e.getMessage());
            throw new CommunicationFileProcessException("Lỗi xử lý cấu trúc dữ liệu import bài giao tiếp: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunicationLobbyResponse> getLobbyForAdmin() {
        return communicationRepository.findAllByOrderByCommunicationIdAsc().stream()
                .map(c -> CommunicationLobbyResponse.builder()
                        .communicationId(c.getCommunicationId())
                        .title(c.getTitle())
                        .languagePair(c.getLanguagePair())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommunicationAdminDetailResponse getDetailForAdmin(Long id) {
        Communication communication = communicationRepository.findById(id)
                .orElseThrow(() -> new CommunicationNotFoundException("Không tìm thấy bài học giao tiếp có ID: " + id));

        // Phẳng hóa danh sách thực thể thoại bảng con thành cấu trúc Map linh hoạt để Admin tiện render Form cấu hình
        List<Map<String, Object>> dialoguesRaw = communication.getDialogues().stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dialogueOrder", d.getDialogueOrder());
            map.put("speaker", d.getSpeaker());
            map.put("role", d.getRole());
            map.put("text_kr", d.getTextKr());
            map.put("text_vi", d.getTextVi());
            return map;
        }).collect(Collectors.toList());

        return CommunicationAdminDetailResponse.builder()
                .communicationId(communication.getCommunicationId())
                .title(communication.getTitle())
                .language_pair(communication.getLanguagePair())
                .dialogues(dialoguesRaw)
                .vocabulary(communication.getVocabulary())
                .grammar(communication.getGrammar())
                .build();
    }

    @Override
    @Transactional
    public void updateLobby(Long id, CommunicationLobbyUpdateRequest request) {
        Communication communication = communicationRepository.findById(id)
                .orElseThrow(() -> new CommunicationNotFoundException("Không thể cập nhật, ID bài giao tiếp không tồn tại: " + id));

        communication.setTitle(request.getTitle());
        communicationRepository.save(communication);
        log.info("Cập nhật thành công tiêu đề bài học ngoài Lobby cho ID: {}", id);
    }

    @Override
    @Transactional
    public void updateDetail(Long id, CommunicationDetailUpdateRequest request) {
        Communication communication = communicationRepository.findById(id)
                .orElseThrow(() -> new CommunicationNotFoundException("Không thể cập nhật, ID bài giao tiếp không tồn tại: " + id));

        // 1. Ghi đè thông tin chung và khối mảng dữ liệu JSONB
        communication.setTitle(request.getTitle());
        communication.setLanguagePair(request.getLanguage_pair());
        communication.setVocabulary(request.getVocabulary());
        communication.setGrammar(request.getGrammar());

        // 2. Dọn dẹp sạch danh sách câu thoại cũ bằng 1 câu lệnh xoá gốc chống Lock DB
        communicationRepository.save(communication);
        dialogueCommunicationRepository.deleteByCommunicationId(communication.getCommunicationId());
        communication.getDialogues().clear(); // Dọn RAM đồng bộ

        // 3. Đổ danh sách câu thoại mới đã chỉnh sửa từ form Admin đẩy xuống
        if (request.getDialogues() != null && !request.getDialogues().isEmpty()) {
            for (int i = 0; i < request.getDialogues().size(); i++) {
                Map<String, Object> node = request.getDialogues().get(i);

                DialogueCommunication dialogue = DialogueCommunication.builder()
                        .communication(communication)
                        .dialogueOrder(i + 1) // Thiết lập lại chỉ số thứ tự đồng bộ tăng dần
                        .speaker((String) node.get("speaker"))
                        .role((String) node.get("role"))
                        .textKr((String) node.get("text_kr"))
                        .textVi((String) node.get("text_vi"))
                        .build();

                communication.getDialogues().add(dialogue);
            }
            communicationRepository.save(communication);
            log.info("Đã cập nhật chuyên sâu toàn bộ nội dung chi tiết bài học giao tiếp ID: {}", id);
        }
    }

    @Override
    @Transactional
    public void deleteCommunication(Long id) {
        if (!communicationRepository.existsById(id)) {
            throw new CommunicationNotFoundException("Không tìm thấy ID bài giao tiếp hợp lệ để thực hiện tác vụ xóa.");
        }
        // Xóa thực thể cha -> Hibernate tự động dọn sạch sành sanh các câu thoại bảng con liên quan và cột JSONB
        communicationRepository.deleteById(id);
        log.warn("Đã xóa vĩnh viễn bài học giao tiếp ID: {} ra khỏi cơ sở dữ liệu.", id);
    }

    // ==========================================
    // 2. NHÓM NGƯỜI DÙNG (USER)
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<CommunicationLobbyResponse> getLobbyForUser(String username) {
        // Kiểm tra tài khoản học viên tồn tại để bảo đảm an toàn hệ thống (Đồng bộ phong cách GrammarService của ông giáo)
        userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Học viên không tồn tại trên hệ thống với username: " + username));

        return communicationRepository.findAllByOrderByCommunicationIdAsc().stream()
                .map(c -> CommunicationLobbyResponse.builder()
                        .communicationId(c.getCommunicationId())
                        .title(c.getTitle())
                        .languagePair(c.getLanguagePair())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommunicationUserDetailResponse getDetailForUser(Long id, String username) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Học viên không tồn tại trên hệ thống với username: " + username));

        Communication communication = communicationRepository.findById(id)
                .orElseThrow(() -> new CommunicationNotFoundException("Bài học giao tiếp không tồn tại với ID: " + id));

        // Áp dụng Stream map thực thể sang DTO. Nhờ cấu hình @OrderBy("dialogueOrder ASC") ở Entity nên mảng chatbox luôn load chuẩn mạch
        List<DialogueCommunicationResponse> dialogueDtos = communication.getDialogues().stream()
                .map(d -> DialogueCommunicationResponse.builder()
                        .speaker(d.getSpeaker())
                        .role(d.getRole())
                        .text_kr(d.getTextKr())
                        .text_vi(d.getTextVi())
                        .build())
                .collect(Collectors.toList());

        // Đóng gói lồng Object chuẩn cấu trúc JSONB gốc để Client dễ dàng điều khiển đóng/mở khối từ vựng và ngữ pháp
        return CommunicationUserDetailResponse.builder()
                .lesson(CommunicationUserDetailResponse.LessonInfo.builder()
                        .title(communication.getTitle())
                        .language_pair(communication.getLanguagePair())
                        .build())
                .dialogue(dialogueDtos)
                .knowledge(CommunicationUserDetailResponse.KnowledgeInfo.builder()
                        .vocabulary(communication.getVocabulary())
                        .grammar(communication.getGrammar())
                        .build())
                .build();
    }
}