package org.pknu.weather.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.tag.enums.EnumTagMapper;
import org.pknu.weather.post.dto.TagDto;
import org.pknu.weather.post.dto.TagWithSelectedStatusDto;
import org.pknu.weather.tag.service.TagQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TagControllerV1 {
    private final EnumTagMapper enumTagMapper;
    private final TagQueryService tagQueryService;

    @GetMapping("/tags")
    public ApiResponse<Map<String, List<TagDto>>> getTagList() {
        Map<String, List<TagDto>> tagMap = enumTagMapper.getAllDto();
        return ApiResponse.onSuccess(tagMap);
    }

    @GetMapping("/selected-tags")
    public ApiResponse<Map<String, List<TagWithSelectedStatusDto>>> getSelectedOrNotTagList(
            @RequestHeader("Authorization") String authorization) {
        String email = TokenConverter.getEmailByToken(authorization);
        Map<String, List<TagWithSelectedStatusDto>> selectedOrNotTags = tagQueryService.getTagsWithSelectionStatus(email);
        return ApiResponse.onSuccess(selectedOrNotTags);
    }
}
