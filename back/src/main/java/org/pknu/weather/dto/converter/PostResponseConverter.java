package org.pknu.weather.dto.converter;

import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.RecommendationUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.pknu.weather.dto.PostResponse;

import java.util.ArrayList;
import java.util.List;

public class PostResponseConverter {
    /**
     * @param postViewer 게시글을 조회한 사람
     * @param postList post list
     * @param hasNext 다음 게시글이 존재하는지 여부
     * @return PostResponse.PostList
     */

    public static PostResponse.PostList toPostList(Member postViewer, List<Post> postList, boolean hasNext) {
        List<PostResponse.Post> list = new ArrayList<>(postList.stream()
                .map(post -> {
                    return toPost(post, postViewer);
                })
                .toList());

        if (hasNext) {
            list.remove(list.size() - 1);
        }

        return PostResponse.PostList.builder()
                .postList(list)
                .listSize(postList.size() - 1)
                .hasNext(hasNext)  // size + 1개를 더 조회해서 확인함
                .build();
    }

    /**
     * @param postViewer 게시글을 조회한 사람
     * @param latestPostList post list
     * @return List<PostResponse.Post> 형태로 반환합니다.
     */
    public static List<PostResponse.Post> toLatestPostList(Member postViewer, List<Post> latestPostList) {
        return latestPostList.stream()
                .map(post -> {
                    return toPost(post, postViewer);
                })
                .toList();
    }

    private static PostResponse.Post toPost(Post post, Member postViewer) {
        Member postAuthor  = post.getMember();

        return PostResponse.Post.builder()
                .postInfo(toPostInfo(post, postViewer))
                .memberInfo(toMemberInfo(postAuthor))
                .build();
    }

    private static PostResponse.MemberInfo toMemberInfo(Member postAuthor) {
        Location location = postAuthor.getLocation();
        return PostResponse.MemberInfo.builder()
                .memberName(postAuthor.getNickname())
                .profileImageUrl(postAuthor.getProfileImage())
                .sensitivity(postAuthor.getSensitivity())
                .city(location.getCity())
                .street(location.getStreet())
                .build();
    }

    private static PostResponse.PostInfo toPostInfo(Post post, Member postViewer) {
        List<Recommendation> recommendationList = post.getRecommendationList();

        return PostResponse.PostInfo.builder()
                .postId(post.getId())
                .content(post.getContent())
                .createdAt(DateTimeFormatter.pastTimeToString(post.getCreatedAt()))
                .likeCount(RecommendationUtils.likeCount(recommendationList))
                .likeClickable(RecommendationUtils.isClickable(recommendationList, postViewer))
                .build();
    }
}
