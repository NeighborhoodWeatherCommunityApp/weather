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
import WeatherHeader from '../components/WeatherHeader';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import Posts from '../components/Posts';
import KakaoShareButton from '../components/KakaoShareButton';
import globalStyles from '../globalStyles';
import {fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');

const HomeScreen = ({accessToken, navigation}) => {
  const {refresh, setRefresh} = useRefresh();
  const [weatherData, setWeatherData] = useState(null);
  const [showText, setShowText] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [buttonBackgroundColor, setButtonBackgroundColor] = useState('#3f7dfd');

  const fetchWeather = async () => {
    try {
      const data = await fetchWeatherData(accessToken);
      setWeatherData(data.result);
      updateButtonBackgroundColor(data.result?.weatherPerHourList || []);
    } catch (error) {
      console.error('Error fetching weather data:', error);
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

  useEffect(() => {
    if (refresh) {
      console.log('Refresh HomeScreen');
      fetchWeather();
      setRefresh(false);
    }
  }, [refresh]);

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchWeather();
    setRefreshing(false);
  };

  useFocusEffect(
    useCallback(() => {
      fetchWeather();
    }, []),
  );

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
  );
};

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
  },
  emptyContainer: {
    flex: 1,
  },
  floatingButton: {
    position: 'absolute',
    bottom: width * 0.05,
    right: width * 0.05,
    width: width * 0.16,
    height: width * 0.16,
    borderRadius: 999,
    justifyContent: 'center',
    alignItems: 'center',
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
});

export default HomeScreen;
