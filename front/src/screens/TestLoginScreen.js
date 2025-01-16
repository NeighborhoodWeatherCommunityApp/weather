import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  Alert,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
  Platform,
  Keyboard,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {fetchMemberInfo} from '../api/api';

const {width, height} = Dimensions.get('window');

const TestLoginScreen = ({setAccessToken, setIsLoggedIn, setIsNewMember}) => {
  const [testToken, setTestToken] = useState('');

  const handleRegularLogin = async () => {
    try {
      if (!testToken) {
        Alert.alert('토큰 입력 오류', '테스트 토큰을 입력해주세요.');
        return;
      }

      const response = await fetchMemberInfo(testToken);

      if (response.isSuccess) {
        setAccessToken(testToken);
        setIsLoggedIn(true);
        setIsNewMember(false);

        await AsyncStorage.setItem('accessToken', testToken);

        Alert.alert(
          '로그인 성공',
          '테스트 토큰으로 성공적으로 로그인되었습니다.',
        );
      } else {
        Alert.alert('로그인 실패', '알맞지 않은 토큰입니다.');
      }
    } catch (err) {}
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>테스트 계정 로그인</Text>
      <View style={styles.separator} />

      <TextInput
        style={styles.input}
        placeholder="테스트 계정 토큰을 입력해 주세요."
        value={testToken}
        onChangeText={setTestToken}
        autoCapitalize="none"
        multiline={false}
        placeholderTextColor="#888"
        returnKeyType="done"
        onSubmitEditing={() => Keyboard.dismiss()}
      />

      <TouchableOpacity
        style={styles.completeButton}
        onPress={handleRegularLogin}>
        <Text style={styles.completeButtonText}>로그인</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
    paddingHorizontal: 20,
  },
  header: {
    fontSize: 18,
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
    marginTop: Platform.OS === 'ios' ? height * 0.01 : height * 0.01,
  },
  separator: {
    borderBottomWidth: 0.9,
    borderColor: '#e5e5e5',
    width: '90%',
    alignSelf: 'center',
    marginTop: height * 0.01,
    marginBottom: height * 0.1,
  },
  input: {
    width: '90%',
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
    marginBottom: 20,
    borderRadius: 8,
    backgroundColor: '#fff',
    height: 60,
    alignSelf: 'center',
  },
  completeButton: {
    width: '90%',
    marginTop: height * 0.01,
    backgroundColor: '#2f5af4',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  completeButtonText: {
    fontSize: 16,
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default TestLoginScreen;
