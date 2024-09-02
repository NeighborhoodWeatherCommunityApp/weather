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
    public static class PostList {
        private List<Post> postList;
        private Integer listSize;
        private Boolean hasNext;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LikePost {
        private org.pknu.weather.domain.Post post;
        private Integer likeCount;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private PostInfo postInfo;
        private MemberInfo memberInfo;
        private LikeInfo likeInfo;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostInfo {
        private Long postId;
        private String content;
        private String createdAt;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private String memberName;
        private String profileImageUrl;
        private Sensitivity sensitivity;
        private String city;        // 구군면
        private String street;      // 읍면동
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LikeInfo {
        private Integer count;       // 좋아요 개수
        private Boolean likeClickable;
    }
}
