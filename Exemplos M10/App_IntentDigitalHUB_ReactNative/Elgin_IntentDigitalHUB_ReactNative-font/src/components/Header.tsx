import React, {FC} from 'react';
import {View, Text, StyleSheet, Image} from 'react-native';

interface Props {
  textTitle: string;
}

const Header: FC<Props> = ({textTitle}) => {
  const Logo = require('../icons/elgin_logo.png');
  return (
    <View style={styles.headerView}>
      <Text style={styles.headerText}>{textTitle}</Text>
      <Image style={styles.headerIcon} resizeMode="contain" source={Logo} />
    </View>
  );
};

const styles = StyleSheet.create({
  headerView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 10,
  },
  headerText: {
    color: 'black',
    fontSize: 30,
    fontWeight: 'bold',
  },
  headerIcon: {
    width: 150,
    height: 50,
  },
});

export default Header;
