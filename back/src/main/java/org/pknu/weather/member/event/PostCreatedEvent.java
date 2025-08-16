package org.pknu.weather.member.event;

import lombok.Getter;

@Getter
public class PostCreatedEvent extends AbstractExpEvent {
    public PostCreatedEvent(String targetEmail) {
        super(targetEmail);
    }
}
