package com.stocat.amumal.post.controller;

import com.stocat.amumal.common.response.ApiResponse;
import com.stocat.amumal.post.dto.CreatePostRequest;
import com.stocat.amumal.post.dto.CreatePostResponse;
import com.stocat.amumal.post.dto.GetPostResponse;
import com.stocat.amumal.post.dto.UpdatePostRequest;
import com.stocat.amumal.post.dto.UpdatePostResponse;
import com.stocat.amumal.post.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatePostResponse> createPost(@RequestBody CreatePostRequest request) {
        return ApiResponse.of("게시글이 생성되었습니다.", postService.createPost(request));
    }

    @GetMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetPostResponse> getPost(
            @PathVariable("post_id") Long postId,
            @RequestParam("user_id") Long userId
    ) {
        return ApiResponse.of("게시글 조회에 성공했습니다.", postService.getPost(postId, userId));
    }

    @PutMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UpdatePostResponse> updatePost(
            @PathVariable("post_id") Long postId,
            @RequestBody UpdatePostRequest request
    ) {
        return ApiResponse.of("게시글이 수정되었습니다.", postService.updatePost(postId, request));
    }
}
