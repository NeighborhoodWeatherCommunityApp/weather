package org.pknu.weather.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.stream.Stream;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(DataJpaTestConfig.class)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    void member를_조회하면_location은_즉시_로딩_테스트() {
        // given
        Member member = TestDataCreator.getBusanMember();
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member res = memberRepository.safeFindById(member.getId());

        // then
        assertThat(Hibernate.isInitialized(res.getLocation()))
                .isTrue();
    }

    @Test
    @Transactional
    void member를_조회하면_location은_즉시_로딩_테스트2() {
        // given
        Member member = TestDataCreator.getBusanMember();
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member res = memberRepository.safeFindByEmail(member.getEmail());

        // then
        assertThat(Hibernate.isInitialized(res.getLocation()))
                .isTrue();
    }

    @Test
    @Transactional
    void member는_레벨과_경험치를_가진다() {
        // given
        Member member = TestDataCreator.getBusanMember();

        // when
        Member result = memberRepository.save(member);
        em.flush();
        em.clear();

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        assertThat(result.getSensitivity()).isEqualTo(member.getSensitivity());
        assertThat(result.getLevel()).isEqualTo(Level.LV1);
        assertThat(result.getExp()).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("expProvider")
    @Transactional
    void member의_경험치량이_조건에_충족하면_레벨업합니다(Level level) {
        // given
        Member member = TestDataCreator.getBusanMember();
        member.addExp(level.getRequiredExp());

        // when
        Member result = memberRepository.save(member);
        em.flush();
        em.clear();

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        assertThat(result.getSensitivity()).isEqualTo(member.getSensitivity());
        assertThat(result.getLevel()).isEqualTo(level);
        assertThat(result.getExp()).isEqualTo(level.getRequiredExp());
    }

    static Stream<Arguments> expProvider() {
        return Stream.of(
                Arguments.of(Level.LV2),
                Arguments.of(Level.LV3),
                Arguments.of(Level.LV4),
                Arguments.of(Level.LV5),
                Arguments.of(Level.LV6)
        );
    }
}