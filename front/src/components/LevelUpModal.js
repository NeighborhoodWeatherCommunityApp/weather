// src/components/LevelUpModal.js
import React, {useEffect, useRef} from 'react';
import {
  Modal,
  View,
  Text,
  Animated,
  TouchableOpacity,
  StyleSheet,
  Image,
} from 'react-native';
import {useLevelUp} from '../contexts/LevelUpContext';

const LevelUpModal = () => {
  const {levelUpInfo, closeLevelUp} = useLevelUp();
  const scaleAnim = useRef(new Animated.Value(0.5)).current;
  const opacityAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (levelUpInfo) {
      Animated.parallel([
        Animated.timing(scaleAnim, {
          toValue: 1,
          duration: 400,
          useNativeDriver: true,
        }),
        Animated.timing(opacityAnim, {
          toValue: 1,
          duration: 400,
          useNativeDriver: true,
        }),
      ]).start();
    }
  }, [levelUpInfo]);

  if (!levelUpInfo) return null;

  return (
    <Modal visible transparent animationType="fade">
      <View style={styles.overlay}>
        <Animated.View
          style={[
            styles.popup,
            {transform: [{scale: scaleAnim}], opacity: opacityAnim},
          ]}>
          {/*
          <Image
            source={require('../../assets/images/icon_app.png')}  // 필요시 대체
            style={styles.image}
          />
          */}
          <Text style={styles.title}>레벨업</Text>
          <Text style={styles.subtitle}>
            {levelUpInfo.levelTitle} 레벨에 도달했어요 🎉
          </Text>
          <TouchableOpacity style={styles.button} onPress={closeLevelUp}>
            <Text style={styles.buttonText}>닫기</Text>
          </TouchableOpacity>
        </Animated.View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.4)',
  },
  popup: {
    backgroundColor: '#fff',
    borderRadius: 20,
    padding: 30,
    width: 300,
    alignItems: 'center',
  },
  image: {width: 80, height: 80, marginBottom: 10},
  title: {fontSize: 22, fontWeight: 'bold', color: '#2f5af4', marginBottom: 15},
  subtitle: {
    fontSize: 15,
    textAlign: 'center',
    color: '#444',
    marginBottom: 20,
  },
  button: {
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 12,
    backgroundColor: '#2f5af4',
  },
  buttonText: {color: '#fff', fontWeight: '600'},
});

export default LevelUpModal;
