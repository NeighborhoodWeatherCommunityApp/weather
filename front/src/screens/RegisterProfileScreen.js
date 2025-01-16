import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Image,
  Alert,
  PermissionsAndroid,
  Platform,
  Linking,
  Dimensions,
} from 'react-native';
import {launchImageLibrary} from 'react-native-image-picker';
import Geolocation from 'react-native-geolocation-service';
import {check, PERMISSIONS, request, RESULTS} from 'react-native-permissions';
import {registerProfile, sendLocationToBackend} from '../api/api';
import AsyncStorage from '@react-native-async-storage/async-storage';

const {width, height} = Dimensions.get('window');

const RegisterProfileScreen = ({
  route,
  setIsNewMember,
  setIsLoggedIn,
  setIsDeleted,
  navigation,
  setIsProfileCompleted,
}) => {
  const [nickname, setNickname] = useState('');
  const [profileImage, setProfileImage] = useState(null);
  const [selectedType, setSelectedType] = useState(null);
  const [loading, setLoading] = useState(false);

  const {accessToken, agreements} = route.params || {};

  useEffect(() => {
    if (Platform.OS === 'android') {
      requestStoragePermission();
    }
  }, []);

  const handleImagePicker = () => {
    const options = {
      mediaType: 'photo',
      maxWidth: 320,
      maxHeight: 320,
    };

    launchImageLibrary(options, response => {
      if (response.didCancel) {
        console.log('Image selection canceled');
      } else if (response.errorCode) {
        console.error('ImagePicker Error: ', response.errorCode);
      } else {
        const selectedImage = response.assets[0];

        const imageUri = selectedImage.uri.startsWith('file://')
          ? selectedImage.uri
          : `file://${selectedImage.uri}`;

        setProfileImage({
          uri: imageUri,
          name: selectedImage.fileName || 'profile.jpg',
          type: selectedImage.type || 'image/jpeg',
        });
      }
    });
  };

  const handleSaveProfile = async () => {
    if (!nickname) {
      Alert.alert('닉네임 입력', '닉네임을 입력해주세요.');
      return;
    }

    if (!selectedType) {
      Alert.alert('유형 선택', '날씨 체감 유형을 선택해주세요.');
      return;
    }

    const sensitivityMap = {
      hot: 'HOT',
      none: 'NONE',
      cold: 'COLD',
    };

    try {
      setLoading(true);

      const result = await registerProfile(
        nickname,
        sensitivityMap[selectedType],
        profileImage,
        accessToken,
      );

      console.log('registered new member info:', result);

      setIsNewMember(false);
      setIsLoggedIn(true);
      setIsDeleted(false);

      const permissionGranted = await requestLocationPermission();
      if (permissionGranted) {
        getCurrentLocation();
      } else {
        Alert.alert(
          '위치 권한 필요',
          '위치 정보를 등록하려면 위치 권한이 필요합니다. 앱 설정에서 권한을 허용해 주세요.',
          [
            {text: '취소', style: 'cancel', onPress: () => setLoading(false)},
            {
              text: '설정 열기',
              onPress: async () => {
                try {
                  await Linking.openSettings();
                } catch (error) {
                  console.error('Error opening settings:', error);
                  Alert.alert('오류', '설정을 열 수 없습니다.');
                }
              },
            },
          ],
        );
      }
    } catch (error) {
      console.error('프로필 저장 오류:', error);
      Alert.alert('프로필 저장 실패', error.message);
    } finally {
      setLoading(false);
    }
  };

  const requestLocationPermission = async () => {
    try {
      if (Platform.OS === 'ios') {
        let status = await check(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
        if (status === RESULTS.DENIED || status === RESULTS.BLOCKED) {
          status = await request(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
        }
        return status === RESULTS.GRANTED;
      } else {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        );
        return granted === PermissionsAndroid.RESULTS.GRANTED;
      }
    } catch (error) {
      console.error('위치 권한 요청 오류:', error);
      return false;
    }
  };

  const getCurrentLocation = () => {
    Geolocation.getCurrentPosition(
      async position => {
        const {longitude, latitude} = position.coords;

        try {
          const locationResponse = await sendLocationToBackend(
            longitude,
            latitude,
            accessToken,
          );
          console.log('Send location successful:', locationResponse);

          Alert.alert('위치 등록 완료', '위치 정보가 등록되었습니다.');
          setLoading(false);

          setIsLoggedIn(true);
          setIsNewMember(prevState => !prevState);
          setIsNewMember(false);
          console.log('isNewMember 변경됨: false');
          await AsyncStorage.setItem('isProfileCompleted', 'true');
          const storedProfileCompleted = await AsyncStorage.getItem(
            'isProfileCompleted',
          );
          console.log('Stored isProfileCompleted:', storedProfileCompleted);

          setIsProfileCompleted(true);
          console.log('State: isProfileCompleted updated to true');

          if (storedProfileCompleted === 'true') {
            await AsyncStorage.removeItem('isProfileCompleted');
            console.log(
              'isProfileCompleted has been removed after being stored.',
            );
          }
        } catch (error) {
          console.error('위치 등록 실패:', error);
          Alert.alert(
            '위치 등록 실패',
            '위치 정보를 전송하는 중 오류가 발생했습니다.',
          );
          setLoading(false);
        }
      },
      error => {
        console.error('위치 정보 가져오기 오류:', error);
        Alert.alert(
          '위치 정보를 가져올 수 없습니다.',
          '위치 권한을 확인해주세요.',
        );
        setLoading(false);
      },
      {enableHighAccuracy: true, timeout: 15000, maximumAge: 10000},
    );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.headerTitle}>프로필 설정</Text>
      <View style={styles.separator} />

      <View style={styles.profileSection}>
        <Image
          source={
            profileImage
              ? {uri: profileImage.uri}
              : require('../../assets/images/profile.png')
          }
          style={styles.profileImage}
        />
        <TouchableOpacity style={styles.imageEdit} onPress={handleImagePicker}>
          <Text style={styles.plusIcon}>+</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.profileInfoContainer}>
        <Text style={styles.nickname}>닉네임</Text>
        <TextInput
          style={styles.input}
          value={nickname}
          placeholder="닉네임을 입력해주세요"
          onChangeText={setNickname}
          placeholderTextColor="#999"
        />
      </View>

      <View style={styles.typeSelection}>
        <Text style={styles.subHeader}>당신은 어떤 유형인가요?</Text>

        <TouchableOpacity
          style={[
            styles.typeButton,
            selectedType === 'hot' && styles.selectedButton,
          ]}
          onPress={() => setSelectedType('hot')}>
          <Text
            style={[
              styles.typeButtonText,
              selectedType === 'hot' && styles.selectedButtonText,
            ]}>
            남들보다 더위를 많이 타는 편
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            styles.typeButton,
            selectedType === 'none' && styles.selectedButton,
          ]}
          onPress={() => setSelectedType('none')}>
          <Text
            style={[
              styles.typeButtonText,
              selectedType === 'none' && styles.selectedButtonText,
            ]}>
            평범한 편
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            styles.typeButton,
            selectedType === 'cold' && styles.selectedButton,
          ]}
          onPress={() => setSelectedType('cold')}>
          <Text
            style={[
              styles.typeButtonText,
              selectedType === 'cold' && styles.selectedButtonText,
            ]}>
            남들보다 추위를 많이 타는 편
          </Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.saveButton} onPress={handleSaveProfile}>
        <Text style={styles.saveButtonText}>
          {loading ? '저장 중...' : '완료'}
        </Text>
      </TouchableOpacity>
    </View>
  );
};

const requestStoragePermission = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
      {
        title: '사진 접근 권한',
        message: '사진 접근 권한이 필요합니다.',
        buttonNeutral: '나중에',
        buttonNegative: '취소',
        buttonPositive: '허용',
      },
    );
    if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
      console.log('Storage permission denied');
    }
  } catch (err) {
    console.warn(err);
  }
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
    marginTop: Platform.OS === 'ios' ? height * 0.07 : height * 0.045,
  },
  nickname: {
    color: '#000',
    marginBottom: 10,
    marginTop: height * 0.05,
  },
  separator: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
    marginTop: -8,
    marginBottom: 20,
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
  imageEdit: {
    position: 'absolute',
    bottom: 0,
    right: width * 0.4,
    backgroundColor: '#2f5af4',
    borderRadius: 20,
    paddingVertical: Platform.OS === 'ios' ? 4 : 2,
    paddingHorizontal: 8,
  },
  plusIcon: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  profileInfoContainer: {
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  label: {
    fontSize: 14,
    color: '#777',
    marginBottom: 5,
  },
  input: {
    fontSize: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    paddingVertical: 5,
    color: '#000',
  },
  typeSelection: {
    marginVertical: 10,
    paddingHorizontal: 20,
  },
  typeButton: {
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 10,
    paddingVertical: 15,
    alignItems: 'center',
    marginVertical: 5,
  },
  typeButtonText: {
    color: '#374151',
    fontSize: 14,
  },
  selectedButton: {
    borderColor: '#2f5af4',
    backgroundColor: '#2f5af4',
  },
  selectedButtonText: {
    color: '#fff',
  },
  saveButton: {
    marginTop: height * 0.11,
    marginHorizontal: 20,
    backgroundColor: '#F2F3F5',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  saveButtonText: {
    fontSize: 16,
    color: '#000',
  },
  subHeader: {
    color: '#000',
    marginBottom: 10,
  },
});

export default RegisterProfileScreen;
