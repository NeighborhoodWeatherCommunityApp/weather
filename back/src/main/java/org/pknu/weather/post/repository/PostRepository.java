package org.pknu.weather.post.repository;

import java.util.Optional;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.exception.GeneralException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    @EntityGraph(attributePaths = {"location"})
    Optional<Post> findById(Long postId);

    default Post safeFindById(Long postId) {
        return findById(postId).orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
    }
}
