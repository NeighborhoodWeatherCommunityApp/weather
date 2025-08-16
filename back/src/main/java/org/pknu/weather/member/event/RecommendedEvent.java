package org.pknu.weather.member.event;

import lombok.Getter;

@Getter
public class RecommendedEvent extends AbstractExpEvent {
    public RecommendedEvent(String targetEmail) {
        super(targetEmail);
    }
}
