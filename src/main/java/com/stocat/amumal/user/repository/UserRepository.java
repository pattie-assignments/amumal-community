package com.stocat.amumal.user.repository;

import com.stocat.amumal.user.domain.User;

import java.util.Optional;

// 저장소 접근 메서드 정의
public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    void deleteById(Long userId);
}
