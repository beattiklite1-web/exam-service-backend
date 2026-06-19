/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;

import com.huuhung.exam_service.entity.DialogueCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogueCommunicationRepository extends JpaRepository<DialogueCommunication, Long> {
    // Tự động giải quyết cascade xóa hoặc truy vấn phụ nếu cần
    @Modifying
    @Query("DELETE FROM DialogueCommunication d WHERE d.communication.id = :communicationId")
    void deleteByCommunicationId(@Param("communicationId") Long communicationId);
}