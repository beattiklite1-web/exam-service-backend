/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.dto.request.GrammarInfoUpdateRequest;
import com.huuhung.exam_service.dto.request.GrammarRequest;
import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.entity.*;
import com.huuhung.exam_service.exception.*;
import com.huuhung.exam_service.repository.*;
import com.huuhung.exam_service.service.GrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrammarServiceImpl implements GrammarService {

    private final GrammarRepository grammarRepository;
    private final GrammarSectionRepository grammarSectionRepository;
    private final GrammarQuizRepository grammarQuizRepository;
    private final UserRepository userRepository;

    // ==========================================
    // 1. NHÓM QUẢN TRỊ (ADMIN)
    // ==========================================

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void importFullGrammar(GrammarRequest request) {
        try {
            // Tạo mới thực thể cha Grammar lớn
            Grammar grammar = Grammar.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .build();
            grammar = grammarRepository.save(grammar);

            // Đọc mảng "grammarSection" trực tiếp theo cấu trúc chuẩn của file JSON đầu vào
            if (request.getGrammarSectionsJson() != null) {
                List<GrammarSection> sections = new ArrayList<>();
                for (Map<String, Object> node : request.getGrammarSectionsJson()) {
                    sections.add(GrammarSection.builder()
                            .grammar(grammar)
                            .sectionOrder((Integer) node.get("sectionOrder"))
                            .title((String) node.get("title"))
                            .structure((String) node.get("structure"))
                            .usage((String) node.get("usage"))
                            .examples((List<Map<String, Object>>) node.get("examples"))
                            .build());
                }
                grammarSectionRepository.saveAll(sections);
            }

            // Đọc mảng "questions" trực tiếp theo cấu trúc chuẩn của file JSON đầu vào
            if (request.getGrammarQuizzesJson() != null) {
                List<GrammarQuiz> quizzes = new ArrayList<>();
                for (Map<String, Object> node : request.getGrammarQuizzesJson()) {
                    quizzes.add(GrammarQuiz.builder()
                            .grammar(grammar)
                            .questionOrder((Integer) node.get("questionOrder"))
                            .type((String) node.get("type"))
                            .title((String) node.get("title"))
                            .options((List<Map<String, Object>>) node.get("options"))
                            .correctAnswer((String) node.get("correctAnswer"))
                            .acceptedAnswers((List<Object>) node.get("acceptedAnswers"))
                            .exactMatchNoSpace((String) node.get("exactMatchNoSpace"))
                            .hint((String) node.get("hint"))
                            .explanation((String) node.get("explanation"))
                            .build());
                }
                grammarQuizRepository.saveAll(quizzes);
            }
        } catch (Exception e) {
            // Sử dụng chung cơ chế báo lỗi nạp file giống Vocabulary của ông giáo
            throw new VocabFileProcessException("Lỗi định dạng dữ liệu JSON: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public GrammarAdminDetailResponse getGrammarDetailForAdmin(Long grammarId) {
        Grammar grammar = grammarRepository.findById(grammarId)
                .orElseThrow(() -> new GrammarNotFoundException("Bài học ngữ pháp không tồn tại."));

        List<Map<String, Object>> sections = grammar.getGrammarSections().stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sectionOrder", s.getSectionOrder());
            map.put("title", s.getTitle());
            map.put("structure", s.getStructure());
            map.put("usage", s.getUsage());
            map.put("examples", s.getExamples());
            return map;
        }).collect(Collectors.toList());

        // ADMIN PRIVILEGE: Đã sửa lỗi logic map biến quiz thành q chuẩn xác từ A-Z
        List<QuizAdminResponse> adminQuestions = grammar.getGrammarQuizzes().stream().map(q -> QuizAdminResponse.builder()
                .questionOrder(q.getQuestionOrder())
                .type(q.getType())
                .title(q.getTitle())
                .options(q.getOptions())
                .hint(q.getHint())
                .correctAnswer(q.getCorrectAnswer())       // Đã fix thành q
                .acceptedAnswers(q.getAcceptedAnswers())   // Đã fix thành q
                .exactMatchNoSpace(q.getExactMatchNoSpace()) // Đã fix thành q
                .explanation(q.getExplanation())           // Đã fix thành q
                .build()).collect(Collectors.toList());

        return GrammarAdminDetailResponse.builder()
                .grammarId(grammar.getGrammarId())
                .title(grammar.getTitle())
                .description(grammar.getDescription())
                .grammarSections(sections)
                .questions(adminQuestions)
                .build();
    }

    @Override
    @Transactional
    public void updateGrammarInfo(Long grammarId, GrammarInfoUpdateRequest request) {
        Grammar grammar = grammarRepository.findById(grammarId)
                .orElseThrow(() -> new GrammarNotFoundException("Không tìm thấy bài học ngữ pháp ID: " + grammarId));
        
        grammar.setTitle(request.getTitle());
        grammar.setDescription(request.getDescription());
        grammarRepository.save(grammar);
    }

    @Override
    @Transactional
    public void deleteGrammar(Long grammarId) {
        if (!grammarRepository.existsById(grammarId)) {
            throw new GrammarNotFoundException("ID bài học ngữ pháp không tồn tại để xóa.");
        }
        grammarRepository.deleteById(grammarId);
    }

    // ==========================================
    // 2. NHÓM NGƯỜI DÙNG (USER)
    // ==========================================

    @Override
    public List<GrammarLobbyResponse> getGrammarLobby(String username) {
        // Kiểm tra xem User có tồn tại không để đảm bảo an toàn bảo mật hệ thống
        userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        // Lấy toàn bộ danh sách bài học ngữ pháp xếp thứ tự tăng dần từ Bài 1 -> Bài 60
        List<Grammar> grammars = grammarRepository.findAllByOrderByGrammarIdAsc();

        // Chuyển đổi sang List Response mỏng nhẹ (Bỏ hoàn toàn check biến tiến độ isPassed)
        return grammars.stream().map(g -> GrammarLobbyResponse.builder()
                .grammarId(g.getGrammarId())
                .title(g.getTitle())
                .description(g.getDescription())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GrammarDetailResponse getGrammarDetail(Long grammarId) {
        // Sử dụng FETCH JOIN kéo trọn gói dữ liệu 3 bảng lồng nhau bằng 1 câu SQL tối ưu
        Grammar grammar = grammarRepository.findById(grammarId)
                .orElseThrow(() -> new GrammarNotFoundException("Bài học ngữ pháp không tồn tại."));

        // Gom cấu trúc mảng lý thuyết
        List<Map<String, Object>> sections = grammar.getGrammarSections().stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sectionOrder", s.getSectionOrder());
            map.put("title", s.getTitle());
            map.put("structure", s.getStructure());
            map.put("usage", s.getUsage());
            map.put("examples", s.getExamples());
            return map;
        }).collect(Collectors.toList());

        // CHỐNG CHEAT: Ẩn toàn bộ đáp án đúng và lời giải chi tiết khi học viên tải bài học
        List<QuizSecureResponse> secureQuestions = grammar.getGrammarQuizzes().stream().map(q -> QuizSecureResponse.builder()
                .questionOrder(q.getQuestionOrder())
                .type(q.getType())
                .title(q.getTitle())
                .options(q.getOptions())
                .hint(q.getHint())
                .build()).collect(Collectors.toList());

        return GrammarDetailResponse.builder()
                .grammarId(grammar.getGrammarId())
                .title(grammar.getTitle())
                .description(grammar.getDescription())
                .grammarSections(sections)
                .questions(secureQuestions)
                .build();
    }

    @Override
    @Transactional
    public QuizResultResponse submitGrammarQuiz(Long grammarId, String username, Map<Integer, String> studentAnswers) {
        Grammar grammar = grammarRepository.findById(grammarId)
                .orElseThrow(() -> new GrammarNotFoundException("Bài học ngữ pháp không tồn tại để chấm điểm."));

        userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        List<GrammarQuiz> actualQuizzes = grammar.getGrammarQuizzes();
        int totalQuestions = actualQuizzes.size();
        int correctCount = 0;

        List<QuizCheckDetailResponse> checkDetails = new ArrayList<>();

        for (GrammarQuiz quiz : actualQuizzes) {
            String studentAns = studentAnswers.get(quiz.getQuestionOrder());
            boolean isCorrect = false;

            if (studentAns != null) {
                studentAns = studentAns.trim();

                // 1. Trắc nghiệm khách quan chọn đáp án
                if ("multiple_choice".equals(quiz.getType()) || "mcq".equals(quiz.getType())) {
                    isCorrect = studentAns.equalsIgnoreCase(quiz.getCorrectAnswer());
                } 
                // 2. Tự luận dịch Hàn - Việt (so khớp trong mảng acceptedAnswers)
                else if ("translate_ko_vi".equals(quiz.getType())) {
                    List<Object> accepted = quiz.getAcceptedAnswers();
                    String finalAns = studentAns.toLowerCase();
                    isCorrect = accepted != null && accepted.stream()
                            .anyMatch(ans -> ans.toString().trim().toLowerCase().equals(finalAns));
                } 
                // 3. Tự luận dịch Việt - Hàn (xóa trắng khoảng trống so khớp exactMatchNoSpace)
                else if ("translate_vi_ko".equals(quiz.getType())) {
                    String cleanStudent = studentAns.replace(" ", "");
                    isCorrect = cleanStudent.equals(quiz.getExactMatchNoSpace());
                }
            }

            if (isCorrect) {
                correctCount++;
            }

            String displayCorrectAnswer = quiz.getCorrectAnswer();
            if ("translate_ko_vi".equals(quiz.getType())) {
                List<Object> accepted = quiz.getAcceptedAnswers();
                if (accepted != null && !accepted.isEmpty()) {
                    // Trích xuất phần tử mẫu đầu tiên trong mảng làm đáp án chuẩn trả về hiển thị
                    displayCorrectAnswer = accepted.get(0).toString(); 
                }
            }
            // Trả đáp án đúng chuẩn kèm lời giải chi tiết sau khi nộp bài thành công
            checkDetails.add(QuizCheckDetailResponse.builder()
                    .questionOrder(quiz.getQuestionOrder())
                    .isCorrect(isCorrect)
                    .correctAnswer(displayCorrectAnswer)
                    .explanation(quiz.getExplanation())
                    .build());
        }

        // ĐÃ LOẠI BỎ TOÀN BỘ TIẾN TRÌNH: Không lưu dữ liệu vào user.getCompletedExamIds() nữa

        return QuizResultResponse.builder()
                .score(correctCount + "/" + totalQuestions)
                .details(checkDetails)
                .build();
    }
}