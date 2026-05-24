package com.stocat.amumal.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostSummaryResponse(
        @JsonProperty("post_id")
        Long postId,
        String title,
        String writer,
        @JsonProperty("created_at")
        String createdAt,
        @JsonProperty("like_count")
        int likeCount,
        @JsonProperty("comment_count")
        int commentCount,
        @JsonProperty("view_count")
        int viewCount
) {
}
