package com.zs.service;

import com.zs.dto.AuthResponse;
import com.zs.dto.LoginRequest;
import com.zs.dto.RegisterRequest;
import com.zs.entity.User;
import com.zs.repository.UserRepository;
import com.zs.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (req.getPhone() != null && userRepository.existsByPhone(req.getPhone())) {
            throw new RuntimeException("该手机号已注册");
        }

        User user = User.builder()
                .nickname(req.getNickname())
                .phone(req.getPhone())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token, user.getId(), user.getNickname());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("手机号未注册"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token, user.getId(), user.getNickname());
    }
}
