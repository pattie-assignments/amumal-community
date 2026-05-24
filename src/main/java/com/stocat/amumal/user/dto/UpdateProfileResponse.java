package com.stocat.amumal.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateProfileResponse(
        @JsonProperty("user_id")
        Long userId,
        String nickname,
        @JsonProperty("profile_image")
        String profileImage
) {
}
