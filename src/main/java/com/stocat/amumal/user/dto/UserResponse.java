package com.stocat.amumal.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
        @JsonProperty("user_id")
        Long userId,
        String email,
        String nickname,
        @JsonProperty("profile_image")
        String profileImage
) {
}
