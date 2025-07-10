import React, {useState, useEffect, useRef} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Platform,
  Alert,
  Linking,
  TouchableOpacity,
  Dimensions,
  Switch,
  ScrollView,
  AppState,
} from 'react-native';
import {
  fetchAlarmSetting,
  updateAlarmSetting,
  createAlarmSetting,
  sendTestAlarm,
} from '../api/api';
import messaging from '@react-native-firebase/messaging';
import {PermissionsAndroid} from 'react-native';
import {getLatestFcmToken} from '../utils/fcm';
import AsyncStorage from '@react-native-async-storage/async-storage';

const {width, height} = Dimensions.get('window');

const AVAILABLE_TIMES = ['07:00', '12:00', '18:00'];

const TIME_KEY_MAP = {
  '07:00': 'MORNING',
  '12:00': 'AFTERNOON',
  '18:00': 'EVENING',
};
const REVERSE_TIME_KEY_MAP = {
  MORNING: '07:00',
  AFTERNOON: '12:00',
  EVENING: '18:00',
};

const NotificationSettingScreen = ({accessToken}) => {
  const [summaryAlarmTimes, setSummaryAlarmTimes] = useState([]);
  const [agreeTempAlarm, setAgreeTempAlarm] = useState(false);
  const [agreePrecipAlarm, setAgreePrecipAlarm] = useState(false);
  const [agreeDustAlarm, setAgreeDustAlarm] = useState(false);
  const [agreeUvAlarm, setAgreeUvAlarm] = useState(false);
  const [agreeLiveRainAlarm, setAgreeLiveRainAlarm] = useState(false);
  const [permissionGranted, setPermissionGranted] = useState(false);
  const [permissionChecked, setPermissionChecked] = useState(false);
  const [alarmExists, setAlarmExists] = useState(false);
  const appState = useRef(AppState.currentState);

  const loadAlarmSettings = async () => {
    try {
      const fcmToken = await getLatestFcmToken();
      const res = await fetchAlarmSetting(accessToken, fcmToken);
      if (!res) return;

      const {
        agreeTempAlarm,
        agreePrecipAlarm,
        agreeDustAlarm,
        agreeUvAlarm,
        agreeLiveRainAlarm,
        summaryAlarmTimes,
      } = res;

      setAgreeTempAlarm(agreeTempAlarm);
      setAgreePrecipAlarm(agreePrecipAlarm);
      setAgreeDustAlarm(agreeDustAlarm);
      setAgreeUvAlarm(agreeUvAlarm);
      setAgreeLiveRainAlarm(agreeLiveRainAlarm);

      if (Array.isArray(summaryAlarmTimes)) {
        const times = summaryAlarmTimes
          .map(key => REVERSE_TIME_KEY_MAP[key])
          .filter(Boolean);
        setSummaryAlarmTimes(times);
      }

      setAlarmExists(true);
    } catch (error) {
      console.error('알림 설정 불러오기 실패:', error);
    }
  };

  useEffect(() => {
    checkPermission();
    loadAlarmSettings();

    getLatestFcmToken().then(token => {
      console.log('[NotificationSettingScreen] 현재 FCM 토큰:', token);
    });

    const subscription = AppState.addEventListener(
      'change',
      handleAppStateChange,
    );

    const unsubscribeOnMessage = messaging().onMessage(async remoteMessage => {
      const {title, body} = remoteMessage.notification || {};
      Alert.alert(title || '알림 수신', body || '새로운 알림이 도착했습니다.');
    });

    return () => {
      subscription.remove();
      unsubscribeOnMessage();
    };
  }, []);

  const checkPermission = async () => {
    let firebaseGranted = false;
    let androidPermissionGranted = true;

    if (Platform.OS === 'android') {
      // Android 13+에서 POST_NOTIFICATIONS 권한 체크
      if (Platform.Version >= 33) {
        const hasPermission = await PermissionsAndroid.check(
          PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
        );
        androidPermissionGranted = hasPermission;

        // 만약 권한이 없으면 직접 요청
        if (!hasPermission) {
          const granted = await PermissionsAndroid.request(
            PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
          );
          androidPermissionGranted =
            granted === PermissionsAndroid.RESULTS.GRANTED;
        }
      }

      // FCM 등록 허용 상태 확인
      const authStatus = await messaging().hasPermission?.();
      firebaseGranted =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;
    } else {
      // iOS는 Firebase 측 권한 요청 필수
      const authStatus = await messaging().requestPermission();
      firebaseGranted =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;
    }

    const finalPermission =
      Platform.OS === 'android'
        ? androidPermissionGranted && firebaseGranted
        : firebaseGranted;

    setPermissionGranted(finalPermission);
    setPermissionChecked(true);
  };

  const handleAppStateChange = nextAppState => {
    if (
      appState.current.match(/inactive|background/) &&
      nextAppState === 'active'
    ) {
      checkPermission();
    }
    appState.current = nextAppState;
  };

  const toggleTime = time => {
    setSummaryAlarmTimes(prev =>
      prev.includes(time)
        ? prev.filter(t => t !== time)
        : prev.length < 3
        ? [...prev, time]
        : prev,
    );
  };

  const handleConfirm = async () => {
    try {
      if (summaryAlarmTimes.length === 0) {
        Alert.alert(
          '알림 시간 설정 필요',
          '알림 시간을 최소 1개 이상 선택해 주세요.',
        );
        return;
      }

      const nothingChecked =
        !agreeTempAlarm &&
        !agreePrecipAlarm &&
        !agreeDustAlarm &&
        !agreeUvAlarm;

      if (nothingChecked) {
        Alert.alert(
          '알림 내용 설정 필요',
          '알림 내용을 최소 1개 이상 선택해 주세요.',
        );
        return;
      }

      if (!permissionGranted) {
        Alert.alert(
          '알림 권한이 꺼져 있습니다',
          '설정에서 권한을 허용해 주세요.',
          [
            {text: '설정으로 가기', onPress: () => Linking.openSettings()},
            {text: '취소', style: 'cancel'},
          ],
        );
        return;
      }

      const fcmToken = await getLatestFcmToken();
      const formattedTimes = summaryAlarmTimes.map(time => TIME_KEY_MAP[time]);

      const alarmPayload = {
        fcmToken,
        agreeTempAlarm,
        agreePrecipAlarm,
        agreeDustAlarm,
        agreeUvAlarm,
        agreeLiveRainAlarm,
        summaryAlarmTimes: formattedTimes,
      };

      console.log(
        '[NotificationSettingScreen] update/createAlarmSetting body:',
        JSON.stringify(alarmPayload, null, 2),
      );

      console.log('[handleConfirm] alarmExists =', alarmExists);
      console.log(
        '[handleConfirm] 저장된 토큰 =',
        await AsyncStorage.getItem('fcmToken'),
      );
      console.log('[handleConfirm] 현재 토큰   =', fcmToken);

      // if (alarmExists) {
      //   await updateAlarmSetting(accessToken, alarmPayload);
      // } else {
      //   await createAlarmSetting(accessToken, alarmPayload);
      //   setAlarmExists(true);
      // }
      if (alarmExists) {
        try {
          await updateAlarmSetting(accessToken, alarmPayload);
        } catch (err) {
          const code = err?.response?.data?.code;
          if (code === 'ALARM_404_1') {
            console.log('[handleConfirm] 토큰 레코드 없음 → POST 재시도');
            await createAlarmSetting(accessToken, alarmPayload);
          } else {
            throw err;
          }
        }
      } else {
        await createAlarmSetting(accessToken, alarmPayload);
      }

      setAlarmExists(true);

      Alert.alert('알림 설정 완료', '설정이 정상적으로 저장되었습니다.');
    } catch (error) {
      console.error('알림 설정 실패:', error);
    }
  };

  const handleTestAlarm = async () => {
    try {
      const fcmToken = await getLatestFcmToken();
      await sendTestAlarm(accessToken, fcmToken);
      Alert.alert(
        '테스트 알림 발송 완료',
        '기기로 테스트 알림을 전송했습니다.',
      );
    } catch (error) {
      console.error('테스트 알림 실패:', error);
    }
  };

  useEffect(() => {
    const unsubscribe = messaging().onMessage(async remoteMessage => {
      console.log('FCM 메시지 수신:', JSON.stringify(remoteMessage, null, 2));

      const {notification, data} = remoteMessage;

      if (notification) {
        Alert.alert(notification.title || '알림', notification.body || '');
      } else if (data?.title || data?.body) {
        Alert.alert(data.title || '알림', data.body || '');
      } else {
        Alert.alert('알림', '새로운 알림이 도착했습니다.');
      }
    });

    return unsubscribe;
  }, []);

  if (!permissionChecked) return null;

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.scrollContent}>
      <View style={styles.headerRow}>
        <Text style={styles.headerTitle}>알림 설정</Text>
        <Switch
          value={permissionGranted}
          onValueChange={() => {
            Alert.alert(
              '알림 권한 변경',
              '알림 권한 설정을 변경하시려면 설정 앱으로 이동해주세요.',
              [
                {
                  text: '설정으로 가기',
                  onPress: () => Linking.openSettings(),
                },
                {text: '취소', style: 'cancel'},
              ],
            );
          }}
        />
      </View>
      <View style={styles.separatorMy} />

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>알림 시간 설정</Text>
        <Text style={styles.subtext}>
          원하는 시간에 날씨 알림을 보내드려요 (최대 3개 선택)
        </Text>
        <View style={styles.timeContainer}>
          {AVAILABLE_TIMES.map(time => (
            <TouchableOpacity
              key={time}
              onPress={() => toggleTime(time)}
              disabled={!permissionGranted}
              style={[
                styles.timeButton,
                summaryAlarmTimes.includes(time) && styles.timeButtonSelected,
                !permissionGranted && styles.disabled,
              ]}>
              <Text
                style={[
                  styles.timeText,
                  summaryAlarmTimes.includes(time) && styles.timeTextSelected,
                ]}>
                {time}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>알림 내용 설정</Text>
        <Toggle
          label="기온"
          value={agreeTempAlarm}
          onValueChange={setAgreeTempAlarm}
          disabled={!permissionGranted}
        />
        <Toggle
          label="강수 확률"
          value={agreePrecipAlarm}
          onValueChange={setAgreePrecipAlarm}
          disabled={!permissionGranted}
        />
        <Toggle
          label="미세먼지"
          value={agreeDustAlarm}
          onValueChange={setAgreeDustAlarm}
          disabled={!permissionGranted}
        />
        <Toggle
          label="자외선"
          value={agreeUvAlarm}
          onValueChange={setAgreeUvAlarm}
          disabled={!permissionGranted}
        />
      </View>

      <View style={styles.section}>
        <Toggle
          label="게시글 우산 알림"
          value={agreeLiveRainAlarm}
          onValueChange={setAgreeLiveRainAlarm}
          disabled={!permissionGranted}
        />
        <Text style={styles.subtext}>
          우리 동네 사용자가 비가 온다고 공유하면 알려드려요
        </Text>
      </View>

      <TouchableOpacity style={styles.button} onPress={handleConfirm}>
        <Text style={styles.buttonText}>설정 저장</Text>
      </TouchableOpacity>

      {/* <TouchableOpacity
        style={[styles.button, {marginTop: 12}]}
        onPress={handleTestAlarm}>
        <Text style={styles.buttonText}>테스트 알림 전송</Text>
      </TouchableOpacity> */}
    </ScrollView>
  );
};

const Toggle = ({label, value, onValueChange, disabled}) => (
  <View style={styles.toggleRow}>
    <Text style={styles.toggleLabel}>{label}</Text>
    <Switch value={value} onValueChange={onValueChange} disabled={disabled} />
  </View>
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollContent: {
    padding: 20,
    paddingBottom: 40,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: Platform.OS === 'ios' ? height * 0.035 : height * 0.0035,
    marginBottom: 18,
    paddingHorizontal: width * 0.02,
  },
  headerTitle: {
    fontSize: 18,
    textAlign: 'left',
    color: '#333',
    marginTop: Platform.OS === 'ios' ? height * 0.001 : height * 0.001,
  },
  section: {
    backgroundColor: '#F8F9FB',
    borderRadius: 12,
    padding: 16,
    marginBottom: 18,
  },
  sectionTitle: {
    fontSize: 17,
    fontWeight: '600',
    marginBottom: 10,
    color: '#333',
  },
  subtext: {
    fontSize: 13,
    color: '#555',
    marginBottom: 18,
  },
  subnote: {
    fontSize: 12,
    color: '#888',
    marginTop: 6,
  },
  timeContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  timeButton: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 20,
    paddingVertical: 6,
    paddingHorizontal: 16,
    backgroundColor: '#fff',
    marginRight: 10,
    marginBottom: 10,
  },
  timeButtonSelected: {
    backgroundColor: '#2f5af4',
    borderColor: '#2f5af4',
  },
  timeText: {
    color: '#333',
    fontWeight: '500',
  },
  timeTextSelected: {
    color: '#fff',
  },
  toggleRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginVertical: 8,
  },
  toggleLabel: {
    fontSize: 16,
    color: '#333',
  },
  button: {
    // backgroundColor: '#3f51b5',
    backgroundColor: '#2f5af4',
    padding: 14,
    borderRadius: 10,
    alignItems: 'center',
    marginTop: 12,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
  },
  disabled: {
    opacity: 0.4,
  },
  separatorMy: {
    borderBottomWidth: 0.7,
    borderColor: '#E5E5E5',
    marginHorizontal: 3,
    marginTop: -8,
    marginBottom: 20,
  },
});

export default NotificationSettingScreen;
