import React, {useState} from 'react';
import {Picker} from '@react-native-picker/picker';

import {ToastAndroid} from 'react-native';
import {StyleSheet, Text, View, TouchableOpacity} from 'react-native';

import Header from '../components/Header';
import Footer from '../components/Footer';
import {RadioButton} from 'react-native-paper';
import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {LerPeso} from '../intentDigitalHubService/BALANCA/LerPeso';
import {ConfigurarModeloBalanca} from '../intentDigitalHubService/BALANCA/ConfigurarModeloBalanca';
import {AbrirSerial} from '../intentDigitalHubService/BALANCA/AbrirSerial';
import {Fechar} from '../intentDigitalHubService/BALANCA/Fechar';
import {ConfigurarProtocoloComunicacao} from '../intentDigitalHubService/BALANCA/ConfigurarProtocoloComunicacao';

const modelOptionsData = [
  {
    label: 'DP3005',
    value: '0',
  },
  {
    label: 'SA110',
    value: '1',
  },
  {
    label: 'DPSC',
    value: '2',
  },
  {
    label: 'DP30CK',
    value: '3',
  },
];

const protocolOptionsData = [
  {
    label: 'PROTOCOL 0',
    value: '0',
  },
  {
    label: 'PROTOCOL 1',
    value: '1',
  },
  {
    label: 'PROTOCOL 2',
    value: '2',
  },
  {
    label: 'PROTOCOL 3',
    value: '3',
  },
  {
    label: 'PROTOCOL 4',
    value: '4',
  },
  {
    label: 'PROTOCOL 5',
    value: '5',
  },
  {
    label: 'PROTOCOL 6',
    value: '6',
  },
  {
    label: 'PROTOCOL 7',
    value: '7',
  },
];

const Balanca = () => {
  const [weigthValue, setWeigthValue] = useState('00.00');
  const [selectedModel, setSelectaedModel] = useState('0');
  const [selectedProtocol, setSelectedProtocol] = useState('0');

  function sendConfigBalanca() {
    const commands = [
      new ConfigurarModeloBalanca(Number(selectedModel)),
      new ConfigurarProtocoloComunicacao(Number(selectedProtocol)),
    ];
    IntentDigitalHubCommandStarter.startCommands(commands, resultString => {
      const configurarModeloBalancaResult =
        JSON.parse(resultString)[0].resultado;
      const configurarProtocoloComunicacao =
        JSON.parse(resultString)[1].resultado;

      ToastAndroid.show(
        `ConfigurarModeloBalanca: ${configurarModeloBalancaResult}\nConfigurarProtocoloComunicacao: ${configurarProtocoloComunicacao}`,
        ToastAndroid.LONG,
      );
    });
  }

  function sendLerPeso() {
    const commands = [
      new AbrirSerial(2400, 8, 'N', 1),
      new LerPeso(1),
      new Fechar(),
    ];
    IntentDigitalHubCommandStarter.startCommands(commands, resultString => {
      const abrirSerialResult = JSON.parse(resultString)[0].resultado;
      const lerPesoReturn = JSON.parse(resultString)[1].resultado;
      const fecharReturn = JSON.parse(resultString)[2].resultado;
      ToastAndroid.show(
        `AbrirSerial: ${abrirSerialResult}\nLerPeso: ${lerPesoReturn}\nFechar: ${fecharReturn}`,
        ToastAndroid.LONG,
      );

      if (lerPesoReturn > 0.0) {
        setWeigthValue(String(lerPesoReturn / 1000));
      }
    });
  }

  return (
    <View style={styles.mainView}>
      <Header textTitle="BALANÇA" />
      <View style={styles.balanceView}>
        <View style={styles.outputView}>
          <Text style={styles.optionText}>VALOR BALANÇA:</Text>
          <Text style={styles.optionText}>{weigthValue}</Text>
        </View>
        <View style={styles.modelsView}>
          <View style={styles.lineButtonView}>
            <Text style={styles.optionText}>MODELOS:</Text>
          </View>
          <RadioButton.Group
            onValueChange={newValue => setSelectaedModel(newValue)}
            value={selectedModel}>
            <View style={styles.modelOptionsContainer}>
              {modelOptionsData.map((item, index) => (
                <View key={index} style={styles.modelOption}>
                  <RadioButton key={index} value={item.value} color="#0069A5" />
                  <Text style={styles.modelOptionLabel}>{item.label}</Text>
                </View>
              ))}
            </View>
          </RadioButton.Group>
        </View>
        <View style={styles.pickersView}>
          <View style={styles.pickerAlign}>
            <Text style={styles.optionText}>PROTOCOLOS: </Text>
            <Picker
              style={styles.fontPicker}
              selectedValue={selectedProtocol}
              onValueChange={(itemValue, _) => setSelectedProtocol(itemValue)}>
              {protocolOptionsData.map((item, index) => (
                <Picker.Item
                  key={index}
                  label={item.label}
                  value={item.value}
                />
              ))}
            </Picker>
          </View>
        </View>
        <View style={styles.buttonsView}>
          <View style={styles.lineButtonView}>
            <TouchableOpacity
              style={styles.actionButton}
              onPress={sendConfigBalanca}>
              <Text style={styles.textButton}>CONFIGURAR MODELO BALANÇA</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton} onPress={sendLerPeso}>
              <Text style={styles.textButton}>LER PESO</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
      <Footer />
    </View>
  );
};

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    alignItems: 'stretch',
    justifyContent: 'space-between',
  },
  balanceView: {
    alignItems: 'stretch',
    flexDirection: 'column',
    justifyContent: 'center',
    paddingHorizontal: 10,
  },
  outputView: {
    flexDirection: 'row',
    marginBottom: 40,
  },
  modelsView: {},
  modelOptionsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  modelOption: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems: 'center',
  },
  modelOptionLabel: {
    color: 'black',
  },
  pickersView: {
    justifyContent: 'space-between',
  },
  pickerView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  optionText: {
    flex: 1,
    color: 'black',
    fontSize: 14,
    fontWeight: 'bold',
  },
  fontPicker: {
    flex: 1,
  },
  pickerAlign: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  buttonsView: {
    flexDirection: 'column',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  lineButtonView: {
    flexDirection: 'row',
  },
  actionButton: {
    height: 45,
    width: 300,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 5,
    justifyContent: 'center',
    marginHorizontal: 30,
    marginVertical: 10,
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
  },
});

export default Balanca;
