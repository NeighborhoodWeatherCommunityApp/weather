package org.pknu.weather.member.event;

import lombok.Getter;

@Getter
public class RecommendEvent extends AbstractExpEvent {
    public RecommendEvent(String targetEmail) {
        super(targetEmail);
    }
}
