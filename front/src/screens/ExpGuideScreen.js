import React from 'react';
import {
  ScrollView,
  SafeAreaView,
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
    title: '쌔싹',
    level: '1, → 0 exp',
    description: '처음 시작하는 단계입니다.',
    icon: require('../../assets/images/LV1.png'),
  },
  {
    title: '바람',
    level: '2 → 100 exp',
    description: '부드러운 바람처럼 성장하고 있습니다.',
    icon: require('../../assets/images/LV2.png'),
  },
  {
    title: '구름',
    level: '3 → 1000 exp',
    description: '높이 올라가고 있습니다.',
    icon: require('../../assets/images/LV3.png'),
  },
  {
    title: '비',
    level: '4 → 5000 exp',
    description: '풍부한 경험을 쌓고 있습니다.',
    icon: require('../../assets/images/LV4.png'),
  },
  {
    title: '번개',
    level: '5 → 10000 exp',
    description: '빛나는 성과를 보여주고 있습니다.',
    icon: require('../../assets/images/LV5.png'),
  },
  {
    title: '태풍',
    level: 'Max → 20000 exp',
    description: '최고 수준의 전문가입니다.',
    icon: require('../../assets/images/LV6.png'),
  },
];

const ExpGuideScreen = () => {
  return (
    <SafeAreaView style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}>
        <View style={styles.pageWrapper}>
          <Text style={styles.header}>사용자 등급 안내</Text>

          <View style={styles.innerBox}>
            {expLevels.map((level, index) => (
              <View key={index} style={styles.levelRow}>
                <Image source={level.icon} style={styles.icon} />
                <View style={styles.levelTextBox}>
                  <Text style={styles.levelTitle}>
                    {level.title}{' '}
                    <Text style={styles.levelSubText}>(Lv. {level.level})</Text>
                  </Text>
                  <Text style={styles.levelDesc}>{level.description}</Text>
                </View>
              </View>
            ))}
          </View>

          <View style={styles.expRuleBox}>
            <Text style={styles.expRuleHeader}>경험치 획득 및 감소 규칙</Text>

            <View style={styles.ruleGroup}>
              <Text style={styles.ruleItem}>출석 체크 +1 exp</Text>
              <Text style={styles.ruleItem}>게시글 작성 +10 exp</Text>
              <Text style={styles.ruleItem}>좋아요 클릭 +1 exp</Text>
              <Text style={styles.ruleItem}>좋아요 받음 +3 exp</Text>
              <Text style={styles.ruleItem}>카카오톡 공유 +5 exp</Text>
            </View>

            <View style={styles.ruleGroup}>
              <Text style={styles.ruleItem}>
                일주일 연속 출석 체크 시 +15 exp
              </Text>
              <Text style={styles.ruleItem}>
                일주일간 아무런 활동이 없을 경우 -5 exp
              </Text>
            </View>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9fbfc',
  },
  scrollContent: {
    paddingBottom: 40,
  },
  pageWrapper: {
    width: '90%',
    alignSelf: 'center',
  },
  header: {
    fontSize: 18,
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
    marginTop:
      Platform.OS === 'ios' ? SCREEN_HEIGHT * 0.01 : SCREEN_HEIGHT * 0.025,
    marginBottom: 30,
  },
  innerBox: {
    width: '100%',
    backgroundColor: '#fff',
    borderRadius: 12,
    paddingTop: 30,
    paddingBottom: 10,
    paddingHorizontal: 20,
    justifyContent: 'space-between',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2,
    marginBottom: 20,
  },
  levelRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 35,
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
  levelSubText: {
    fontSize: 13,
    color: '#999',
    fontWeight: '400',
  },
  levelDesc: {
    fontSize: 13,
    color: '#777',
  },
  expRuleBox: {
    width: '100%',
    backgroundColor: '#fff',
    borderRadius: 12,
    paddingVertical: 20,
    paddingHorizontal: 20,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.06,
    shadowRadius: 3,
    elevation: 1,
  },
  expRuleHeader: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 14,
    color: '#333',
  },
  ruleGroup: {
    marginBottom: 14,
  },
  ruleItem: {
    fontSize: 13,
    color: '#555',
    marginBottom: 6,
  },
});

export default ExpGuideScreen;
