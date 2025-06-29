import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  Dimensions,
  ActivityIndicator,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {fetchUserLocation, fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');

const aspectRatio = height / width;
const isIpad = aspectRatio < 1.78;

const WeatherHeaderCommunity = ({accessToken, refreshing, onWeatherData}) => {
  const [userLocation, setUserLocation] = useState({city: '', street: ''});
  const [weatherData, setWeatherData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [backgroundColors, setBackgroundColors] = useState([
    '#4e9cf5',
    '#498bf5',
    '#3f6be8',
    '#3564e8',
  ]);

  const isNightTime = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6;
  };

  const isCloudyOrRainyCondition = data => {
    if (
      !data ||
      !data.weatherPerHourList ||
      data.weatherPerHourList.length === 0
    ) {
      return false;
    }

    const firstItem = data.weatherPerHourList[0];
    return firstItem.skyType === 'CLOUDY' || firstItem.rain > 0;
  };

  const loadData = async () => {
    try {
      const location = await fetchUserLocation(accessToken);
      const weather = await fetchWeatherData(accessToken);

      setUserLocation({
        city: location?.city || '',
        street: location?.street || '',
      });
      setWeatherData(weather?.result || null);
      updateBackgroundColors(weather?.result || null);

      onWeatherData?.(weather?.result);
    } catch (error) {
      console.error('Error fetching data:', error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();

    const interval = setInterval(() => {
      console.log('Refreshing community weather data...');
      loadData();
    }, 300000);

    return () => clearInterval(interval);
  }, [accessToken]);

  useEffect(() => {
    if (refreshing) {
      loadData();
    }
  }, [refreshing]);

  const updateBackgroundColors = data => {
    if (isNightTime()) {
      setBackgroundColors(['#405063', '#1D2837', '#161B2C']);
    } else if (isCloudyOrRainyCondition(data)) {
      setBackgroundColors(['#a2c8db', '#8BAEBF', '#7998a6']);
    } else {
      setBackgroundColors(['#4e9cf5', '#498bf5', '#3f6be8', '#3564e8']);
    }
  };

  const getWeatherIcon = (currentSkyType, rain) => {
    const currentHour = new Date().getHours();
    const isNight = currentHour >= 18 || currentHour < 6;

    if (rain > 0) {
      return isNight
        ? require('../../assets/images/icon_weather_rainNight.png')
        : require('../../assets/images/icon_weather_rain.png');
    }

    if (isNight) {
      switch (currentSkyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_weather_clearNight.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_weather_partlycloudyNight.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_weather_partlycloudyNight.png');
        default:
          return require('../../assets/images/icon_weather_clearNight.png');
      }
    } else {
      switch (currentSkyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_weather_clear.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_weather_partlycloudy.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_weather_cloudy.png');
        default:
          return require('../../assets/images/icon_weather_clear.png');
      }
    }
  };

  if (loading) {
    return (
      // 시간별 배경 색상 확인 필요
      <View style={[styles.headerContainer, {backgroundColor: '#405063'}]}>
        <ActivityIndicator size="large" color="#fff" />
      </View>
    );
  }

  return (
    <LinearGradient
      colors={backgroundColors}
      start={{x: 0, y: 0}}
      end={{x: 1, y: 1}}
      style={styles.headerContainer}>
      <View style={styles.textContainer}>
        <View style={styles.locationContainer}>
          <Image
            source={require('../../assets/images/icon_location.png')}
            style={styles.locationIcon}
          />
          <Text style={styles.location}>
            {userLocation.city} {userLocation.street}
          </Text>
        </View>
        <Text style={styles.temperature}>{weatherData?.currentTmp}°C</Text>
      </View>

      <View style={styles.iconContainer}>
        <Image
          source={getWeatherIcon(
            weatherData?.currentSkyType,
            weatherData?.rain || 0,
          )}
          style={styles.weatherIcon}
          resizeMode="contain"
        />
      </View>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    width: width,
    height: isIpad
      ? height * 0.17
      : Platform.OS === 'ios'
      ? height * 0.15
      : height * 0.13,
    borderBottomLeftRadius: 20,
    borderBottomRightRadius: 20,
    paddingLeft: width * 0.07,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  textContainer: {
    flexDirection: 'column',
    justifyContent: 'center',
  },
  locationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 5,
    marginTop: Platform.OS === 'ios' ? height * 0.04 : height * 0.01,
  },
  locationIcon: {
    tintColor: '#fff',
    width: width * 0.05,
    height: width * 0.05,
    marginRight: 5,
  },
  location: {
    color: '#fff',
    fontSize: width * 0.04,
  },
  temperature: {
    color: '#fff',
    fontSize: width * 0.09,
    fontWeight: 'bold',
  },
  iconContainer: {
    marginTop: Platform.OS === 'ios' ? height * 0.03 : height * 0.01,
    marginRight: height * 0.02,
    width: width * 0.25,
    height: width * 0.25,
    justifyContent: 'center',
    alignItems: 'center',
  },
  weatherIcon: {
    width: '100%',
    height: '100%',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default WeatherHeaderCommunity;
