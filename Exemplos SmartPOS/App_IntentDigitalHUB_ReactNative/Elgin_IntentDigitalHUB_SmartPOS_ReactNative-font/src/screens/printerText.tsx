import React, {FC, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  Alert,
} from 'react-native';
import {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RadioButton, Checkbox} from 'react-native-paper';
import {Picker} from '@react-native-picker/picker';

import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {ImpressaoTexto} from '../intentDigitalHubService/TERMICA/Commands/ImpressaoTexto';
import {AvancaPapel} from '../intentDigitalHubService/TERMICA/Commands/AvancaPapel';
import {TermicaCommand} from '../intentDigitalHubService/TERMICA/TermicaCommand';
import {Corte} from '../intentDigitalHubService/TERMICA/Commands/Corte';
import {ImprimeXMLSAT} from '../intentDigitalHubService/TERMICA/Commands/ImprimeXMLSAT';
import {ImprimeXMLNFCe} from '../intentDigitalHubService/TERMICA/Commands/ImprimeXMLNFCe';
import XmlFile from '../rawXmls/xmlFile';
import Header from '../components/Header';
import Footer from '../components/Footer';
import {RootStackParamList} from '../appNavigator';

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

const PrinterText: FC<
  NativeStackScreenProps<RootStackParamList, 'PrinterText'>
> = ({route}) => {
  const [text, setText] = useState('ELGIN DEVELOPER COMMNUNITY');
  const [selectedFontFamily, setSelectedFontFamily] = useState('FONT A');
  const [selectedFontSize, setSelectedFontSize] = useState(17);
  const [optionTextAlign, setOptionTextAlign] = useState('0');

  const [isBold, setIsBold] = useState(false);
  const [isUnderline, setIsUnderline] = useState(false);
  const [isCutPaperActive, setIsCutPaperActive] = useState(false);

  const showCutPaper = route.params.connectionType === 'ip';

  const checkBoxType = [
    {
      id: 'NEGRITO',
      textButton: 'NEGRITO',
      value: isBold,
      setValue: (value: boolean) => setIsBold(value),
    },
    {
      id: 'SUBLINHADO',
      textButton: 'SUBLINHADO',
      value: isUnderline,
      setValue: (value: boolean) => setIsUnderline(value),
    },
  ];

  const buttonOptionRender = [
    {id: 'NFCE', textButton: 'NFCE', onPress: () => doPrinterXmlNFCe()},
    {id: 'SAT', textButton: 'SAT', onPress: () => doPrinterXmlSAT()},
  ];

  function getStiloValue() {
    let stilo = 0;

    if (selectedFontFamily === 'FONT B') {
      stilo += 1;
    }
    if (isUnderline) {
      stilo += 2;
    }
    if (isBold) {
      stilo += 8;
    }
    return stilo;
  }

  function doPrinterText() {
    if (text === '') {
      Alert.alert('Alerta', 'Campo mensagem vazio!');
    } else {
      const stilo = getStiloValue();
      const command = new ImpressaoTexto(
        text,
        Number(optionTextAlign),
        stilo,
        Number(selectedFontSize),
      );
      const commands: TermicaCommand[] = [command];
      commands.push(new AvancaPapel(10));
      if (isCutPaperActive) {
        commands.push(new Corte(0));
      }
      IntentDigitalHubCommandStarter.startCommands(commands, _ => null);
    }
  }

  function doPrinterXmlSAT() {
    const command = new ImprimeXMLSAT(XmlFile.XML_SAT.XmlContentInString, 0);
    const commands: TermicaCommand[] = [command];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    IntentDigitalHubCommandStarter.startCommands(commands, _ => null);
  }

  function doPrinterXmlNFCe() {
    const command = new ImprimeXMLNFCe(
      XmlFile.XML_NFCE.XmlContentInString,
      1,
      'CODIGO-CSC-CONTRIBUINTE-36-CARACTERES',
      0,
    );
    const commands: TermicaCommand[] = [command];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    IntentDigitalHubCommandStarter.startCommands(commands, _ => null);
  }

  return (
    <View style={styles.mainView}>
      <Header textTitle="IMPRESSORA" />
      <View style={styles.printerSettingsView}>
        <Text style={styles.titleText}>IMPRESSÃO DE TEXTO</Text>
        <View style={styles.inputContainer}>
          <Text style={styles.labelText}>MENSAGEM:</Text>
          <TextInput
            placeholder={'Insira sua mensagem aqui'}
            style={styles.input}
            onChangeText={setText}
            value={text}
          />
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
        <View style={styles.fontStyleView}>
          <Text style={styles.labelText}>ESTILIZAÇÃO</Text>
          <View style={styles.fontPickerView}>
            <Text style={styles.optionText}>FONT FAMILY: </Text>
            <Picker
              style={styles.fontPicker}
              selectedValue={selectedFontFamily}
              onValueChange={(itemValue, _) =>
                setSelectedFontFamily(itemValue)
              }>
              <Picker.Item label="FONT A" value="FONT A" />
              <Picker.Item label="FONT B" value="FONT B" />
            </Picker>
          </View>
          <View style={styles.fontPickerView}>
            <Text style={styles.optionText}>FONT SIZE: </Text>
            <Picker
              style={styles.fontPicker}
              selectedValue={selectedFontSize}
              onValueChange={(itemValue, _) => setSelectedFontSize(itemValue)}>
              <Picker.Item label="17" value="17" />
              <Picker.Item label="34" value="34" />
              <Picker.Item label="51" value="51" />
              <Picker.Item label="68" value="68" />
            </Picker>
          </View>
          <View style={styles.fontStylesSelect}>
            {checkBoxType.map(({id, textButton, value, setValue}, _) => (
              <View key={id} style={styles.checkBoxStyleView}>
                <Checkbox
                  disabled={false}
                  status={value ? 'checked' : 'unchecked'}
                  onPress={() => setValue(!value)}
                />
                <Text style={styles.checkboxText}>{textButton}</Text>
              </View>
            ))}

            {showCutPaper && (
              <View style={styles.checkBoxStyleView}>
                <Checkbox
                  disabled={false}
                  status={isCutPaperActive ? 'checked' : 'unchecked'}
                  onPress={() => setIsCutPaperActive(!isCutPaperActive)}
                />
                <Text style={styles.checkboxText}>CUT PAPER</Text>
              </View>
            )}
          </View>
          <View>
            <TouchableOpacity
              style={styles.printButtonView}
              onPress={doPrinterText}>
              <Text style={styles.textButton}>IMPRIMIR TEXTO</Text>
            </TouchableOpacity>
          </View>
          <View style={styles.buttonOptionview}>
            {buttonOptionRender.map(({id, textButton, onPress}) => (
              <View key={id} style={styles.buttonOptionStyle}>
                <TouchableOpacity style={styles.buttonOption} onPress={onPress}>
                  <Text style={styles.textButton}>{textButton}</Text>
                </TouchableOpacity>
              </View>
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
    backgroundColor: 'white',
  },
  printerSettingsView: {
    flex: 1,
    paddingHorizontal: 10,
  },
  labelText: {
    color: 'black',
    fontSize: 15,
    fontWeight: 'bold',
  },
  optionText: {
    color: 'black',
    fontSize: 16,
    fontWeight: 'bold',
  },
  checkboxText: {
    color: 'black',
    fontSize: 14,
    fontWeight: 'bold',
  },
  titleText: {
    color: 'black',
    textAlign: 'center',
    fontSize: 26,
    fontWeight: 'bold',
    marginVertical: 5,
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginVertical: 10,
  },
  input: {
    flex: 1,
    borderBottomWidth: 0.5,
    borderBottomColor: 'black',
    textAlignVertical: 'bottom',
    padding: 0,
    marginLeft: 5,
    marginBottom: 5,
    fontSize: 17,
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
  fontPickerView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  fontPicker: {
    width: '50%',
    height: 50,
  },
  fontStyleView: {
    flexDirection: 'column',
    marginTop: 10,
  },
  fontStylesSelect: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
  },
  checkBoxStyleView: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  printButtonView: {
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderRadius: 20,
    borderColor: '#0069A5',
    backgroundColor: '#0069A5',
    height: 50,
    marginTop: 20,
    marginVertical: 5,
  },
  buttonOptionview: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  buttonOption: {
    width: '100%',
    height: 45,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 20,
    justifyContent: 'center',
  },
  buttonOptionStyle: {
    width: '49%',
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 16,
  },
});

export default PrinterText;
