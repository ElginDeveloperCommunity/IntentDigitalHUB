/**
 * Sample React Native Menu
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {FC, useEffect} from 'react';
import {
  StyleSheet,
  Text,
  View,
  Image,
  TouchableOpacity,
  PermissionsAndroid,
  BackHandler,
} from 'react-native';
import {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../appNavigator';
import Footer from '../components/Footer';

const Menu: FC<NativeStackScreenProps<RootStackParamList, 'Menu'>> = ({
  navigation,
}) => {
  const Logo = require('../icons/elgin_logo.png');

  //Ao entrar na aplicação, para prosseguir, é necessário a permissão de acesso ao armazenamento externo
  useEffect(() => {
    //Pede a permissão de armazenamento
    askWritePermission();
    return () => {};
  }, []);

  async function askWritePermission(): Promise<void> {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
    );

    //Caso a permissão não seja concedida, mostre o toast com a informação e feche a aplicação
    if (
      granted === PermissionsAndroid.RESULTS.DENIED ||
      granted === PermissionsAndroid.RESULTS.NEVER_ASK_AGAIN
    ) {
      BackHandler.exitApp();
    }
  }

  return (
    <View style={styles.mainView}>
      <View style={styles.bannerView}>
        <Image style={styles.banner} source={Logo} />
      </View>
      <View style={styles.menuView}>
        <TouchableOpacity
          style={styles.buttonMenu}
          onPress={() => navigation.navigate('ElginPay')}>
          <Image
            style={styles.icon}
            source={require('../icons/elginpay_logo.png')}
          />
          <Text style={styles.textButton}>ELGIN PAY</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.buttonMenu}
          onPress={() => navigation.navigate('Printer')}>
          <Image style={styles.icon} source={require('../icons/printer.png')} />
          <Text style={styles.textButton}>IMPRESSORA</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.buttonMenu}
          onPress={() => navigation.navigate('BarCodeReader')}>
          <Image
            style={styles.icon}
            source={require('../icons/bar_code.png')}
          />
          <Text style={styles.textButton}>LEITOR DE CÓDIGO</Text>
        </TouchableOpacity>
      </View>
      <Footer />
    </View>
  );
};

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
    flexDirection: 'column',
    paddingHorizontal: 20,
  },
  bannerView: {
    alignItems: 'flex-start',
    justifyContent: 'flex-start',
    flexDirection: 'row',
  },
  banner: {
    resizeMode: 'contain',
    flex: 1,
    height: 100,
  },
  menuView: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'stretch',
  },
  buttonMenu: {
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderRadius: 20,
    height: 100,
    marginVertical: 5,
  },
  icon: {
    width: '100%',
    height: 50,
    resizeMode: 'contain',
  },
  menuRow: {
    flexDirection: 'row',
    justifyContent: 'center',
  },
  textButton: {
    fontWeight: 'bold',
    textAlign: 'center',
    color: 'black',
  },
});

export default Menu;
