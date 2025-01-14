import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  ScrollView,
  Image,
  Alert,
} from 'react-native';
import {fetchPostTags, createPost, fetchMemberInfo} from '../api/api';

const PostCreationScreen = ({navigation, accessToken, route}) => {
  const {onPostCreated} = route.params || {};
  const [temperatureTags, setTemperatureTags] = useState([]);
  const [weatherTags, setWeatherTags] = useState([]);
  const [humidityTags, setHumidityTags] = useState([]);
  const [windTags, setWindTags] = useState([]);
  const [airQualityTags, setAirQualityTags] = useState([]);

  const [temperature, setTemperature] = useState(null);
  const [weather, setWeather] = useState(null);
  const [humidity, setHumidity] = useState(null);
  const [wind, setWind] = useState(null);
  const [airQuality, setAirQuality] = useState(null);
  const [description, setDescription] = useState('');

  const [nickname, setNickname] = useState('');
  const [profileImage, setProfileImage] = useState(null);

  useEffect(() => {
    const loadTags = async () => {
      try {
        const tags = await fetchPostTags(accessToken);
        setTemperatureTags(tags.TemperatureTag);
        setWeatherTags(tags.SkyTag);
        setHumidityTags(tags.HumidityTag);
        setWindTags(tags.WindTag);
        setAirQualityTags(tags.DustTag);
      } catch (error) {
        console.error('Failed to fetch tags:', error);
      }
    };

    const loadMemberInfo = async () => {
      try {
        const memberInfo = await fetchMemberInfo(accessToken);
        setNickname(memberInfo.result.nickname || '사용자');
        setProfileImage(
          memberInfo.result.profileImage?.startsWith('http')
            ? {uri: memberInfo.result.profileImage}
            : require('../../assets/images/profile.png'),
        );
      } catch (error) {
        console.error('Failed to fetch member info:', error);
        setProfileImage(require('../../assets/images/profile.png'));
      }
    };

    loadTags();
    loadMemberInfo();
  }, [accessToken]);

  const handleTagPress = (type, selectedTag) => {
    switch (type) {
      case 'temperature':
        setTemperature(selectedTag.code);
        break;
      case 'weather':
        setWeather(selectedTag.code);
        break;
      case 'humidity':
        setHumidity(selectedTag.code);
        break;
      case 'wind':
        setWind(selectedTag.code);
        break;
      case 'airQuality':
        setAirQuality(selectedTag.code);
        break;
      default:
        break;
    }
  };

  const handleSubmit = async () => {
    if (!temperature || !weather || !humidity || !wind || !airQuality) {
      Alert.alert('태그 선택', '아직 선택하지 않은 태그가 있어요.');
      return;
    }

    const postData = {
      content: description,
      temperatureTagCode: temperature,
      skyTagCode: weather,
      humidityTagCode: humidity,
      windTagCode: wind,
      dustTagCode: airQuality,
    };

    try {
      const response = await createPost(postData, accessToken);
      console.log('Post created successfully:', response);

      if (onPostCreated) {
        onPostCreated();
      }

      navigation.popToTop();
      navigation.jumpTo('Community');
    } catch (error) {
      console.error('Failed to create post:', error.message);
    }
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <Image source={profileImage} style={styles.profileImage} />
        <Text style={styles.nickname}>{nickname}</Text>
      </View>

      <View style={styles.separator} />

      <TextInput
        style={styles.textInput}
        placeholder="현재 날씨가 어떤지, 오늘 입은 옷 등을 공유해 주세요"
        placeholderTextColor="#888"
        multiline
        value={description}
        onChangeText={setDescription}
      />

      <View style={styles.section}>
        <Text style={styles.label}>온도는 어떤가요?</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          <View style={styles.tagContainer}>
            {temperatureTags.map(tag => (
              <TouchableOpacity
                key={tag.code}
                style={[
                  styles.tag,
                  temperature === tag.code && styles.selectedTag,
                ]}
                onPress={() => handleTagPress('temperature', tag)}>
                <Text
                  style={[
                    styles.tagText,
                    temperature === tag.code && styles.selectedTagText,
                  ]}>
                  {tag.text}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </ScrollView>
      </View>

      <View style={styles.section}>
        <Text style={styles.label}>날씨는 어떤가요?</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          <View style={styles.tagContainer}>
            {weatherTags.map(tag => (
              <TouchableOpacity
                key={tag.code}
                style={[styles.tag, weather === tag.code && styles.selectedTag]}
                onPress={() => handleTagPress('weather', tag)}>
                <Text
                  style={[
                    styles.tagText,
                    weather === tag.code && styles.selectedTagText,
                  ]}>
                  {tag.text}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </ScrollView>
      </View>

      <View style={styles.section}>
        <Text style={styles.label}>습한가요?</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          <View style={styles.tagContainer}>
            {humidityTags.map(tag => (
              <TouchableOpacity
                key={tag.code}
                style={[
                  styles.tag,
                  humidity === tag.code && styles.selectedTag,
                ]}
                onPress={() => handleTagPress('humidity', tag)}>
                <Text
                  style={[
                    styles.tagText,
                    humidity === tag.code && styles.selectedTagText,
                  ]}>
                  {tag.text}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </ScrollView>
      </View>

      <View style={styles.section}>
        <Text style={styles.label}>바람은 어떤가요?</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          <View style={styles.tagContainer}>
            {windTags.map(tag => (
              <TouchableOpacity
                key={tag.code}
                style={[styles.tag, wind === tag.code && styles.selectedTag]}
                onPress={() => handleTagPress('wind', tag)}>
                <Text
                  style={[
                    styles.tagText,
                    wind === tag.code && styles.selectedTagText,
                  ]}>
                  {tag.text}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </ScrollView>
      </View>

      <View style={styles.section}>
        <Text style={styles.label}>미세먼지는 어떤가요?</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          <View style={styles.tagContainer}>
            {airQualityTags.map(tag => (
              <TouchableOpacity
                key={tag.code}
                style={[
                  styles.tag,
                  airQuality === tag.code && styles.selectedTag,
                ]}
                onPress={() => handleTagPress('airQuality', tag)}>
                <Text
                  style={[
                    styles.tagText,
                    airQuality === tag.code && styles.selectedTagText,
                  ]}>
                  {tag.text}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </ScrollView>
      </View>

      <TouchableOpacity style={styles.shareButton} onPress={handleSubmit}>
        <Text style={styles.shareButtonText}>공유하기</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  contentContainer: {
    padding: 20,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 20,
    marginTop: Platform.OS === 'ios' ? 15 : 10,
  },
  profileImage: {
    width: 50,
    height: 50,
    borderRadius: 25,
    marginRight: 10,
  },
  nickname: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  separator: {
    height: 1,
    backgroundColor: '#E5E7EB',
    marginBottom: 20,
  },
  textInput: {
    height: 100,
    borderColor: '#E5E7EB',
    borderWidth: 1,
    borderRadius: 16,
    padding: 10,
    marginBottom: 20,
    textAlignVertical: 'top',
    color: '#333',
  },
  section: {
    marginBottom: 20,
  },
  label: {
    fontSize: Platform.OS === 'ios' ? 15 : 16,
    marginBottom: 10,
    color: Platform.OS === 'ios' ? '#494949' : '#494949',
  },
  tagContainer: {
    flexDirection: 'row',
  },
  tag: {
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 20,
    backgroundColor: '#f0f0f0',
    marginRight: 8,
  },
  tagText: {
    fontSize: 14,
    color: '#333',
  },
  selectedTag: {
    backgroundColor: '#2f5af4',
  },
  selectedTagText: {
    color: '#fff',
  },
  shareButton: {
    backgroundColor: '#2f5af4',
    borderRadius: 8,
    padding: 12,
    alignItems: 'center',
    marginTop: 10,
  },
  shareButtonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default PostCreationScreen;
