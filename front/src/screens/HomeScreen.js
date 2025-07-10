import React, {useState, useCallback, useEffect} from 'react';
import {useRefresh} from '../contexts/RefreshContext';
import {
  ScrollView,
  View,
  StatusBar,
  RefreshControl,
  TouchableOpacity,
  StyleSheet,
  Image,
  Dimensions,
} from 'react-native';
import {useFocusEffect} from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {Alert} from 'react-native';
import WeatherHeader from '../components/WeatherHeader';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import Posts from '../components/Posts';
import KakaoShareButton from '../components/KakaoShareButton';
import globalStyles from '../globalStyles';
import {InteractionManager} from 'react-native';
import {fetchWeatherData, checkInAttendance, fetchMemberInfo} from '../api/api';

const {width, height} = Dimensions.get('window');

const HomeScreen = ({accessToken, navigation}) => {
  const {refresh, setRefresh} = useRefresh();
  const [weatherData, setWeatherData] = useState(null);
  const [showText, setShowText] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [buttonBackgroundColor, setButtonBackgroundColor] = useState('#3f7dfd');
  const [isFetchingWeather, setIsFetchingWeather] = useState(false); // 중복 호출 방지

  const fetchWeather = async () => {
    if (isFetchingWeather) {
      console.log('날씨 데이터 중복 호출 방지');
      return;
    }

    setIsFetchingWeather(true);
    try {
      const data = await fetchWeatherData(accessToken);

      if (data?.result?.weatherPerHourList) {
        const uniqueWeatherList = [];
        const seenHours = new Set();

        for (const item of data.result.weatherPerHourList) {
          const itemHour = new Date(item.hour).getHours();
          if (!seenHours.has(itemHour)) {
            uniqueWeatherList.push(item);
            seenHours.add(itemHour);
          }
        }

        setWeatherData({
          ...data.result,
          weatherPerHourList: uniqueWeatherList,
        });

        updateButtonBackgroundColor(uniqueWeatherList);
      } else {
        setWeatherData(data.result);
      }
    } catch (error) {
      console.error('Error fetching weather data:', error);
    } finally {
      setIsFetchingWeather(false);
    }
  };

  const isNightTime = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6;
  };

  const updateButtonBackgroundColor = weatherPerHourList => {
    if (!weatherPerHourList || weatherPerHourList.length === 0) {
      setButtonBackgroundColor(isNightTime() ? '#1D2837' : '#3f7dfd');
      return;
    }

    const currentHour = new Date().getHours();
    const closestWeather = weatherPerHourList.reduce((closest, item) => {
      const itemHour = new Date(item.hour).getHours();
      if (
        !closest ||
        Math.abs(itemHour - currentHour) <
          Math.abs(new Date(closest.hour).getHours() - currentHour)
      ) {
        return item;
      }
      return closest;
    }, null);

    if (isNightTime()) {
      setButtonBackgroundColor('#1D2837');
    } else if (
      closestWeather?.rain > 0 ||
      closestWeather?.skyType === 'CLOUDY'
    ) {
      setButtonBackgroundColor('#7998a6');
    } else {
      setButtonBackgroundColor('#3f7dfd');
    }
  };

  // 출석 체크 로직
  const checkAttendanceOncePerDay = async () => {
    const today = getKstToday(); // 한국 시간 기준 오늘 날짜 사용

    try {
      const memberInfo = await fetchMemberInfo(accessToken);
      console.log('Fetched member info:', memberInfo);

      const email = memberInfo?.result?.email;
      if (!email) {
        console.warn('이메일 정보를 가져오지 못했습니다.');
        return;
      }

      const attendanceKey = `lastAttendanceDate_${email}`;
      const lastCheckInDate = await AsyncStorage.getItem(attendanceKey);

      if (lastCheckInDate === today) {
        console.log(`이미 ${email} 계정으로 출석 완료된 날짜입니다:`, today);
        return;
      }

      const result = await checkInAttendance(accessToken);
      if (result?.isSuccess) {
        console.log('출석 체크 성공:', result);
        await AsyncStorage.setItem(attendanceKey, today);
        Alert.alert('출석 완료', '오늘도 출석했습니다!');
      } else {
        throw new Error(result?.message || '출석 실패');
      }
    } catch (error) {
      console.error('출석 체크 실패:', error.message);
      Alert.alert('출석 실패', error.message || '오류가 발생했습니다.');
    }
  };

  const getKstToday = () => {
    const now = new Date();
    const offsetMs = 9 * 60 * 60 * 1000;
    const kstNow = new Date(now.getTime() + offsetMs);
    return kstNow.toISOString().split('T')[0];
  };

  useFocusEffect(
    useCallback(() => {
      fetchWeather();
    }, []),
  );

  useEffect(() => {
    if (refresh) {
      console.log('Refresh HomeScreen');
      fetchWeather();
      setRefresh(false);
    }
  }, [refresh]);

  useEffect(() => {
    checkAttendanceOncePerDay();
  }, []);

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchWeather();
    setRefreshing(false);
  };

  // useFocusEffect(
  //   useCallback(() => {
  //     fetchWeather();
  //   }, []),
  // );

  useEffect(() => {
    const interval = setInterval(() => {
      console.log('Refreshing weather data...');
      fetchWeather();
    }, 300000);
    return () => clearInterval(interval);
  }, [accessToken]);

  return (
    <View style={globalStyles.container}>
      <StatusBar hidden={true} />

      <WeatherHeader
        accessToken={accessToken}
        weatherData={weatherData}
        onToggleChange={setShowText}
        refreshing={refreshing}
      />

      {weatherData ? (
        <ScrollView
          style={styles.scrollView}
          refreshControl={
            <RefreshControl
              refreshing={refreshing}
              onRefresh={onRefresh}
              tintColor="transparent"
              colors={['#3f7dfd', '#ff5733', '#33ff57']}
            />
          }>
          <Posts
            accessToken={accessToken}
            weatherData={weatherData}
            refreshing={refreshing}
          />
          <HourlyForecast
            accessToken={accessToken}
            weatherData={weatherData}
            showText={showText}
            refreshing={refreshing}
          />
          <AirQuality
            accessToken={accessToken}
            weatherData={weatherData}
            refreshing={refreshing}
          />
          <WeatherGraph
            accessToken={accessToken}
            weatherData={weatherData}
            refreshing={refreshing}
          />
          <KakaoShareButton
            accessToken={accessToken}
            weatherData={weatherData}
          />
        </ScrollView>
      ) : (
        <View style={styles.emptyContainer} />
      )}

      <View style={styles.floatingButtonWrapper}>
        <TouchableOpacity
          style={[
            styles.floatingButton,
            {backgroundColor: buttonBackgroundColor},
          ]}
          onPress={() => navigation.navigate('PostCreationScreen')}>
          <Image
            source={require('../../assets/images/icon_pencil.png')}
            style={styles.buttonIcon}
          />
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
  },
  emptyContainer: {
    flex: 1,
  },
  floatingButtonWrapper: {
    position: 'absolute',
    bottom: width * 0.05,
    right: width * 0.05,
    width: width * 0.16,
    height: width * 0.16,
    zIndex: 9999,
  },
  floatingButton: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 999,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.3,
    shadowRadius: 5,
    elevation: 5,
  },
  buttonIcon: {
    width: 25,
    height: 25,
    tintColor: '#FFFFFF',
  },
  bottomGuideDummy: {
    position: 'absolute',
    bottom: height * 0.002,
    left: 0,
    right: 0,
    height: 0,
    zIndex: 9999,
  },
});

export default HomeScreen;
