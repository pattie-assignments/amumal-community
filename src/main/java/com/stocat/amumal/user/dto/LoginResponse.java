package com.stocat.amumal.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("user_id")
        Long userId
) {
}
