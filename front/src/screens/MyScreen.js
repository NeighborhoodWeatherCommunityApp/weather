import React, {useEffect, useState} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  ActivityIndicator,
  Image,
  TouchableOpacity,
  Alert,
  Dimensions,
  Linking,
  Platform,
} from 'react-native';
import {useFocusEffect} from '@react-navigation/native';
import {fetchMemberInfo, deleteMember} from '../api/api';
import profilePlaceholder from '../../assets/images/profile.png';
import {logout} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';
import appleAuth from '@invertase/react-native-apple-authentication';

const {width, height} = Dimensions.get('window');

const MyScreen = ({
  accessToken,
  setIsNewMember,
  setLocationId,
  setIsLoggedIn,
  setAccessToken,
  setIsDeleted,
  navigation,
  setIsProfileCompleted,
}) => {
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [profileImage, setProfileImage] = useState(profilePlaceholder);
  const [loading, setLoading] = useState(true);

  const personalInfoUrl =
    'https://safe-scabiosa-656.notion.site/16d2f55997ef804d8a16c3c1a5dd1879?pvs=4';

  const openPersonalInfoPage = () => {
    Linking.openURL(personalInfoUrl).catch(err =>
      console.error('Failed to open URL:', err),
    );
  };

  const termsUrls = {
    service:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef804b9169f2b2ee6775dd?pvs=4',
    location:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef80768b0fd53594df6843?pvs=4',
    personalInfo:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef806784c0cc8f3462b80c?pvs=4',
  };

  const openTermsView = (title, key) => {
    navigation.navigate('TermsViewScreen', {title, url: termsUrls[key]});
  };

  const loadMemberInfo = async () => {
    try {
      const memberInfo = await fetchMemberInfo(accessToken);
      console.log('Fetched member info:', memberInfo);
      if (
        memberInfo.result.profileImage &&
        memberInfo.result.profileImage.startsWith('http')
      ) {
        setProfileImage({uri: memberInfo.result.profileImage});
      } else {
        setProfileImage(profilePlaceholder);
      }
      setNickname(memberInfo.result.nickname || '닉네임');
      setEmail(memberInfo.result.email || 'example@email.com');
      setLoading(false);
    } catch (error) {
      console.error('회원 정보 불러오는 중 오류 발생:', error);
      setLoading(false);
    }
  };

  useFocusEffect(
    React.useCallback(() => {
      loadMemberInfo();
    }, []),
  );

  const handleLogout = async () => {
    Alert.alert(
      '로그아웃',
      '로그아웃 하시겠습니까?',
      [
        {
          text: '취소',
          style: 'cancel',
        },
        {
          text: '확인',
          onPress: async () => {
            try {
              const loginMethod = await AsyncStorage.getItem('loginMethod');
              console.log('Current login method:', loginMethod);

              if (loginMethod === 'kakao') {
                console.log('Performing Kakao logout...');
                await logout();
              } else if (loginMethod === 'apple') {
                console.log('Performing Apple logout...');
                console.log('Apple logout logic executed.');
              }

              setAccessToken(null);
              setIsLoggedIn(false);

              await AsyncStorage.removeItem('accessToken');
              await AsyncStorage.removeItem('refreshToken');
              await AsyncStorage.removeItem('loginMethod');

              navigation.replace('LoginScreen');
              console.log('Logout successful');
            } catch (err) {
              console.error('Logout failed:', err.message);
              Alert.alert('로그아웃', '메인 화면으로 이동합니다.');
              setAccessToken(null);
              setIsLoggedIn(false);
              setIsNewMember(false);
              setIsProfileCompleted(false);

              await AsyncStorage.removeItem('accessToken');
              await AsyncStorage.removeItem('refreshToken');
              await AsyncStorage.removeItem('loginMethod');

              navigation.replace('LoginScreen');
            }
          },
        },
      ],
      {cancelable: true},
    );
  };

  const handleDeleteAccount = async () => {
    if (!accessToken) {
      Alert.alert('오류', '유효한 인증 토큰이 없습니다.');
      return;
    }

    console.log('Starting account deletion process...');
    console.log('Current accessToken:', accessToken);

    try {
      const loginMethod = await AsyncStorage.getItem('loginMethod');
      console.log('Current login method:', loginMethod);

      let authenticationCode = null;

      if (loginMethod === 'apple') {
        try {
          const appleCredential = await appleAuth.performRequest({
            requestedOperation: appleAuth.Operation.LOGIN,
            requestedScopes: [appleAuth.Scope.EMAIL, appleAuth.Scope.FULL_NAME],
          });

          authenticationCode = appleCredential.authorizationCode;
          console.log(
            'Received Apple authentication code:',
            authenticationCode,
          );

          if (!authenticationCode) {
            Alert.alert(
              '오류',
              'Apple 인증 코드가 유효하지 않습니다. 다시 시도해주세요.',
            );
            return;
          }
        } catch (error) {
          console.error('Apple authentication error:', error);
          Alert.alert(
            'Apple 로그인 실패',
            '회원 탈퇴를 위해서는 로그인이 필요합니다.',
          );
          return;
        }
      }

      Alert.alert(
        '회원 탈퇴',
        '정말 탈퇴하시겠습니까?',
        [
          {text: '아니오', style: 'cancel'},
          {
            text: '네',
            onPress: async () => {
              try {
                console.log('Sending delete request with:', {
                  accessToken,
                  loginMethod,
                  authenticationCode,
                });

                await deleteMember(
                  accessToken,
                  loginMethod,
                  authenticationCode,
                );
                console.log('Account deletion successful.');
                Alert.alert('탈퇴 완료', '회원 탈퇴가 완료되었습니다.');
                await AsyncStorage.setItem('isProfileCompleted', 'false');
                setIsProfileCompleted(false);

                await AsyncStorage.multiRemove([
                  'accessToken',
                  'refreshToken',
                  'loginMethod',
                  'appleUserId',
                ]);

                console.log('Local data cleared.');

                setIsDeleted(true);
              } catch (error) {
                console.error('Failed to delete member:', error);
                if (error.code === 'MEMBER_404_1') {
                  Alert.alert(
                    '회원 탈퇴 실패',
                    '사용자를 찾을 수 없습니다. 이미 탈퇴된 계정일 수 있습니다.',
                  );
                } else {
                  Alert.alert(
                    '회원 탈퇴 실패',
                    '회원 탈퇴에 실패했습니다. 다시 시도해주세요.',
                  );
                }
              }
            },
          },
        ],
        {cancelable: true},
      );
    } catch (error) {
      console.error('Error handling delete account:', error);
    }
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#999999" />
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.headerTitle}>마이페이지</Text>
      <View style={styles.separatorMy} />

      <View style={styles.profileSection}>
        <Image source={profileImage} style={styles.profileImage} />
      </View>

      <View style={styles.profileInfoContainer}>
        <View style={styles.profileTextContainer}>
          <Text style={styles.nickname}>{nickname}</Text>
          <Text style={styles.email}>{email}</Text>
        </View>
        <TouchableOpacity onPress={() => navigation.navigate('ProfileScreen')}>
          <Text style={styles.profileEdit}>프로필 수정</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.separator} />
      <View style={styles.menuSection}>
        <TouchableOpacity
          style={styles.menuItem}
          onPress={() => navigation.navigate('NotificationSettingScreen')}>
          <Text style={styles.menuText}>알림 설정</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.menuItem}
          onPress={() => openTermsView('이용약관', 'service')}>
          <Text style={styles.menuText}>이용약관</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.menuItem}
          onPress={openPersonalInfoPage}>
          <Text style={styles.menuText}>개인정보 처리방침</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.separator} />
      <View style={styles.footerSection}>
        <View style={styles.appInfo}>
          <Text style={styles.footerText}>앱 버전</Text>
          <Text style={styles.footerValue}>1.0.0</Text>
        </View>
        <TouchableOpacity onPress={handleDeleteAccount} style={styles.menuItem}>
          <Text style={styles.menuText}>회원탈퇴</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.separator} />
      <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
        <Text style={styles.logoutText}>로그아웃</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  headerTitle: {
    fontSize: 18,
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
    marginTop: Platform.OS === 'ios' ? height * 0.075 : height * 0.045,
  },
  profileSection: {
    alignItems: 'center',
    marginBottom: 10,
  },
  profileImage: {
    width: width * 0.27,
    height: width * 0.27,
    borderRadius: 9999,
    backgroundColor: '#e0e0e0',
    marginTop: height * 0.01,
  },
  profileInfoContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    marginBottom: 10,
  },
  profileTextContainer: {
    flexDirection: 'column',
  },
  nickname: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: 3,
    marginTop: height * 0.05,
  },
  email: {
    fontSize: 14,
    color: '#777',
    marginTop: height * 0.01,
    marginBottom: height * 0.01,
  },
  profileEdit: {
    fontSize: 14,
    color: '#777',
    fontWeight: 'bold',
  },
  menuSection: {
    marginTop: 10,
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 15,
    paddingHorizontal: 20,
  },
  menuText: {
    fontSize: 16,
    color: '#333',
  },
  menuArrow: {
    fontSize: 16,
    color: '#ccc',
    fontWeight: 'bold',
  },
  separator: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
  },
  separatorMy: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
    marginTop: -8,
    marginBottom: 20,
  },
  footerSection: {
    marginTop: 10,
  },
  appInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 15,
    paddingHorizontal: 20,
  },
  footerText: {
    fontSize: 16,
    color: '#333',
  },
  footerValue: {
    fontSize: 16,
    color: '#666',
  },
  logoutButton: {
    marginTop: Platform.OS === 'ios' ? height * 0.053 : height * 0.05,
    marginHorizontal: 20,
    backgroundColor: '#F2F3F5',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  logoutText: {
    fontSize: 16,
    color: '#000',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default MyScreen;
