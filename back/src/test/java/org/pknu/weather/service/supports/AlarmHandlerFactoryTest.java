package org.pknu.weather.service.supports;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.service.handler.AlarmHandler;
import org.pknu.weather.service.handler.AlarmHandlerFactory;
import org.pknu.weather.service.handler.ArgsAlarmHandler;
import org.pknu.weather.service.handler.NoArgsAlarmHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlarmHandlerFactoryTest {

    @Test
    void 올바른_핸들러_리턴_테스트() {
        // given
        NoArgsAlarmHandler weatherUpdateHandler = new DummyAlarmHandler(AlarmType.WEATHER_UPDATE);
        NoArgsAlarmHandler weatherSummaryHandler = new DummyAlarmHandler(AlarmType.WEATHER_SUMMARY);

        List<NoArgsAlarmHandler> handlerList = List.of(weatherSummaryHandler, weatherUpdateHandler);

        AlarmHandlerFactory factory = new AlarmHandlerFactory(null, handlerList);

        // when
        AlarmHandler foundHandler = factory.getNoArgsAlarmHandler(AlarmType.WEATHER_SUMMARY);

        //then
        assertThat(foundHandler)
                .isNotNull()
                .extracting(AlarmHandler::getAlarmType)
                .isEqualTo(AlarmType.WEATHER_SUMMARY);
    }


    @Test
    void 잘못된_핸들러_호출_테스트() {
        // Given
        NoArgsAlarmHandler weatherUpdateHandler = new DummyAlarmHandler(AlarmType.WEATHER_UPDATE);
        List<NoArgsAlarmHandler> handlerList = List.of(weatherUpdateHandler);

        AlarmHandlerFactory factory = new AlarmHandlerFactory(null,handlerList);

        // When & Then
        assertThatThrownBy(() -> factory.getNoArgsAlarmHandler(AlarmType.WEATHER_SUMMARY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 알람 타입: " + AlarmType.WEATHER_SUMMARY);

    }


    @Test
    void 빈_핸들러리스트에_핸들러_호출_테스트() {
        List<ArgsAlarmHandler<?>> emptyList1 = new ArrayList<>();
        List<NoArgsAlarmHandler> emptyList2 = new ArrayList<>();
        AlarmHandlerFactory factory = new AlarmHandlerFactory(emptyList1, emptyList2);

        // When & Then
        assertThatThrownBy(() -> {
            factory.getNoArgsAlarmHandler(AlarmType.WEATHER_SUMMARY);})
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 알람 타입: " + AlarmType.WEATHER_SUMMARY);
    }

    private static class DummyAlarmHandler implements NoArgsAlarmHandler {
        AlarmType alarmType;
        DummyAlarmHandler (AlarmType alarmType){
            this.alarmType = alarmType;
        }
        @Override
        public AlarmType getAlarmType() {
            return this.alarmType;
        }

        @Override
        public void handleRequest() {
        }

    }
}
