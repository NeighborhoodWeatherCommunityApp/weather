package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.service.MainPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메인 화면에 사용되는 API를 관리하는 컨트롤러. 화면용입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainPageController {
    private final MainPageService mainPageService;

//    @GetMapping
//    public ResponseEntity<> getMainPageResource() {
//
//    }
//
}
