/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.TopicEps;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author qnam0
 */
@Repository
public interface TopicEpsRepository extends JpaRepository<TopicEps, Integer> {
    List<TopicEps> findByBookIdOrderByIdAsc(Integer bookId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TopicEps t WHERE t.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Integer bookId);
    
    @Query("SELECT DISTINCT t FROM TopicEps t LEFT JOIN FETCH t.lessons WHERE t.bookId = :bookId ORDER BY t.id ASC")
    List<TopicEps> findByBookIdWithLessonsFetchAsync(@Param("bookId") Integer bookId); // 🌟 Ép kiểu Integer
}
