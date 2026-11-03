package com.finova.backend.service;

import com.finova.backend.dto.RegisterRequest;
import com.finova.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {

        // 아이디 중복 검사
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 비밀번호 BCrypt 해시
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // 회원정보 저장
        try {
            userMapper.insertUser(
                    request.getUsername(),
                    passwordHash,
                    request.getName()
            );
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
    }
}