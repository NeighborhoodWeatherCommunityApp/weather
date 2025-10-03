/* 개발 서버 */
/* const BASE_URL = 'https://weather-community.shop'; */
/* 운영 서버 */

const BASE_URL = 'https://weather-community.store';

import AsyncStorage from '@react-native-async-storage/async-storage';

export const sendAccessTokenToBackend = async (accessToken, type) => {
  try {
    console.log('Sending access token and type to backend...');
    console.log('Request URL:', `${BASE_URL}/token`);
    console.log('Access token:', accessToken);
    console.log('Type:', type);

    const response = await fetch(`${BASE_URL}/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({accessToken, type}),
    });

    console.log('Response status:', response.status);

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Failed to send access token:', response.status, errorText);
      throw new Error(errorText || 'Failed to send access token to backend');
    }

    const data = await response.json();

    console.log('Backend response:', data);

    if (!data.isSuccess) {
      console.error('Backend error:', data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data;
  } catch (error) {
    console.error('Error sending access token:', error.message);
    throw error;
  }
};

export const refreshAccessToken = async () => {
  try {
    const accessToken = await AsyncStorage.getItem('accessToken');
    const refreshToken = await AsyncStorage.getItem('refreshToken');

    if (!accessToken || !refreshToken) {
      throw new Error('로컬 토큰이 없습니다.');
    }

    console.log('[refreshAccessToken] sending →', {
      accessToken,
      refreshToken,
    });

    const res = await fetch(`${BASE_URL}/refreshToken`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({accessToken, refreshToken}),
    });

    const data = await res.json();
    console.log('[refreshAccessToken] response', data);

    if (!data?.isSuccess || !data?.result) {
      throw new Error(data?.message || '토큰 재발급 실패');
    }

    const {accessToken: newAccessToken, refreshToken: newRefreshToken} =
      data.result;

    if (!newAccessToken) throw new Error('accessToken 누락');

    await AsyncStorage.setItem('accessToken', newAccessToken);

    // 새 refreshToken 이 오면 만료 3 일 이하 ->  덮어쓰기
    if (newRefreshToken) {
      await AsyncStorage.setItem('refreshToken', newRefreshToken);
      console.log('refreshToken 갱신 완료');
    } else {
      console.log('refreshToken 그대로 유지');
    }

    return {
      accessToken: newAccessToken,
      refreshToken: newRefreshToken ?? refreshToken,
    };
  } catch (err) {
    console.error('[refreshAccessToken] 실패:', err);
    throw err;
  }
};

export const fetchWeatherData = async (accessToken, locationId) => {
  try {
    const url = locationId
      ? `${BASE_URL}/api/v1/main/weather?locationId=${locationId}`
      : `${BASE_URL}/api/v1/main/weather`;

    console.log('Attempting weather data API:', url);

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch weather data:',
        response.status,
        errorText,
      );
      throw new Error(`Failed to fetch weather data: ${errorText}`);
    }

    const data = await response.json();

    if (!data.isSuccess) {
      console.error('Backend error:', data.code, data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data;
  } catch (error) {
    console.error('Error fetching weather data:', error.message);
    throw error;
  }
};

export const sendLocationToBackend = async (
  longitude,
  latitude,
  accessToken,
) => {
  const getKSTISOString = () => {
    const now = new Date();
    const kstOffset = 9 * 60 * 60 * 1000;
    const kstTime = new Date(now.getTime() + kstOffset);
    return kstTime.toISOString();
  };

  const requestTime = getKSTISOString();
  console.log(`[${requestTime} KST] Sending location data:`, {
    latitude,
    longitude,
  });

  try {
    const postData = {latitude, longitude};

    // console.log('Sending location data:', postData);

    const response = await fetch(`${BASE_URL}/api/v1/location/coor`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(postData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to send location data:',
        response.status,
        errorText,
      );
      throw new Error(errorText || 'Failed to send location data to backend');
    }

    const data = await response.json();

    if (!data.isSuccess) {
      console.error('Backend error:', data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data;
  } catch (error) {
    console.error('Error sending location data:', error.message);
    throw error;
  }
};

export const createPost = async (postData, accessToken) => {
  const url = `${BASE_URL}/api/v1/post`;

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(postData),
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error('Failed to create post:', response.status, errorResponse);
      throw new Error('Failed to create post');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error creating post:', error);
    throw error;
  }
};

export const createInterestPost = async (postData, accessToken) => {
  const url = `${BASE_URL}/api/v1/post/hobby`;

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(postData),
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error(
        'Failed to create interest post:',
        response.status,
        errorResponse,
      );
      throw new Error('Failed to create interest post');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error creating interest post:', error);
    throw error;
  }
};

export const fetchPosts = async (
  accessToken,
  postType = 'WEATHER',
  lastPostId = null,
  locationId = null,
  size = null,
) => {
  try {
    let url = `${BASE_URL}/api/v1/community/posts?size=${size}&postType=${postType}`;

    if (lastPostId !== null) {
      url += `&lastPostId=${lastPostId}`;
    }

    if (locationId) {
      url += `&locationId=${locationId}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Failed to fetch posts:', response.status, errorText);
      throw new Error('Failed to fetch posts');
    }

    const data = await response.json();
    return data.result?.postList || [];
  } catch (error) {
    console.error('Error fetching posts:', error);
    throw error;
  }
};

export const fetchPopularPosts = async (
  accessToken,
  lastPostId = null,
  size = 10,
) => {
  try {
    let url = `${BASE_URL}/api/v1/main/posts/popular?&size=${size}`;

    if (lastPostId) {
      url += `&lastPostId=${lastPostId}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error(
        'Failed to fetch popular posts:',
        response.status,
        errorResponse,
      );
      throw new Error('Failed to fetch popular posts');
    }

    const data = await response.json();
    return data.result;
  } catch (error) {
    console.error('Error fetching popular posts:', error);
    throw error;
  }
};

export const toggleLikePost = async (accessToken, postId) => {
  try {
    const url = `${BASE_URL}/api/v1/post/recommendation?&postId=${postId}`;

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error('Failed to like post:', response.status, errorResponse);
      throw new Error('Failed to like post');
    }

    const data = await response.json();
    console.log('toggleLikePost 좋아요 응답:', data);
    return data;
  } catch (error) {
    console.error('Error liking post:', error);
    throw error;
  }
};

export const fetchWeatherTags = async accessToken => {
  try {
    const url = `${BASE_URL}/api/v1/main/weather/simple/tags?`;

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch weather tags:',
        response.status,
        errorText,
      );
      throw new Error('Failed to fetch weather tags');
    }

    const data = await response.json();
    return data.result;
  } catch (error) {
    console.error('Error fetching weather tags:', error);
    throw error;
  }
};

export const fetchPostTags = async accessToken => {
  const url = `${BASE_URL}/api/v1/tags`;

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.text();
      console.error('Failed to fetch tags:', response.status, errorResponse);
      throw new Error('Failed to fetch tags');
    }

    const data = await response.json();
    return data.result;
  } catch (error) {
    console.error('Error fetching tags:', error);
    throw error;
  }
};

export const fetchRainForecast = async accessToken => {
  try {
    const url = `${BASE_URL}/api/v1/main/weather/simple/rain?`;

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch rain forecast:',
        response.status,
        errorText,
      );
      throw new Error('Failed to fetch rain forecast');
    }

    const data = await response.json();
    if (!data.isSuccess) {
      console.error('Backend error:', data.code, data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data.result;
  } catch (error) {
    console.error('Error fetching rain forecast:', error.message);
    throw error;
  }
};

export const fetchUserLocation = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/location/defaultLoc`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch user location:',
        response.status,
        errorText,
      );
      throw new Error(`Failed to fetch user location: ${errorText}`);
    }

    const data = await response.json();
    if (!data.isSuccess) {
      console.error('Backend error:', data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data.result;
  } catch (error) {
    console.error('Error fetching user location:', error.message);
    throw error;
  }
};

export const fetchExtraWeatherInfo = async (accessToken, locationId) => {
  try {
    const url = locationId
      ? `${BASE_URL}/api/v1/main/extraWeatherInfo?locationId=${locationId}`
      : `${BASE_URL}/api/v1/main/extraWeatherInfo`;

    console.log('Attempting extre weather info API:', url);

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();
    if (result.isSuccess) {
      console.log('Fetched extra weather info:', result.result);
      return result.result;
    } else {
      console.error('Backend error:', result.message);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error fetching extra weather info:', error);
    throw error;
  }
};

export const fetchMemberInfo = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/member/info`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();
    // console.log('Fetch memeber info api:', result);

    if (result.isSuccess) {
      return result;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error fetching member info:', error);
    throw error;
  }
};

export const registerProfile = async (
  nickname,
  sensitivity,
  profileImage,
  accessToken,
) => {
  const formData = new FormData();

  formData.append('nickname', nickname);
  formData.append('sensitivity', sensitivity);

  if (profileImage) {
    formData.append('profileImg', {
      uri: profileImage.uri,
      type: profileImage.type || 'image/jpeg',
      name: profileImage.name || 'profile.jpg',
    });
  }

  formData._parts.forEach(part => {
    console.log(`FormData key: ${part[0]}, value: ${JSON.stringify(part[1])}`);
  });

  try {
    const response = await fetch(`${BASE_URL}/api/v1/member/info`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      body: formData,
    });

    console.log('Full Response:', response);

    const responseData = await response.json();

    console.log('Parsed Response Data:', responseData);

    if (!response.ok) {
      console.error('Server Error Details:', responseData);
      throw new Error(responseData.message || '프로필 저장 실패');
    }

    return responseData;
  } catch (error) {
    console.error('Error registering profile:', error);
    throw error;
  }
};

export const fetchLocationInfo = async (
  accessToken,
  province = '',
  city = '',
) => {
  const url = `${BASE_URL}/api/v1/location/locationInfo?province=${province}&city=${city}`;

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error(
        'Failed to fetch location info:',
        response.status,
        errorResponse,
      );
      throw new Error('Failed to fetch location info');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching location info:', error);
    throw error;
  }
};

export const submitAddress = async (accessToken, province, city, street) => {
  const url = `${BASE_URL}/api/v1/location/locationInfo`;

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify({province, city, street}),
    });

    const data = await response.json();

    if (!response.ok) {
      console.error('Failed to submit address:', data);
      throw new Error('Failed to submit address');
    }
    return data;
  } catch (error) {
    console.error('Error submitting address:', error);
    throw error;
  }
};

export const deleteMember = async (
  accessToken,
  loginMethod,
  authenticationCode = null,
) => {
  const url = `${BASE_URL}/api/v1/member`;

  console.log('Attempting to delete member with API:', url);
  console.log('Authorization header:', `Bearer ${accessToken}`);
  console.log('Login method:', loginMethod);

  if (!['apple', 'kakao'].includes(loginMethod)) {
    console.error('Invalid login method:', loginMethod);
    throw new Error('유효하지 않은 로그인 방식입니다.');
  }

  try {
    const options = {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    };

    if (loginMethod === 'apple' && authenticationCode) {
      options.body = JSON.stringify({authenticationCode});
    }

    console.log('Request details:', {
      url,
      headers: options.headers,
      method: options.method,
      body: options.body || null,
    });

    const response = await fetch(url, options);

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error('Failed to delete member:', errorResponse);

      if (response.status === 400) {
        throw new Error('잘못된 요청입니다.');
      } else if (response.status === 401) {
        throw new Error('인증 정보가 유효하지 않습니다.');
      } else if (response.status === 403) {
        throw new Error('권한이 없습니다.');
      } else if (response.status >= 500) {
        throw new Error('서버 에러가 발생했습니다. 관리자에게 문의하세요.');
      } else {
        throw new Error('알 수 없는 에러가 발생했습니다.');
      }
    }

    return await response.json();
  } catch (error) {
    console.error('Error Deleting member:', error);
    throw error;
  }
};

export const registerTermsAgreement = async (accessToken, agreements) => {
  const payload = {
    isServiceTermsAgreed: agreements.isServiceTermsAgreed,
    isPrivacyPolicyAgreed: agreements.isPrivacyPolicyAgreed,
    isLocationServiceTermsAgreed: agreements.isLocationServiceTermsAgreed,
    isPushNotificationAgreed: agreements.isPushNotificationAgreed,
  };

  try {
    const response = await fetch(`${BASE_URL}/api/v1/member/terms`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    const responseData = await response.json();

    if (!response.ok) {
      console.error('Server Error Details:', responseData);
      throw new Error(responseData.message || '약관 동의 실패');
    }

    return responseData;
  } catch (error) {
    console.error('Error registering terms agreement:', error);
    throw error;
  }
};

// 출석 체크 API
export const checkInAttendance = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/attendance/check-in`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();
    if (result.isSuccess) {
      console.log('출석 체크 API 호출 성공:', result);
      return result;
    } else {
      console.warn('출석 체크 API 호출 실패 (isSuccess = false):', result);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error during attendance check-in:', error);
    throw error;
  }
};

// 카카오 공유 경험치 획득 API
export const rewardKakaoShare = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/epx/rewards/share-kakao`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();
    console.log('카카오 공유 경험치 지급 API 응답:', result);

    if (result?.isSuccess) {
      return result;
    } else {
      console.warn('카카오 공유 경험치 지급 실패:', result?.message);
      throw new Error(result?.message || 'Unknown error');
    }
  } catch (error) {
    console.error('카카오 공유 경험치 API 호출 오류:', error?.message || error);
    throw error;
  }
};

// 선택 여부가 포함된 태그 조회 API
export const fetchSelectedTags = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/selected-tags`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();

    if (result.isSuccess) {
      console.log('선택된 태그 조회 API 호출 성공:', result.result);
      return result.result;
    } else {
      console.warn('선택된 태그 조회 API 호출 실패 (isSuccess=false):', result);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('선택된 태그 조회 API 호출 오류:', error);
    throw error;
  }
};

// 마이페이지 진입시 레벨업 여부를 확인 API
export const checkLevelUp = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/member/level`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();

    if (result.isSuccess) {
      console.log('레벨업 확인 API 호출 성공:', result.result);
      return result.result;
    } else {
      console.warn('레벨업 확인 API 호출 실패 (isSuccess=false):', result);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('레벨업 확인 API 호출 오류:', error);
    throw error;
  }
};

// 알림 기능 관련
// 알림 생성 API
export const createAlarmSetting = async (accessToken, alarmData) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v2/alarm`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(alarmData),
    });

    const responseData = await response.json();
    console.log('[POST] 알림 생성 API 응답:', responseData);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return responseData;
  } catch (error) {
    console.error('알림 생성 API 호출 실패:', error);
    throw error;
  }
};

// 알림 조회 API
export const fetchAlarmSetting = async (accessToken, fcmToken) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v2/alarm/by-fcm`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({fcmToken}),
    });

    const responseData = await response.json();
    console.log('[POST] 알림 조회 API 응답:', responseData);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return responseData.result;
  } catch (error) {
    console.error('알림 조회 API 호출 실패:', error);
    throw error;
  }
};

// 알림 수정 API
export const updateAlarmSetting = async (accessToken, alarmData) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v2/alarm`, {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(alarmData),
    });

    const responseData = await response.json();
    console.log('[PATCH] 알림 수정 API 응답:', responseData);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return responseData;
  } catch (error) {
    console.error('알림 수정 API 호출 실패:', error);
    throw error;
  }
};

// 테스트 알림 호출 API
export const sendTestAlarm = async (accessToken, fcmToken) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v2/testAlarm`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({fcmToken}),
    });

    const result = await response.json();
    console.log('테스트 알림 호출 API 응답:', result);

    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return result;
  } catch (error) {
    console.error('테스트 알림 API 호출 실패:', error);
    throw error;
  }
};
