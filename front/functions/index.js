// functions/index.js
const {onRequest} = require('firebase-functions/v2/https');
const {google} = require('googleapis');

const SPREADSHEET_ID = '1SGBecmEf-SBUaUu81T4fKtI0AVOeJIBQ-VtSZvu6aZY';
const SHEET_NAME = 'Sheet1';
const SHEET_NAME_POST = 'PostLogs';

exports.appendLog = onRequest({region: 'asia-northeast3'}, async (req, res) => {
  try {
    const auth = new google.auth.GoogleAuth({
      scopes: ['https://www.googleapis.com/auth/spreadsheets'],
    });
    const client = await auth.getClient();
    const sheets = google.sheets({version: 'v4', auth: client});

    const {values} = req.body;
    if (!Array.isArray(values))
      throw new Error('req.body.values 가 배열이 아닙니다');

    await sheets.spreadsheets.values.append({
      spreadsheetId: SPREADSHEET_ID,
      range: `${SHEET_NAME}!A1`,
      valueInputOption: 'USER_ENTERED',
      requestBody: {values},
    });

    return res.status(200).send('ok');
  } catch (e) {
    console.error(e);
    return res.status(500).send(String(e));
  }
});

exports.appendPostLog = onRequest(
  {region: 'asia-northeast3'},
  async (req, res) => {
    try {
      const auth = new google.auth.GoogleAuth({
        scopes: ['https://www.googleapis.com/auth/spreadsheets'],
      });
      const client = await auth.getClient();
      const sheets = google.sheets({version: 'v4', auth: client});

      const {values} = req.body;
      if (!Array.isArray(values) || values.length === 0)
        throw new Error('req.body.values 가 비어 있습니다');

      await sheets.spreadsheets.values.append({
        spreadsheetId: SPREADSHEET_ID,
        range: `${SHEET_NAME_POST}!A1`,
        valueInputOption: 'USER_ENTERED',
        requestBody: {values},
      });

      return res.status(200).send('ok');
    } catch (e) {
      console.error(e);
      return res.status(500).send(String(e));
    }
  },
);
