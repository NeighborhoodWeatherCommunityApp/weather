import React, {useEffect, useState} from 'react';
import {
  FlatList,
  Text,
  View,
  StyleSheet,
  Image,
  ActivityIndicator,
  Alert,
  TouchableOpacity,
  Dimensions,
  Platform,
} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchPosts, toggleLikePost} from '../api/api';

const {width, height} = Dimensions.get('window');

const aspectRatio = height / width;

const PostScroll = ({
  accessToken,
  refreshPosts,
  onRefreshComplete,
  refreshing = false,
  onRefresh,
}) => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const postType = 'WEATHER';

  const levelImages = {
    LV1: require('../../assets/images/LV1.png'),
    LV2: require('../../assets/images/LV2.png'),
    LV3: require('../../assets/images/LV3.png'),
    LV4: require('../../assets/images/LV4.png'),
    LV5: require('../../assets/images/LV5.png'),
    LV6: require('../../assets/images/LV6.png'),
  };

  useEffect(() => {
    loadPosts();
  }, []);

  useEffect(() => {
    if (refreshPosts) {
      loadPosts();
      if (onRefreshComplete) {
        onRefreshComplete();
      }
    }
  }, [refreshPosts]);

  const loadPosts = async () => {
    try {
      setLoading(true);
      const fetchedPosts = await fetchPosts(
        accessToken,
        postType,
        null,
        null,
        100000000,
      );
      console.log('Fetched scroll posts:', fetchedPosts);
      setPosts(fetchedPosts);
    } catch (error) {
      console.error('Error loading posts:', error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleLikePress = async postId => {
    /*
    setPosts(prevPosts =>
      prevPosts.map(post =>
        post.postInfo.postId === postId
          ? {
              ...post,
              postInfo: {
                ...post.postInfo,
                likeClickable: !post.postInfo.likeClickable,
                likeCount: post.postInfo.likeClickable
                  ? Math.max(post.postInfo.likeCount - 1, 0)
                  : post.postInfo.likeCount + 1,
              },
            }
          : post,
      ),
    );
    */

    try {
      await toggleLikePost(accessToken, postId);
      loadPosts();
    } catch (error) {
      console.error('Failed to like/unlike post:', error.message);
      Alert.alert(
        'Error',
        '서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.',
      );
    }
  };

  const getSensitivityBadge = sensitivity => {
    switch (sensitivity) {
      case 'HOT':
        return {
          text: '더위를 잘 타는 유형',
          backgroundColor: '#ffe5e5',
          color: '#e74c3c',
        };
      case 'COLD':
        return {
          text: '추위를 잘 타는 유형',
          backgroundColor: '#e6f0ff',
          color: '#3aa2e8',
        };
      case 'NONE':
      default:
        return {
          text: '평범한 유형',
          backgroundColor: '#e8f9e3',
          color: '#59bd83',
        };
    }
  };

  const renderPost = ({item}) => (
    <View style={styles.shadowContainer}>
      <Card containerStyle={[styles.card, globalStyles.transparentBackground]}>
        <View style={styles.header}>
          <Image
            source={
              item.memberInfo.profileImageUrl
                ? {uri: item.memberInfo.profileImageUrl}
                : require('../../assets/images/profile.png')
            }
            style={styles.profileImage}
          />
          <View style={styles.userInfoContainer}>
            <View style={styles.userRow}>
              <Text style={styles.username}>{item.memberInfo.memberName}</Text>
              {item.memberInfo.levelName &&
                levelImages[item.memberInfo.levelName] && (
                  <Image
                    source={levelImages[item.memberInfo.levelName]}
                    style={styles.levelBadge}
                    resizeMode="contain"
                  />
                )}

              {(() => {
                const badge = getSensitivityBadge(item.memberInfo.sensitivity);
                return (
                  <View
                    style={[
                      styles.sensitivityBadge,
                      {backgroundColor: badge.backgroundColor},
                    ]}>
                    <Text
                      style={[styles.sensitivityText, {color: badge.color}]}>
                      {badge.text}
                    </Text>
                  </View>
                );
              })()}
            </View>
            <Text style={styles.timeAgo}>{item.postInfo.createdAt}</Text>
          </View>
          <TouchableOpacity
            style={styles.likeContainer}
            onPress={() => handleLikePress(item.postInfo.postId)}>
            <Image
              source={
                item.postInfo.likeClickable
                  ? require('../../assets/images/icon_heart0.png')
                  : require('../../assets/images/icon_heart2.png')
              }
              style={[
                styles.likeIcon,
                {
                  tintColor: item.postInfo.likeClickable
                    ? '#d3d3d3'
                    : '#da4133',
                },
              ]}
            />
            <Text style={styles.likeCount}>{item.postInfo.likeCount}</Text>
          </TouchableOpacity>
        </View>
        <Text style={styles.content}>{item.postInfo.content}</Text>
      </Card>
    </View>
  );

  return (
    <FlatList
      data={posts}
      renderItem={renderPost}
      keyExtractor={item => item.postInfo.postId.toString()}
      refreshing={refreshing}
      onRefresh={onRefresh}
      contentContainerStyle={[
        styles.contentContainer,
        posts.length === 0 && !loading ? styles.emptyContainer : null,
      ]}
      ListEmptyComponent={
        loading ? (
          <View style={styles.loadingContainer}>
            <ActivityIndicator size="large" color="#999999" />
          </View>
        ) : (
          <View style={styles.emptyStateContainer}>
            <Text style={styles.emptyStateText}>
              아직 작성된 게시글이 없습니다.
            </Text>
          </View>
        )
      }
    />
  );
};

const styles = StyleSheet.create({
  shadowContainer: {
    justifyContent: 'center',
    alignItems: 'center',
    width: width * 0.94,
    backgroundColor: '#fff',
    borderRadius: 10,
    marginVertical: 10,
    marginHorizontal: 10,
    marginBottom: 1,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  card: {
    borderRadius: 10,
    borderColor: '#fff',
    backgroundColor: '#fff',
    paddingTop: -15,
    padding: 15,
    width: '100%',
    minHeight: Platform.OS === 'ios' ? 130 : 140,
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
  userInfoContainer: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'center',
  },
  userRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  username: {
    fontWeight: 'bold',
    color: '#333',
    fontSize: 14,
    marginRight: 2,
  },
  userIcon: {
    width: 18,
    height: 18,
  },
  timeAgo: {
    fontSize: 12,
    color: '#888',
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
    marginTop: 10,
    color: '#444',
    fontSize: 14,
    lineHeight: 20,
  },
  contentContainer: {
    paddingBottom: 10,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 20,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyStateContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  emptyStateText: {
    fontSize: 16,
    color: '#888',
    textAlign: 'center',
    marginTop: 20,
  },
  sensitivityBadge: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 12,
    marginLeft: -2,
  },
  sensitivityText: {
    fontSize: 11,
    fontWeight: '500',
  },
  levelBadge: {
    width: 36,
    height: 16,
    marginLeft: -7,
  },
});

export default PostScroll;
