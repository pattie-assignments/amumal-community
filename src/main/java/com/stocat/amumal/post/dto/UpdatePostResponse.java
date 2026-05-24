package com.stocat.amumal.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePostResponse(
        @JsonProperty("post_id")
        Long postId,
        String title,
        String content,
        String image
) {
}
