import React, {useState, useEffect} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createStackNavigator} from '@react-navigation/stack';
import HomeScreen from './src/screens/HomeScreen';
import CommunityScreen from './src/screens/CommunityScreen';
import MyScreen from './src/screens/MyScreen';
import TermsAgreementScreen from './src/screens/TermsAgreementScreen';
import RegisterProfileScreen from './src/screens/RegisterProfileScreen';
import PostCreationScreen from './src/screens/PostCreationScreen';
import TestLoginScreen from './src/screens/TestLoginScreen';
import LoginScreen from './src/screens/LoginScreen';
import ProfileScreen from './src/screens/ProfileScreen';
import TermsViewScreen from './src/screens/TermsViewScreen';
import {StatusBar, Image, Platform, View, StyleSheet} from 'react-native';
import {refreshAccessToken, fetchMemberInfo} from './src/api/api';
import AsyncStorage from '@react-native-async-storage/async-storage';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const AuthStack = ({
  setIsLoggedIn,
  setAccessToken,
  setIsNewMember,
  setIsDeleted,
  setIsProfileCompleted,
  isProfileCompleted,
}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="LoginScreen">
      {props => (
        <LoginScreen
          {...props}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsNewMember={setIsNewMember}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
          isProfileCompleted={isProfileCompleted}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="TestLoginScreen">
      {props => (
        <TestLoginScreen
          {...props}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="TermsAgreementScreen">
      {props => (
        <TermsAgreementScreen
          {...props}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsNewMember={setIsNewMember}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="RegisterProfileScreen">
      {props => (
        <RegisterProfileScreen
          {...props}
          setIsNewMember={setIsNewMember}
          setIsLoggedIn={setIsLoggedIn}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
        />
      )}
    </Stack.Screen>
  </Stack.Navigator>
);

const HomeStack = ({accessToken}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="HomeScreen">
      {props => <HomeScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
    <Stack.Screen name="PostCreationScreen">
      {props => <PostCreationScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
    <Stack.Screen name="TestLoginScreen" component={TestLoginScreen} />
  </Stack.Navigator>
);

const MyStack = ({
  accessToken,
  setIsNewMember,
  setLocationId,
  setIsLoggedIn,
  setAccessToken,
  setIsDeleted,
  setIsProfileCompleted,
}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="MyScreen">
      {props => (
        <MyScreen
          {...props}
          accessToken={accessToken}
          setIsNewMember={setIsNewMember}
          setLocationId={setLocationId}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="ProfileScreen">
      {props => <ProfileScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
    <Stack.Screen name="TermsViewScreen" component={TermsViewScreen} />
  </Stack.Navigator>
);

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isAutoLoggingIn, setIsAutoLoggingIn] = useState(true);
  const [accessToken, setAccessToken] = useState(null);
  const [isNewMember, setIsNewMember] = useState(false);
  const [locationId, setLocationId] = useState(null);
  const [isDeleted, setIsDeleted] = useState(false);
  const [isProfileCompleted, setIsProfileCompleted] = useState(true);

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const storedAccessToken = await AsyncStorage.getItem('accessToken');
        const refreshToken = await AsyncStorage.getItem('refreshToken');
        const loginMethod = await AsyncStorage.getItem('loginMethod');

        console.log('Retrieved accessToken:', storedAccessToken);
        console.log('Retrieved refreshToken:', refreshToken);
        console.log('Login method:', loginMethod);

        if (storedAccessToken) {
          try {
            const memberInfoResponse = await fetchMemberInfo(storedAccessToken);
            console.log('회원 정보 응답:', memberInfoResponse);

            if (memberInfoResponse.isSuccess) {
              const memberData = memberInfoResponse;
              console.log('회원 정보:', memberData);

              setAccessToken(storedAccessToken);
              setIsLoggedIn(true);
            } else {
              console.error(
                'Failed to fetch member info:',
                memberInfoResponse.message,
              );
              setIsLoggedIn(false);
            }
          } catch (error) {
            console.error('Error fetching member info:', error.message);
            setIsLoggedIn(false);
          }
        }

        if (!storedAccessToken && refreshToken) {
          try {
            const newAccessToken = await refreshAccessToken(refreshToken);
            const memberInfoResponse = await fetchMemberInfo(newAccessToken);

            if (memberInfoResponse.isSuccess) {
              const memberData = memberInfoResponse.result;
              console.log('Access token 갱신 및 회원 정보:', memberData);

              setAccessToken(newAccessToken);
              setIsLoggedIn(true);
              await AsyncStorage.setItem('accessToken', newAccessToken);
            } else {
              console.error(
                'Failed to fetch member info with new token:',
                memberInfoResponse.message,
              );
              setIsLoggedIn(false);
            }
          } catch (error) {
            console.error('Token refresh failed:', error.message);
            await AsyncStorage.removeItem('accessToken');
            await AsyncStorage.removeItem('refreshToken');
            setIsLoggedIn(false);
          }
        }
      } catch (error) {
        console.error('자동 로그인 처리 중 오류 발생:', error.message);
        setIsLoggedIn(false);
      } finally {
        setIsAutoLoggingIn(false);
      }
    };

    checkLoginStatus();
  }, []);

  useEffect(() => {
    console.log(
      `현재 상태: isLoggedIn=${isLoggedIn}, isNewMember=${isNewMember}, isDeleted=${isDeleted}, isProfileCompleted=${isProfileCompleted}`,
    );
  }, [isLoggedIn, isNewMember, isDeleted, isProfileCompleted]);

  useEffect(() => {
    if (!isNewMember && isLoggedIn && isProfileCompleted) {
      console.log('Navigating to HomeStack as isNewMember is false');
    }

    (async () => {
      await AsyncStorage.removeItem('isProfileCompleted');
      console.log('isProfileCompleted has been removed from AsyncStorage');
    })();
  }, [isNewMember, isLoggedIn, isDeleted, isProfileCompleted]);

  if (isAutoLoggingIn) {
    return <View style={styles.autoLoginBackground} />;
  }

  if (isDeleted || !isLoggedIn || !isProfileCompleted) {
    return (
      <NavigationContainer>
        <AuthStack
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsNewMember={setIsNewMember}
          setIsDeleted={setIsDeleted}
          setIsProfileCompleted={setIsProfileCompleted}
          isProfileCompleted={isProfileCompleted}
        />
      </NavigationContainer>
    );
  }

  return (
    <>
      <StatusBar
        translucent
        backgroundColor="transparent"
        barStyle="dark-content"
      />
      <NavigationContainer>
        {isLoggedIn ? (
          isNewMember ? (
            <Stack.Navigator screenOptions={{headerShown: false}}>
              <Stack.Screen name="RegisterProfileScreen">
                {props => (
                  <RegisterProfileScreen
                    {...props}
                    accessToken={accessToken}
                    setIsNewMember={setIsNewMember}
                    setIsLoggedIn={setIsLoggedIn}
                    setIsProfileCompleted={setIsProfileCompleted}
                    setIsDeleted={setIsDeleted}
                  />
                )}
              </Stack.Screen>
            </Stack.Navigator>
          ) : (
            <Tab.Navigator
              initialRouteName="Home"
              screenOptions={({route}) => ({
                headerShown: false,
                tabBarIcon: ({focused, color}) => {
                  let iconSource;
                  let size;

                  switch (route.name) {
                    case 'HomeStack':
                      iconSource = require('./assets/images/icon_tab_home.png');
                      size = 26;
                      break;
                    case 'Community':
                      iconSource = require('./assets/images/icon_tab_community.png');
                      size = 24;
                      break;
                    case 'My':
                      iconSource = require('./assets/images/icon_tab_my.png');
                      size = 29;
                      break;
                    default:
                      size = 25;
                  }

                  return (
                    <Image
                      source={iconSource}
                      style={{
                        width: size,
                        height: size,
                        tintColor: focused ? '#3f51b5' : color,
                      }}
                    />
                  );
                },
                tabBarActiveTintColor: '#3f51b5',
                tabBarInactiveTintColor: 'gray',
                tabBarStyle: {
                  paddingTop: 5,
                  paddingBottom: 10,
                  height: 80,
                },
                tabBarLabelStyle: {
                  fontSize: Platform.OS === 'ios' ? 10 : 12,
                  paddingBottom: Platform.OS === 'ios' ? 18 : 10,
                },
              })}>
              <Tab.Screen
                name="HomeStack"
                options={{
                  tabBarLabel: '홈',
                }}>
                {props => <HomeStack {...props} accessToken={accessToken} />}
              </Tab.Screen>
              <Tab.Screen
                name="Community"
                options={{
                  tabBarLabel: '탐색',
                }}>
                {props => (
                  <CommunityScreen {...props} accessToken={accessToken} />
                )}
              </Tab.Screen>
              <Tab.Screen
                name="My"
                options={{
                  tabBarLabel: '프로필',
                }}>
                {props => (
                  <MyStack
                    {...props}
                    accessToken={accessToken}
                    setIsNewMember={setIsNewMember}
                    setLocationId={setLocationId}
                    setIsLoggedIn={setIsLoggedIn}
                    setAccessToken={setAccessToken}
                    setIsDeleted={setIsDeleted}
                    setIsProfileCompleted={setIsProfileCompleted}
                  />
                )}
              </Tab.Screen>
            </Tab.Navigator>
          )
        ) : (
          <AuthStack
            setIsLoggedIn={setIsLoggedIn}
            setAccessToken={setAccessToken}
            setIsNewMember={setIsNewMember}
            setIsDeleted={setIsDeleted}
            setIsProfileCompleted={setIsProfileCompleted}
          />
        )}
      </NavigationContainer>
    </>
  );
};

const styles = StyleSheet.create({
  autoLoginBackground: {
    flex: 1,
    backgroundColor: '#f5f6fA',
  },
});

export default App;
