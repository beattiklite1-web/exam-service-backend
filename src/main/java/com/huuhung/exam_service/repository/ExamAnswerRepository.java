/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {

    /**
     * Tìm danh sách đáp án của một đề thi cụ thể.
     * Thường dùng khi muốn trả về đáp án để chấm điểm.
     */
    List<ExamAnswer> findByExamIdOrderByQuestionOrderAsc(Long examId);

    /**
     * Xóa toàn bộ đáp án của một đề thi.
     * Thường dùng khi Admin muốn cập nhật lại bộ đáp án mới cho đề đó.
     */
    void deleteByExamId(Long examId);
}
