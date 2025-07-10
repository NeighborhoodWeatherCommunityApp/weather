package org.pknu.weather.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.domain.exp.ExpRewardLimitPolicy;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpRewardService {
    private final MemberRepository memberRepository;
    private final Map<ExpEvent, ExpRewardLimitPolicy> policyMap;

    @Async
    @Transactional
    public void rewardExp(String email, ExpEvent expEvent) {
        Member member = memberRepository.safeFindByEmail(email);

        ExpRewardLimitPolicy policy = policyMap.get(expEvent);
        if (policy != null && !policy.canReward(member.getId())) {
            return;
        }

        member.addExp(expEvent.getRewardExpAmount());
    }

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void decreaseExp() {
        LocalDateTime localDateTime = LocalDateTime.now()
                .withHour(6)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .minusDays(7);

        List<Member> inactiveMemberList = memberRepository.findMembersInactiveSince(localDateTime).stream()
                .peek(member -> {
                    member.decreaseExp(ExpEvent.INACTIVE_7_DAYS.getRewardExpAmount());
                })
                .toList();

        memberRepository.saveAll(inactiveMemberList);
    }
}
