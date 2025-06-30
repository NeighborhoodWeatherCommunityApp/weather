import messaging from '@react-native-firebase/messaging';
import AsyncStorage from '@react-native-async-storage/async-storage';

/**
 * 최신 FCM 토큰을 반환하고, 변경되었으면 AsyncStorage 업데이트
 * @returns {Promise<string|null>}
 */
export const getLatestFcmToken = async () => {
  try {
    await messaging().registerDeviceForRemoteMessages();
    const currentToken = await messaging().getToken();
    const savedToken = await AsyncStorage.getItem('fcmToken');

    if (currentToken && currentToken !== savedToken) {
      await AsyncStorage.setItem('fcmToken', currentToken);
      console.log('[fcm] 새 토큰 저장:', currentToken);
    }

    return currentToken;
  } catch (e) {
    console.error('[fcm] 토큰 가져오기 실패:', e);
    return null;
  }
};
