import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Dimensions} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {fetchExtraWeatherInfo} from '../api/api';

const {width} = Dimensions.get('window');

const AirQuality = ({accessToken, weatherData}) => {
  const [extraWeatherInfo, setExtraWeatherInfo] = useState({
    pm25Grade: 0,
    pm10Grade: 0,
    uvGrade: 0,
    o3Grade: 0,
    pm10Value: null,
    pm25Value: null,
  });
  const [loading, setLoading] = useState(false);

  const getGradeText = grade => {
    if (grade === 0) return '좋음';
    switch (grade) {
      case 1:
        return '좋음';
      case 2:
        return '보통';
      case 3:
        return '나쁨';
      case 4:
        return '매우 나쁨';
      default:
        return '정보 없음';
    }
  };

  const getGradientColors = grade => {
    if (grade === 0) return ['#EFF6FF', '#DBEAFE'];
    switch (grade) {
      case 1:
        return ['#EFF6FF', '#DBEAFE'];
      case 2:
        return ['#F0FDF4', '#DCFCE7'];
      case 3:
      case 4:
        return ['#FEF2F2', '#FEE2E2'];
      default:
        return ['#F4F4F5', '#E4E4E7'];
    }
  };

  const getTextColor = grade => {
    if (grade === 0) return '#2F5AF4';
    switch (grade) {
      case 1:
        return '#2F5AF4';
      case 2:
        return '#22C55E';
      case 3:
      case 4:
        return '#EF4444';
      default:
        return '#666';
    }
  };

  const loadExtraWeatherInfo = async () => {
    if (loading || !accessToken) return;
    setLoading(true);
    try {
      const data = await fetchExtraWeatherInfo(accessToken);
      // console.log('Fetched extra weather info:', data);
      setExtraWeatherInfo(data);
    } catch (error) {
      console.error('Error fetching extra weather info:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (weatherData) {
      loadExtraWeatherInfo();
    }
  }, [weatherData]);

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        {['pm10Grade', 'pm25Grade'].map((key, index) => {
          const gradeText = getGradeText(extraWeatherInfo[key]);
          const valueKey = key === 'pm25Grade' ? 'pm25Value' : 'pm10Value';
          const value = extraWeatherInfo[valueKey];

          return (
            <View style={styles.shadowContainer} key={index}>
              <LinearGradient
                colors={getGradientColors(extraWeatherInfo[key])}
                style={styles.box}>
                <Text style={styles.title}>
                  {key === 'pm25Grade' ? '초미세먼지' : '미세먼지'}
                </Text>
                <Text
                  style={[
                    styles.value,
                    {color: getTextColor(extraWeatherInfo[key])},
                  ]}>
                  {gradeText}
                </Text>
                {gradeText !== '정보 없음' && value !== null && (
                  <Text style={styles.valueText}>{value} µg/m³</Text>
                )}
              </LinearGradient>
            </View>
          );
        })}
      </View>
      <View style={styles.row}>
        {['uvGrade', 'o3Grade'].map((key, index) => (
          <View style={styles.shadowContainer} key={index}>
            <LinearGradient
              colors={getGradientColors(extraWeatherInfo[key])}
              style={styles.box}>
              <Text style={styles.title}>
                {key === 'uvGrade' ? '자외선' : '오존'}
              </Text>
              <Text
                style={[
                  styles.value,
                  {color: getTextColor(extraWeatherInfo[key])},
                ]}>
                {getGradeText(extraWeatherInfo[key])}
              </Text>
            </LinearGradient>
          </View>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'column',
    justifyContent: 'center',
    marginTop: Platform.OS === 'ios' ? 8 : 8,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
    paddingHorizontal: 10,
  },
  shadowContainer: {
    width: width * 0.46,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  box: {
    height: 77,
    borderRadius: 10,
    paddingBottom: 5,
    alignItems: 'center',
    justifyContent: 'center',
  },
  title: {
    fontSize: 13,
    color: '#333',
    marginBottom: 6,
  },
  value: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  valueText: {
    fontSize: 11,
    color: '#555',
    marginTop: 1,
  },
});

export default AirQuality;
