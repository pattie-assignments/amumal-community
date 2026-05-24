package com.stocat.amumal.post.service;

import com.stocat.amumal.common.exception.ApiException;
import com.stocat.amumal.post.domain.Post;
import com.stocat.amumal.post.dto.CreatePostRequest;
import com.stocat.amumal.post.dto.CreatePostResponse;
import com.stocat.amumal.post.repository.PostRepository;
import com.stocat.amumal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

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

        // TODO: 아이디값 생성을 repo에 위임
        Post savedPost = postRepository.save(new Post(
                null,
                request.userId(),
                request.title().trim(),
                request.content().trim(),
                request.image() == null ? null : request.image().trim()
        ));

        return new CreatePostResponse(
                savedPost.getId(),
                savedPost.getUserId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getImage()
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
