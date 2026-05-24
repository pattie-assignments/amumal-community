package com.stocat.amumal.post.service;

import com.stocat.amumal.post.dto.CreatePostRequest;
import com.stocat.amumal.post.dto.CreatePostResponse;
import com.stocat.amumal.post.dto.UpdatePostRequest;
import com.stocat.amumal.post.dto.UpdatePostResponse;

public interface PostService {

    CreatePostResponse createPost(CreatePostRequest request);

    UpdatePostResponse updatePost(Long postId, UpdatePostRequest request);
}
