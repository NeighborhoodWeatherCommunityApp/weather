package org.pknu.weather.repository;

import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
