import React, {useEffect, useState} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  TouchableOpacity,
  Alert,
  Dimensions,
} from 'react-native';
import {launchImageLibrary} from 'react-native-image-picker';
import profilePlaceholder from '../../assets/images/profile.png';
import loadingIcon from '../../assets/images/icon_loading.png';
import {fetchMemberInfo, registerProfile} from '../api/api';

const {width, height} = Dimensions.get('window');

const ProfileScreen = ({accessToken, navigation, route}) => {
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [selectedType, setSelectedType] = useState(null);
  const [profileImage, setProfileImage] = useState(profilePlaceholder);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const loadMemberInfo = async () => {
      try {
        const memberInfo = await fetchMemberInfo(accessToken);
        if (
          memberInfo.result.profileImage &&
          memberInfo.result.profileImage.startsWith('http')
        ) {
          setProfileImage({uri: memberInfo.result.profileImage});
        }
        setNickname(memberInfo.result.nickname || '닉네임');
        setEmail(memberInfo.result.email || 'example@email.com');
        setSelectedType(memberInfo.result.sensitivity.toLowerCase());
        setLoading(false);
      } catch (error) {
        console.error('Error fetching profile data:', error);
        setLoading(false);
      }
    };
    loadMemberInfo();
  }, [accessToken]);

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
        console.error('ImagePicker Error:', response.errorCode);
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
    if (!profileImage) {
      Alert.alert('프로필 이미지 필요', '프로필 이미지를 선택해주세요.');
      return;
    }

    if (!selectedType) {
      Alert.alert('유형 선택 필요', '날씨 체감 유형을 선택해주세요.');
      return;
    }

    try {
      setSaving(true);

      const result = await registerProfile(
        nickname,
        selectedType.toUpperCase(),
        profileImage,
        accessToken,
      );

      if (result.isSuccess) {
        Alert.alert('프로필 수정 완료', '프로필 정보가 수정되었습니다.');
        navigation.navigate('MyScreen', {refresh: true});
      } else {
      }
    } catch (error) {
      console.error('Error registering profile:', error);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <Image source={loadingIcon} style={styles.loadingIcon} />
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.headerTitle}>프로필 설정</Text>
      <View style={styles.separatorMy} />

      <View style={styles.profileSection}>
        <Image source={profileImage} style={styles.profileImage} />
        <TouchableOpacity style={styles.imageEdit} onPress={handleImagePicker}>
          <Text style={styles.plusIcon}>+</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.profileInfoContainer}>
        <View style={styles.profileTextContainer}>
          <Text style={styles.nickname}>{nickname}</Text>
          <Text style={styles.email}>{email}</Text>
        </View>
      </View>

      <View style={styles.separator} />
      <View style={styles.typeSelection}>
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
          {saving ? '저장 중...' : '수정하기'}
        </Text>
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
    marginTop: Platform.OS === 'ios' ? height * 0.06 : height * 0.045,
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
    /*backgroundColor: '#3f51b5',*/
    backgroundColor: '#2f5af4',
    borderRadius: 20,
    paddingVertical: Platform.OS === 'ios' ? 4 : 4,
    paddingHorizontal: Platform.OS === 'ios' ? 8 : 10,
  },
  plusIcon: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  profileInfoContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems: 'center',
    paddingHorizontal: 20,
    marginBottom: 20,
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
  separator: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
    marginTop: -8,
    marginBottom: 20,
  },
  separatorMy: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
    marginTop: -8,
    marginBottom: 20,
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
    marginTop: Platform.OS === 'ios' ? height * 0.126 : height * 0.118,
    marginHorizontal: 20,
    backgroundColor: '#f2f3f5',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  saveButtonText: {
    fontSize: 16,
    color: '#000',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingIcon: {
    width: 40,
    height: 40,
  },
});

export default ProfileScreen;
