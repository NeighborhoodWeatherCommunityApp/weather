package org.pknu.weather.event.exp;

import lombok.Getter;

@Getter
public class AttendanceCheckedEvent extends AbstractExpEvent {
    public AttendanceCheckedEvent(String targetEmail) {
        super(targetEmail);
    }
}
