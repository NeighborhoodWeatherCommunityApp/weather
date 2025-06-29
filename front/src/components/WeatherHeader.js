import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  Dimensions,
  Switch,
  ActivityIndicator,
  Alert,
  PermissionsAndroid,
  Platform,
  Linking,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import LinearGradient from 'react-native-linear-gradient';
import Geolocation from 'react-native-geolocation-service';
import {check, request, PERMISSIONS, RESULTS} from 'react-native-permissions';
import {useRefresh} from '../contexts/RefreshContext';
import {CopilotStep, walkthroughable} from 'react-native-copilot';
import {
  fetchUserLocation,
  fetchWeatherTags,
  sendLocationToBackend,
} from '../api/api';

const {width, height} = Dimensions.get('window');

const aspectRatio = height / width;
const isIpad = aspectRatio < 1.78;

const CopilotView = walkthroughable(View);

const WeatherHeader = ({
  accessToken,
  weatherData,
  onToggleChange,
  refreshing,
}) => {
  const [userLocation, setUserLocation] = useState({city: '', street: ''});
  const [weatherTags, setWeatherTags] = useState([]);
  const [isToggled, setIsToggled] = useState(false);
  const [loadingLocation, setLoadingLocation] = useState(true);
  const [loadingTags, setLoadingTags] = useState(true);
  const [backgroundColors, setBackgroundColors] = useState([
    '#4e9cf5',
    '#498bf5',
    '#3f6be8',
    '#3564e8',
  ]);
  const {setRefresh} = useRefresh();

  const isNightTime = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6;
  };

  const isCloudyOrRainyCondition = () => {
    if (
      !weatherData ||
      !weatherData.weatherPerHourList ||
      weatherData.weatherPerHourList.length === 0
    ) {
      return false;
    }

    const firstItem = weatherData.weatherPerHourList[0];
    return firstItem.skyType === 'CLOUDY' || firstItem.rain > 0;
  };

  const loadLocationData = async () => {
    try {
      setLoadingLocation(true);
      const location = await fetchUserLocation(accessToken);

      setUserLocation({
        city: location?.city || '',
        street: location?.street || '',
      });
    } catch (error) {
      console.error('Error fetching location data:', error.message);
    } finally {
      setLoadingLocation(false);
    }
  };

  const loadTagData = async () => {
    try {
      setLoadingTags(true);
      const tags = await fetchWeatherTags(accessToken);

      setWeatherTags(Array.isArray(tags) ? tags : []);

      const storedState = await AsyncStorage.getItem('switchState');
      if (storedState !== null) {
        const parsedState = JSON.parse(storedState);
        setIsToggled(parsedState);
        onToggleChange(parsedState);
      }
    } catch (error) {
      console.error('Error fetching tag data:', error.message);
    } finally {
      setLoadingTags(false);
    }
  };

  useEffect(() => {
    const updateLocation = async () => {
      if (!accessToken) {
        console.log('Access Token이 필요합니다.');
        return;
      }

      const requestAndCheckPermission = async () => {
        const hasPermission = await requestLocationPermission();

        if (!hasPermission) {
          Alert.alert(
            '위치 권한 필요',
            '현재 위치를 가져오려면 위치 권한이 필요합니다. 앱 설정에서 권한을 허용해 주세요.',
            [
              {text: '취소', style: 'cancel'},
              {text: '설정 열기', onPress: () => Linking.openSettings()},
            ],
          );
          return false;
        }
        return true;
      };

      const permissionGranted = await requestAndCheckPermission();
      if (!permissionGranted) {
        return;
      }

      setLoadingLocation(true);

      Geolocation.getCurrentPosition(
        async position => {
          const {longitude, latitude} = position.coords;

          try {
            const response = await sendLocationToBackend(
              longitude,
              latitude,
              accessToken,
            );

            console.log('Location updated successfully:', response);

            setUserLocation({
              city: response.city || '',
              street: response.street || '',
            });

            loadLocationData();
            loadTagData();
            setRefresh(true);
          } catch (error) {
            console.error('Error sending location to backend:', error.message);
            // Alert.alert('위치 전송 실패', '나중에 다시 시도해 주세요.');
          } finally {
            setLoadingLocation(false);
          }
        },
        error => {
          console.error('Error getting current location:', error.message);
          Alert.alert('위치 확인 실패', '현재 위치를 확인할 수 없습니다.');
          setLoadingLocation(false);
        },
        {enableHighAccuracy: true, timeout: 15000, maximumAge: 10000},
      );
    };

    updateLocation();
  }, [accessToken]);

  useEffect(() => {
    loadLocationData();
    loadTagData();
  }, [accessToken]);

  useEffect(() => {
    if (refreshing) {
      console.log('Refreshing WeatherHeader data...');
      loadLocationData();
      loadTagData();
    }
  }, [refreshing, accessToken, onToggleChange]);

  useEffect(() => {
    if (weatherData) {
      updateBackgroundColors(weatherData);
    }
  }, [weatherData]);

  const updateBackgroundColors = (data = null) => {
    if (isNightTime()) {
      setBackgroundColors(['#405063', '#1D2837', '#161B2C']);
    } else if (data && isCloudyOrRainyCondition()) {
      setBackgroundColors(['#a2c8db', '#8BAEBF', '#7998a6']);
    } else {
      setBackgroundColors(['#4e9cf5', '#498bf5', '#3f6be8', '#3564e8']);
    }
  };

  const handleToggle = async () => {
    const newState = !isToggled;
    setIsToggled(newState);
    onToggleChange(newState);
    await AsyncStorage.setItem('switchState', JSON.stringify(newState));
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

  useEffect(() => {
    loadLocationData();
  }, [accessToken]);

  const requestLocationPermission = async () => {
    if (Platform.OS === 'ios') {
      const status = await check(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
      if (status === RESULTS.DENIED || status === RESULTS.BLOCKED) {
        const result = await request(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
        return result === RESULTS.GRANTED;
      }
      return status === RESULTS.GRANTED;
    } else {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        {
          title: '위치 권한 요청',
          message:
            '앱에서 사용자의 현재 위치를 기반으로 실시간 날씨 정보를 정확하게 제공하기 위해 위치 정보 권한이 필요합니다.',
          buttonNeutral: '나중에',
          buttonNegative: '취소',
          buttonPositive: '허용',
        },
      );
      return granted === PermissionsAndroid.RESULTS.GRANTED;
    }
  };

  if (loadingLocation) {
    return (
      <LinearGradient
        colors={backgroundColors}
        start={{x: 0, y: 0}}
        end={{x: 1, y: 1}}
        style={styles.headerContainer}>
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      </LinearGradient>
    );
  }

  return (
    <LinearGradient
      colors={backgroundColors}
      start={{x: 0, y: 0}}
      end={{x: 1, y: 1}}
      style={styles.headerContainer}>
      <CopilotStep
        text="텍스트로 직관적인 날씨를 볼 수 있어요"
        order={1}
        name="toggle">
        <CopilotView style={styles.switch}>
          <Switch value={isToggled} onValueChange={handleToggle} />
        </CopilotView>
      </CopilotStep>

      <View style={styles.infoContainer}>
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
        <Text style={styles.feelsLike}>
          체감 {weatherData?.currentSensibleTmp?.toFixed(1)}°C
        </Text>
      </View>

      <Image
        source={getWeatherIcon(
          weatherData?.currentSkyType,
          weatherData?.rain || 0,
        )}
        style={styles.weatherIcon}
        resizeMode="contain"
      />

      <CopilotStep
        text="태그로 빠르고 간편하게 날씨를 알 수 있어요"
        order={2}
        name="weatherTags">
        <CopilotView style={styles.tagsContainer}>
          {weatherTags.length > 0 ? (
            weatherTags.map((tag, index) => (
              <View key={index} style={styles.tag}>
                <Text
                  style={styles.tagText}
                  numberOfLines={1}
                  ellipsizeMode="tail">
                  {tag.text}
                </Text>
              </View>
            ))
          ) : (
            <View style={styles.tagNone}>
              <Text style={styles.tagTextNone}>
                게시글을 작성해서 태그를 공유해 주세요!
              </Text>
            </View>
          )}
        </CopilotView>
      </CopilotStep>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    width: width,
    height: isIpad
      ? height * 0.28
      : Platform.OS === 'ios'
      ? height * 0.23
      : height * 0.2,
    paddingVertical: 20,
    paddingHorizontal: 20,
    paddingLeft: width * 0.07,
    borderBottomLeftRadius: 20,
    borderBottomRightRadius: 20,
  },
  switch: {
    marginTop: Platform.OS === 'ios' ? height * 0.03 : height * 0.003,
    position: 'absolute',
    top: width * 0.05,
    right: width * 0.07,
    zIndex: 1,
  },
  infoContainer: {
    marginTop: Platform.OS === 'ios' ? height * 0.03 : height * 0.002,
    marginBottom: 40,
  },
  locationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: Platform.OS === 'ios' ? 5 : 1,
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
    marginTop: 5,
  },
  feelsLike: {
    color: '#fff',
    fontSize: width * 0.04,
    marginTop: Platform.OS === 'ios' ? 5 : -3,
  },
  weatherIcon: {
    position: 'absolute',
    bottom: height * 0.04,
    right: width * 0.05,
    width: width * 0.22,
    height: width * 0.22,
  },
  tagsContainer: {
    position: 'absolute',
    bottom: 10,
    left: 20,
    flexDirection: 'row',
    flexWrap: 'nowrap',
  },
  tagsContainer: {
    position: 'absolute',
    bottom: 10,
    left: 20,
    flexDirection: 'row',
    flexWrap: 'nowrap',
  },
  tag: {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 20,
    paddingVertical: 3,
    paddingHorizontal: 12,
    marginRight: 8,
    marginBottom: 5,
    maxWidth: width * 0.4,
  },
  tagNone: {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 20,
    paddingVertical: 3,
    paddingHorizontal: 12,
    marginRight: 8,
    marginBottom: 5,
  },
  tagText: {
    color: '#fff',
    fontSize: width * 0.031,
    paddingBottom: Platform.OS === 'ios' ? 0 : 3,
    overflow: 'hidden',
  },
  tagTextNone: {
    color: '#fff',
    fontSize: width * 0.031,
    paddingBottom: Platform.OS === 'ios' ? 0 : 3,
    overflow: 'hidden',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default WeatherHeader;
