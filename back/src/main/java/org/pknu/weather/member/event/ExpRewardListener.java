package org.pknu.weather.member.event;

import lombok.AllArgsConstructor;
import org.pknu.weather.member.exp.ExpEvent;
import org.pknu.weather.member.service.ExpRewardService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class ExpRewardListener {
    private final ExpRewardService expRewardService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostCreatedEvent event) {
        reward(event.getTargetEmail(), ExpEvent.CREATE_POST);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AttendanceCheckedEvent event) {
        reward(event.getTargetEmail(), ExpEvent.ATTENDANCE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RecommendedEvent event) {
        reward(event.getTargetEmail(), ExpEvent.RECOMMENDED);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RecommendEvent event) {
        reward(event.getTargetEmail(), ExpEvent.RECOMMENDED);
    }

    private void reward(String email, ExpEvent expEvent) {
        expRewardService.rewardExp(email, expEvent);
    }
}
