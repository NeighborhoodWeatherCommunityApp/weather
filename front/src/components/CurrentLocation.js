import React from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {Icon} from 'react-native-elements';

const CurrentLocation = () => {
  return (
    <View style={styles.container}>
      <View style={styles.locationContainer}>
        <Text style={styles.location}>사상구</Text>
        <Text style={styles.subLocation}>모라동</Text>
      </View>
      <Icon name="sunny" type="ionicon" size={50} color="#FFD700" />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 10,
    marginTop: 15,
    flexDirection: 'row',
  },
  locationContainer: {
    alignItems: 'flex-end',
    marginRight: 17,
  },
  location: {
    fontSize: 22,
    color: '#fff',
    marginTop: 10,
    marginBottom: 7,
  },
  subLocation: {
    fontSize: 22,
    color: '#fff',
  },
});

export default CurrentLocation;
