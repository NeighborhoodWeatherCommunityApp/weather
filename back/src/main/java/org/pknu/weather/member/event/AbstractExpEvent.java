package org.pknu.weather.member.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractExpEvent {
    protected String targetEmail;
}
