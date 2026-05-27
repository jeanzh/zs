package com.zs.repository;

import com.zs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByWechatOpenId(String wechatOpenId);
    boolean existsByPhone(String phone);
}
