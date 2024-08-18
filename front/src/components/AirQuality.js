import React from 'react';
import {View, Text, StyleSheet} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import globalStyles from '../globalStyles';

const AirQuality = () => {
  return (
    <View style={styles.container}>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>초미세먼지</Text>
        <Text style={styles.value}>매우 나쁨</Text>
        {/* <Text style={styles.data}>120</Text> */}
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>미세먼지</Text>
        <Text style={styles.value}>보통</Text>
        {/* <Text style={styles.data}>75</Text> */}
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>자외선</Text>
        <Text style={styles.value}>좋음</Text>
        {/* <Text style={styles.data}>7</Text> */}
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>오존</Text>
        <Text style={styles.value}>매우 나쁨</Text>
        {/* <Text style={styles.data}>200</Text> */}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 5,
    marginVertical: 5,
    padding: 9,
    borderRadius: 10,
  },
  box: {
    borderRadius: 10,
    padding: 10,
    alignItems: 'center',
    margin: 3,
    width: '23%',
  },
  title: {
    fontSize: 13,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 8,
  },
  value: {
    fontSize: 12,
    color: '#fff',
    marginBottom: 5,
  },
  data: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
  },
});

export default AirQuality;
