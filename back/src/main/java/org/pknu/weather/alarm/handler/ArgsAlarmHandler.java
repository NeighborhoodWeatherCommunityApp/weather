package org.pknu.weather.alarm.handler;

public interface ArgsAlarmHandler<T> extends AlarmHandler {
    void handleRequest(T info);
}
