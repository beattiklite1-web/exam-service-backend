/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.security;

import com.huuhung.exam_service.entity.User;
import com.huuhung.exam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm user trong DB bằng Repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng có tên: " + username));

        // 2. Chuyển đổi từ Entity User (của Hùng) sang UserDetails (của Spring Security)
        // Lưu ý: org.springframework.security.core.userdetails.User là lớp dựng sẵn của Spring
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Mật khẩu đã mã hóa trong DB
                .authorities(user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new) // Chuyển "ROLE_USER" thành GrantedAuthority
                        .collect(Collectors.toList()))
                .disabled(!user.isEnabled()) // Nếu enabled = false thì không cho login
                .build();
    }
}
