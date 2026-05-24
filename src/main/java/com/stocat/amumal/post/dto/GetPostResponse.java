package com.stocat.amumal.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetPostResponse(
        @JsonProperty("post_id")
        Long postId,
        @JsonProperty("user_id")
        Long userId,
        String title,
        String content,
        String image,
        String writer,
        @JsonProperty("created_at")
        String createdAt,
        @JsonProperty("view_count")
        int viewCount,
        @JsonProperty("like_count")
        int likeCount,
        @JsonProperty("comment_count")
        int commentCount,
        @JsonProperty("is_liked")
        boolean isLiked
) {
}
