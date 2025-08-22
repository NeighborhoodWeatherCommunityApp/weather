import {Platform, Alert, PermissionsAndroid, AppState} from 'react-native';
import {
  getMessaging,
  getToken,
  onMessage,
  isSupported,
  getPermission,
  requestPermission,
  AuthorizationStatus,
} from '@react-native-firebase/messaging';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {useEffect, useRef} from 'react';

export const requestUserPermission = async () => {
  const messaging = getMessaging();

  if (Platform.OS === 'ios') {
    const authStatus = await requestPermission(messaging, {
      alert: true,
      badge: false,
      sound: true,
    });

    const enabled =
      authStatus === AuthorizationStatus.AUTHORIZED ||
      authStatus === AuthorizationStatus.PROVISIONAL;

    if (enabled) {
      console.log('iOS 알림 권한 허용됨:', authStatus);
    } else {
      Alert.alert(
        '알림 권한이 꺼져 있습니다',
        '설정에서 푸시 알림을 허용해주세요.',
      );
    }
  } else if (Platform.OS === 'android' && Platform.Version >= 33) {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('Android 알림 권한 허용됨');
    } else {
      Alert.alert(
        '알림 권한이 꺼져 있습니다',
        '설정에서 푸시 알림을 허용해주세요.',
      );
    }
  } else {
    console.log('Android 12 이하 - 알림 권한 요청 불필요');
  }
};

export const getFcmToken = async () => {
  try {
    const messaging = getMessaging();
    await messaging.registerDeviceForRemoteMessages();
    const token = await getToken(messaging);

    if (token) {
      console.log('FCM Token:', token);
      await AsyncStorage.setItem('fcmToken', token);
    } else {
      console.log('토큰이 생성되지 않았습니다.');
    }

    return token;
  } catch (error) {
    console.log('FCM 토큰 요청 실패:', error);
    return null;
  }
};

export const onMessageListener = () => {
  const messaging = getMessaging();
  onMessage(messaging, async remoteMessage => {
    console.log('포그라운드 알림 수신:', remoteMessage);
    Alert.alert(
      remoteMessage.notification.title,
      remoteMessage.notification.body,
    );
  });
};

/* 앱 상태 변경 시 FCM 토큰을 확인하여 변경되었으면 저장만 수행 */
export const useFcmTokenSync = () => {
  const appState = useRef(AppState.currentState);

  useEffect(() => {
    const checkAndUpdateFcmToken = async () => {
      try {
        const messaging = getMessaging();

        let enabled = false;

        if (Platform.OS === 'ios') {
          const authStatus = await messaging.hasPermission();
          enabled = authStatus === 1 || authStatus === 2;
        } else if (Platform.OS === 'android') {
          const granted = await PermissionsAndroid.check(
            PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
          );
          enabled = granted;
        }

        if (!enabled) {
          console.log(
            '알림 권한이 허용되지 않아 FCM 토큰을 요청하지 않습니다.',
          );
          return;
        }

        await messaging.registerDeviceForRemoteMessages();
        const currentToken = await getToken(messaging);
        const storedToken = await AsyncStorage.getItem('fcmToken');

        if (storedToken !== currentToken) {
          console.log('FCM 토큰이 변경되어 저장을 업데이트합니다');
          console.log('새 FCM 토큰:', currentToken);
          await AsyncStorage.setItem('fcmToken', currentToken);
        } else {
          console.log('기존 FCM 토큰과 동일합니다.');
          console.log('기존 FCM 토큰:', storedToken);
        }
      } catch (error) {
        console.log('FCM 토큰 비교 중 오류:', error);
      }
    };

    const subscription = AppState.addEventListener('change', nextAppState => {
      if (
        appState.current.match(/inactive|background/) &&
        nextAppState === 'active'
      ) {
        console.log('앱이 포그라운드로 복귀함, FCM 토큰 확인');
        checkAndUpdateFcmToken();
      }
      appState.current = nextAppState;
    });

    return () => {
      subscription.remove();
    };
  }, []);
};
