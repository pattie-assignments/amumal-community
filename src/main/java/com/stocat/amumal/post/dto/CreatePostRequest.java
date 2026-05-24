package com.stocat.amumal.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePostRequest(
        @JsonProperty("user_id")
        Long userId,
        String title,
        String content,
        String image
) {
}
