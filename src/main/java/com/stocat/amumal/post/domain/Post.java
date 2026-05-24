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
}
