package org.pknu.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.common.Sensitivity;

import java.util.List;

public class PostResponse {


    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Posts {
        private List<Post> postList;
        private Integer listSize;
        private Boolean hasNext;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private Long postId;
        private String profileImageUrl;
        private String memberName;
        private String createdAt;
        private String city;        // 구군면
        private String street;      // 읍면동
        private Sensitivity sensitivity;
        private Integer like;       // 좋아요
        private Boolean likeClickable;
        private String content;
    }
}
