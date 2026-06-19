    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.repository;



import com.huuhung.exam_service.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Tìm user theo username (Dùng cho Login và lấy Profile)
    Optional<User> findByUsername(String username);
    
    // Kiểm tra tồn tại để chặn đăng ký trùng tên
    boolean existsByUsername(String username);
    
    // Kiểm tra tồn tại để chặn đăng ký trùng email
    boolean existsByEmail(String email);
    
    // Tìm kiếm User theo Username hoặc Email (không phân biệt hoa thường)
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
}
