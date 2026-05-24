package com.stocat.amumal.post.service;

import com.stocat.amumal.common.exception.ApiException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    public GetPostsResponse getPosts(Long cursor, int size) {
        postValidator.validateListSize(size);

        // 다음 페이지 존재 여부를 확인하기 위해 요청 개수보다 1개 더 조회
        List<Post> posts = postRepository.findAllByCursor(cursor, size + 1);
        // 조회 결과가 요청 개수보다 많으면 다음 페이지가 존재
        boolean hasNext = posts.size() > size;
        // 응답에는 요청한 개수만 전달, 초과 1개는 hasNext 판단에만 사용
        List<Post> pagePosts = hasNext ? posts.subList(0, size) : posts;

        List<PostSummaryResponse> postResponses = pagePosts.stream()
                .map(post -> {
                    // TODO: 목록 조회인데 게시글 작성자가 존재하지 않는 글이 존재한다고 ERR 발생하는 것이 맞는가 고민
                    User writer = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));

                    return new PostSummaryResponse(
                            post.getId(),
                            post.getTitle(),
                            writer.getNickname(),
                            post.getCreatedAt().format(DATE_TIME_FORMATTER),
                            post.getLikeCount(),
                            post.getCommentCount(),
                            post.getViewCount()
                    );
                })
                .toList();

        // 다음 값이 존재하고, 현재 게시글 목록 반환값이 null 이 아니라면 다음 커서 값을 제공
        Long nextCursor = hasNext && !pagePosts.isEmpty()
                ? pagePosts.get(pagePosts.size() - 1).getId()
                : null;

        return new GetPostsResponse(postResponses, hasNext, nextCursor);
    }

    @Override
    public GetPostResponse getPost(Long postId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));

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

        postValidator.validateUpdatePost(request);

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

}
