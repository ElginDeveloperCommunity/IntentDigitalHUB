import React, {useState} from 'react';

import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  ScrollView,
  TextInput,
  Alert,
} from 'react-native';
import RNFS from 'react-native-fs';

import Header from '../components/Header';
import Footer from '../components/Footer';
import {RadioButton} from 'react-native-paper';
import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {AtivarSAT} from '../intentDigitalHubService/SAT/AtivarSAT';
import {AssociarAssinatura} from '../intentDigitalHubService/SAT/AssociarAssinatura';
import {ConsultarSAT} from '../intentDigitalHubService/SAT/ConsultarSAT';
import {EnviarDadosVenda} from '../intentDigitalHubService/SAT/EnviarDadosVenda';
import XmlFile from '../xmlStorageService/xmlFile';
import {CancelarUltimaVenda} from '../intentDigitalHubService/SAT/CancelarUltimaVenda';
import SatXmlCancelarUltimaVendaSat from '../rawXmls/sat_cancelamento';
import {ExtrairLogs} from '../intentDigitalHubService/SAT/ExtrairLogs';
import {ConsultarStatusOperacional} from '../intentDigitalHubService/SAT/ConsultarStatusOperacional';

const satOptionsRadioButton = [
  {
    label: 'SMART SAT',
    value: 'SMART SAT',
  },
  {
    label: 'SATGO',
    value: 'SATGO',
  },
];

const SAT = () => {
  const [cfeCancelamento, setCFeCancelamento] = useState('');
  const [textReturn, setTextReturn] = useState('');
  const [selectedOptionSat, setSelectedOptionSat] = useState('SMART SAT');
  const [activationCode, setActvationCode] = useState('123456789');

  const buttons = [
    {textButton: 'CONSULTAR SAT', onPress: () => sendConsultarSat()},
    {textButton: 'CANCELAMENTO', onPress: () => cancelarVendaSat()},
    {textButton: 'STATUS OPERACIONAL', onPress: () => sendStatusOperacional()},
    {textButton: 'ATIVAR', onPress: () => sendAtivarSat()},
    {textButton: 'REALIZAR VENDA', onPress: () => enviarDadosVendaSat()},
    {textButton: 'ASSOCIAR', onPress: () => sendAssociarSat()},
    {textButton: 'EXTRAIR LOGS', onPress: () => extrairLog()},
  ];

  function sendAtivarSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new AtivarSAT(
      Number(numSessao),
      2,
      activationCode,
      '14200166000166',
      15,
    );
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result = JSON.parse(resultString)[0].resultado;
      setTextReturn(result);
    });
  }

  function sendAssociarSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new AssociarAssinatura(
      Number(numSessao),
      activationCode,
      '16716114000172',
      'SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT',
    );
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result = JSON.parse(resultString)[0].resultado;
      setTextReturn(result);
    });
  }

  function sendConsultarSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new ConsultarSAT(Number(numSessao));
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result = JSON.parse(resultString)[0].resultado;
      setTextReturn(result);
    });
  }

  function sendStatusOperacional() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new ConsultarStatusOperacional(
      Number(numSessao),
      activationCode,
    );
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result = JSON.parse(resultString)[0].resultado;
      setTextReturn(result);
    });
  }

  function enviarDadosVendaSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();
    setCFeCancelamento('');

    const directory = RNFS.ExternalDirectoryPath;
    let fileName = '';
    if (selectedOptionSat === 'SMART SAT') {
      fileName = XmlFile.SAT_ENVIAR_DADOS_VENDA.xmlArchiveName + '.xml';
    } else {
      fileName = XmlFile.SAT_GO_ENVIAR_DADOS_VENDA.xmlArchiveName + '.xml';
    }
    const path =
      directory.substring(directory.indexOf('/Android')) + '/' + fileName;

    const command = new EnviarDadosVenda(
      Number(numSessao),
      activationCode,
      `path=${path}`,
    );
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result: string = JSON.parse(resultString)[0].resultado;

      //TRATAMENTO PARA PEGAR O CÓDIGO CFE ATUAL PARA CANCELAMENTO
      if (result.includes('|')) {
        const listOfReturn = result.split('|');
        const newReturn = listOfReturn.find(value => value.includes('CFe'));
        if (newReturn) {
          setCFeCancelamento(newReturn);
        }
      }
      setTextReturn(result);
    });
  }

  function generateXmlForSatCancellation() {
    const searchRegExp = /"/g;
    let xmlCancel = new SatXmlCancelarUltimaVendaSat()
      .getXml()
      .replace('novoCFe', cfeCancelamento)
      .replace(searchRegExp, '\\"');
    return xmlCancel;
  }

  function cancelarVendaSat() {
    var numSessao = Math.floor(Math.random() * 999999).toString();
    const xmlCancel = generateXmlForSatCancellation();

    if (!cfeCancelamento) {
      Alert.alert('Atenção', 'Não foi feita uma venda para cancelar!');
    }

    const command = new CancelarUltimaVenda(
      Number(numSessao),
      activationCode,
      cfeCancelamento,
      xmlCancel,
    );
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result = JSON.parse(resultString)[0].resultado;
      setTextReturn(result);
    });
  }

  function extrairLog() {
    const numSessao = Math.floor(Math.random() * 999999);

    const command = new ExtrairLogs(numSessao, activationCode);
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result = JSON.parse(resultString)[0].resultado;

      if (result === 'DeviceNotFound') {
        setTextReturn(result);
      } else {
        setTextReturn('O log do SAT está salvo em:' + result);
      }
    });
  }

  return (
    <View style={styles.mainView}>
      <Header textTitle={'SAT'} />
      <View style={styles.satMenuView}>
        <View style={styles.satReturnContainer}>
          <Text style={styles.satReturnTitle}>RETORNO:</Text>
          <ScrollView>
            <Text style={styles.satReturnText}>{textReturn}</Text>
          </ScrollView>
        </View>
        <View style={styles.satControlsContainer}>
          <RadioButton.Group
            onValueChange={newValue => setSelectedOptionSat(newValue)}
            value={selectedOptionSat}>
            <View style={styles.satOptionsContainer}>
              {satOptionsRadioButton.map((item, index) => (
                <View key={index} style={styles.satOption}>
                  <RadioButton key={index} value={item.value} color="#0069A5" />
                  <Text style={styles.satOptionsOptionLabel}>{item.label}</Text>
                </View>
              ))}
            </View>
          </RadioButton.Group>
          <View style={styles.inputContainer}>
            <Text style={styles.inputlabel}>Código de Ativação: </Text>
            <TextInput
              placeholder={'000'}
              style={styles.input}
              keyboardType="numeric"
              onChangeText={setActvationCode}
              value={activationCode}
            />
          </View>
          <View style={styles.actionButtonsView}>
            {buttons.map(({textButton, onPress}, index) => (
              <TouchableOpacity
                style={styles.submitionButton}
                key={index}
                onPress={onPress}>
                <Text style={styles.textButton}>{textButton}</Text>
              </TouchableOpacity>
            ))}
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
  satReturnTitle: {
    color: 'black',
    fontSize: 16,
    marginBottom: 20,
  },
  inputlabel: {
    color: 'black',
    fontSize: 14,
    fontWeight: 'bold',
  },
  titleText: {
    textAlign: 'center',
    fontSize: 30,
    fontWeight: 'bold',
  },
  satMenuView: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  satReturnContainer: {
    flex: 1,
    flexDirection: 'column',
    padding: 15,
    borderWidth: 2.5,
    borderColor: 'black',
    borderRadius: 10,
    margin: 10,
  },
  satControlsContainer: {
    flex: 1,
    flexDirection: 'column',
    alignItems: 'stretch',
    justifyContent: 'center',
    margin: 10,
  },
  radioButtonsView: {
    justifyContent: 'space-between',
  },
  satOptionsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  satOption: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems: 'center',
  },
  satOptionsOptionLabel: {
    color: 'black',
  },
  inputContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
    marginVertical: 25,
    alignContent: 'center',
    alignItems: 'center',
  },
  input: {
    borderBottomWidth: 1,
    borderBottomColor: 'black',
    width: '50%',
  },
  actionButtonsView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    width: '100%',
  },
  submitionButton: {
    flexBasis: '48%',
    height: 40,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 13,
    justifyContent: 'center',
    margin: 2.5,
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 14,
  },
  satReturnText: {
    color: 'black',
  },
});

export default SAT;
