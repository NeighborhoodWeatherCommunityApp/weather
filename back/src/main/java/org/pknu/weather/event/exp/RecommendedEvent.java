package org.pknu.weather.event.exp;

import lombok.Getter;

@Getter
public class RecommendedEvent extends AbstractExpEvent {
    public RecommendedEvent(String targetEmail) {
        super(targetEmail);
    }
}
