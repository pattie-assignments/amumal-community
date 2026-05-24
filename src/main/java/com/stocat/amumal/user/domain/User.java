package com.stocat.amumal.user.domain;

public record User(
        Long id,
        String email,
        String password,
        String nickname,
        String profileImage
) {
}
