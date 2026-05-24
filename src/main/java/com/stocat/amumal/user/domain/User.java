package com.stocat.amumal.user.domain;

public class User {

    // id, email 값은 변경 불가능 (setter 도 존재하지 않음)
    private final Long id;
    private final String email;
    private String password;
    private String nickname;
    private String profileImage;

    public User(Long id, String email, String password, String nickname, String profileImage) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
