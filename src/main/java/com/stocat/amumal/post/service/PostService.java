package com.stocat.amumal.post.service;

import com.stocat.amumal.post.dto.CreatePostRequest;
import com.stocat.amumal.post.dto.CreatePostResponse;

public interface PostService {

    CreatePostResponse createPost(CreatePostRequest request);
}
