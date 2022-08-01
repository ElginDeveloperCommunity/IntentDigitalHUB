import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  Alert,
} from 'react-native';

import CheckBox from '@react-native-community/checkbox';
import {Picker} from '@react-native-picker/picker';
import {RadioButton} from 'react-native-paper';

import IntentDigitalHubCommandStarter from '../../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {ImpressaoCodigoBarras} from '../../intentDigitalHubService/TERMICA/ImpressaoCodigoBarras';
import {TermicaCommand} from '../../intentDigitalHubService/TERMICA/TermicaCommand';
import {AvancaPapel} from '../../intentDigitalHubService/TERMICA/AvancaPapel';
import {Corte} from '../../intentDigitalHubService/TERMICA/Corte';
import {ImpressaoQRCode} from '../../intentDigitalHubService/TERMICA/ImpressaoQRCode';
import {DefinePosicao} from '../../intentDigitalHubService/TERMICA/DefinePosicao';

const alignTextOptionData = [
  {
    label: 'ESQUERDA',
    value: '0',
  },
  {
    label: 'CENTRALIZADO',
    value: '1',
  },
  {
    label: 'DIREITA',
    value: '2',
  },
];

const barcodeTypes = [
  {
    label: 'EAN_8',
    typeValue: 3,
    defaultMessage: '40170725',
  },
  {
    label: 'EAN_13',
    typeValue: 2,
    defaultMessage: '0123456789012',
  },
  {
    label: 'QR_CODE',
    typeValue: null,
    defaultMessage: 'ELGIN DEVELOPERS COMMUNITY',
  },
  {
    label: 'UPC_A',
    typeValue: 0,
    defaultMessage: '123601057072',
  },
  {
    label: 'CODE_39',
    typeValue: 4,
    defaultMessage: 'CODE39',
  },
  {
    label: 'ITF',
    typeValue: 5,
    defaultMessage: '05012345678900',
  },
  {
    label: 'CODE_BAR',
    typeValue: 6,
    defaultMessage: 'A3419500A',
  },
  {
    label: 'CODE_93',
    typeValue: 7,
    defaultMessage: 'CODE93',
  },
  {
    label: 'CODE_128',
    typeValue: 8,
    defaultMessage: '{C1233',
  },
];

const PrinterBarCode = () => {
  //Variáveis de entrada
  const [codigo, setCodigo] = useState('40170725');
  const [selectedCodeTypeIdx, setSelectedCodeTypeIdx] = useState(0);
  const [selectedHeigthCode, setSelectedHeigthCode] = useState(20);
  const [selectedCodeWidth, setSelectedCodeWidth] = useState(1);
  const [optionTextAlign, setOptionTextAlign] = useState('0');
  const [isCutPaperActive, setIsCutPaperActive] = useState(false);

  //CHAMADA A FUNÇÃO DE TIPO DE BARCODE ESCOLHIDO - DEFAULT E QR CODE
  function doAllTypesOfBarCodes() {
    if (codigo === '') {
      Alert.alert('Alert!', 'Campo código vazio!');
    } else {
      if (barcodeTypes[selectedCodeTypeIdx].label === 'QR_CODE') {
        doPrinterQrCode();
      } else {
        doPrinterBarCodeDefault();
      }
    }
  }

  function doPrinterBarCodeDefault() {
    const HRI = 4;
    const mainCommand = new ImpressaoCodigoBarras(
      barcodeTypes[selectedCodeTypeIdx].typeValue!,
      codigo,
      Number(selectedHeigthCode),
      Number(selectedCodeWidth),
      HRI,
    );

    const posicaoCommand = new DefinePosicao(Number(optionTextAlign));
    const commands: TermicaCommand[] = [posicaoCommand, mainCommand];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    IntentDigitalHubCommandStarter.startCommands(commands, _ => null);
  }

  function doPrinterQrCode() {
    const mainCommand = new ImpressaoQRCode(
      codigo,
      Number(selectedCodeWidth),
      2,
    );

    const posicaoCommand = new DefinePosicao(Number(optionTextAlign));
    const commands: TermicaCommand[] = [posicaoCommand, mainCommand];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    IntentDigitalHubCommandStarter.startCommands(commands, _ => null);
  }

  return (
    <View style={styles.mainView}>
      <Text style={styles.titleText}>IMPRESSÃO DE CÓDIGO DE BARRAS</Text>
      <View style={styles.printerSettingsView}>
        <View style={styles.inputContainer}>
          <Text style={styles.labelText}>CÓDIGO:</Text>
          <TextInput
            style={styles.inputMensage}
            onChangeText={setCodigo}
            value={codigo}
          />
        </View>
        <View style={styles.codeTypePickerView}>
          <Text style={styles.labelText}>TIPO DE CÓDIGO DE BARRAS: </Text>
          <Picker
            style={styles.codeTypePicker}
            selectedValue={selectedCodeTypeIdx}
            onValueChange={(_, index) => {
              setSelectedCodeTypeIdx(index);
              setCodigo(barcodeTypes[index].defaultMessage);
            }}>
            {barcodeTypes.map((option, index) => (
              <Picker.Item
                key={option.label}
                label={option.label}
                value={index}
              />
            ))}
          </Picker>
        </View>
        <Text style={styles.labelText}>ALINHAMENTO:</Text>
        <RadioButton.Group
          onValueChange={newValue => setOptionTextAlign(newValue)}
          value={optionTextAlign}>
          <View style={styles.alignSettingsContainer}>
            {alignTextOptionData.map((item, index) => (
              <View key={index} style={styles.alignSettingsOption}>
                <RadioButton key={index} value={item.value} color="#0069A5" />
                <Text style={styles.alignSettingsOptionLabel}>
                  {item.label}
                </Text>
              </View>
            ))}
          </View>
        </RadioButton.Group>
        <View style={styles.barCodeStyleView}>
          <Text style={styles.labelText}>ESTILIZAÇÃO:</Text>
          <View style={styles.barCodeStyleSettingView}>
            <View style={styles.barCodeStylePicker}>
              <Text style={styles.optionText}>
                {barcodeTypes[selectedCodeTypeIdx].label === 'QR_CODE'
                  ? 'SQUARE'
                  : 'WIDTH'}
              </Text>
              <Picker
                style={styles.sizePicker}
                selectedValue={selectedCodeWidth}
                onValueChange={(itemValue, _) =>
                  setSelectedCodeWidth(itemValue)
                }>
                <Picker.Item label="1" value="1" />
                <Picker.Item label="2" value="2" />
                <Picker.Item label="3" value="3" />
                <Picker.Item label="4" value="4" />
                <Picker.Item label="5" value="5" />
                <Picker.Item label="6" value="6" />
              </Picker>
            </View>
            {barcodeTypes[selectedCodeTypeIdx].label !== 'QR_CODE' ? (
              <View style={[styles.barCodeStylePickerHeigth]}>
                <Text style={styles.optionText}>HEIGHT:</Text>
                <Picker
                  style={styles.sizePicker}
                  selectedValue={selectedHeigthCode}
                  onValueChange={(itemValue, _) =>
                    setSelectedHeigthCode(itemValue)
                  }>
                  <Picker.Item label="20" value="20" />
                  <Picker.Item label="60" value="60" />
                  <Picker.Item label="120" value="120" />
                  <Picker.Item label="200" value="200" />
                </Picker>
              </View>
            ) : (
              <></>
            )}
            <View style={styles.checkBoxStyleView}>
              <CheckBox
                disabled={false}
                value={isCutPaperActive}
                onValueChange={newValue => setIsCutPaperActive(newValue)}
              />
              <Text style={styles.optionText}>CUT PAPER</Text>
            </View>
          </View>
        </View>
        <View style={styles.printterButtonView}>
          <TouchableOpacity
            style={styles.printterButton}
            onPress={doAllTypesOfBarCodes}>
            <Text style={styles.textButton}>IMPRIMIR CÓDIGO DE BARRAS</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    paddingHorizontal: 20,
    alignItems: 'stretch',
  },
  titleText: {
    color: 'black',
    textAlign: 'center',
    fontSize: 30,
    fontWeight: 'bold',
  },
  optionText: {
    color: 'black',
    fontSize: 14,
    fontWeight: 'bold',
  },
  labelText: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: 15,
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
  },
  printerSettingsView: {
    flex: 1,
  },
  inputContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  inputMensage: {
    flex: 1,
    color: 'black',
    borderBottomWidth: 0.5,
    borderBottomColor: 'black',
    textAlignVertical: 'bottom',
    padding: 0,
    marginLeft: 5,
    marginBottom: 5,
    fontSize: 17,
  },
  codeTypePickerView: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 2,
  },
  codeTypePicker: {
    width: 200,
    height: 50,
  },
  sizePicker: {
    width: 150,
    height: 50,
  },
  barCodeStyleView: {
    flexDirection: 'column',
  },

  barCodeStylePicker: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  barCodeStylePickerHeigth: {
    flexDirection: 'row',
    alignItems: 'center',
    display: 'flex',
  },
  barCodeStyleSettingView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },

  alignSettingsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  alignSettingsOption: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  alignSettingsOptionLabel: {
    color: 'black',
  },
  checkBoxStyleView: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  printterButtonView: {
    alignSelf: 'stretch',
    justifyContent: 'center',
    height: 80,
  },
  printterButton: {
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'stretch',
    borderWidth: 2,
    borderRadius: 5,
    borderColor: '#0069A5',
    backgroundColor: '#0069A5',
    height: 50,
    marginVertical: 5,
  },
});

export default PrinterBarCode;
