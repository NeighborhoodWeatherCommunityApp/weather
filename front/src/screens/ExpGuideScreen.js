import React from 'react';
import {
  Text,
  View,
  StyleSheet,
  Image,
  Platform,
  Dimensions,
} from 'react-native';

const {height: SCREEN_HEIGHT} = Dimensions.get('window');

const expLevels = [
  {
    title: '새싹 (Newbie)',
    description: '처음 시작하는 단계입니다.',
    icon: require('../../assets/images/LV1.png'),
  },
  {
    title: '바람 (Breeze)',
    description: '부드러운 바람처럼 성장하고 있습니다.',
    icon: require('../../assets/images/LV2.png'),
  },
  {
    title: '구름 (Cloudy)',
    description: '높이 올라가고 있습니다.',
    icon: require('../../assets/images/LV3.png'),
  },
  {
    title: '비 (Rainy)',
    description: '풍부한 경험을 쌓고 있습니다.',
    icon: require('../../assets/images/LV4.png'),
  },
  {
    title: '번개 (Lightning)',
    description: '빛나는 성과를 보여주고 있습니다.',
    icon: require('../../assets/images/LV5.png'),
  },
  {
    title: '태풍 (Typhoon)',
    description: '최고 수준의 전문가입니다.',
    icon: require('../../assets/images/LV6.png'),
  },
];

const ExpGuideScreen = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.header}>사용자 등급 안내</Text>

      <View style={styles.innerBox}>
        {expLevels.map((level, index) => (
          <View key={index} style={styles.levelRow}>
            <Image source={level.icon} style={styles.icon} />
            <View style={styles.levelTextBox}>
              <Text style={styles.levelTitle}>{level.title}</Text>
              <Text style={styles.levelDesc}>{level.description}</Text>
            </View>
          </View>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9fbfc',
    justifyContent: 'flex-start',
    alignItems: 'center',
    paddingBottom: 30,
    minHeight: SCREEN_HEIGHT,
  },
  header: {
    fontSize: 18,
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
    marginTop:
      Platform.OS === 'ios' ? SCREEN_HEIGHT * 0.075 : SCREEN_HEIGHT * 0.045,
    marginBottom: 40,
  },
  innerBox: {
    width: '90%',
    backgroundColor: '#fff',
    borderRadius: 12,
    paddingVertical: 24,
    paddingHorizontal: 20,
    minHeight: SCREEN_HEIGHT * 0.7,
    justifyContent: 'space-between',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2,
  },
  levelRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 22,
  },
  icon: {
    width: 40,
    height: 40,
    marginRight: 20,
  },
  levelTextBox: {
    flex: 1,
  },
  levelTitle: {
    fontSize: 15,
    fontWeight: '600',
    color: '#222',
    marginBottom: 2,
  },
  levelDesc: {
    fontSize: 13,
    color: '#777',
  },
});

export default ExpGuideScreen;
