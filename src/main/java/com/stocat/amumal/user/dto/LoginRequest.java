package com.stocat.amumal.user.dto;

public record LoginRequest(
        String email,
        String password
) {
}
