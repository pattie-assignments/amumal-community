package com.stocat.amumal.user.validator;

import com.stocat.amumal.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,20}$");
    private static final Pattern IMAGE_FILE_PATTERN =
            Pattern.compile("^.+\\.(png|jpg|jpeg|gif|webp)$", Pattern.CASE_INSENSITIVE);

    public void validateEmail(String email) {
        if (isBlank(email)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요.");
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "올바른 이메일 주소 형식을 입력해주세요. (예: example@adapterz.kr)");
        }
    }

    public void validatePassword(String password) {
        validateRequiredPassword(password);

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.");
        }
    }

    public void validateRequiredPassword(String password) {
        if (isBlank(password)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요");
        }
    }

    public void validatePasswordConfirm(
            String password,
            String passwordConfirm,
            String emptyMessage,
            String mismatchMessage
    ) {
        if (isBlank(passwordConfirm)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, emptyMessage);
        }

        if (!password.equals(passwordConfirm)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, mismatchMessage);
        }
    }

    public void validateNickname(String nickname) {
        if (isBlank(nickname)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임을 입력해주세요.");
        }

        if (nickname.chars().anyMatch(Character::isWhitespace)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "띄어쓰기를 없애주세요.");
        }

        if (nickname.trim().length() > 10) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임은 최대 10자 까지 작성 가능합니다.");
        }
    }

    public void validateRequiredProfileImage(String profileImage) {
        if (isBlank(profileImage)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "프로필 사진을 추가해주세요.");
        }
    }

    public void validateOptionalProfileImage(String profileImage) {
        if (isBlank(profileImage) || !IMAGE_FILE_PATTERN.matcher(profileImage.trim()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "유효한 파일이 아닙니다.(올바른 사진 확장자가 아닐 경우)");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
