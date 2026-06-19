/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.UserVocabularyProgress;
import java.util.List;
import java.util.Optional;
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
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, Long> {
    
    Optional<UserVocabularyProgress> findByUserIdAndVocabularyId(Long userId, Long vocabId);

    // Đếm số từ đã thuộc của 1 user trong 1 Part cụ thể
    @Query("SELECT COUNT(p) FROM UserVocabularyProgress p " +
           "WHERE p.user.id = :userId " +
           "AND p.vocabulary.part.id = :partId " +
           "AND p.isMastered = true")
    long countMasteredByPart(@Param("userId") Long userId, @Param("partId") Long partId);

    // Lấy danh sách ID các từ đã thuộc để highlight ✅ ở Frontend
    @Query("SELECT p.vocabulary.id FROM UserVocabularyProgress p " +
           "WHERE p.user.id = :userId AND p.isMastered = true")
    List<Long> getMasteredVocabIds(@Param("userId") Long userId);
    
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserVocabularyProgress p " + 
           "WHERE p.user.id = :userId " + 
           "AND p.vocabulary.part.id = :partId")
    void deleteByUserIdAndPartId(@Param("userId") Long userId, @Param("partId") Long partId);
}
