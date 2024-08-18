import React from 'react';
import {View, StyleSheet, Dimensions} from 'react-native';
import Svg, {Path, Line, Text as SvgText} from 'react-native-svg';
import globalStyles from '../globalStyles';

const WeatherGraph = () => {
  const {width} = Dimensions.get('window');
  const graphWidth = width - 40;
  const graphHeight = 200;
  const paddingLeft = 60;

  const temperatureData = [23, 24, 26, 28, 30, 29, 27, 25, 23, 24, 26, 25, 24];
  const maxTemperature = Math.max(...temperatureData);
  const minTemperature = Math.min(...temperatureData);

  const getX = index => {
    const interval = (graphWidth - paddingLeft) / (temperatureData.length - 1);
    return paddingLeft + interval * index;
  };

  const getY = temperature => {
    const scale = (graphHeight - 40) / (maxTemperature - minTemperature);
    return graphHeight - (temperature - minTemperature) * scale - 20;
  };

  const pathData = temperatureData
    .map((temp, index) => {
      const x = getX(index);
      const y = getY(temp);
      return `${index === 0 ? 'M' : 'L'} ${x} ${y}`;
    })
    .join(' ');

  const generateHours = () => {
    const hours = [];
    const currentHour = new Date().getHours();
    for (let i = 0; i < 13; i++) {
      // 12시간 후까지
      hours.push((currentHour + i) % 24);
    }
    return hours;
  };

  const hours = generateHours();

  return (
    <View style={[styles.container, globalStyles.transparentBackground]}>
      <Svg height={graphHeight} width={graphWidth}>
        <Line
          x1={paddingLeft}
          y1={getY(maxTemperature)}
          x2={graphWidth}
          y2={getY(maxTemperature)}
          stroke="white"
          strokeWidth="0.5"
        />
        <SvgText
          x="0"
          y={getY(maxTemperature) + 5}
          fill="white"
          fontSize="10"
          fontWeight="bold">
          최고({maxTemperature}°C)
        </SvgText>

        <Line
          x1={paddingLeft}
          y1={getY(maxTemperature - 2)}
          x2={graphWidth}
          y2={getY(maxTemperature - 2)}
          stroke="white"
          strokeWidth="0.5"
        />
        <SvgText
          x="0"
          y={getY(maxTemperature - 1) + 5}
          fill="white"
          fontSize="10">
          더움
        </SvgText>

        <SvgText
          x="0"
          y={getY((maxTemperature + minTemperature) / 2) + 5}
          fill="white"
          fontSize="10">
          보통
        </SvgText>

        <Line
          x1={paddingLeft}
          y1={getY(minTemperature + 2)}
          x2={graphWidth}
          y2={getY(minTemperature + 2)}
          stroke="white"
          strokeWidth="0.5"
        />
        <SvgText
          x="0"
          y={getY(minTemperature + 1) + 5}
          fill="white"
          fontSize="10">
          선선
        </SvgText>

        <Line
          x1={paddingLeft}
          y1={getY(minTemperature)}
          x2={graphWidth}
          y2={getY(minTemperature)}
          stroke="white"
          strokeWidth="0.5"
        />
        <SvgText
          x="0"
          y={getY(minTemperature) + 5}
          fill="white"
          fontSize="10"
          fontWeight="bold">
          최저({minTemperature}°C)
        </SvgText>

        {hours.map((hour, index) => (
          <SvgText
            key={index}
            x={getX(index)}
            y={graphHeight - 5}
            fill="white"
            fontSize="10"
            textAnchor="middle">
            {hour}
          </SvgText>
        ))}

        <Path d={pathData} fill="none" stroke="white" strokeWidth="2" />
      </Svg>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    marginVertical: 10,
    borderRadius: 10,
    padding: 20,
  },
  text: {
    color: '#fff',
    marginBottom: 10,
  },
});

export default WeatherGraph;
