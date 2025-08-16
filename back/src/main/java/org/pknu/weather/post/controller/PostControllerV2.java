package org.pknu.weather.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.post.dto.PostRequest;
import org.pknu.weather.post.service.PostService;
import org.pknu.weather.common.validation.annotation.PostFieldsRequired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Validated
public class PostControllerV2 {
    private final PostService postService;

    @PostMapping("/post")
    public ApiResponse<Object> createWeatherPost(@RequestHeader("Authorization") String authorization,
                                                 @Valid @PostFieldsRequired @RequestBody
                                                        PostRequest.CreatePostAndTagParameters params) {
        String email = TokenConverter.getEmailByToken(authorization);
        boolean isSuccess = postService.createWeatherPostV2(email, params);
        return ApiResponse.of(isSuccess);
    }
}
