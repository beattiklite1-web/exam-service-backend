/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.LessonEps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author qnam0
 */
@Repository
public interface LessonEpsRepository extends JpaRepository<LessonEps, Long> {
    // Dùng để xóa nhanh khi Admin muốn reset tập sách
    @Modifying
    @Query("DELETE FROM LessonEps l WHERE l.topic.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Integer bookId);
}