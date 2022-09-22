import React, {useState, useEffect, FC} from 'react';
import {RadioButton} from 'react-native-paper';

import {
  StyleSheet,
  Text,
  View,
  Image,
  TouchableOpacity,
  TextInput,
  Alert,
} from 'react-native';
import {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../appNavigator';

import Header from '../components/Header';
import Footer from '../components/Footer';

import {StatusImpressora} from '../intentDigitalHubService/TERMICA/Commands/StatusImpressora';
import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {AbreConexaoImpressora} from '../intentDigitalHubService/TERMICA/Commands/AbreConexaoImpressora';
import {FechaConexaoImpressora} from '../intentDigitalHubService/TERMICA/Commands/FechaConexaoImpressora';

const Printer: FC<NativeStackScreenProps<RootStackParamList, 'Printer'>> = ({
  navigation,
}) => {
  const [printerConnection, setPrinterConnection] = useState('interna');
  const [printerModelIp, setPrinterModelIp] = useState('');
  const [ipConection, setIpConection] = useState('192.168.0.31:9100');

  const buttonsPrinter = [
    {
      id: 'TEXT',
      icon: require('../icons/printer_text.png'),
      textButton: 'IMPRESSÃO DE TEXTO',
      onPress: () =>
        navigation.navigate({
          name: 'PrinterText',
          params: {
            connectionType: printerConnection,
          },
        }),
    },
    {
      id: 'BARCODE',
      icon: require('../icons/printer_bar_code.png'),
      textButton: 'IMPRESSÃO DE CÓDIGO DE BARRAS',
      onPress: () =>
        navigation.navigate({
          name: 'PrinterBarcode',
          params: {
            connectionType: printerConnection,
          },
        }),
    },
    {
      id: 'IMAGE',
      icon: require('../icons/printer_image.png'),
      textButton: 'IMPRESSÃO DE IMAGEM',
      onPress: () =>
        navigation.navigate({
          name: 'PrinterImage',
          params: {
            connectionType: printerConnection,
          },
        }),
    },
  ];

  useEffect(() => {
    startConnectPrinterIntern();

    return () => {
      stopConnectPrinter();
    };
  }, []);

  function actualStatusPrinter() {
    const command = new StatusImpressora(3);
    IntentDigitalHubCommandStarter.startCommand(command, resultString => {
      const result = JSON.parse(resultString)[0].resultado;
      if (result === 5) {
        Alert.alert('Retorno', 'Papel está presente e não está próximo!');
      } else if (result === 6) {
        Alert.alert('Retorno', 'Papel está próximo do fim!');
      } else if (result === 7) {
        Alert.alert('Retorno', 'Papel ausente!');
      } else {
        Alert.alert('Retorno', 'Status Desconhecido');
      }
    });
  }

  function changePrinterChoose(value: string) {
    if (value === 'ip') {
      Alert.alert(
        'Impressora Externa IP',
        'Escolha o modelo da impressora que deseja utilziar',
        [
          {text: 'Cancelar', onPress: () => startConnectPrinterIntern()},
          {text: 'I8', onPress: () => startConnectPrinterIP('i8')},
          {text: 'i9', onPress: () => startConnectPrinterIP('i9')},
        ],
      );
    } else {
      startConnectPrinterIntern();
    }
  }

  function startConnectPrinterIntern() {
    setPrinterConnection('interna');
    const command = new AbreConexaoImpressora(5, 'SMARTPOS', '', 0);
    IntentDigitalHubCommandStarter.startCommand(command, _ => null);
  }

  function startConnectPrinterIP(model: string) {
    if (ipConection !== '') {
      var ip = ipConection.split(':')[0];
      var port = ipConection.split(':')[1];
      console.log(ip, port);

      if (isIpAdressValid()) {
        const command = new AbreConexaoImpressora(3, model, ip, Number(port));
        IntentDigitalHubCommandStarter.startCommand(command, resultString => {
          const result = JSON.parse(resultString)[0].resultado;
          if (Number(result) !== 0) {
            Alert.alert(
              'ERRO',
              'Não foi possível realizar a conexão por IP. Inciando conexão com impressora interna',
              [{text: 'OK', onPress: () => startConnectPrinterIntern()}],
            );
          }
        });
        setPrinterConnection('ip');
        setPrinterModelIp(model);
      } else {
        Alert.alert('Alert', 'Digíte um endereço e porta IP válido!');
      }
    } else {
      Alert.alert('Alert', 'Digíte um endereço e porta IP válido!');
    }
  }

  function stopConnectPrinter() {
    const command = new FechaConexaoImpressora();
    IntentDigitalHubCommandStarter.startCommand(command, _ => null);
  }

  function isIpAdressValid() {
    let ipValid = false;

    if (
      /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?[0-9][0-9])$/.test(
        ipConection,
      )
    ) {
      ipValid = true;
      return ipValid;
    } else {
      ipValid = false;
      return ipValid;
    }
  }

  return (
    <View style={styles.mainView}>
      <Header textTitle="IMPRESSORA" />
      <View style={styles.menuView}>
        <View style={styles.settingsPrinterHeader}>
          <View style={styles.printerConnectionOption}>
            <RadioButton
              value="interna"
              color="#0069A5"
              status={printerConnection === 'interna' ? 'checked' : 'unchecked'}
              onPress={() => changePrinterChoose('interna')}
            />
            <Text style={styles.labelText}>IMP. INTERNA</Text>
          </View>

          <View style={styles.printerConnectionOption}>
            <RadioButton
              value="ip"
              color="#0069A5"
              status={printerConnection === 'ip' ? 'checked' : 'unchecked'}
              onPress={() => changePrinterChoose('ip')}
            />
            <Text style={styles.labelText}>
              IMP. EXTERNA - IP {printerModelIp}
            </Text>
          </View>
        </View>
        <View style={styles.conectionView}>
          <Text>IP:</Text>
          <TextInput
            style={styles.inputMensage}
            placeholder="192.168.0.1:9100"
            placeholderTextColor="#999"
            autoCapitalize="none"
            keyboardType="default"
            autoCorrect={false}
            onChangeText={setIpConection}
            value={ipConection}
          />
        </View>
        {buttonsPrinter.map(({icon, textButton, onPress}, index) => (
          <TouchableOpacity
            style={[styles.buttonMenu]}
            key={index}
            onPress={onPress}>
            <Image style={styles.icon} source={icon} />
            <Text style={styles.menuTextButton}>{textButton}</Text>
          </TouchableOpacity>
        ))}
        <TouchableOpacity
          style={styles.statusButton}
          onPress={actualStatusPrinter}>
          <Image
            style={styles.statusIcon}
            source={require('../icons/status.png')}
          />
          <Text style={styles.statusButtonTXT}>STATUS IMPRESSORA</Text>
        </TouchableOpacity>
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
  menuView: {
    flexDirection: 'column',
    flex: 1,
    justifyContent: 'center',
    alignItems: 'stretch',
    paddingHorizontal: 10,
  },
  labelText: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: 16,
  },
  buttonMenu: {
    borderWidth: 2,
    borderColor: 'black',
    height: 90,
    fontWeight: 'bold',
    borderRadius: 20,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 8,
  },
  buttonMenuSelected: {borderColor: '#0069A5'},
  menuTextButton: {
    color: 'black',
    textAlign: 'center',
    fontSize: 12,
    fontWeight: 'bold',
  },
  icon: {
    width: 50,
    height: 50,
  },
  statusButton: {
    flexDirection: 'row',
    height: 50,
    borderWidth: 2,
    borderRadius: 20,
    borderColor: 'black',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 7,
  },
  statusButtonTXT: {
    color: 'black',
    fontSize: 12,
    fontWeight: 'bold',
    marginLeft: 5,
  },
  statusIcon: {
    width: 20,
    height: 20,
    marginLeft: 5,
  },
  settingsPrinterHeader: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    paddingBottom: 5,
  },
  printerConnectionOption: {flexDirection: 'row', alignItems: 'center'},
  settingPrinterBody: {
    flex: 1,
    borderColor: 'black',
    borderWidth: 2,
    borderRadius: 5,
    alignItems: 'stretch',
    justifyContent: 'space-between',
  },
  conectionView: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    justifyContent: 'center',
    alignItems: 'center',
    alignContent: 'center',
    marginBottom: 20,
  },
  inputMensage: {
    flex: 1,
    borderBottomColor: 'black',
    borderBottomWidth: 1,
    fontSize: 16,
    color: 'black',
    // textAlignVertical: 'bottom',
    padding: 0,
    marginLeft: 5,
  },
});

export default Printer;
