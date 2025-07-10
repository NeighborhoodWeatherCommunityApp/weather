import React, {useRef, useState} from 'react';
import {
  View,
  Image,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
  FlatList,
  StatusBar,
  Text,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const {width, height} = Dimensions.get('window');

const IMAGES = [
  require('../../assets/images/onboarding/step1.png'),
  require('../../assets/images/onboarding/step2.png'),
  require('../../assets/images/onboarding/step3.png'),
  require('../../assets/images/onboarding/step4.png'),
  require('../../assets/images/onboarding/step5.png'),
];

const OnboardingScreen = ({onFinish}) => {
  const [index, setIndex] = useState(0);
  const listRef = useRef(null);

  const completeOnboarding = async () => {
    await AsyncStorage.setItem('homeTutorialCompleted', 'true');
    onFinish();
  };

  const handleNext = () => {
    if (index < IMAGES.length - 1) {
      listRef.current?.scrollToIndex({index: index + 1});
    } else {
      completeOnboarding();
    }
  };

  const onViewableItemsChanged = useRef(({viewableItems}) => {
    if (viewableItems[0]) setIndex(viewableItems[0].index);
  }).current;

  return (
    <View style={styles.container}>
      <StatusBar hidden />

      {/* Skip 버튼 */}
      <TouchableOpacity style={styles.skipBtn} onPress={completeOnboarding}>
        <Text style={styles.skipText}>건너뛰기</Text>
      </TouchableOpacity>

      {/* 이미지 슬라이드 */}
      <FlatList
        ref={listRef}
        data={IMAGES}
        keyExtractor={(_, i) => String(i)}
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        onViewableItemsChanged={onViewableItemsChanged}
        viewabilityConfig={{itemVisiblePercentThreshold: 50}}
        renderItem={({item}) => (
          <Image source={item} style={styles.image} resizeMode="cover" />
        )}
      />

      {/* 인디케이터 + 다음 버튼 */}
      <View style={styles.bottomArea}>
        <View style={styles.indicatorContainer}>
          {IMAGES.map((_, i) => (
            <View
              key={i}
              style={[styles.indicator, index === i && styles.activeIndicator]}
            />
          ))}
        </View>

        <TouchableOpacity style={styles.nextBtn} onPress={handleNext}>
          <Text style={styles.nextText}>
            {index === IMAGES.length - 1 ? '시작하기' : '다음'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: '#fff'},
  image: {width, height},
  skipBtn: {
    position: 'absolute',
    top: 50,
    right: 25,
    zIndex: 10,
    padding: 8,
  },
  skipText: {color: '#fff', fontSize: 14},
  bottomArea: {
    position: 'absolute',
    bottom: 60,
    width,
    paddingHorizontal: 24,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  indicatorContainer: {flexDirection: 'row'},
  indicator: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: 'rgba(255,255,255,0.3)',
    marginHorizontal: 4,
  },
  activeIndicator: {backgroundColor: 'rgba(255,255,255,1)'},
  nextBtn: {
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 20,
    backgroundColor: 'rgba(255,255,255,0.15)',
  },
  nextText: {color: '#fff', fontSize: 14},
});

export default OnboardingScreen;
