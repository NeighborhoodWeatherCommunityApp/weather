package org.pknu.weather.dto.converter;

import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.RecommendationUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.pknu.weather.dto.PostResponse;

import java.util.List;

public class PostResponseConverter {

    public static PostResponse.PostList toPostList(Member member, List<Post> postList, boolean hasNext) {
        List<PostResponse.Post> list = postList.stream()
                .map(p -> toPost(member, p))
                .toList();

        if(hasNext) {
            list.remove(list.size() - 1);
        }

        return PostResponse.PostList.builder()
                .postList(list)
                .listSize(postList.size() - 1)
                .hasNext(hasNext)  // size + 1개를 더 조회해서 확인함
                .build();
    }

    public static List<PostResponse.Post> toPopularPostList(Member member, List<Post> popularPostList) {
        return popularPostList.stream()
                .map(post -> toPost(member, post))
                .toList();
    }

    private static PostResponse.Post toPost(Member member, Post post) {
        return PostResponse.Post.builder()
                .postInfo(toPostInfo(post))
                .memberInfo(toMemberInfo(member))
                .likeInfo(toLikeInfo(post.getRecommendationList(), member))
                .build();
    }

    private static PostResponse.MemberInfo toMemberInfo(Member member) {
        Location location = member.getLocation();
        return PostResponse.MemberInfo.builder()
                .memberName(member.getNickname())
                .profileImageUrl(member.getProfileImage())
                .sensitivity(member.getSensitivity())
                .city(location.getCity())
                .street(location.getStreet())
                .build();
    }

    private static PostResponse.PostInfo toPostInfo(Post post) {
        return PostResponse.PostInfo.builder()
                .postId(post.getId())
                .content(post.getContent())
                .createdAt(DateTimeFormatter.pastTimeToString(post.getCreatedAt()))
                .build();
    }

    private static PostResponse.LikeInfo toLikeInfo(List<Recommendation> recommendationList, Member member) {
        return PostResponse.LikeInfo.builder()
                .count(RecommendationUtils.likeCount(recommendationList))
                .likeClickable(RecommendationUtils.isClickable(recommendationList, member))
                .build();
    }
}
