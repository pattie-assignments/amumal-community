package com.stocat.amumal.post.repository;

import com.stocat.amumal.post.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryPostRepository implements PostRepository {

    private long sequence = 0;
    private final Map<Long, Post> posts = new HashMap<>();

    @Override
    public Post save(Long userId, String title, String content, String image) {
        long id = ++sequence;
        // post 객체 생성
        Post savedPost = new Post(id, userId, title, content, image);
        // post 저장
        posts.put(id, savedPost);
        return savedPost;
    }

    @Override
    public Post update(Post post) {
        Post existingPost = posts.get(post.getId());
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setImage(post.getImage());
        return existingPost;
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return Optional.ofNullable(posts.get(postId));
    }
}
