/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;



import com.huuhung.exam_service.entity.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrammarRepository extends JpaRepository<Grammar, Long> {
    
    // Lấy danh sách Lobby sắp xếp theo ID tăng dần (Bài 1 -> Bài 60)
    List<Grammar> findAllByOrderByGrammarIdAsc();

    // Dùng FETCH JOIN bốc trọn gói dữ liệu 3 bảng lồng nhau chỉ bằng 1 câu lệnh SQL
    @Query("SELECT DISTINCT g FROM Grammar g " +
           "LEFT JOIN FETCH g.grammarSections s " +
           "LEFT JOIN FETCH g.grammarQuizzes q " +
           "WHERE g.grammarId = :grammarId " +
           "ORDER BY s.sectionOrder ASC, q.questionOrder ASC")
    Optional<Grammar> findDetailByGrammarId(@Param("grammarId") Long grammarId);
}