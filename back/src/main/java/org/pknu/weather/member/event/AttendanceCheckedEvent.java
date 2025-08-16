package org.pknu.weather.member.event;

import lombok.Getter;

@Getter
public class AttendanceCheckedEvent extends AbstractExpEvent {
    public AttendanceCheckedEvent(String targetEmail) {
        super(targetEmail);
    }
}
