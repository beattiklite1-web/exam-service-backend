/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.PageEps;
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
public interface PageEpsRepository extends JpaRepository<PageEps, String> {
    List<PageEps> findByBookIdOrderByPageNumberAsc(Integer bookId);
    
    // Tìm các trang nằm trong dải trang của một bài học
    List<PageEps> findByBookIdAndPageNumberBetween(Integer bookId, Integer start, Integer end);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PageEps p WHERE p.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Integer bookId);
}