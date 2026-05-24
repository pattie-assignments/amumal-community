package com.stocat.amumal.post.domain;

import java.time.LocalDateTime;

public class Post {

    private final Long id;
    private final Long userId;
    private final LocalDateTime createdAt;
    private String title;
    private String content;
    private String image;
    private int viewCount;
    private int likeCount;
    private int commentCount;

    public Post(Long id, Long userId, String title, String content, String image) {
        this.id = id;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
