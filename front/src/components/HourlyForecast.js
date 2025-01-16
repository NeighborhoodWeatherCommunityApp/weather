import React, {useEffect, useState, useMemo} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  Dimensions,
  ActivityIndicator,
  Platform,
} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';

const {width, height} = Dimensions.get('window');
const aspectRatio = height / width;

const HourlyForecast = ({weatherData, showText, refreshing}) => {
  const [hourlyData, setHourlyData] = useState([]);
  const [loading, setLoading] = useState(true);

  const marginTop = useMemo(() => {
    return aspectRatio < 2.1 ? -width * 0.01 : -width * 0.01;
  }, []);

  const loadData = () => {
    if (
      weatherData?.weatherPerHourList &&
      weatherData.weatherPerHourList.length > 0
    ) {
      setHourlyData(weatherData.weatherPerHourList);
      console.log('Hourly Weather data:', JSON.stringify(weatherData, null, 2));
    } else {
      setHourlyData([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    if (weatherData) {
      loadData();
    }
  }, [weatherData, refreshing]);

  const formatHour = isoString => {
    const date = new Date(isoString);
    const hours = date.getHours().toString().padStart(2, '0');
    return `${hours}시`;
  };

  const isNightTime = hour => {
    return hour >= 18 || hour < 6;
  };

  const getWeatherIcon = (skyType, rain, hour) => {
    if (rain > 0) {
      return require('../../assets/images/icon_weather_rain.png');
    }

    const isNight = isNightTime(hour);

    if (isNight) {
      switch (skyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_weather_clearNight.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_weather_partlycloudyNight.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_weather_partlycloudyNight.png');
        default:
          return require('../../assets/images/icon_default.png');
      }
    } else {
      switch (skyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_weather_clear.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_weather_partlycloudy.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_weather_cloudy.png');
        default:
          return require('../../assets/images/icon_default.png');
      }
    }
  };

  const placeholderData = Array(8).fill({});

  return (
    <ScrollView
      horizontal
      contentContainerStyle={[
        styles.scrollContent,
        {paddingRight: width * 0.025},
        {alignItems: 'center'},
      ]}
      style={[styles.container, {marginRight: 0}, {marginTop}]}
      showsHorizontalScrollIndicator={false}
      showsVerticalScrollIndicator={false}
      nestedScrollEnabled={false}
      bounces={true}
      scrollEnabled={true}>
      {(loading ? placeholderData : hourlyData || []).map((item, i) => (
        <View key={i} style={styles.shadowContainer}>
          <Card
            containerStyle={[styles.card, globalStyles.transparentBackground]}>
            <View style={styles.content}>
              {loading ? (
                <ActivityIndicator size="small" color="#999999" />
              ) : item ? (
                <>
                  <Text style={styles.textTime}>{formatHour(item.hour)}</Text>

                  <Image
                    source={getWeatherIcon(
                      item.skyType,
                      item.rain,
                      new Date(item.hour).getHours(),
                    )}
                    style={styles.icon}
                  />

                  <View style={styles.placeholder}>
                    {showText && item.tmpAdverb ? (
                      <Text style={styles.adverbText}>{item.tmpAdverb}</Text>
                    ) : null}
                  </View>

                  <Text style={styles.tmpText}>
                    {showText ? item.tmpText : `${item.tmp}°C`}
                  </Text>

                  <View style={styles.placeholder}>
                    {showText && item.rainAdverb ? (
                      <Text style={styles.rainAdverb}>{item.rainAdverb}</Text>
                    ) : null}
                  </View>

                  <Text style={styles.rainText}>
                    {showText ? item.rainText : `${item.rain}mm`}
                  </Text>
                </>
              ) : null}
            </View>
          </Card>
        </View>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingRight: 0,
    marginRight: 0,
  },
  scrollContent: {
    paddingRight: 10,
    marginRight: 0,
  },
  shadowContainer: {
    marginLeft: width * 0.025,
    marginVertical: 10,
    backgroundColor: '#fff',
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: {width: 1, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  card: {
    borderRadius: 12,
    borderColor: '#fff',
    padding: -30,
    width: width * 0.13,
    height: Platform.OS === 'ios' ? 140 : 150,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  content: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  placeholder: {
    height: 12,
    justifyContent: 'center',
  },
  textTime: {
    fontSize: 14,
    color: '#333',
    marginBottom: -10,
    fontWeight: '600',
    textAlign: 'center',
    marginTop: -20,
  },
  icon: {
    width: 45,
    height: 45,
    marginBottom: -5,
    marginTop: 10,
  },
  rainAdverb: {
    fontSize: 11,
    marginBottom: -13,
    textAlign: 'center',
  },
  adverbText: {
    fontSize: 12,
    marginBottom: Platform.OS === 'ios' ? -5 : -8,
    textAlign: 'center',
    marginTop: 5,
  },
  tmpText: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: -4,
    marginTop: 5,
    textAlign: 'center',
  },
  rainText: {
    fontSize: 13,
    color: '#555',
    textAlign: 'center',
    marginTop: 5,
    marginBottom: -5,
  },
  loadingText: {
    fontSize: 12,
    color: '#999',
    marginTop: 5,
    textAlign: 'center',
  },
});

export default HourlyForecast;
