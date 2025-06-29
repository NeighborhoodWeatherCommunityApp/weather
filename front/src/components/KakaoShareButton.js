import React, {useState, useEffect} from 'react';
import {View, StyleSheet, Alert, Dimensions, Platform} from 'react-native';
import {Button} from 'react-native-elements';
import KakaoShareLink from 'react-native-kakao-share-link';
import {
  fetchWeatherData,
  fetchRainForecast,
  rewardKakaoShare,
} from '../api/api';

const {width} = Dimensions.get('window');

const KakaoShareButton = ({accessToken}) => {
  const [weatherInfo, setWeatherInfo] = useState(null);
  const [rainComment, setRainComment] = useState('');

  useEffect(() => {
    const fetchWeather = async () => {
      try {
        const data = await fetchWeatherData(accessToken);
        if (data.result) {
          setWeatherInfo(data.result);
        }
      } catch (error) {
        console.error('날씨 정보 가져오기 오류:', error);
      }
    };

    const fetchRain = async () => {
      try {
        const rainData = await fetchRainForecast(accessToken);
        console.log('Fetched rain forecast:', rainData);
        setRainComment(rainData.rainComment || '');
      } catch (error) {
        console.error('비 예보 정보 가져오기 오류:', error);
      }
    };

    fetchWeather();
    fetchRain();
  }, [accessToken]);

  const getWeatherEmoji = skyType => {
    switch (skyType) {
      case 'CLEAR':
        return '☀️';
      case 'PARTLYCLOUDY':
        return '⛅️';
      case 'CLOUDY':
        return '☁️';
      default:
        return '🌈';
    }
  };

  const shareWeatherInfo = async () => {
    if (!weatherInfo) {
      Alert.alert(
        '오류',
        '날씨 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.',
      );
      return;
    }

    const {city, street, currentTmp, currentSkyType, temperature} = weatherInfo;
    const {maxTmp, minTmp} = temperature;
    const weatherEmoji = getWeatherEmoji(currentSkyType);

    try {
      // 공유 요청 먼저 실행
      const response = await KakaoShareLink.sendFeed({
        content: {
          title: `${weatherEmoji} ${city} ${street} 날씨입니다!`,
          description: `현재 ${currentTmp}°C   (↑)${maxTmp}° (↓)${minTmp}°\n${rainComment} ${weatherEmoji}`,
          imageUrl:
            'https://raw.githubusercontent.com/NeighborhoodWeatherCommunityApp/weather/main/front/assets/images/icon_app.png',
          link: {
            mobileWebUrl: 'https://링크 추가.com',
            webUrl: 'https://링크 추가.com',
          },
        },
        buttons: [
          {
            title: '날씨톡톡 앱에서 확인해 보세요.',
            link: {
              mobileWebUrl: 'https://링크 추가.com',
              webUrl: 'https://링크 추가.com',
            },
          },
        ],
      });

      // 공유 성공 여부와 관계없이 API 호출
      const rewardResult = await rewardKakaoShare(accessToken);

      if (rewardResult?.isSuccess) {
      } else {
        console.warn(
          '카카오 공유 경험치 지급 실패:',
          rewardResult?.message || '응답 없음',
        );
      }
    } catch (error) {
      console.error('공유 또는 경험치 API 호출 오류:', error?.message || error);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.shadowContainer}>
        <Button
          title="카카오톡 친구에게 날씨 공유하기"
          onPress={shareWeatherInfo}
          buttonStyle={styles.kakaoButton}
          titleStyle={styles.kakaoTitle}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    marginTop: 5,
    marginBottom: 30,
  },
  shadowContainer: {
    width: width * 0.94,
    borderRadius: 8,
    backgroundColor: '#fff',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  gradientButton: {
    borderRadius: 8,
    padding: 1,
  },
  kakaoButton: {
    backgroundColor: '#FEE500',
    borderRadius: 8,
    height: 50,
    justifyContent: 'center',
  },
  kakaoTitle: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#3C1E1E',
  },
});

export default KakaoShareButton;
