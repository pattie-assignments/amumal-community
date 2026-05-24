package com.stocat.amumal.post.service;

import com.stocat.amumal.common.DateTimeConstants;
import com.stocat.amumal.common.exception.ApiException;
import com.stocat.amumal.common.exception.ErrorCode;
import com.stocat.amumal.post.domain.Post;
import com.stocat.amumal.post.dto.CreatePostRequest;
import com.stocat.amumal.post.dto.CreatePostResponse;
import com.stocat.amumal.post.dto.GetPostResponse;
import com.stocat.amumal.post.dto.GetPostsResponse;
import com.stocat.amumal.post.dto.PostSummaryResponse;
import com.stocat.amumal.post.dto.UpdatePostRequest;
import com.stocat.amumal.post.dto.UpdatePostResponse;
import com.stocat.amumal.post.repository.PostRepository;
import com.stocat.amumal.post.validator.PostValidator;
import com.stocat.amumal.user.domain.User;
import com.stocat.amumal.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostValidator postValidator;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, PostValidator postValidator) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postValidator = postValidator;
    }

    @Override
    public CreatePostResponse createPost(CreatePostRequest request) {
        postValidator.validateCreatePost(request);

        userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

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
    public GetPostsResponse getPosts(Long cursor, int size) {
        postValidator.validateListSize(size);

        // 다음 페이지 존재 여부를 확인하기 위해 요청 개수보다 1개 더 조회
        List<Post> posts = postRepository.findAllByCursor(cursor, size + 1);
        boolean hasNext = posts.size() > size;
        List<Post> pagePosts = hasNext ? posts.subList(0, size) : posts;

        List<PostSummaryResponse> postResponses = pagePosts.stream()
                .map(post -> {
                    // 게시글 작성자가 존재하지 않는 글이 존재하는 것은 예외임, 잘못된 데이터임 -> 회원 탈퇴 시 게시글 삭제되기때문
                    User writer = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new ApiException(ErrorCode.POST_AUTHOR_NOT_FOUND));

                    return new PostSummaryResponse(
                            post.getId(),
                            post.getTitle(),
                            writer.getNickname(),
                            post.getCreatedAt().format(DateTimeConstants.DATE_TIME_FORMATTER),
                            post.getLikeCount(),
                            post.getCommentCount(),
                            post.getViewCount()
                    );
                })
                .toList();

        Long nextCursor = hasNext && !pagePosts.isEmpty()
                ? pagePosts.get(pagePosts.size() - 1).getId()
                : null;

        return new GetPostsResponse(postResponses, hasNext, nextCursor);
    }

    @Override
    public GetPostResponse getPost(Long postId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        User writer = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new ApiException(ErrorCode.POST_AUTHOR_NOT_FOUND));

        post.increaseViewCount();

        return new GetPostResponse(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getImage(),
                writer.getNickname(),
                post.getCreatedAt().format(DateTimeConstants.DATE_TIME_FORMATTER),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                false
        );
    }

    @Override
    public UpdatePostResponse updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        postValidator.validateUpdatePost(request);

        if (!post.getUserId().equals(request.userId())) {
            throw new ApiException(ErrorCode.POST_UPDATE_FORBIDDEN);
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
}
