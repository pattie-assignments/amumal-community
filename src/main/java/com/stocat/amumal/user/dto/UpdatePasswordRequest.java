package com.stocat.amumal.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePasswordRequest(
        String password,
        @JsonProperty("password_confirm")
        String passwordConfirm
) {
}
