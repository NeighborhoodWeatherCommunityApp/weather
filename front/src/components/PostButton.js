import React from 'react';
import {View, StyleSheet} from 'react-native';
import {Button, Card} from 'react-native-elements';
import {useNavigation} from '@react-navigation/native';
import globalStyles from '../globalStyles';

const PostButton = () => {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Card containerStyle={[styles.card, globalStyles.transparentBackground]}>
        <Button
          title="우리 동네 날씨를 알려주세요!"
          onPress={() => navigation.navigate('PostCreationScreen')}
          buttonStyle={styles.button}
          titleStyle={styles.buttonTitle}
        />
      </Card>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 2,
  },
  button: {
    backgroundColor: 'transparent',
    paddingVertical: 3,
  },
  buttonTitle: {
    fontSize: 14,
  },
});

export default PostButton;
