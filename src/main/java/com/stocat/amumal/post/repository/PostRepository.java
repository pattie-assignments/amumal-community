package com.stocat.amumal.post.repository;

import com.stocat.amumal.post.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Long userId, String title, String content, String image);

    Post update(Post post);

    Optional<Post> findById(Long postId);

    List<Post> findAllByCursor(Long cursor, int size);
}
