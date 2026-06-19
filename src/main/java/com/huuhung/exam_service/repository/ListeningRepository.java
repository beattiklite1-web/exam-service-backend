/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.Listening;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningRepository extends JpaRepository<Listening, Long> {
    // Lấy tất cả câu hỏi nghe thuộc về một Part
    List<Listening> findByPartId(Long partId);
    
    // Đếm số lượng câu hỏi trong một Part (để tính toán nếu cần)
    long countByPartId(Long partId);
    
    // Tìm kiếm theo bài học nếu ông muốn lọc theo lessonTag (giống getWords)
    List<Listening> findByPartIdAndLessonTag(Long partId, Integer lessonTag);
    
    @Modifying
    @Query("DELETE FROM Listening l WHERE l.part.id = :partId")
    void deleteByPartId(Long partId);
}
