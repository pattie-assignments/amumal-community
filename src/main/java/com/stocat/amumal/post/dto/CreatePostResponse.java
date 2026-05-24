package com.stocat.amumal.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePostResponse(
        @JsonProperty("post_id")
        Long postId,
        @JsonProperty("user_id")
        Long userId,
        String title,
        String content,
        String image
) {
}
