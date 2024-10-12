package org.pknu.weather.aop;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.controller.MainPageControllerV1;
import org.pknu.weather.domain.Member;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class AspectTest {

    @Autowired
    MainPageControllerV1 mainPageControllerV1;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void aopTest() {
        // given
        Member member = Member.builder().build();

        // when
        Member save = memberRepository.save(member);

        // then
        Assertions.assertThrows(Exception.class,
                () -> mainPageControllerV1.getMainPageResource(save.getId()));
    }

}