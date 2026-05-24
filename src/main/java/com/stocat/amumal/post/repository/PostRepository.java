package com.stocat.amumal.post.repository;

import com.stocat.amumal.post.domain.Post;

import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    Post update(Post post);

    Optional<Post> findById(Long postId);
}
