import {Platform} from 'react-native';
import CryptoJS from 'crypto-js';

// Function URL
const ENDPOINT =
  'https://asia-northeast3-custom-router-459400-c0.cloudfunctions.net/appendLog';

const POST_LOG_ENDPOINT =
  'https://asia-northeast3-custom-router-459400-c0.cloudfunctions.net/appendPostLog';

const getKSTTimestamp = () => {
  const now = new Date(Date.now() + 9 * 60 * 60 * 1000);
  return now.toISOString().replace('T', ' ').substring(0, 19);
};

async function appendToGoogleSheet(values) {
  console.log('[appendToGoogleSheet] POST', ENDPOINT, values);
  const res = await fetch(ENDPOINT, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({values: [values]}),
  });

  console.log('[appendToGoogleSheet] status', res.status);

  if (!res.ok) {
    console.error('[appendToGoogleSheet] error body', await res.text());
    throw new Error(`HTTP ${res.status}`);
  }
}

async function appendToPostSheet(values) {
  const res = await fetch(POST_LOG_ENDPOINT, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({values: [values]}),
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}: ${await res.text()}`);
}

export async function logUserAction(user, actionName) {
  const {email, nickname, exp, levelKey, rankName, province, city, street} =
    user;

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
}

export async function logPostCreation(user, actionName) {
  const {email, nickname, exp, levelKey, rankName, province, city, street} =
    user;

  const hashedEmail = CryptoJS.SHA256(email).toString(CryptoJS.enc.Hex);

  await appendToPostSheet([
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
}

export async function logAttendCheck(userId) {
  await appendToGoogleSheet([
    getKSTTimestamp(),
    userId,
    'attend_check',
    Platform.OS,
  ]);
}
