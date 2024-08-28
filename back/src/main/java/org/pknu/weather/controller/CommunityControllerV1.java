package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.service.PostQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityControllerV1 {
    private final PostQueryService postQueryService;

    @GetMapping("/posts")
    public ApiResponse<PostResponse.Posts> getPosts(Long memberId, Long lastPostId, Long size) {
        PostResponse.Posts posts = postQueryService.getPosts(memberId, lastPostId, size);
        return ApiResponse.onSuccess(posts);
    }
}
