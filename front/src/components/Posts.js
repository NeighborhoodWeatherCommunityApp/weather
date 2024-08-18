import React, {useRef, useEffect} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  Dimensions,
  TouchableOpacity,
} from 'react-native';
import {Card} from 'react-native-elements';
import Icon from 'react-native-vector-icons/Ionicons';
import globalStyles from '../globalStyles';

const Posts = () => {
  const screenWidth = Dimensions.get('window').width;
  const scrollViewRef = useRef(null);

  useEffect(() => {
    if (scrollViewRef.current) {
      scrollViewRef.current.scrollTo({x: screenWidth, animated: false});
    }
  }, [screenWidth]);

  return (
    <ScrollView
      ref={scrollViewRef}
      horizontal
      pagingEnabled
      style={styles.container}
      contentContainerStyle={styles.contentContainer}>
      <View style={[styles.section, {width: screenWidth}]}>
        <Card
          containerStyle={[styles.card, globalStyles.transparentBackground]}>
          <Text style={styles.sectionText}>외출하셨나요?</Text>
          <Text style={styles.sectionText}>우리 동네의 날씨는 어떤가요?</Text>
          <View style={styles.buttonContainer}>
            <TouchableOpacity style={styles.button}>
              <Text style={styles.buttonText}>자세하게 공유하기</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.button}>
              <Text style={styles.buttonText}>간단하게 공유하기</Text>
            </TouchableOpacity>
          </View>
        </Card>
      </View>

      {[...Array(5)].map((_, i) => (
        <View key={i} style={[styles.section, {width: screenWidth}]}>
          <Card
            containerStyle={[styles.card, globalStyles.transparentBackground]}>
            <View style={styles.header}>
              <View style={styles.profileInfo}>
                <Image
                  source={require('../../assets/images/profile.png')}
                  style={styles.profileImage}
                />
                <View style={styles.userInfo}>
                  <View style={styles.userRow}>
                    <Text style={styles.username}>작성자 이름</Text>
                    <Icon
                      name="sunny-outline"
                      size={18}
                      color="#fff"
                      style={styles.userIcon}
                    />
                  </View>
                  <Text style={styles.timeAgo}>1시간 전</Text>
                </View>
              </View>
              <View style={styles.likeContainer}>
                <Icon
                  name="heart-outline"
                  size={20}
                  color="#fff"
                  style={styles.likeIcon}
                />
                <Text style={styles.likeCount}>34</Text>
              </View>
            </View>
            <Text style={styles.content}>
              글자 수 제한은 어떻게 하면 좋을지 고민
            </Text>
          </Card>
        </View>
      ))}

      <View style={[styles.section, {width: screenWidth}]}>
        <Card
          containerStyle={[styles.card, globalStyles.transparentBackground]}>
          <View style={styles.moreContainer}>
            <Text style={styles.moreText}>더 보기</Text>
          </View>
        </Card>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    marginTop: 2,
  },
  contentContainer: {
    alignItems: 'center',
  },
  section: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 15,
    marginHorizontal: 5,
    width: 350,
    height: 130,
    justifyContent: 'space-between',
    position: 'relative',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  profileInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  profileImage: {
    width: 35,
    height: 35,
    borderRadius: 20,
    marginRight: 10,
  },
  userInfo: {
    flex: 1,
  },
  userRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  username: {
    color: '#fff',
    fontWeight: 'bold',
  },
  userIcon: {
    marginLeft: 7,
  },
  timeAgo: {
    color: '#ddd',
    fontSize: 12,
    marginTop: 2,
  },
  likeContainer: {
    position: 'absolute',
    top: -3,
    right: 0,
    alignItems: 'center',
  },
  likeIcon: {
    marginBottom: 1,
  },
  likeCount: {
    color: '#fff',
    fontSize: 13,
  },
  content: {
    color: '#fff',
    marginTop: 13,
  },
  sectionText: {
    color: '#fff',
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 5,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 10,
  },
  button: {
    flex: 1,
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    shadowColor: 'transparent',
    elevation: 0,
    padding: 10,
    borderRadius: 5,
    marginHorizontal: 5,
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontSize: 14,
  },
  moreContainer: {
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    height: 110,
  },
  moreText: {
    color: '#fff',
    fontSize: 18,
    textAlign: 'center',
  },
});

export default Posts;
