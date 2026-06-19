/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.dto.response.PartProgressProjectionReponse;
import com.huuhung.exam_service.entity.VocabularyPart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import static org.springframework.data.redis.serializer.RedisSerializationContext.java;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author qnam0
 */
@Repository
public interface VocabularyPartRepository extends JpaRepository<VocabularyPart, Long> {
    Optional<VocabularyPart> findByPartNumber(Integer partNumber);
    
    // 🌟 KHÓA CHẶT N+1: Đếm tổng từ vựng và số từ đã thuộc (isMastered = true) của User trong 1 câu SQL duy nhất!
    @Query("SELECT new com.huuhung.exam_service.dto.response.PartProgressProjectionReponse(" +
           "  p.id, p.partNumber, p.title, " +
           "  (SELECT COUNT(v.id) FROM Vocabulary v WHERE v.part.id = p.id), " +
           "  (SELECT COUNT(pr.id) FROM UserVocabularyProgress pr WHERE pr.user.id = :userId AND pr.vocabulary.part.id = p.id AND pr.isMastered = true)" +
           ") FROM VocabularyPart p")
    List<PartProgressProjectionReponse> getPartsProgressWithSingleQuery(@Param("userId") Long userId);
}
