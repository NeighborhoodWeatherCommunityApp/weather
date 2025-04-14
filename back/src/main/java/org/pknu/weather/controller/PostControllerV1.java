package org.pknu.weather.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostRequestConverter;
import org.pknu.weather.service.PostService;
import org.pknu.weather.validation.annotation.IsPositive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class PostControllerV1 {
    private final PostService postService;

    @PostMapping("/post")
    public ApiResponse<Object> createWeatherPost(@RequestHeader("Authorization") String authorization,
                                                 @Valid @RequestBody PostRequest.Params params) {
        PostRequest.CreatePost createPost = PostRequestConverter.toCreatePost(params);
        String email = TokenConverter.getEmailByToken(authorization);
        boolean isSuccess = postService.createWeatherPost(email, createPost);
        return ApiResponse.of(isSuccess);
    }

    @PostMapping("/post/hobby")
    public ApiResponse<Object> createHobbyPost(@RequestHeader("Authorization") String authorization,
                                               @RequestBody PostRequest.HobbyParams params) {
        String email = TokenConverter.getEmailByToken(authorization);
        boolean isSuccess = postService.createHobbyPost(email, params);
        return ApiResponse.of(isSuccess);
    }

    @PostMapping("/post/recommendation")
    public ApiResponse<Object> addRecommendation(@RequestHeader("Authorization") String authorization,
                                                 @IsPositive Long postId) {
        String senderEmail = TokenConverter.getEmailByToken(authorization);
        boolean result = postService.addRecommendation(senderEmail, postId);
        return ApiResponse.of(result);
    }
}
