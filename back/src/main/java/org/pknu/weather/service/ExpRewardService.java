package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.ExpEvent;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpRewardService {
    private final MemberRepository memberRepository;

    @Transactional
    public void rewardExp(String email, ExpEvent expEvent) {
        Member member = memberRepository.safeFindByEmail(email);
        member.addExp(expEvent.getRewardExpAmount());
    }
}
