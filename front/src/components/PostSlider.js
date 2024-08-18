import React from 'react';
import {View, StyleSheet} from 'react-native';

const PostSlider = ({activeSlide, totalSlides}) => {
  return (
    <View style={styles.indicatorContainer}>
      {[...Array(totalSlides)].map((_, i) => (
        <View
          key={i}
          style={[
            styles.indicator,
            {
              backgroundColor:
                i === activeSlide ? '#fff' : 'rgba(255, 255, 255, 0.5)',
            },
          ]}
        />
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  indicatorContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    flex: 1,
  },
  indicator: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginHorizontal: 4,
  },
});

export default PostSlider;
