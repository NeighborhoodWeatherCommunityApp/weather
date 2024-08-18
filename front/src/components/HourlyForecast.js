import React from 'react';
import {ScrollView, Text, View, StyleSheet} from 'react-native';
import {Card, Icon} from 'react-native-elements';
import globalStyles from '../globalStyles';

const HourlyForecast = () => {
  return (
    <ScrollView horizontal style={styles.container}>
      {[...Array(7)].map((_, i) => (
        <Card
          key={i}
          containerStyle={[styles.card, globalStyles.transparentBackground]}>
          <View style={styles.content}>
            <Text style={styles.textTime}>12시</Text>
            <Icon name="rainy" type="ionicon" size={30} color="#fff" />
            <Text style={styles.text}>약한 비</Text>
            <Text style={styles.text}>27°C</Text>
          </View>
        </Card>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    marginVertical: 5,
    paddingHorizontal: 5,
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 10,
    marginHorizontal: 5,
    alignItems: 'center',
  },
  content: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  textTime: {
    color: '#fff',
    fontSize: 13,
    marginBottom: 5,
    textAlign: 'center',
  },
  text: {
    color: '#fff',
    fontSize: 13,
    marginVertical: 4,
    textAlign: 'center',
  },
});

export default HourlyForecast;
