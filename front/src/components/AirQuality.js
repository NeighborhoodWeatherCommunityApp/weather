import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Dimensions} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {fetchExtraWeatherInfo} from '../api/api';

const {width} = Dimensions.get('window');

const AirQuality = ({accessToken}) => {
  const [extraWeatherInfo, setExtraWeatherInfo] = useState({
    pm25Grade: 0,
    pm10Grade: 0,
    uvGrade: 0,
    o3Grade: 0,
  });
  const [loading, setLoading] = useState(true);

  const getGradeText = grade => {
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

  useEffect(() => {
    const loadExtraWeatherInfo = async () => {
      try {
        const data = await fetchExtraWeatherInfo(accessToken);
        console.log('fetched extra weather info:', data);
        setExtraWeatherInfo(data);
      } catch (error) {
        console.error('Error fetching extra weather info:', error);
      } finally {
        setLoading(false);
      }
    };

    if (accessToken) {
      loadExtraWeatherInfo();
    }
  }, [accessToken]);

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        {['pm10Grade', 'pm25Grade'].map((key, index) => (
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
                {getGradeText(extraWeatherInfo[key])}
              </Text>
            </LinearGradient>
          </View>
        ))}
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
    marginTop: 10,
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
    backgroundColor: '#fff',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.12,
    shadowRadius: 6,
    elevation: 3,
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
    marginBottom: 8,
  },
  value: {
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default AirQuality;
