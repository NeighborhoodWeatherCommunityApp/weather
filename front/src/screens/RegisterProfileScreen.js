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
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import {launchImageLibrary} from 'react-native-image-picker';
import Geolocation from 'react-native-geolocation-service';
import {check, PERMISSIONS, request, RESULTS} from 'react-native-permissions';
import {registerProfile, sendLocationToBackend} from '../api/api';

const RegisterProfileScreen = ({setIsNewMember, accessToken, memberId}) => {
  const [nickname, setNickname] = useState('');
  const [profileImage, setProfileImage] = useState(null);
  const [loading, setLoading] = useState(false);

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
        setProfileImage({
          uri: selectedImage.uri,
          name: selectedImage.fileName,
          type: selectedImage.type,
        });
      }
    });
  };

  const handleSaveProfile = async () => {
    if (!nickname) {
      Alert.alert('닉네임 필요', '닉네임을 입력해주세요.');
      return;
    }

    if (!profileImage) {
      Alert.alert('프로필 이미지 필요', '프로필 이미지를 선택해주세요.');
      return;
    }

    try {
      setLoading(true);
      const result = await registerProfile(
        nickname,
        profileImage,
        accessToken,
        memberId,
      );
      console.log('registered new member info:', result);

      const permissionGranted = await requestLocationPermission();
      if (permissionGranted) {
        getCurrentLocation();
      } else {
        Alert.alert(
          '위치 권한 필요',
          '위치 정보를 등록하려면 권한을 허용해주세요.',
        );
        setLoading(false);
        return;
      }
    } catch (error) {
      console.error('프로필 저장 오류:', error);
      Alert.alert('프로필 저장 실패', error.message);
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
      console.error('Error requesting location permission:', error);
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
          setIsNewMember(false);
        } catch (error) {
          console.error('Error sending location data:', error);
          Alert.alert(
            '위치 등록 실패',
            '위치 정보를 전송하는 중 오류가 발생했습니다.',
          );
          setLoading(false);
        }
      },
      error => {
        console.error('Error getting current position:', error);
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
      <View style={styles.profileContainer}>
        <Image
          source={
            profileImage
              ? {uri: profileImage.uri}
              : {uri: 'https://via.placeholder.com/100'}
          }
          style={styles.profileImage}
        />
        <TouchableOpacity
          style={styles.editIconContainer}
          onPress={handleImagePicker}>
          <Icon name="add-circle-outline" size={30} color="#2f5af4" />
        </TouchableOpacity>
      </View>
      <Text style={styles.label}>닉네임</Text>
      <TextInput
        style={styles.input}
        value={nickname}
        placeholder="닉네임을 입력하세요"
        onChangeText={setNickname}
        editable={true}
      />
      <TouchableOpacity style={styles.submitButton} onPress={handleSaveProfile}>
        <Text style={styles.submitButtonText}>
          {loading ? '저장 중...' : '저장하기'}
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
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#fff',
  },
  profileContainer: {
    marginTop: 50,
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
  },
  profileImage: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: '#e0e0e0',
  },
  editIconContainer: {
    position: 'absolute',
    bottom: 0,
    right: 0,
  },
  label: {
    fontSize: 18,
    marginTop: 30,
    marginBottom: 20,
    color: '#333',
    fontWeight: 'bold',
  },
  input: {
    width: '100%',
    height: 40,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    fontSize: 16,
    color: '#333',
    marginBottom: 30,
  },
  submitButton: {
    width: '100%',
    padding: 15,
    backgroundColor: '#2f5af4',
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 50,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default RegisterProfileScreen;

/*
// sensitivity 값 에러로 인해 주석 처리합니다.
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
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import {launchImageLibrary} from 'react-native-image-picker';
import {registerProfile} from '../api/api';

const RegisterProfileScreen = ({setIsNewMember, accessToken, memberId}) => {
  const [nickname, setNickname] = useState('');
  const [selectedType, setSelectedType] = useState(null);
  const [profileImage, setProfileImage] = useState(null);

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
        setProfileImage(selectedImage);
      }
    });
  };

  const handleSaveProfile = async () => {
    if (!nickname) {
      Alert.alert('닉네임 필요', '닉네임을 입력해주세요.');
      return;
    }

    if (!selectedType) {
      Alert.alert('유형 선택 필요', '유형을 선택해주세요.');
      return;
    }

    const sensitivityMap = {
      hot: 'HOT',
      normal: 'NORMAL',
      cold: 'COLD',
    };

    try {
      const result = await registerProfile(
        nickname,
        sensitivityMap[selectedType],
        profileImage,
        accessToken,
        memberId,
      );
      Alert.alert('저장 완료', '프로필이 저장되었습니다.');
      setIsNewMember(false);
    } catch (error) {
      console.error('프로필 저장 오류:', error);
      Alert.alert('프로필 저장 실패', error.message);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.profileContainer}>
        <Image
          source={
            profileImage
              ? {uri: profileImage.uri}
              : {uri: 'https://via.placeholder.com/100'}
          }
          style={styles.profileImage}
        />
        <TouchableOpacity
          style={styles.editIconContainer}
          onPress={handleImagePicker}>
          <Icon name="add-circle-outline" size={30} color="#2f5af4" />
        </TouchableOpacity>
      </View>
      <Text style={styles.label}>닉네임</Text>
      <TextInput
        style={styles.input}
        value={nickname}
        placeholder="닉네임을 입력하세요"
        onChangeText={setNickname}
        editable={true}
      />
      <Text style={styles.label}>유형</Text>
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
          더위를 많이 타는 편
        </Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={[
          styles.typeButton,
          selectedType === 'normal' && styles.selectedButton,
        ]}
        onPress={() => setSelectedType('normal')}>
        <Text
          style={[
            styles.typeButtonText,
            selectedType === 'normal' && styles.selectedButtonText,
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
          추위를 많이 타는 편
        </Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.submitButton} onPress={handleSaveProfile}>
        <Text style={styles.submitButtonText}>저장하기</Text>
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
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#fff',
  },
  profileContainer: {
    marginTop: 50,
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
  },
  profileImage: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: '#e0e0e0',
  },
  editIconContainer: {
    position: 'absolute',
    bottom: 0,
    right: 0,
  },
  label: {
    fontSize: 18,
    marginTop: 30,
    marginBottom: 20,
    color: '#333',
    fontWeight: 'bold',
  },
  input: {
    width: '100%',
    height: 40,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    fontSize: 16,
    color: '#333',
    marginBottom: 30,
  },
  typeButton: {
    width: '100%',
    padding: 15,
    borderWidth: 1,
    borderColor: '#2f5af4',
    borderRadius: 5,
    alignItems: 'center',
    marginVertical: 5,
    backgroundColor: '#fff',
  },
  typeButtonText: {
    color: '#2f5af4',
    fontSize: 16,
  },
  selectedButton: {
    backgroundColor: '#2f5af4',
  },
  selectedButtonText: {
    color: '#fff',
  },
  submitButton: {
    width: '100%',
    padding: 15,
    backgroundColor: '#2f5af4',
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 50,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default RegisterProfileScreen;
*/