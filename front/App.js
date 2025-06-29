import React, {useState, useEffect} from 'react';
import {RefreshProvider} from './src/contexts/RefreshContext';
import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createStackNavigator} from '@react-navigation/stack';
import HomeScreen from './src/screens/HomeScreen';
import CommunityScreen from './src/screens/CommunityScreen';
import MyScreen from './src/screens/MyScreen';
import TermsAgreementScreen from './src/screens/TermsAgreementScreen';
import RegisterProfileScreen from './src/screens/RegisterProfileScreen';
import PostCreationScreen from './src/screens/PostCreationScreen';
import TestLoginScreen from './src/screens/TestLoginScreen';
import LoginScreen from './src/screens/LoginScreen';
import ProfileScreen from './src/screens/ProfileScreen';
import NotificationSettingScreen from './src/screens/NotificationSettingScreen';
import TermsViewScreen from './src/screens/TermsViewScreen';
import ExpGuideScreen from './src/screens/ExpGuideScreen';
import {
  StatusBar,
  Image,
  Platform,
  View,
  StyleSheet,
  AppState,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {
  requestUserPermission,
  getFcmToken,
  onMessageListener,
} from './src/firebase/pushNotification';
import {useFcmTokenSync} from './src/firebase/pushNotification';
import {LevelUpProvider} from './src/contexts/LevelUpContext';
import LevelUpModal from './src/components/LevelUpModal';
import {CopilotProvider} from 'react-native-copilot';
import {refreshAccessToken, fetchMemberInfo} from './src/api/api';
import {logUserAction} from './src/api/googleSheetLogger';
import {GOOGLE_ACCESS_TOKEN, GOOGLE_REFRESH_TOKEN} from '@env';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const AuthStack = ({
  setIsLoggedIn,
  setAccessToken,
  setIsNewMember,
  setIsDeleted,
  setIsProfileCompleted,
  isProfileCompleted,
}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="LoginScreen">
      {props => (
        <LoginScreen
          {...props}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsNewMember={setIsNewMember}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
          isProfileCompleted={isProfileCompleted}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="TestLoginScreen">
      {props => (
        <TestLoginScreen
          {...props}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="TermsAgreementScreen">
      {props => (
        <TermsAgreementScreen
          {...props}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsNewMember={setIsNewMember}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="RegisterProfileScreen">
      {props => (
        <RegisterProfileScreen
          {...props}
          setIsNewMember={setIsNewMember}
          setIsLoggedIn={setIsLoggedIn}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
        />
      )}
    </Stack.Screen>
  </Stack.Navigator>
);

const HomeStack = ({accessToken}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="HomeScreen">
      {props => <HomeScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
    <Stack.Screen name="PostCreationScreen">
      {props => <PostCreationScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
    <Stack.Screen name="TestLoginScreen" component={TestLoginScreen} />
  </Stack.Navigator>
);

const MyStack = ({
  accessToken,
  setIsNewMember,
  setLocationId,
  setIsLoggedIn,
  setAccessToken,
  setIsDeleted,
  setIsProfileCompleted,
}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="MyScreen">
      {props => (
        <MyScreen
          {...props}
          accessToken={accessToken}
          setIsNewMember={setIsNewMember}
          setLocationId={setLocationId}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="ExpGuideScreen" component={ExpGuideScreen} />
    <Stack.Screen name="ProfileScreen">
      {props => <ProfileScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
    <Stack.Screen name="NotificationSettingScreen">
      {props => (
        <NotificationSettingScreen {...props} accessToken={accessToken} />
      )}
    </Stack.Screen>
    <Stack.Screen name="TermsViewScreen" component={TermsViewScreen} />
  </Stack.Navigator>
);

const extractTokens = raw => {
  if (!raw) return {};
  if (raw.accessToken || raw.refreshToken) return raw; // camelCase
  if (raw.result) return raw.result; // { result: { … } }
  return {
    accessToken: raw.access_token,
    refreshToken: raw.refresh_token,
  }; // snake_case
};

const App = () => {
  useFcmTokenSync();

  // useEffect(() => {
  //   if (Platform.OS !== 'android') return;

  //   const clearBadge = async () => {
  //     try {
  //       await notifee.cancelAllNotifications(); // 알림 모두 취소
  //       await notifee.setBadgeCount(0); // 배지 카운트 0
  //     } catch (e) {
  //       console.warn('알림 초기화 실패', e);
  //     }
  //   };

  //   clearBadge(); // 콜드스타트 시 실행
  //   const sub = AppState.addEventListener('change', state => {
  //     if (state === 'active') clearBadge(); // 포그라운드 복귀 시
  //   });
  //   return () => sub.remove();
  // }, []);

  const verticalOffset =
    Platform.OS === 'android' ? StatusBar.currentHeight ?? 0 : 0;

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isAutoLoggingIn, setIsAutoLoggingIn] = useState(true);
  const [accessToken, setAccessToken] = useState(null);
  const [isNewMember, setIsNewMember] = useState(false);
  const [locationId, setLocationId] = useState(null);
  const [isDeleted, setIsDeleted] = useState(false);
  const [isProfileCompleted, setIsProfileCompleted] = useState(true);

  // useEffect(() => {
  //   const checkLoginStatus = async () => {
  //     try {
  //       const storedAccessToken = await AsyncStorage.getItem('accessToken');
  //       const refreshToken = await AsyncStorage.getItem('refreshToken');
  //       const loginMethod = await AsyncStorage.getItem('loginMethod');

  //       console.log('Retrieved accessToken:', storedAccessToken);
  //       console.log('Retrieved refreshToken:', refreshToken);
  //       console.log('Login method:', loginMethod);

  //       if (storedAccessToken) {
  //         try {
  //           const memberInfoResponse = await fetchMemberInfo(storedAccessToken);
  //           // console.log('회원 정보 응답:', memberInfoResponse);

  //           if (memberInfoResponse.isSuccess) {
  //             const memberData = memberInfoResponse;
  //             // console.log('회원 정보:', memberData);

  //             setAccessToken(storedAccessToken);
  //             setIsLoggedIn(true);

  //             await logUserAction(memberData.result, 'app_open'); // 앱 진입 로그 기록
  //           } else {
  //             console.error(
  //               'Failed to fetch member info:',
  //               memberInfoResponse.message,
  //             );
  //             setIsLoggedIn(false);
  //           }
  //         } catch (error) {
  //           console.error('Error fetching member info:', error.message);
  //           setIsLoggedIn(false);
  //         }
  //       }

  //       if (!storedAccessToken && refreshToken) {
  //         try {
  //           const newAccessToken = await refreshAccessToken(refreshToken);
  //           const memberInfoResponse = await fetchMemberInfo(newAccessToken);

  //           if (memberInfoResponse.isSuccess) {
  //             const memberData = memberInfoResponse.result;
  //             console.log('Access token 갱신 및 회원 정보:', memberData);

  //             setAccessToken(newAccessToken);
  //             setIsLoggedIn(true);
  //             await AsyncStorage.setItem('accessToken', newAccessToken);

  //             await logUserAction(memberData.result, 'app_open'); // 앱 진입 로그 기록
  //           } else {
  //             console.error(
  //               'Failed to fetch member info with new token:',
  //               memberInfoResponse.message,
  //             );
  //             setIsLoggedIn(false);
  //           }
  //         } catch (error) {
  //           console.error('Token refresh failed:', error.message);
  //           await AsyncStorage.removeItem('accessToken');
  //           await AsyncStorage.removeItem('refreshToken');
  //           setIsLoggedIn(false);
  //         }
  //       }
  //     } catch (error) {
  //       console.error('자동 로그인 처리 중 오류 발생:', error.message);
  //       setIsLoggedIn(false);
  //     } finally {
  //       setIsAutoLoggingIn(false);
  //     }
  //   };

  //   checkLoginStatus();
  // }, []);

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const storedAccessToken = await AsyncStorage.getItem('accessToken');
        const refreshToken = await AsyncStorage.getItem('refreshToken');
        const loginMethod = await AsyncStorage.getItem('loginMethod');

        console.log('Retrieved accessToken:', storedAccessToken);
        console.log('Retrieved refreshToken:', refreshToken);
        console.log('Login method:', loginMethod);

        let validAccessToken = storedAccessToken;

        // 1. access token으로 회원 정보 요청 시도
        let memberInfoResponse = null;
        if (storedAccessToken) {
          try {
            memberInfoResponse = await fetchMemberInfo(storedAccessToken);
            if (!memberInfoResponse.isSuccess)
              throw new Error('access token expired or invalid');
          } catch (err) {
            console.warn(
              'Stored access token 사용 실패, refresh token으로 재발급 시도:',
              err.message,
            );
            validAccessToken = null;
          }
        }

        // // 2. access token이 없고 refresh token이 있다면 → 재발급 시도
        // if (!validAccessToken && refreshToken) {
        //   try {
        //     const refreshed = await refreshAccessToken(refreshToken);
        //     const {accessToken: newAccessToken, refreshToken: newRefreshToken} =
        //       refreshed;

        //     await AsyncStorage.setItem('accessToken', newAccessToken);
        //     if (newRefreshToken) {
        //       await AsyncStorage.setItem('refreshToken', newRefreshToken);
        //     } else {
        //       console.warn(
        //         'refreshToken이 응답에 없음, 기존 refreshToken 유지',
        //       );
        //     }

        //     validAccessToken = newAccessToken;
        //     memberInfoResponse = await fetchMemberInfo(newAccessToken);
        //     if (!memberInfoResponse.isSuccess)
        //       throw new Error('refresh 후에도 회원 정보 실패');
        //   } catch (err) {
        //     console.error('refresh token 사용 실패:', err.message);
        //     await AsyncStorage.removeItem('accessToken');
        //     await AsyncStorage.removeItem('refreshToken');
        //     setIsLoggedIn(false);
        //     setIsAutoLoggingIn(false);
        //     return;
        //   }
        // }

        // 2. access token이 없고 refresh token이 있다면 → 재발급 시도
        if (!validAccessToken && refreshToken) {
          try {
            const refreshedRaw = await refreshAccessToken(refreshToken);
            const {accessToken: newAccessToken, refreshToken: newRefreshToken} =
              extractTokens(refreshedRaw);

            // 새 accessToken 저장
            await AsyncStorage.setItem('accessToken', newAccessToken);

            // rolling-refresh: 새 refreshToken이 오면 반드시 덮어쓰기
            if (newRefreshToken) {
              await AsyncStorage.setItem('refreshToken', newRefreshToken);
              console.log('새 refreshToken 저장 완료');
            } else {
              console.warn(
                '응답에 새 refreshToken이 없어 기존 것을 유지합니다.',
              );
            }

            validAccessToken = newAccessToken;
            memberInfoResponse = await fetchMemberInfo(newAccessToken);
            if (!memberInfoResponse.isSuccess)
              throw new Error('refresh 후에도 회원 정보 실패');
          } catch (err) {
            console.error('refresh token 사용 실패:', err.message);
            await AsyncStorage.removeItem('accessToken');
            await AsyncStorage.removeItem('refreshToken');
            setIsLoggedIn(false);
            setIsAutoLoggingIn(false);
            return;
          }
        }

        // 3. 유효한 access token으로 로그인 유지
        if (validAccessToken && memberInfoResponse?.isSuccess) {
          const memberData = memberInfoResponse.result;
          setAccessToken(validAccessToken);
          setIsLoggedIn(true);
          await logUserAction(memberData, 'app_open');
        } else {
          console.log('최종적으로 유효한 토큰 없음, 로그인 필요');
          setIsLoggedIn(false);
        }
      } catch (error) {
        console.error('자동 로그인 처리 중 오류 발생:', error.message);
        setIsLoggedIn(false);
      } finally {
        setIsAutoLoggingIn(false);
      }
    };

    checkLoginStatus();
  }, []);

  // // Google Sheets
  // useEffect(() => {
  //   const injectPlaygroundTokensOnce = async () => {
  //     const existing = await AsyncStorage.getItem('googleRefreshToken');
  //     if (!existing) {
  //       // 토큰 재발급 받은 경우 수동 저장
  //       await AsyncStorage.setItem('accessTokenForSheets', GOOGLE_ACCESS_TOKEN);
  //       await AsyncStorage.setItem('googleRefreshToken', GOOGLE_REFRESH_TOKEN);
  //       console.log('New Playground 토큰 저장 완료');
  //     }
  //   };

  //   injectPlaygroundTokensOnce();
  // }, []);

  // // // Google Sheets - 토큰 제거용
  // useEffect(() => {
  //   const resetPlaygroundTokens = async () => {
  //     await AsyncStorage.removeItem('accessTokenForSheets');
  //     await AsyncStorage.removeItem('googleRefreshToken');
  //     console.log('기존 토큰 제거 완료, 다음 실행 시 .env 값 반영');
  //   };

  //   resetPlaygroundTokens();
  // }, []);

  // Google Sheets
  // 토큰이 없으면 .env 값을 저장, 이미 있으면 그대로 두기
  useEffect(() => {
    const ensureSheetsTokens = async () => {
      const [[, access], [, refresh]] = await AsyncStorage.multiGet([
        'accessTokenForSheets',
        'googleRefreshToken',
      ]);

      if (!access || !refresh) {
        await AsyncStorage.multiSet([
          ['accessTokenForSheets', GOOGLE_ACCESS_TOKEN],
          ['googleRefreshToken', GOOGLE_REFRESH_TOKEN],
        ]);
        console.log('Google Sheets 토큰 초기화 완료');
      } else {
        console.log('Google Sheets 토큰 이미 존재, 초기화 생략');
      }
    };

    ensureSheetsTokens();
  }, []);

  useEffect(() => {
    const initPushNotification = async () => {
      await requestUserPermission();
      const fcmToken = await getFcmToken();

      if (fcmToken) {
        console.log('토큰 정상 수신 완료');
      } else {
        console.log('토큰을 받아오지 못함');
      }

      onMessageListener();
    };

    // initPushNotification();
  }, []);

  useEffect(() => {
    console.log(
      `현재 상태: isLoggedIn=${isLoggedIn}, isNewMember=${isNewMember}, isDeleted=${isDeleted}, isProfileCompleted=${isProfileCompleted}`,
    );
  }, [isLoggedIn, isNewMember, isDeleted, isProfileCompleted]);

  useEffect(() => {
    if (!isNewMember && isLoggedIn && isProfileCompleted) {
      console.log('Navigating to HomeStack as isNewMember is false');
    }

    (async () => {
      await AsyncStorage.removeItem('isProfileCompleted');
      console.log('isProfileCompleted has been removed from AsyncStorage');
    })();
  }, [isNewMember, isLoggedIn, isDeleted, isProfileCompleted]);

  if (isAutoLoggingIn) {
    return <View style={styles.autoLoginBackground} />;
  }

  if (isDeleted || !isLoggedIn || !isProfileCompleted) {
    return (
      <NavigationContainer>
        <AuthStack
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsNewMember={setIsNewMember}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
          isProfileCompleted={isProfileCompleted}
        />
      </NavigationContainer>
    );
  }

  return (
    <>
      <CopilotProvider
        verticalOffset={verticalOffset}
        tooltipStyle={{borderRadius: 10}}
        overlay="svg"
        animated
        onStop={() => AsyncStorage.setItem('homeTutorialCompleted', 'true')}
        onFinish={() => AsyncStorage.setItem('homeTutorialCompleted', 'true')}>
        <LevelUpProvider>
          <RefreshProvider>
            <StatusBar
              translucent
              backgroundColor="transparent"
              barStyle="dark-content"
            />
            <NavigationContainer>
              {isLoggedIn ? (
                isNewMember ? (
                  <Stack.Navigator screenOptions={{headerShown: false}}>
                    <Stack.Screen name="RegisterProfileScreen">
                      {props => (
                        <RegisterProfileScreen
                          {...props}
                          accessToken={accessToken}
                          setIsNewMember={setIsNewMember}
                          setIsLoggedIn={setIsLoggedIn}
                          setIsProfileCompleted={setIsProfileCompleted}
                          setIsDeleted={setIsDeleted}
                        />
                      )}
                    </Stack.Screen>
                  </Stack.Navigator>
                ) : (
                  <Tab.Navigator
                    initialRouteName="Home"
                    screenOptions={({route}) => ({
                      headerShown: false,
                      tabBarIcon: ({focused, color}) => {
                        let iconSource;
                        let size;

                        switch (route.name) {
                          case 'HomeStack':
                            iconSource = require('./assets/images/icon_tab_home.png');
                            size = 26;
                            break;
                          case 'Community':
                            iconSource = require('./assets/images/icon_tab_community.png');
                            size = 24;
                            break;
                          case 'My':
                            iconSource = require('./assets/images/icon_tab_my.png');
                            size = 29;
                            break;
                          default:
                            size = 25;
                        }

                        return (
                          <Image
                            source={iconSource}
                            style={{
                              width: size,
                              height: size,
                              tintColor: focused ? '#3f51b5' : color,
                            }}
                          />
                        );
                      },
                      tabBarActiveTintColor: '#3f51b5',
                      tabBarInactiveTintColor: 'gray',
                      tabBarStyle: {
                        paddingTop: 5,
                        paddingBottom: 10,
                        height: 80,
                      },
                      tabBarLabelStyle: {
                        fontSize: Platform.OS === 'ios' ? 10 : 12,
                        paddingBottom: Platform.OS === 'ios' ? 18 : 10,
                      },
                    })}>
                    <Tab.Screen
                      name="HomeStack"
                      options={{
                        tabBarLabel: '홈',
                      }}>
                      {props => (
                        <HomeStack {...props} accessToken={accessToken} />
                      )}
                    </Tab.Screen>
                    <Tab.Screen
                      name="Community"
                      options={{
                        tabBarLabel: '탐색',
                      }}>
                      {props => (
                        <CommunityScreen {...props} accessToken={accessToken} />
                      )}
                    </Tab.Screen>
                    <Tab.Screen
                      name="My"
                      options={{
                        tabBarLabel: '프로필',
                      }}>
                      {props => (
                        <MyStack
                          {...props}
                          accessToken={accessToken}
                          setIsNewMember={setIsNewMember}
                          setLocationId={setLocationId}
                          setIsLoggedIn={setIsLoggedIn}
                          setAccessToken={setAccessToken}
                          setIsDeleted={setIsDeleted}
                          setIsProfileCompleted={setIsProfileCompleted}
                        />
                      )}
                    </Tab.Screen>
                  </Tab.Navigator>
                )
              ) : (
                <AuthStack
                  setIsLoggedIn={setIsLoggedIn}
                  setAccessToken={setAccessToken}
                  setIsNewMember={setIsNewMember}
                  setIsDeleted={setIsDeleted}
                  setIsProfileCompleted={setIsProfileCompleted}
                />
              )}
            </NavigationContainer>
            <LevelUpModal />
          </RefreshProvider>
        </LevelUpProvider>
      </CopilotProvider>
    </>
  );
};

const styles = StyleSheet.create({
  autoLoginBackground: {
    flex: 1,
    backgroundColor: '#f5f6fA',
  },
});

export default App;
