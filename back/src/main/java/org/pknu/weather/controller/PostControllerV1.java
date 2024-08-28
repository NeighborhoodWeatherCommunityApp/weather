package org.pknu.weather.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostRequestConverter;
import org.pknu.weather.service.PostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostControllerV1 {
    private final PostRequestConverter postRequestConverter;
    private final PostService postService;

    @PostMapping("/post")
    public ApiResponse<Object> createPost(Long memberId, @Valid @RequestBody PostRequest.Params params) {
        PostRequest.CreatePost createPost = postRequestConverter.toCreatePost(params);
        boolean isSuccess = postService.createPost(memberId, createPost);
        return ApiResponse.of(isSuccess);
    }
}
