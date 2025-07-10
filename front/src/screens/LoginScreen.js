import React, {useState, useEffect, useRef} from 'react';
import {
  Text,
  Alert,
  Image,
  StyleSheet,
  TouchableHighlight,
  Platform,
  Dimensions,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {login, getProfile} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';
import appleAuth, {
  AppleButton,
} from '@invertase/react-native-apple-authentication';
import {
  sendAccessTokenToBackend,
  refreshAccessToken,
  fetchMemberInfo,
} from '../api/api';
import {Animated} from 'react-native';

const {width, height} = Dimensions.get('window');

const LoginScreen = ({
  setIsLoggedIn,
  setAccessToken,
  setIsNewMember,
  setIsDeleted,
  navigation,
  setIsProfileCompleted,
  isProfileCompleted,
}) => {
  const [token, setToken] = useState('');
  const fadeAnim = useRef(new Animated.Value(0)).current;

  const saveLoginMethod = async method => {
    try {
      await AsyncStorage.setItem('loginMethod', method);
      console.log('Login method saved:', method);
    } catch (error) {
      console.error('Failed to save login method:', error);
    }
  };

  const handleSignInApple = async () => {
    try {
      const appleAuthRequestResponse = await appleAuth.performRequest({
        requestedOperation: appleAuth.Operation.LOGIN,
        requestedScopes: [appleAuth.Scope.FULL_NAME, appleAuth.Scope.EMAIL],
      });

      console.log('Apple Auth Response:', appleAuthRequestResponse);

      const {identityToken, authorizationCode} = appleAuthRequestResponse;

      if (!authorizationCode) {
        console.error('Error: authenticationCode is missing!');
        /* Alert.alert(
          '애플 로그인 실패',
          'authenticationCode를 가져올 수 없습니다.',
        ); */
        return;
      }

      console.log('Apple authenticationCode:', authorizationCode);
      const credentialState = await appleAuth.getCredentialStateForUser(
        appleAuthRequestResponse.user,
      );

      if (credentialState === appleAuth.State.AUTHORIZED) {
        const response = await sendAccessTokenToBackend(identityToken, 'apple');

        if (response.isSuccess) {
          const accessToken = response.result.accessToken;
          const refreshToken = response.result.refreshToken;
          const isNewMember = response.result.isNewMember;

          console.log('Apple Login Success:', {accessToken, isNewMember});

          await AsyncStorage.setItem('authenticationCode', authorizationCode);
          console.log('authenticationCode saved successfully');

          await saveLoginMethod('apple');

          let memberInfoSuccess = false;
          try {
            const memberInfoResponse = await fetchMemberInfo(accessToken);
            if (memberInfoResponse.isSuccess) {
              memberInfoSuccess = true;
              // console.log('회원 정보:', memberInfoResponse.result);
            } else {
              console.error(
                'Failed to fetch member info:',
                memberInfoResponse.message,
              );
            }
          } catch (error) {
            console.error('Error fetching member info:', error.message);
          }

          setAccessToken(accessToken);

          if (isNewMember && !memberInfoSuccess) {
            navigation.navigate('TermsAgreementScreen', {accessToken});
            setIsProfileCompleted(false);
          } else {
            if (!isProfileCompleted && !memberInfoSuccess) {
              navigation.navigate('TermsAgreementScreen', {accessToken});
            } else {
              setIsLoggedIn(true);
              setIsProfileCompleted(true);
              setIsNewMember(false);
              setIsDeleted(false);
            }
          }

          await AsyncStorage.setItem('accessToken', accessToken);
          await AsyncStorage.setItem('refreshToken', refreshToken);
        } else {
          Alert.alert(
            '로그인 실패',
            response.message || '서버 오류가 발생했습니다.',
          );
        }
      } else {
        /* Alert.alert('애플 로그인 실패', '사용자가 인증되지 않았습니다.'); */
      }
    } catch (error) {
      console.error('Apple Login Error:', error.message);
      /* Alert.alert(
        '애플 로그인 실패',
        error.message || '알 수 없는 오류가 발생했습니다.',
      ); */
    }
  };

  const handleKakaoLogin = async () => {
    try {
      console.log('Starting Kakao login...');
      const token = await login();
      console.log('Kakao login successful, token:', token.accessToken);

      try {
        const profile = await getProfile();
        console.log('카카오 사용자 정보:', profile);
      } catch (error) {
        console.error('카카오 사용자 정보 가져오기 실패:', error);
      }

      const response = await sendAccessTokenToBackend(
        token.accessToken,
        'kakao',
      );

      if (response.isSuccess) {
        const accessToken = response.result.accessToken;
        const refreshToken = response.result.refreshToken;
        const isNewMember = response.result.isNewMember;

        console.log('Kakao Login Success:', {accessToken, isNewMember});

        await saveLoginMethod('kakao');

        let memberInfoSuccess = false;
        try {
          const memberInfoResponse = await fetchMemberInfo(accessToken);
          if (memberInfoResponse.isSuccess) {
            memberInfoSuccess = true;
            // console.log('회원 정보:', memberInfoResponse.result);
          } else {
            console.error(
              'Failed to fetch member info:',
              memberInfoResponse.message,
            );
          }
        } catch (error) {
          console.error('Error fetching member info:', error.message);
        }

        setAccessToken(accessToken);

        if (isNewMember && !memberInfoSuccess) {
          navigation.navigate('TermsAgreementScreen', {accessToken});
          setIsNewMember(true);
          setIsProfileCompleted(false);
        } else {
          if (!isProfileCompleted && !memberInfoSuccess) {
            navigation.navigate('TermsAgreementScreen', {accessToken});
          } else {
            setIsLoggedIn(true);
            setIsProfileCompleted(true);
            setIsNewMember(false);
            setIsDeleted(false);
          }
        }

        await AsyncStorage.setItem('accessToken', accessToken);
        await AsyncStorage.setItem('refreshToken', refreshToken);
      } else {
        Alert.alert(
          '로그인 실패',
          response.message || '서버 오류가 발생했습니다.',
        );
      }
    } catch (err) {
      console.error('Kakao Login Failed:', err.message);
    }
  };

  useEffect(() => {
    Animated.timing(fadeAnim, {
      toValue: 1,
      duration: 800,
      useNativeDriver: true,
    }).start();
  }, []);

  useEffect(() => {
    const checkStoredTokens = async () => {
      const logoutState = await AsyncStorage.getItem('logoutState');
      if (logoutState === 'true') {
        console.log('Logout state detected, skipping auto-login.');
        return;
      }

      const storedRefreshToken = await AsyncStorage.getItem('refreshToken');
      if (!storedRefreshToken) {
        console.log('No refresh token found, login required.');
        return;
      }

      try {
        console.log('Refreshing access token...');
        let retryCount = 0;
        let response;

        while (retryCount < 3) {
          response = await refreshAccessToken(storedRefreshToken);
          if (response.isSuccess) break;
          retryCount++;
          console.warn(`Retrying token refresh... Attempt ${retryCount}`);
          await new Promise(resolve => setTimeout(resolve, 2000));
        }

        if (response.isSuccess) {
          const newAccessToken = response.result.accessToken;
          const newRefreshToken = response.result.refreshToken;

          setAccessToken(newAccessToken);

          if (newRefreshToken && newRefreshToken !== storedRefreshToken) {
            console.log('New refresh token detected, updating storage.');
            await AsyncStorage.setItem('refreshToken', newRefreshToken);
          }

          await AsyncStorage.setItem('accessToken', newAccessToken);
          setIsLoggedIn(true);
        } else {
          console.log('Failed to refresh tokens after retries, logging out.');

          await AsyncStorage.removeItem('accessToken');
          await AsyncStorage.removeItem('refreshToken');
          setIsLoggedIn(false);
        }
      } catch (error) {
        console.error('Failed to refresh token:', error.message);

        if (
          error.message.includes('Network Error') ||
          error.message.includes('timeout')
        ) {
          console.warn('Network error detected, will retry later.');
          setTimeout(() => {
            checkStoredTokens();
          }, 5 * 60 * 1000);
          return;
        }

        await AsyncStorage.removeItem('accessToken');
        await AsyncStorage.removeItem('refreshToken');
        setIsLoggedIn(false);
      }
    };

    checkStoredTokens();

    const interval = setInterval(checkStoredTokens, 15 * 60 * 1000);

    return () => {
      clearInterval(interval);
    };
  }, []);

  return (
    <LinearGradient
      colors={['#f8fbff', '#dceeff']}
      start={{x: 0, y: 0}}
      end={{x: 1, y: 1}}
      style={styles.container}>
      <Animated.View style={{opacity: fadeAnim, alignItems: 'center'}}>
        <Image
          source={require('../../assets/images/icon_app.png')}
          style={styles.appIcon}
        />
        <Text style={styles.appTitle}>날씨톡톡</Text>
        <Text style={styles.appSubtitle}>우리 동네 날씨 커뮤니티</Text>
      </Animated.View>

      <TouchableHighlight style={styles.kakaoButton} onPress={handleKakaoLogin}>
        <Text style={styles.kakaoButtonText}>카카오톡으로 로그인</Text>
      </TouchableHighlight>

      {Platform.OS === 'ios' && (
        <AppleButton
          buttonStyle={AppleButton.Style.BLACK}
          buttonType={AppleButton.Type.SIGN_IN}
          style={styles.appleButton}
          onPress={handleSignInApple}
        />
      )}

      <TouchableHighlight
        style={styles.testAccountButton}
        underlayColor="transparent"
        onPress={() => navigation.navigate('TestLoginScreen')}>
        <Text style={styles.testAccountButtonText}>관리자 로그인</Text>
      </TouchableHighlight>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 24,
  },
  appIcon: {
    width: width * 0.25,
    height: width * 0.25,
    borderRadius: 16,
    marginBottom: height * 0.04,
  },
  appTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: '#2c3e50',
    marginBottom: height * 0.01,
    letterSpacing: 0.5,
  },
  appSubtitle: {
    fontSize: 15,
    color: '#555',
    lineHeight: 25,
    opacity: 0.85,
    letterSpacing: 0.2,
    marginBottom: height * 0.1,
  },
  title: {
    fontSize: 20,
    color: '#fff',
    marginBottom: 20,
  },
  input: {
    width: '90%',
    padding: 10,
    borderWidth: 1,
    borderColor: '#fff',
    marginBottom: 20,
    borderRadius: 8,
    backgroundColor: '#fff',
    height: 60,
    marginTop: -width * 0.3,
  },
  kakaoButton: {
    width: '90%',
    backgroundColor: '#FEE500',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: Platform.OS === 'ios' ? 20 : 10,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  kakaoButtonText: {
    fontSize: 16,
    color: '#3C1E1E',
    fontWeight: 'bold',
  },
  appleButton: {
    width: '90%',
    height: 45,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  testAccountButton: {
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: Platform.OS === 'ios' ? width * 0.02 : -width * 0.01,
  },
  testAccountButtonText: {
    color: '#494949',
    fontSize: 10,
    fontWeight: 'bold',
  },
});

export default LoginScreen;
