import React, {useState} from 'react';

import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  TextInput,
} from 'react-native';
import Header from '../components/Header';
import Footer from '../components/Footer';
import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {IntentDigitalHubModule} from '../intentDigitalHubService/IntentDigitalHubModule';

const BarCodeReader = () => {
  const [barCode, setBarCode] = useState('');
  const [barCodeType, setBarCodeType] = useState('');

  const handleIniciarLeitura = () => {
    IntentDigitalHubCommandStarter.startIntent(
      IntentDigitalHubModule.SCANNER,
      resultString => {
        const result = JSON.parse(resultString)[0].resultado;
        console.log(result);
        setBarCode(result[1]);
        setBarCodeType(result[3]);
      },
    );
  };

  const handleLimparCampos = () => {
    setBarCode('');
    setBarCodeType('');
  };

  return (
    <View style={styles.mainView}>
      <Header textTitle={'CÓDIGO DE BARRAS'} />
      <View style={styles.configView}>
        <View style={styles.inputGroup}>
          <View style={styles.inputContainer}>
            <Text style={styles.inputLabel}>COD.</Text>
            <TextInput style={styles.input} value={barCode} editable={false} />
          </View>
          <View style={styles.inputContainer}>
            <Text style={styles.inputLabel}>TYPE</Text>
            <TextInput
              style={styles.input}
              value={barCodeType}
              editable={false}
            />
          </View>
        </View>
      </View>
      <View style={styles.configView}>
        <View style={styles.submitionButtonsView}>
          <TouchableOpacity
            style={styles.submitionButton}
            onPress={() => handleIniciarLeitura()}>
            <Text style={styles.textButton}>INICIAR LEITURA</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.submitionButton}
            onPress={() => handleLimparCampos()}>
            <Text style={styles.textButton}>LIMPAR CAMPOS</Text>
          </TouchableOpacity>
        </View>
      </View>
      <Footer />
    </View>
  );
};

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-between',
    backgroundColor: 'white',
    paddingHorizontal: 10,
  },
  configView: {
    flexDirection: 'column',
    justifyContent: 'flex-start',
    paddingHorizontal: 10,
    marginTop: 20,
  },
  inputGroup: {
    flexDirection: 'column',
    alignItems: 'stretch',
    justifyContent: 'space-between',
    borderWidth: 2,
    borderColor: 'black',
    borderRadius: 20,
    padding: 10,
    marginBottom: 15,
  },
  inputContainer: {
    flexDirection: 'column',
    alignItems: 'stretch',
    justifyContent: 'space-between',
    marginBottom: 15,
  },
  input: {
    borderWidth: 2,
    borderColor: 'black',
    borderRadius: 20,
    textAlignVertical: 'bottom',
    padding: 10,
    fontSize: 17,
    color: 'black',
    height: 40,
    textAlign: 'center',
  },
  inputLabel: {
    fontWeight: 'bold',
    fontSize: 16,
  },
  submitionButtonsView: {
    marginTop: 5,
    flexDirection: 'column',
  },
  submitionButton: {
    height: 50,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 10,
    justifyContent: 'center',
    marginBottom: 10,
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 14,
  },
});

export default BarCodeReader;
