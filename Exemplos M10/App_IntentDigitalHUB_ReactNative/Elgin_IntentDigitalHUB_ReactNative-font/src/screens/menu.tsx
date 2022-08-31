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
import XmlStoreService from '../xmlStorageService/xmlStoreService';

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
    } else {
      //Após a permissão ser concedida, salav no diretório do dispositivo todos os XMLs que serão utilizados
      XmlStoreService.allocateXmls();
    }
  }

  return (
    <View style={styles.mainView}>
      <View style={styles.contentView}>
        <View style={styles.bannerView}>
          <Image style={styles.banner} source={Logo} />
        </View>
        <View style={styles.menuView}>
          <View style={styles.buttonView}>
            <View style={styles.menuRow}>
              <TouchableOpacity
                style={styles.doubleButton}
                onPress={() => navigation.navigate('Bridge')}>
                <Image
                  style={styles.lgIcon}
                  source={require('../icons/elginpay_logo.png')}
                />
                <Text style={styles.textButton}>E1 - BRIDGE</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.doubleButton}
                onPress={() => navigation.navigate('Printer')}>
                <Image
                  style={styles.icon}
                  source={require('../icons/printer.png')}
                />
                <Text style={styles.textButton}>IMPRESSORA</Text>
              </TouchableOpacity>
            </View>
            <View style={styles.menuRow}>
              <TouchableOpacity
                style={styles.doubleButton}
                onPress={() => navigation.navigate('Balanca')}>
                <Image
                  style={styles.icon}
                  source={require('../icons/balanca.png')}
                />
                <Text style={styles.textButton}>BALANÇA</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.doubleButton}
                onPress={() => navigation.navigate('Sat')}>
                <Image
                  style={styles.icon}
                  source={require('../icons/sat.png')}
                />
                <Text style={styles.textButton}>SAT</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
        <Footer />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  contentView: {
    height: '100%',
    width: '90%',
    alignSelf: 'center',

    justifyContent: 'space-between',
  },
  bannerView: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  banner: {
    resizeMode: 'contain',
    width: 490,
    height: 139,
  },
  menuView: {
    flexDirection: 'column',
    justifyContent: 'space-around',
    alignItems: 'center',
    height: 325,
  },
  buttonView: {
    justifyContent: 'space-around',
  },
  buttonMenu: {
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderRadius: 15,
    width: 220,
    height: 130,
    marginHorizontal: 10,
    marginVertical: 5,
  },
  icon: {
    width: 50,
    height: 50,
  },
  lgIcon: {
    width: '100%',
    height: 50,
    resizeMode: 'contain',
  },
  menuRow: {
    flexDirection: 'row',
    justifyContent: 'center',
  },
  doubleButton: {
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',

    borderWidth: 2,
    borderRadius: 15,
    width: 200,
    height: 130,
    marginHorizontal: 10,
    marginVertical: 5,
  },
  textButton: {
    fontWeight: 'bold',
    textAlign: 'center',
    color: 'black',
  },
});

export default Menu;
