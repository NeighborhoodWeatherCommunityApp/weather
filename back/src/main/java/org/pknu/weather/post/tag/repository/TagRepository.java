package org.pknu.weather.post.tag.repository;

import org.pknu.weather.post.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long>, TagCustomRepository {
}
