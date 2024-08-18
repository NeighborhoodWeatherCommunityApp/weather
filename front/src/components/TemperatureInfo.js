import React from 'react';
import {View, Text, StyleSheet} from 'react-native';

const TemperatureInfo = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.temperature}>27°C</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'left',
    borderRadius: 10,
  },
  temperature: {
    fontSize: 22,
    color: '#fff',
    marginLeft: 27,
  },
});

export default TemperatureInfo;
