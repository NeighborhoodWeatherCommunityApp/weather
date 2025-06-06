package org.pknu.weather.event.exp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractExpEvent {
    protected String targetEmail;
}
