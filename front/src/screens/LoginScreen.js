import React, {useState, useEffect} from 'react';
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
import {login} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';
import appleAuth, {
  AppleButton,
} from '@invertase/react-native-apple-authentication';
import {
  sendAccessTokenToBackend,
  refreshAccessToken,
  fetchMemberInfo,
} from '../api/api';

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
        Alert.alert(
          '애플 로그인 실패',
          'authenticationCode를 가져올 수 없습니다.',
        );
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
              console.log('회원 정보:', memberInfoResponse.result);
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
        Alert.alert('애플 로그인 실패', '사용자가 인증되지 않았습니다.');
      }
    } catch (error) {
      console.error('Apple Login Error:', error.message);
      Alert.alert(
        '애플 로그인 실패',
        error.message || '알 수 없는 오류가 발생했습니다.',
      );
    }
  };

  const saveTokens = async (accessToken, refreshToken) => {
    try {
      await AsyncStorage.setItem('accessToken', accessToken);
      await AsyncStorage.setItem('refreshToken', refreshToken);
      console.log('Tokens saved:', {accessToken, refreshToken});
    } catch (error) {
      console.error('Error saving tokens:', error);
    }
  };

  const handleKakaoLogin = async () => {
    try {
      console.log('Starting Kakao login...');
      const token = await login();
      console.log('Kakao login successful, token:', token.accessToken);

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
            console.log('회원 정보:', memberInfoResponse.result);
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
    const checkStoredTokens = async () => {
      const logoutState = await AsyncStorage.getItem('logoutState');
      if (logoutState === 'true') {
        console.log('Logout state detected, skipping auto-login.');
        return;
      }

      const refreshToken = await AsyncStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          const newAccessToken = await refreshAccessToken();
          setAccessToken(newAccessToken);
          setIsLoggedIn(true);
        } catch (error) {
          console.error('Failed to refresh token:', error.message);
        }
      }
    };

    checkStoredTokens();

    const interval = setInterval(async () => {
      const refreshToken = await AsyncStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          const newAccessToken = await refreshAccessToken();
          console.log('Refreshed access token:', newAccessToken);
          setAccessToken(newAccessToken);
        } catch (error) {
          console.error('Failed to refresh token:', error.message);
          await AsyncStorage.removeItem('accessToken');
          await AsyncStorage.removeItem('refreshToken');
          setIsLoggedIn(false);
        }
      }
    }, 15 * 60 * 1000);

    return () => clearInterval(interval);
  }, []);

  return (
    <LinearGradient
      colors={['#2F5AF4', '#0FA2AB']}
      start={{x: 0, y: 0}}
      end={{x: 1, y: 1}}
      style={styles.container}>
      <Image
        source={require('../../assets/images/icon_app.png')}
        style={styles.appIcon}
      />

      <Text style={styles.appTitle}>날씨 톡톡</Text>
      <Text style={styles.appSubtitle}>우리 동네 날씨 커뮤니티</Text>

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
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  appIcon: {
    width: width * 0.27,
    height: width * 0.27,
    marginTop: -width * 0.1,
    marginBottom: 20,
    borderRadius: 20,
  },
  appTitle: {
    fontSize: 22,
    color: '#fff',
    fontWeight: 'bold',
    marginBottom: 10,
  },
  appSubtitle: {
    fontSize: 16,
    color: '#fff',
    marginBottom: Platform.OS === 'ios' ? width * 0.53 : width * 0.7,
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
