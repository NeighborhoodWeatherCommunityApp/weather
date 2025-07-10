package org.pknu.weather.event.exp;

import lombok.Getter;

@Getter
public class PostCreatedEvent extends AbstractExpEvent {
    public PostCreatedEvent(String targetEmail) {
        super(targetEmail);
    }
}
