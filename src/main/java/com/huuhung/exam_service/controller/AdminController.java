/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.controller;

import com.huuhung.exam_service.dto.request.*;
import com.huuhung.exam_service.dto.response.*;
import com.huuhung.exam_service.service.AdminService;
import com.huuhung.exam_service.service.CommunicationService;
import com.huuhung.exam_service.service.EpsService;
import com.huuhung.exam_service.service.GrammarService;
import com.huuhung.exam_service.service.ListeningService;
import com.huuhung.exam_service.service.NewsService;
import com.huuhung.exam_service.service.VocabularyService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final VocabularyService vocabService;
    private final NewsService newsService;
    private final ListeningService listeningService;
    private final EpsService epsService;
    private final GrammarService grammarService;
    private final CommunicationService communicationService;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // USER APIs
    @GetMapping("/users/search")
    public ResponseEntity<List<UserAdminResponse>> search(@RequestParam(required = false) String kw) {
        return ResponseEntity.ok(adminService.searchUsers(kw));
    }

    @PostMapping("/users/{username}/extend")
    public ResponseEntity<String> extend(@PathVariable String username, @RequestParam int hours) {
        adminService.extendUserTime(username, hours);
        return ResponseEntity.ok("Gia hạn thành công cho " + username);
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<String> toggle(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ResponseEntity.ok("Đã cập nhật trạng thái User");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Đã xóa người dùng thành công");
    }
    
    // EXAM APIs
    @PostMapping("/exams/import")
    public ResponseEntity<String> importExam(@RequestBody ExamRequest req) {
        adminService.importExam(req);
        return ResponseEntity.ok("Nạp đề thành công");
    }

    @GetMapping("/exams")
    public ResponseEntity<List<ExamResponse>> exams() {
        return ResponseEntity.ok(adminService.getAllExams());
    }
    
    // 2. Lấy chi tiết đề thi KÈM CÂU HỎI (Mới thêm - Dùng cho trang [id])
    @GetMapping("/exams/{id}")
    public ResponseEntity<ExamResponse> getExamDetail(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getExamById(id));
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<String> updateExam(@PathVariable Long id, @RequestBody ExamUpdateRequest req) {
        adminService.updateExam(id, req);
        return ResponseEntity.ok("Đã sửa đề");
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable Long id) {
        adminService.deleteExam(id);
        return ResponseEntity.ok("Đã xóa đề");
    }

    // QUESTION APIs
    @PutMapping("/questions/{id}")
    public ResponseEntity<String> updateQuestion(@PathVariable Long id, @RequestBody QuestionUpdateRequest req) {
        adminService.updateQuestion(id, req);
        return ResponseEntity.ok("Đã sửa câu hỏi");
    }
    
    
     // ==========================================
    // 1. TỪ VỰNG
    // ==========================================

    @PostMapping("/vocab/import")
    public ResponseEntity<String> importVocab(@RequestBody VocabularyPartRequest request) {
        vocabService.importFullVocabPart(request);
        return ResponseEntity.ok("Import dữ liệu thành công cho " + request.getTitle());
    }

    @GetMapping("/vocab/parts")
    public ResponseEntity<List<PartProgressResponse>> getPartsForAdmin(Principal principal) {
        // Chuyển đổi principal sang ID thông qua service để bảo mật tuyệt đối
        return ResponseEntity.ok(vocabService.getAllPartsWithProgressByUsername(principal.getName()));
    }

    // Lấy danh sách từ của 1 Part để Admin thấy nút Sửa/Xóa cạnh mỗi từ
    @GetMapping("/vocab/parts/{partId}/words")
    public ResponseEntity<List<VocabularyResponse>> getWordsForAdmin(@PathVariable Long partId) {
        return ResponseEntity.ok(vocabService.getWords(partId, null));
    }
    
    // 2. Sửa từ lẻ
    @PutMapping("/vocab/word/{id}")
    public ResponseEntity<VocabularyResponse> updateWord(
            @PathVariable Long id,
            @RequestBody VocabularyRequest request) {
        return ResponseEntity.ok(vocabService.updateWord(id, request));
    }

    // 3. Xóa từ lẻ
    @DeleteMapping("/vocab/word/{id}")
    public ResponseEntity<String> deleteWord(@PathVariable Long id) {
        vocabService.deleteWord(id);
        return ResponseEntity.ok("Đã xóa từ vựng thành công!");
    }

    // 4. Xóa nguyên Part (Xóa cả tập)
    @DeleteMapping("/vocab/part/{partId}")
    public ResponseEntity<String> deletePart(@PathVariable Long partId) {
        vocabService.deletePart(partId);
        return ResponseEntity.ok("Đã xóa toàn bộ Part và dữ liệu liên quan!");
    }
    
    // --- QUẢN LÝ TIN TỨC ---

    // Tìm kiếm và phân trang tin tức cho trang quản lý của Admin
    @GetMapping("/news/search")
    public ResponseEntity<Page<NewsResponse>> searchNews(
            @RequestParam(defaultValue = "") String title,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(newsService.searchNewsAdmin(title, pageable));
    }

    // Tạo bài viết mới
    @PostMapping("/news")
    public ResponseEntity<String> createNews(@RequestBody NewsRequest request) {
        newsService.createNews(request);
        return ResponseEntity.ok("Đã đăng bài viết mới thành công!");
    }
    
    @GetMapping("news/{id}")
    public ResponseEntity<NewsDetailResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsDetail(id));
    }


    // Cập nhật nội dung bài viết
    @PutMapping("/news/{id}")
    public ResponseEntity<String> updateNews(@PathVariable Long id, @RequestBody NewsRequest request) {
        newsService.updateNews(id, request);
        return ResponseEntity.ok("Cập nhật bài viết thành công!");
    }

    // Xóa bài viết
    @DeleteMapping("/news/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok("Đã xóa bài viết vĩnh viễn!");
    }

    // Bật/Tắt tin Hot (Chốt chặn tối đa 3 bài)
    @PatchMapping("/news/{id}/hot")
    public ResponseEntity<String> toggleHot(@PathVariable Long id, @RequestParam boolean status) {
        newsService.toggleHotStatus(id, status);
        return ResponseEntity.ok("Cập nhật trạng thái Hot thành công!");
    }

    // Ẩn/Hiện bài viết nhanh
    @PatchMapping("/news/{id}/publish")
    public ResponseEntity<String> togglePublish(@PathVariable Long id, @RequestParam boolean status) {
        newsService.updatePublishStatus(id, status);
        return ResponseEntity.ok("Đã cập nhật trạng thái hiển thị!");
    }
   

// ==========================================
    // 2. QUẢN LÝ LUYỆN NGHE (LISTENING)
    // ==========================================

    /**
     * Nạp toàn bộ dữ liệu bộ đề nghe từ JSON
     */
    @PostMapping("/listening/import")
    public ResponseEntity<String> importListeningPart(@RequestBody ListeningPartRequest request, Principal principal) {
        log.info("Admin [{}] đang import bộ đề nghe số: {}", principal.getName(), request.getPartNumber());
        listeningService.importFullListeningPart(request);
        return ResponseEntity.ok("Import bộ đề nghe thành công cho tập: " + request.getPartNumber());
    }

  
        @GetMapping("/listening/parts")
    public ResponseEntity<List<ListeningPartResponse>> getLobby() {
        // Không cần Principal vì danh sách Part là chung cho mọi User
        return ResponseEntity.ok(listeningService.getAllParts());
    }
    /**
     * Lấy danh sách câu hỏi của 1 Part để Admin quản lý
     */
    @GetMapping("/listening/parts/{partId}/questions")
    public ResponseEntity<List<ListeningResponse>> getListeningQuestionsForAdmin(@PathVariable Long partId) {
        // Đồng bộ cách đặt URL giống bên Vocab cho dễ nhớ
        return ResponseEntity.ok(listeningService.getListenings(partId));
    }

    /**
     * Cập nhật thông tin chung của Part (Title/Number)
     */
    @PutMapping("/listening/parts/{id}")
    public ResponseEntity<Void> updateListeningPart(
            @PathVariable Long id, 
            @RequestBody ListeningPartUpdateRequest request, 
            Principal principal) {
        log.info("Admin [{}] cập nhật Part nghe ID: {}", principal.getName(), id);
        listeningService.updateListeningPart(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Cập nhật nội dung một câu hỏi lẻ
     */
    @PutMapping("/listening/questions/{id}")
    public ResponseEntity<Void> updateListeningQuestion(
            @PathVariable Long id, 
            @RequestBody ListeningRequest request, 
            Principal principal) {
        log.info("Admin [{}] cập nhật câu hỏi nghe ID: {}", principal.getName(), id);
        listeningService.updateListening(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Xóa một câu hỏi lẻ
     */
    @DeleteMapping("/listening/questions/{id}")
    public ResponseEntity<Void> deleteListeningQuestion(@PathVariable Long id, Principal principal) {
        log.info("Admin [{}] xóa câu hỏi nghe ID: {}", principal.getName(), id);
        listeningService.deleteListening(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Xóa nguyên một Part nghe
     */
    @DeleteMapping("/listening/parts/{id}")
    public ResponseEntity<String> deleteListeningPart(@PathVariable Long id, Principal principal) {
        log.info("Admin [{}] xóa trọn bộ Part nghe ID: {}", principal.getName(), id);
        listeningService.deletePart(id);
        return ResponseEntity.ok("Đã xóa toàn bộ Part nghe và dữ liệu liên quan!");
    }
    
    // ==========================================
    // 3. QUẢN LÝ GIÁO TRÌNH EPS (60 BÀI)
    // ==========================================
    
    
    @PostMapping("/eps/import")
    public ResponseEntity<String> importEpsBook(@RequestBody EpsImportRequest request, Principal principal) {
        log.info("Admin [{}] đang nạp giáo trình Tập: {}", principal.getName(), request.getBookId());
        epsService.importFullEpsBook(request, principal.getName());
        return ResponseEntity.ok("Import thành công giáo trình Tập: " + request.getBookId());
    }

    // 2. Xóa sạch giáo trình theo Tập
    @DeleteMapping("/eps/book/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer bookId, Principal principal) {
        log.warn("Admin [{}] đang xóa sạch dữ liệu Tập: {}", principal.getName(), bookId);
        epsService.deleteBook(bookId, principal.getName());
        return ResponseEntity.ok("Đã xóa sạch dữ liệu Tập " + bookId);
    }

    // 3. Hàm UPDATE trọng tâm: Sửa nội dung trang (Ảnh/Audio/Quiz)
    @PutMapping("/eps/page/{compositeId}")
    public ResponseEntity<String> updatePage(@PathVariable String compositeId, 
                                           @RequestBody EpsPageUpdateRequest request, 
                                           Principal principal) {
        log.info("Admin [{}] đang cập nhật nội dung trang: {}", principal.getName(), compositeId);
        epsService.updatePageContent(compositeId, request, principal.getName());
        return ResponseEntity.ok("Cập nhật trang " + compositeId + " thành công!");
    }
    

    // Lấy menu quản trị cho Admin (Xem danh sách chương/bài của Tập 1 hoặc 2)
    @GetMapping("/eps/menu/{bookId}")
    public ResponseEntity<List<TopicEpsResponse>> getEpsMenuForAdmin(@PathVariable Integer bookId) {
        return ResponseEntity.ok(epsService.getBookMenu(bookId));
    }

    // Xem danh sách các trang trong một bài học (Để Admin chọn trang cần sửa)
    @GetMapping("/eps/lesson/{lessonId}/pages")
    public ResponseEntity<List<PageEpsResponse>> getEpsPagesForAdmin(@PathVariable Long lessonId) {
        return ResponseEntity.ok(epsService.getLessonPages(lessonId));
    }

    // Lấy chi tiết một trang cụ thể (Dùng để đổ dữ liệu vào Form Sửa)
    @GetMapping("/eps/book/{bookId}/page/{pageNumber}")
    public ResponseEntity<PageEpsResponse> getEpsPageDetail(
            @PathVariable Integer bookId, 
            @PathVariable Integer pageNumber) {
        return ResponseEntity.ok(epsService.getSinglePage(bookId, pageNumber));
    }
    
    
    // ==========================================
    // 4. QUẢN LÝ NGỮ PHÁP (GRAMMAR) - MỚI TÍCH HỢP
    // ==========================================

    /**
     * Nạp toàn bộ dữ liệu bài học ngữ pháp + câu hỏi từ tệp JSON
     */
    @PostMapping("/grammar/import")
    public ResponseEntity<String> importGrammar(@RequestBody GrammarRequest request, Principal principal) {
        log.info("Admin [{}] đang tiến hành import bài học ngữ pháp mới: {}", principal.getName(), request.getTitle());
        grammarService.importFullGrammar(request);
        return ResponseEntity.ok("Import dữ liệu ngữ pháp thành công cho bài: " + request.getTitle());
    }

    /**
     * Xem danh sách bài học ngữ pháp tại trang Lobby quản trị của Admin
     */
    @GetMapping("/grammar/lobby")
    public ResponseEntity<List<GrammarLobbyResponse>> getGrammarLobbyForAdmin(Principal principal) {
        log.info("Admin [{}] đang truy cập danh sách Lobby ngữ pháp quản trị", principal.getName());
        return ResponseEntity.ok(grammarService.getGrammarLobby(principal.getName()));
    }

    /**
     * Lấy chi tiết nội dung lý thuyết + 20 câu hỏi của 1 bài để Admin kiểm tra / đổ vào Form sửa
     */
    @GetMapping("/grammar/{grammarId}")
    public ResponseEntity<GrammarAdminDetailResponse> getGrammarDetailForAdmin(@PathVariable Long grammarId, Principal principal) {
        log.info("Admin [{}] đang xem chi tiết bài học ngữ pháp ID: {}", principal.getName(), grammarId);
        return ResponseEntity.ok(grammarService.getGrammarDetailForAdmin(grammarId));
    }

    /**
     * Chỉnh sửa nhanh thông tin cơ bản (Title / Description) của bài học ngữ pháp
     */
    @PutMapping("/grammar/{grammarId}")
    public ResponseEntity<String> updateGrammarInfo(
            @PathVariable Long grammarId, 
            @RequestBody GrammarInfoUpdateRequest request, 
            Principal principal) {
        log.info("Admin [{}] đang cập nhật thông tin bài học ngữ pháp ID: {}", principal.getName(), grammarId);
        grammarService.updateGrammarInfo(grammarId, request);
        return ResponseEntity.ok("Đã cập nhật thông tin bài học ngữ pháp thành công!");
    }

    /**
     * Xóa vĩnh viễn 1 bài học ngữ pháp (Cascade tự động xóa sạch lý thuyết và câu hỏi)
     */
    @DeleteMapping("/grammar/{grammarId}")
    public ResponseEntity<String> deleteGrammar(@PathVariable Long grammarId, Principal principal) {
        log.warn("Admin [{}] đang thực hiện lệnh xóa vĩnh viễn bài học ngữ pháp ID: {}", principal.getName(), grammarId);
        grammarService.deleteGrammar(grammarId);
        return ResponseEntity.ok("Đã xóa bài học ngữ pháp và toàn bộ dữ liệu liên quan thành công!");
    }
    
    
    // ==========================================
    // 5. NHÓM QUẢN LÝ GIAO TIẾP (COMMUNICATION)
    // ==========================================

    /**
     * Nạp toàn bộ cấu trúc bài học giao tiếp và mảng câu thoại phân vai từ đối tượng JSON Request
     */
    @PostMapping("/communication/import")
    public ResponseEntity<String> importFullCommunication(@RequestBody CommunicationRequest request, Principal principal) {
        log.info("Admin [{}] đang tiến hành nạp dữ liệu bài học giao tiếp mới: {}", principal.getName(), request.getLesson().getTitle());
        communicationService.importFullCommunication(request);
        return ResponseEntity.ok("Đã nạp toàn bộ dữ liệu bài học giao tiếp thành công!");
    }

    /**
     * Lấy danh sách quản trị bài học giao tiếp tại sảnh Lobby của Admin
     */
    @GetMapping("/communication/lobby")
    public ResponseEntity<List<CommunicationLobbyResponse>> getCommunicationLobbyForAdmin(Principal principal) {
        log.info("Admin [{}] đang truy cập danh sách sảnh bài học giao tiếp quản trị", principal.getName());
        return ResponseEntity.ok(communicationService.getLobbyForAdmin());
    }

    /**
     * Lấy dữ liệu chi tiết bài học phục vụ cấu hình nạp dữ liệu Form sửa đổi của Admin
     */
    @GetMapping("/communication/{id}")
    public ResponseEntity<CommunicationAdminDetailResponse> getCommunicationDetailForAdmin(@PathVariable Long id, Principal principal) {
        log.info("Admin [{}] đang xem dữ liệu chi tiết bài học giao tiếp ID: {}", principal.getName(), id);
        return ResponseEntity.ok(communicationService.getDetailForAdmin(id));
    }

    /**
     * Chỉnh sửa nhanh tiêu đề bài học bên ngoài sảnh bài học (Lobby)
     */
    @PutMapping("/communication/{id}/lobby")
    public ResponseEntity<String> updateCommunicationLobby(
            @PathVariable Long id, 
            @RequestBody CommunicationLobbyUpdateRequest request, 
            Principal principal) {
        log.info("Admin [{}] đang cập nhật thông tin Lobby bài học giao tiếp ID: {}", principal.getName(), id);
        communicationService.updateLobby(id, request);
        return ResponseEntity.ok("Cập nhật thông tin Lobby bài học giao tiếp thành công!");
    }

    /**
     * Chỉnh sửa chuyên sâu toàn bộ nội dung (Hội thoại, Từ vựng, Ngữ pháp) của bài học giao tiếp
     */
    @PutMapping("/communication/{id}/detail")
    public ResponseEntity<String> updateCommunicationDetail(
            @PathVariable Long id, 
            @RequestBody CommunicationDetailUpdateRequest request, 
            Principal principal) {
        log.info("Admin [{}] đang cập nhật chi tiết nội dung bài học giao tiếp ID: {}", principal.getName(), id);
        communicationService.updateDetail(id, request);
        return ResponseEntity.ok("Cập nhật chi tiết nội dung bài học giao tiếp thành công!");
    }

    /**
     * Xóa vĩnh viễn bài học giao tiếp (Cascade dọn sạch bảng con dữ liệu hội thoại và mảng JSONB liên quan)
     */
    @DeleteMapping("/communication/{id}")
    public ResponseEntity<String> deleteCommunication(@PathVariable Long id, Principal principal) {
        log.warn("Admin [{}] đang kích hoạt lệnh xóa vĩnh viễn bài học giao tiếp có ID: {}", principal.getName(), id);
        communicationService.deleteCommunication(id);
        return ResponseEntity.ok("Xóa bài học giao tiếp và toàn bộ dữ liệu liên quan thành công!");
    }

}
