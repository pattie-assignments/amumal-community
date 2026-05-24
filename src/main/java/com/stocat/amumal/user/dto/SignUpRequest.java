package com.stocat.amumal.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignUpRequest(
        String email,
        String password,
        @JsonProperty("password_confirm") // JSON필드명은 스네이크 케이스, 자바에서는 카멜 케이스를 사용하므로 매핑
        String passwordConfirm,
        String nickname,
        @JsonProperty("profile_image")
        String profileImage
) {
}
