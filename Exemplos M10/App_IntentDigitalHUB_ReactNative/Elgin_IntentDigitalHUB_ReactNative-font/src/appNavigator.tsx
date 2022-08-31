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
import Bridge from './screens/bridge';
import Printer from './screens/printer';
import Sat from './screens/sat';
import Balanca from './screens/balanca';

export type RootStackParamList = {
  Menu: undefined;
  Printer: undefined;
  Sat: undefined;
  Bridge: undefined;
  Balanca: undefined;
};

const AppNavigator = () => {
  const Stack = createNativeStackNavigator<RootStackParamList>();

  return (
    <NavigationContainer>
      <Stack.Navigator
        screenOptions={{headerShown: false}}
        initialRouteName="Menu">
        <Stack.Screen component={Menu} name="Menu" />
        <Stack.Screen component={Bridge} name="Bridge" />
        <Stack.Screen component={Printer} name="Printer" />
        <Stack.Screen component={Sat} name="Sat" />
        <Stack.Screen component={Balanca} name="Balanca" />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;
