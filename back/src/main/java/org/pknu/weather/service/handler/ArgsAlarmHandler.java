package org.pknu.weather.service.handler;

public interface ArgsAlarmHandler<T> extends AlarmHandler {
    void handleRequest(T info);
}
