package org.pknu.weather.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.common.Sensitivity;

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
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostInfo {
        private Long postId;
        private String content;
        private String createdAt;
        private Integer likeCount;
        private Boolean likeClickable;
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
}
