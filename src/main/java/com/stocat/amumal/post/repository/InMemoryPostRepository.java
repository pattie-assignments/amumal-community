package com.stocat.amumal.post.repository;

import com.stocat.amumal.post.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public List<Post> findAllByCursor(Long cursor, int size) {
        return posts.values().stream()
                // 첫 조회면 전체를 대상으로 하고, 다음 조회면 cursor보다 작은 게시글만 조회
                .filter(post -> cursor == null || post.getId() < cursor)
                // postId 기준 내림차순으로 정렬 (id값은 작성 순서대로 증가함)
                .sorted(Comparator.comparing(Post::getId).reversed())
                // 요청한 개수만큼만 잘라서 반환
                .limit(size)
                .toList();
    }
}
