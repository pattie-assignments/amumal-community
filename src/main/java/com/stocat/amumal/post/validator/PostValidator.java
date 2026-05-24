package com.stocat.amumal.post.validator;

import com.stocat.amumal.common.exception.ApiException;
import com.stocat.amumal.post.dto.CreatePostRequest;
import com.stocat.amumal.post.dto.UpdatePostRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {

    public void validateCreatePost(CreatePostRequest request) {
        validateUserId(request.userId());
        validateTitleAndContent(request.title(), request.content());
    }

    public void validateListSize(int size) {
        if (size <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "size는 1 이상이어야 합니다.");
        }
    }

    public void validateUpdatePost(UpdatePostRequest request) {
        validateTitle(request.title());
        validateContent(request.content());
        validateSingleImage(request.image());
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다.");
        }
    }

    private void validateTitleAndContent(String title, String content) {
        if (isBlank(title) || isBlank(content)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제목, 내용을 모두 작성해주세요.");
        }

        validateTitleLength(title);
    }

    private void validateTitle(String title) {
        if (isBlank(title)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제목을 입력해주세요.");
        }

        validateTitleLength(title);
    }

    private void validateTitleLength(String title) {
        if (title.trim().length() > 26) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제목은 최대 26자까지 작성 가능합니다.");
        }
    }

    private void validateContent(String content) {
        if (isBlank(content)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "내용을 입력해주세요.");
        }
    }

    private void validateSingleImage(String image) {
        if (image != null && image.contains(",")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미지 파일은 1개만 업로드할 수 있습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
