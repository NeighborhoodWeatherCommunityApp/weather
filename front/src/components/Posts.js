import React, {useEffect, useState} from 'react';
import {
  FlatList,
  Text,
  View,
  StyleSheet,
  Image,
  Alert,
  TouchableOpacity,
  Dimensions,
} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {fetchPopularPosts, toggleLikePost} from '../api/api';

const {width} = Dimensions.get('window');

const CARD_WIDTH = width * 0.92;
const CARD_MARGIN = 10;

const Posts = ({accessToken, refreshing}) => {
  const navigation = useNavigation();
  const [newPosts, setNewPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadPosts = async () => {
      try {
        const posts = await fetchPopularPosts(accessToken);

        if (posts.length === 0) {
          setNewPosts([
            {
              memberInfo: {
                city: 'city',
                memberName: '알림 메시지',
                profileImageUrl: null,
                sensitivity: 'NONE',
                street: '',
              },
              postInfo: {
                postId: 'placeholder',
                content:
                  '아직 작성된 글이 없습니다.\n우리 동네 날씨를 가장 먼저 알려주세요!',
                createdAt: '방금 전',
                likeClickable: false,
                likeCount: 1,
              },
            },
          ]);
        } else {
          setNewPosts(posts);
          console.log('Fetched popular posts:', posts);
        }
      } catch (error) {
        console.error('Error fetching popular posts:', error.message);
      } finally {
        setLoading(false);
      }
    };

    loadPosts();
  }, [accessToken, refreshing]);

  const handleLikePress = async postId => {
    if (postId === 'placeholder') return;

    try {
      const response = await toggleLikePost(accessToken, postId);
      console.log('Like/unlike response:', response);

      if (response.isSuccess) {
        setNewPosts(prevPosts =>
          prevPosts.map(post =>
            post.postInfo.postId === postId
              ? {
                  ...post,
                  postInfo: {
                    ...post.postInfo,
                    likeClickable: !post.postInfo.likeClickable,
                    likeCount: post.postInfo.likeClickable
                      ? post.postInfo.likeCount + 1
                      : Math.max(post.postInfo.likeCount - 1, 0),
                  },
                }
              : post,
          ),
        );
      } else {
        Alert.alert('Error', '좋아요를 처리할 수 없습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('Failed to like/unlike post:', error.message);
      Alert.alert(
        'Error',
        '서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.',
      );
    }
  };

  const getUserIcon = sensitivity => {
    switch (sensitivity) {
      case 'HOT':
        return require('../../assets/images/icon_weather_clear.png');
      case 'NONE':
        return require('../../assets/images/icon_weather_partlycloudy.png');
      case 'COLD':
        return require('../../assets/images/icon_weather_snow.png');
      default:
        return null;
    }
  };

  const renderPost = ({item, index}) => (
    <View
      style={[
        styles.shadowContainer,
        index === 0 && {marginLeft: CARD_MARGIN * 0.5},
      ]}>
      <View style={styles.card}>
        <View style={styles.header}>
          <Image
            source={
              item.memberInfo.profileImageUrl
                ? {uri: item.memberInfo.profileImageUrl}
                : require('../../assets/images/profile.png')
            }
            style={styles.profileImage}
            onError={() => {}}
          />
          <View style={styles.userInfo}>
            <View style={styles.userRow}>
              <Text style={styles.username}>{item.memberInfo.memberName}</Text>
              <Image
                source={getUserIcon(item.memberInfo.sensitivity)}
                style={styles.userIcon}
              />
            </View>
            <Text style={styles.timeAgo}>{item.postInfo.createdAt}</Text>
          </View>
          <TouchableOpacity
            style={styles.likeContainer}
            onPress={() => handleLikePress(item.postInfo.postId)}
            disabled={item.postInfo.postId === 'placeholder'}>
            <Image
              source={
                !item.postInfo.likeClickable
                  ? require('../../assets/images/icon_heart2.png')
                  : require('../../assets/images/icon_heart0.png')
              }
              style={[
                styles.likeIcon,
                {
                  tintColor: !item.postInfo.likeClickable
                    ? '#da4133'
                    : '#d3d3d3',
                },
              ]}
            />
            <Text style={styles.likeCount}>{item.postInfo.likeCount}</Text>
          </TouchableOpacity>
        </View>
        <Text style={styles.content}>{item.postInfo.content}</Text>
      </View>
    </View>
  );

  return (
    <FlatList
      data={newPosts}
      keyExtractor={item => item.postInfo.postId.toString()}
      renderItem={renderPost}
      horizontal
      showsHorizontalScrollIndicator={false}
      snapToInterval={CARD_WIDTH + CARD_MARGIN}
      snapToAlignment="start"
      decelerationRate="fast"
      contentContainerStyle={{paddingHorizontal: CARD_MARGIN / 2}}
      ItemSeparatorComponent={() => <View style={{width: CARD_MARGIN}} />}
      ListFooterComponent={
        <View style={styles.footerShadowContainer}>
          <View style={styles.card}>
            <TouchableOpacity
              style={styles.moreContainer}
              onPress={() => navigation.navigate('Community')}>
              <Text style={styles.moreText}>더 보기</Text>
            </TouchableOpacity>
          </View>
        </View>
      }
    />
  );
};

const styles = StyleSheet.create({
  shadowContainer: {
    justifyContent: 'center',
    alignItems: 'center',
    width: CARD_WIDTH,
    backgroundColor: '#fff',
    borderRadius: 10,
    marginTop: 10,
    marginBottom: 10,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  footerShadowContainer: {
    justifyContent: 'center',
    alignItems: 'center',
    width: CARD_WIDTH,
    backgroundColor: '#fff',
    borderRadius: 10,
    marginVertical: 10,
    marginLeft: 15,
    marginRight: 10,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 6,
  },
  card: {
    width: '95%',
    backgroundColor: '#fff',
    borderRadius: 10,
    paddingVertical: 12,
    paddingHorizontal: 8,
    minHeight: Platform.OS === 'ios' ? 150 : 170,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 5,
  },
  profileImage: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 10,
  },
  userInfo: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'center',
  },
  userRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  username: {
    color: '#333',
    fontWeight: 'bold',
    fontSize: 14,
    marginRight: 2,
  },
  userIcon: {
    width: 18,
    height: 18,
  },
  timeAgo: {
    color: '#777',
    fontSize: 12,
    marginTop: 2,
  },
  likeContainer: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  likeIcon: {
    width: 20,
    height: 20,
  },
  likeCount: {
    color: '#777',
    fontSize: 12,
    textAlign: 'center',
    marginTop: 2,
  },
  content: {
    color: '#444',
    marginTop: 10,
    fontSize: 14,
    lineHeight: 20,
  },
  moreContainer: {
    justifyContent: 'center',
    alignItems: 'center',
    height: 110,
  },
  moreText: {
    color: '#3f51b5',
    fontSize: 16,
    textAlign: 'center',
  },
});

export default Posts;
