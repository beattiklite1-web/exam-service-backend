/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.Vocabulary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author qnam0
 */
@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findByPartId(Long partId);
    List<Vocabulary> findByPartIdAndLessonTag(Long partId, Integer lessonTag);
    long countByPartId(Long partId);
    
    @Query(value = "SELECT meaning FROM vocabulary WHERE vocabulary_part_id = :partId AND id != :excludeId ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
    List<String> findRandomMeaningsByPartId(@Param("partId") Long partId, @Param("excludeId") Long excludeId);

    @Query(value = "SELECT word FROM vocabulary WHERE vocabulary_part_id = :partId AND id != :excludeId ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
    List<String> findRandomWordsByPartId(@Param("partId") Long partId, @Param("excludeId") Long excludeId);
}
