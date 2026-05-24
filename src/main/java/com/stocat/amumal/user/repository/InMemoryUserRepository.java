package com.stocat.amumal.user.repository;

import com.stocat.amumal.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private long sequence = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        long id = user.id() == null ? ++sequence : user.id();
        User savedUser = new User(id, user.email(), user.password(), user.nickname(), user.profileImage());
        users.put(id, savedUser);
        return savedUser;
    }

    @Override
    public boolean existsByEmail(String email) {
        // 입력한 email 을 가진 유저가 하나라도 인메모리에 존재하면 true를 반환
        return users.values().stream()
                .anyMatch(user -> user.email().equals(email));
    }

    @Override
    public boolean existsByNickname(String nickname) {
        // 입력한 nickname 을 가진 유저가 하나라도 인메모리에 존재하면 true를 반환
        return users.values().stream()
                .anyMatch(user -> user.nickname().equals(nickname));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.email().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findById(Long userId) {
        // 존재하지 않으면 null, 회원이 존재하는 경우에만 User 데이터를 반환합니다.
        return Optional.ofNullable(users.get(userId));
    }
}
