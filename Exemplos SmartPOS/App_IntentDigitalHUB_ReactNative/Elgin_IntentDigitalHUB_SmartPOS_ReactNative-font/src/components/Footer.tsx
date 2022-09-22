import React from 'react';
import {View, Text, StyleSheet} from 'react-native';

const Footer = () => {
  return (
    <View style={styles.viewFooter}>
      <Text style={styles.textFooter}>
        Intent Digital Hub - React Native 1.0.0
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  viewFooter: {
    height: 40,
    alignItems: 'flex-end',
    justifyContent: 'center',
    paddingHorizontal: 10,
  },
  textFooter: {
    fontWeight: 'bold',
    color: 'gray',
  },
});

export default Footer;
