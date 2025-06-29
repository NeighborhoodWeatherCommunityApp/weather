import {Platform} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import CryptoJS from 'crypto-js';
import {GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET} from '@env';

const CLIENT_ID = GOOGLE_CLIENT_ID; // OAuth Client ID
const CLIENT_SECRET = GOOGLE_CLIENT_SECRET; // OAuth Client Secret

const SPREADSHEET_ID = '1SGBecmEf-SBUaUu81T4fKtI0AVOeJIBQ-VtSZvu6aZY'; // 스프레드시트 ID
const SHEET_NAME = 'Sheet1';

const getKSTTimestamp = () => {
  const date = new Date();
  const koreaTime = new Date(date.getTime() + 9 * 60 * 60 * 1000);
  return koreaTime.toISOString().replace('T', ' ').substring(0, 19);
};

const refreshAccessToken = async () => {
  try {
    const refreshToken = await AsyncStorage.getItem('googleRefreshToken');

    if (!refreshToken) throw new Error('Google Refresh Token이 없습니다.');

    const params = new URLSearchParams({
      client_id: CLIENT_ID,
      client_secret: CLIENT_SECRET,
      refresh_token: refreshToken,
      grant_type: 'refresh_token',
    });
    // console.log('토큰 요청 파라미터:', params.toString());

    const response = await fetch('https://oauth2.googleapis.com/token', {
      method: 'POST',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body: params.toString(),
    });

    if (!response.ok) throw new Error('Access Token 갱신 실패');

    const data = await response.json();

    await AsyncStorage.setItem('accessTokenForSheets', data.access_token);
    console.log('Access Token for googleSheets 갱신 완료');

    await logRefreshTokenUsage(); // RefreshToken 사용 기록
    return data.access_token;
  } catch (error) {
    console.error('Access Token for googleSheets갱신 실패:', error.message);
    return null;
  }
};

// Google Sheets에 데이터 추가
const appendToGoogleSheet = async (values, sheetName = SHEET_NAME) => {
  const sendRequest = async token => {
    const url = `https://sheets.googleapis.com/v4/spreadsheets/${SPREADSHEET_ID}/values/${sheetName}!A1:append?valueInputOption=USER_ENTERED`;
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({values: [values]}),
    });
    return response;
  };

  try {
    let accessToken = await AsyncStorage.getItem('accessTokenForSheets');
    if (!accessToken) throw new Error('Access Token 없음');

    let response = await sendRequest(accessToken);

    // 만료되었을 경우 자동 갱신 + 재시도
    if (response.status === 401) {
      const newAccessToken = await refreshAccessToken();
      if (!newAccessToken) throw new Error('AccessToken 재발급 실패');
      await AsyncStorage.setItem('accessTokenForSheets', newAccessToken);
      response = await sendRequest(newAccessToken);
    }

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(JSON.stringify(errorData));
    }

    const result = await response.json();
    console.log('시트 기록 성공:', result);
  } catch (error) {
    console.error('시트 기록 실패:', error.message);
  }
};

// RefreshToken 갱신 로그 기록
const logRefreshTokenUsage = async () => {
  const time = getKSTTimestamp();
  const values = [
    time,
    'system',
    'refresh_used',
    '-',
    '-',
    '-',
    '-',
    '-',
    Platform.OS,
  ];

  await appendToGoogleSheet(values, 'Sheet2');
};

// 앱 접속 로그 기록
export const logUserAction = async (userInfo, actionName) => {
  const {email, nickname, exp, levelKey, rankName, province, city, street} =
    userInfo;

  // 이메일 SHA-256 해싱
  const hashedEmail = CryptoJS.SHA256(email).toString(CryptoJS.enc.Hex);

  await appendToGoogleSheet([
    getKSTTimestamp(),
    hashedEmail,
    actionName,
    nickname,
    exp,
    levelKey,
    rankName,
    `${province} ${city} ${street}`,
    Platform.OS,
  ]);
};

// 출석 전용 로그 기록 - 앱 접속 로그와 정보 중복 제공 우려되어 미사용
export const logAttendCheck = async userId => {
  await appendToGoogleSheet([
    getKSTTimestamp(),
    userId,
    'attend_check',
    Platform.OS,
  ]);
};
