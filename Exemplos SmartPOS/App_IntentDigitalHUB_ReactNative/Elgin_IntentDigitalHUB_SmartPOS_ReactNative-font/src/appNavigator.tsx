/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import React from 'react';

import Menu from './screens/menu';
import ElginPay from './screens/elginPay';
import Printer from './screens/printer';
import PrinterText from './screens/printerText';
import PrinterImage from './screens/printerImage';
import PrinterBarcode from './screens/printerBarcode';
import BarCodeReader from './screens/barCodeReader';

export type RootStackParamList = {
  Menu: undefined;
  Printer: undefined;
  PrinterText: {
    connectionType: string;
  };
  PrinterImage: {
    connectionType: string;
  };
  PrinterBarcode: {
    connectionType: string;
  };
  ElginPay: undefined;
  BarCodeReader: undefined;
};

const AppNavigator = () => {
  const Stack = createNativeStackNavigator<RootStackParamList>();

  return (
    <NavigationContainer>
      <Stack.Navigator
        screenOptions={{headerShown: false}}
        initialRouteName="Menu">
        <Stack.Screen component={Menu} name="Menu" />
        <Stack.Screen component={ElginPay} name="ElginPay" />
        <Stack.Screen component={Printer} name="Printer" />
        <Stack.Screen component={PrinterText} name="PrinterText" />
        <Stack.Screen component={PrinterImage} name="PrinterImage" />
        <Stack.Screen component={PrinterBarcode} name="PrinterBarcode" />
        <Stack.Screen component={BarCodeReader} name="BarCodeReader" />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;
