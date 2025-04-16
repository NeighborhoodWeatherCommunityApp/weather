package org.pknu.weather.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import java.util.stream.Stream;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.exp.Level;
import org.pknu.weather.exception.GeneralException;
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
    @EnumSource(Level.class)
    void member의_경험치량이_조건에_충족하면_레벨업합니다(Level level) {
        // given
        Member member = TestDataCreator.getBusanMember();
        member.addExp(level.getRequiredExp());

        // when
        Member result = memberRepository.save(member);

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        assertThat(result.getSensitivity()).isEqualTo(member.getSensitivity());
        assertThat(result.getLevel()).isEqualTo(level);
        assertThat(result.getExp()).isEqualTo(level.getRequiredExp());
    }

    @ParameterizedTest
    @MethodSource("minusExpProvider")
    void 휴면상태로인해_경험치가_하락하더라도_레벨이_하락하진_않습니다(Level level, Long maxMinusExp) {
        // given
        Member member = TestDataCreator.getBusanMember();
        member.addExp(level.getRequiredExp() + 50);
        member.decreaseExp(maxMinusExp);

        // when
        Member result = memberRepository.save(member);

        // then
        assertThat(result.getLevel()).isEqualTo(level);
        assertThat(result.getExp()).isEqualTo(level.getRequiredExp());
    }


    static Stream<Arguments> minusExpProvider() {
        Long maxMinusExp = Level.getMaxLevel().getRequiredExp() * (-1L);
        return Stream.of(
                Arguments.of(Level.LV1, maxMinusExp),
                Arguments.of(Level.LV2, maxMinusExp),
                Arguments.of(Level.LV3, maxMinusExp),
                Arguments.of(Level.LV4, maxMinusExp),
                Arguments.of(Level.LV5, maxMinusExp),
                Arguments.of(Level.LV6, maxMinusExp)
        );
    }

    @Test
    void 경험치_하락_테스트() {
        // given
        Member member = TestDataCreator.getBusanMember();
        member.addExp(Level.LV3.getRequiredExp() + 50);
        member.decreaseExp(-40L);

        // when
        Member result = memberRepository.save(member);

        // then
        assertThat(result.getLevel()).isEqualTo(Level.LV3);
        assertThat(result.getExp()).isEqualTo(Level.LV3.getRequiredExp() + 10);
    }

    @Test
    void 음수_경험치는_허용되지_않는다() {
        Member member = Member.builder()
                .email("email")
                .exp(-199L)
                .build();

        assertThrows(GeneralException.class, () -> {
            memberRepository.save(member);
        });
    }

    @Test
    void 최대_경험치_초과는_허용되지_않는다() {
        Member member = Member.builder()
                .email("email")
                .exp(Level.getMaxLevel().getRequiredExp() + 100)
                .build();

        assertThrows(GeneralException.class, () -> {
            memberRepository.save(member);
        });
    }
}