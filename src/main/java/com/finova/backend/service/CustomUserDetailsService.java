package com.finova.backend.service;

import com.finova.backend.dto.UserAccount;
import com.finova.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserAccount account = userMapper.findByUsername(username);

        if (account == null || account.getPasswordHash() == null) {
            throw new UsernameNotFoundException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return User.builder()
                .username(account.getUsername())
                .password(account.getPasswordHash())
                .roles("USER")
                .build();
    }
}