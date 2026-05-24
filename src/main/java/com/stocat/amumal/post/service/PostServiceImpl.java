package com.stocat.amumal.post.service;

import com.stocat.amumal.common.exception.ApiException;
import com.stocat.amumal.post.domain.Post;
import com.stocat.amumal.post.dto.CreatePostRequest;
import com.stocat.amumal.post.dto.CreatePostResponse;
import com.stocat.amumal.post.dto.GetPostResponse;
import com.stocat.amumal.post.dto.UpdatePostRequest;
import com.stocat.amumal.post.dto.UpdatePostResponse;
import com.stocat.amumal.user.domain.User;
import com.stocat.amumal.post.repository.PostRepository;
import com.stocat.amumal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class PostServiceImpl implements PostService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CreatePostResponse createPost(CreatePostRequest request) {
        validate(request);

        // 작성자 id가 없는 경우
        userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        Post savedPost = postRepository.save(
                request.userId(),
                request.title().trim(),
                request.content().trim(),
                request.image() == null ? null : request.image().trim()
        );

        return new CreatePostResponse(
                savedPost.getId(),
                savedPost.getUserId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getImage()
        );
    }

    @Override
    public GetPostResponse getPost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // TODO: 에러 메세지가 불명확한 것 같음 (게시글 작성자가 없습니다로 수정 필요)
        User writer = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));

        post.increaseViewCount();

        return new GetPostResponse(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getImage(),
                writer.getNickname(),
                post.getCreatedAt().format(DATE_TIME_FORMATTER),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                false
        );
    }

    @Override
    public UpdatePostResponse updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        validateUpdateRequest(request);

        if (!post.getUserId().equals(request.userId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "게시글 작성자만 수정할 수 있습니다.");
        }

        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setImage(request.image() == null ? null : request.image().trim());

        Post updatedPost = postRepository.update(post);

        return new UpdatePostResponse(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                updatedPost.getImage()
        );
    }

    private void validate(CreatePostRequest request) {
        if (request.userId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다.");
        }

        if (isBlank(request.title()) || isBlank(request.content())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제목, 내용을 모두 작성해주세요.");
        }

        if (request.title().trim().length() > 26) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제목은 최대 26자까지 작성 가능합니다.");
        }
    }

    private void validateUpdateRequest(UpdatePostRequest request) {
        if (isBlank(request.title())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제목을 입력해주세요.");
        }

        if (request.title().trim().length() > 26) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제목은 최대 26자까지 작성 가능합니다.");
        }

        if (isBlank(request.content())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "내용을 입력해주세요.");
        }

        if (request.image() != null && request.image().contains(",")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미지 파일은 1개만 업로드할 수 있습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
