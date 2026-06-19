/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.dto.response.ListeningPartResponse;
import com.huuhung.exam_service.entity.ListeningPart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningPartRepository extends JpaRepository<ListeningPart, Long> {
    
    // Kiểm tra xem số tập này đã tồn tại trong hệ thống chưa
    boolean existsByPartNumber(Integer partNumber);
    
    // Tìm kiếm bộ đề nghe theo số thứ tự tập (Ví dụ: Tập 1, Tập 2...)
    Optional<ListeningPart> findByPartNumber(Integer partNumber);
    
@Query("SELECT new com.huuhung.exam_service.dto.response.ListeningPartResponse (" +
           "  p.id, p.partNumber, p.title, " +
           "  (SELECT COUNT(l.id) FROM Listening l WHERE l.part.id = p.id)" +
           ") FROM ListeningPart p ORDER BY p.partNumber ASC")
    List<ListeningPartResponse> getPartsWithQuestionsCountSingleQuery();
}