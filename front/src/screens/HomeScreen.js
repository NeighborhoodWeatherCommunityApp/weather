import React, {useState} from 'react';
import {ScrollView, View, StyleSheet, StatusBar} from 'react-native';
import {PanGestureHandler, State} from 'react-native-gesture-handler';
// import ToggleViewButton from '../components/ToggleViewButton';
import WeatherInfoSlider from '../components/WeatherInfoSlider';
import CurrentLocation from '../components/CurrentLocation';
import TemperatureInfo from '../components/TemperatureInfo';
import PostButton from '../components/PostButton';
import Posts from '../components/Posts';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import {useNavigation} from '@react-navigation/native';

const HomeScreen = () => {
  // const [showText, setShowText] = useState(true);
  const navigation = useNavigation();

  const handleGesture = event => {
    if (
      event.nativeEvent.translationX > 200 &&
      event.nativeEvent.state === State.END
    ) {
      navigation.navigate('PostCreationScreen');
    }
  };

  return (
    <PanGestureHandler
      onGestureEvent={handleGesture}
      onHandlerStateChange={handleGesture}>
      <ScrollView
        style={styles.container}
        contentContainerStyle={styles.contentContainer}>
        <StatusBar hidden={true} />
        <View style={styles.topSpacer} />
        <View style={styles.topContainer}>
          <WeatherInfoSlider />
          <View style={styles.rightContainer}>
            <CurrentLocation />
            <TemperatureInfo />
            <PostButton />
          </View>
        </View>
        {/* <ToggleViewButton showText={showText} setShowText={setShowText} /> */}
        <Posts />
        <HourlyForecast />
        <AirQuality />
        <WeatherGraph />
      </ScrollView>
    </PanGestureHandler>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#2f5af4',
  },
  contentContainer: {
    paddingBottom: 20,
    alignItems: 'center',
  },
  topSpacer: {
    height: 50,
  },
  topContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '95%',
  },
  rightContainer: {
    width: '50%',
    justifyContent: 'space-between',
  },
});

export default HomeScreen;
