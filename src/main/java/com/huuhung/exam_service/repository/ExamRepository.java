package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e.id FROM Exam e")
    List<Long> findAllExamIds();

    @Query("SELECT e.id FROM Exam e WHERE e.id NOT IN :excludedIds")
    List<Long> findExamIdsExcluding(@Param("excludedIds") List<Long> excludedIds);

    // 🌟 TRUY VẤN SẠCH: Chỉ JOIN FETCH bảng Questions trước để tránh sinh dữ liệu rác trên RAM
    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.questions q WHERE e.id = :examId")
    Optional<Exam> findExamWithQuestionsById(@Param("examId") Long examId);
}