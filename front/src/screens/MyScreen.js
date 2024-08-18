import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Image,
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';

const MyScreen = () => {
  const [selectedType, setSelectedType] = useState(null);

  const handleTypeSelection = type => {
    setSelectedType(type);
  };

  return (
    <View style={styles.container}>
      <View style={styles.profileContainer}>
        <Image
          source={{uri: 'https://via.placeholder.com/100'}}
          style={styles.profileImage}
        />
        <TouchableOpacity style={styles.editIconContainer}>
          <Icon name="add-circle-outline" size={30} color="#2f5af4" />
        </TouchableOpacity>
      </View>
      <Text style={styles.label}>닉네임</Text>
      <TextInput
        style={styles.input}
        value="누구누구"
        placeholder="닉네임을 입력하세요"
        editable={false}
      />
      <Text style={styles.label}>유형</Text>
      <TouchableOpacity
        style={[
          styles.typeButton,
          selectedType === 'hot' && styles.selectedButton,
        ]}
        onPress={() => handleTypeSelection('hot')}>
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
        onPress={() => handleTypeSelection('normal')}>
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
        onPress={() => handleTypeSelection('cold')}>
        <Text
          style={[
            styles.typeButtonText,
            selectedType === 'cold' && styles.selectedButtonText,
          ]}>
          추위를 많이 타는 편
        </Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.submitButton}>
        <Text style={styles.submitButtonText}>수정하기</Text>
      </TouchableOpacity>
    </View>
  );
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

export default MyScreen;
