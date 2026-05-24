package com.stocat.amumal.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateProfileRequest(
        String nickname,
        @JsonProperty("profile_image")
        String profileImage
) {
}
