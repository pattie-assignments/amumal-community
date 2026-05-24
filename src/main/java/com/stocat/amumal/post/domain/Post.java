package com.stocat.amumal.post.domain;

public class Post {

    private final Long id;
    private final Long userId;
    private String title;
    private String content;
    private String image;

    public Post(Long id, Long userId, String title, String content, String image) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.image = image;
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

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }
}
