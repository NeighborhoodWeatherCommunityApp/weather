import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createStackNavigator} from '@react-navigation/stack';
import HomeScreen from './src/screens/HomeScreen';
import CommunityScreen from './src/screens/CommunityScreen';
import MyScreen from './src/screens/MyScreen';
import PostCreationScreen from './src/screens/PostCreationScreen';
import Icon from 'react-native-vector-icons/Ionicons';
import {StatusBar} from 'react-native';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const HomeStack = () => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="Home" component={HomeScreen} />
    <Stack.Screen name="PostCreationScreen" component={PostCreationScreen} />
  </Stack.Navigator>
);

const App = () => {
  return (
    <>
      <StatusBar hidden={true} />
      <NavigationContainer>
        <Tab.Navigator screenOptions={{headerShown: false}}>
          <Tab.Screen
            name="Community"
            component={CommunityScreen}
            options={{
              tabBarIcon: ({color, size}) => (
                <Icon name="people" color={color} size={size} />
              ),
            }}
          />
          <Tab.Screen
            name="HomeStack"
            component={HomeStack}
            options={{
              tabBarIcon: ({color, size}) => (
                <Icon name="home" color={color} size={size} />
              ),
              tabBarLabel: 'Home',
            }}
          />
          <Tab.Screen
            name="My"
            component={MyScreen}
            options={{
              tabBarIcon: ({color, size}) => (
                <Icon name="person" color={color} size={size} />
              ),
            }}
          />
        </Tab.Navigator>
      </NavigationContainer>
    </>
  );
};

export default App;
