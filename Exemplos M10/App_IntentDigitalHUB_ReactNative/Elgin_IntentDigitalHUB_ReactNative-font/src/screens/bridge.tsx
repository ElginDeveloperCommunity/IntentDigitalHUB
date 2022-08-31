import React, {useState} from 'react';

import {
  StyleSheet,
  Text,
  View,
  Image,
  TouchableOpacity,
  Alert,
  TextInput,
} from 'react-native';
import CheckBox from '@react-native-community/checkbox';
import Dialog from 'react-native-dialog';
import {Picker} from '@react-native-picker/picker';
import RNFS from 'react-native-fs';
import Header from '../components/Header';
import Footer from '../components/Footer';
import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {IniciaVendaCredito} from '../intentDigitalHubService/BRIDGE/IniciaVendaCredito';
import {IniciaVendaDebito} from '../intentDigitalHubService/BRIDGE/IniciaVendaDebito';
import {IniciaCancelamentoVenda} from '../intentDigitalHubService/BRIDGE/IniciaCancelamentoVenda';
import {IniciaOperacaoAdministrativa} from '../intentDigitalHubService/BRIDGE/IniciaOperacaoAdministrativa';
import {ImprimirCupomNfce} from '../intentDigitalHubService/BRIDGE/ImprimirCupomNfce';
import {ImprimirCupomSat} from '../intentDigitalHubService/BRIDGE/ImprimirCupomSat';
import {ImprimirCupomSatCancelamento} from '../intentDigitalHubService/BRIDGE/ImprimirCupomSatCancelamento';
import {SetSenhaServer} from '../intentDigitalHubService/BRIDGE/SetSenhaServer';
import {ConsultarStatus} from '../intentDigitalHubService/BRIDGE/ConsultarStatus';
import {GetTimeout} from '../intentDigitalHubService/BRIDGE/GetTimeout';
import {ConsultarUltimaTransacao} from '../intentDigitalHubService/BRIDGE/ConsultarUltimaTransacao';
import {SetTimeout} from '../intentDigitalHubService/BRIDGE/SetTimeout';
import {SetServer} from '../intentDigitalHubService/BRIDGE/SetServer';
import {SetSenha} from '../intentDigitalHubService/BRIDGE/SetSenha';
import {BridgeCommand} from '../intentDigitalHubService/BRIDGE/BridgeCommand';
import XmlFile from '../xmlStorageService/xmlFile';
import XmlSatCancelamento from '../rawXmls/xmlsatcancelamento';

const Bridge = () => {
  const [valor, setValor] = useState('2000');
  const [numParcelas, setnumParcelas] = useState('1');
  const [numIP, setNumIP] = useState('192.168.0.104');
  const [paymentMethod, setPaymentMethod] = useState('Crédito');
  const [installmentType, setInstallmentType] = useState('1');

  const [refCode, setRefCode] = useState('');

  const [isCancelationDialogVisible, setIsCancelationDialogVisible] =
    useState(false);
  const [isAdmDialogVisible, setIsAdmDialogVisible] = useState(false);
  const [isCuponDialogVisible, setIsCuponDialogVisible] = useState(false);
  const [isPasswordDialogVisible, setIsPasswordDialogVisible] = useState(false);
  const [selectedPasswordConfig, setSelectedPasswordConfig] =
    useState('enablePassword');
  const [isInputPasswordDialogVisible, setIsInputPasswordDialogVisible] =
    useState(false);
  const [isTimeOutInputVisible, setIsTimeOutInputVisible] = useState(false);

  const [selectedAdmOperation, setSelectedAdmOperation] = useState('');
  const [selectedCoupon, setSelectedCoupon] = useState('nfce');

  const [newTimeOut, setNewTimeOut] = useState('');

  const [trasactionPort, setTrasactionPort] = useState('3000');
  const [statusPort, setStatusPort] = useState('3001');

  const [sendPassword, setSendPassword] = useState(false);
  const [password, setPassword] = useState('');
  const [passwordEntered, setPasswordEntered] = useState('');

  const PDV = 'PDV1';

  function padTo2Digits(num: number) {
    return num.toString().padStart(2, '0');
  }

  function formatDate(date: Date) {
    return [
      padTo2Digits(date.getDate()),
      padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join('/');
  }
  // Data no formato dd/mm/yyyy
  const todayDate = formatDate(new Date());

  const buttonsPayment = [
    {
      id: 'Crédito',
      icon: require('../icons/card.png'),
      textButton: 'CRÉDITO',
      onPress: () => setPaymentMethod('Crédito'),
    },
    {
      id: 'Débito',
      icon: require('../icons/card.png'),
      textButton: 'DÉBITO',
      onPress: () => setPaymentMethod('Débito'),
    },
  ];

  const buttonsInstallment = [
    {
      id: '3',
      icon: require('../icons/store.png'),
      textButton: 'LOJA',
      onPress: () => setInstallmentType('3'),
    },
    {
      id: '2',
      icon: require('../icons/adm.png'),
      textButton: 'ADM ',
      onPress: () => setInstallmentType('2'),
    },
    {
      id: '1',
      icon: require('../icons/card.png'),
      textButton: 'A VISTA',
      onPress: () => setInstallmentType('1'),
    },
  ];

  const checkBoxType = [
    {
      id: 'passwordSender',
      textButton: 'ENVIAR SENHA NAS TRANSAÇÕES',
      value: sendPassword,
      setValue: (value: boolean) => setSendPassword(value),
    },
  ];

  const consultButtons = [
    {
      id: 'terminalStatus',
      textButton: 'CONSULTAR STATUS DO TERMINAL',
      onPress: () => checkTerminalStatus(),
    },
    {
      id: 'timeOutConfig',
      textButton: 'CONSULTAR TIMEOUT CONFIGURADO',
      onPress: () => checkConfiguredTimeout(),
    },
    {
      id: 'lastTransition',
      textButton: 'CONSULTAR ULTIMA TRANSAÇÃO',
      onPress: () => checkLastTransaction(),
    },
  ];

  const configButtons = [
    {
      id: 'terminalPassword',
      textButton: 'CONFIGURAR SENHA DO TERMINAL',
      onPress: () => setIsPasswordDialogVisible(true),
    },
    {
      id: 'transactionTimeOut',
      textButton: 'CONFIGURAR TIMEOUT PARA TRANSAÇÕES',
      onPress: () => setIsTimeOutInputVisible(true),
    },
  ];

  //Função utilizada para iniciar quaisquer operações Bridge com o fluxo :
  // recebe um comando e o concatena com as funções SetServer e SetSenha,
  // evitando a repetição em todas as funcionalidades e assegurando que as
  // ultimas alterações nos campos de entrada sejam efetivadas antes da excução da operação
  function startBridgeCommand(
    bridgeCommand: BridgeCommand,
    callback: (resultString: string) => void,
  ) {
    const commands = [
      new SetServer(numIP, Number(trasactionPort), Number(statusPort)),
      new SetSenha(password, sendPassword),
      bridgeCommand,
    ];
    IntentDigitalHubCommandStarter.startCommands(commands, callback);
  }

  //Funções dos botões da aplicação

  function sendTransaction() {
    if (
      updateBridgeServer() &&
      isValueValidToElginPay() &&
      isInstallmentsFieldValid()
    ) {
      if (paymentMethod === 'Crédito') {
        const iniciaVendaCreditoCommand = new IniciaVendaCredito(
          generateRandomForBridgeTransactions(),
          PDV,
          valor.replace(/[^\d]+/g, ''),
          Number(installmentType),
          Number(numParcelas),
        );
        startBridgeCommand(iniciaVendaCreditoCommand, resultString => {
          const result = JSON.parse(resultString)[2].resultado;
          Alert.alert('Retorno E1 - BRIDGE', result, [
            {text: 'OK', onPress: () => null},
          ]);
        });
      } else {
        const iniciaVendaDebitoCommand = new IniciaVendaDebito(
          generateRandomForBridgeTransactions(),
          PDV,
          valor.replace(/[^\d]+/g, ''),
        );
        startBridgeCommand(iniciaVendaDebitoCommand, resultString => {
          const result = JSON.parse(resultString)[2].resultado;
          Alert.alert('Retorno E1 - BRIDGE', result, [
            {text: 'OK', onPress: () => null},
          ]);
        });
      }
    }
  }

  function cancelTransaction() {
    if (refCode !== '') {
      setIsCancelationDialogVisible(false);

      if (updateBridgeServer()) {
        const iniciaCancelamentoVendaCommand = new IniciaCancelamentoVenda(
          generateRandomForBridgeTransactions(),
          PDV,
          valor.replace(/[^\d]+/g, ''),
          todayDate,
          refCode,
        );
        startBridgeCommand(iniciaCancelamentoVendaCommand, resultString => {
          const result = JSON.parse(resultString)[2].resultado;
          Alert.alert('Retorno E1 - BRIDGE', result, [
            {text: 'OK', onPress: () => null},
          ]);
        });
      }
    } else {
      Alert.alert(
        'Código de Referência Vazio',
        'Por favor, insira um código de referência válido',
      );
    }
    setTimeout(() => {
      setRefCode('');
    }, 500);
  }

  function admOperation() {
    setIsAdmDialogVisible(false);
    if (updateBridgeServer()) {
      const iniciaOperacaoAdministrativaCommand =
        new IniciaOperacaoAdministrativa(
          generateRandomForBridgeTransactions(),
          PDV,
          Number(selectedAdmOperation),
        );
      startBridgeCommand(iniciaOperacaoAdministrativaCommand, resultString => {
        const result = JSON.parse(resultString)[2].resultado;
        Alert.alert('Retorno E1 - BRIDGE', result, [
          {text: 'OK', onPress: () => null},
        ]);
      });
    }
  }

  // As impressões feitas na aplicação utilizam os arquivos XML salvos previamente no início da aplicação.
  // Para indicar um caminho de arquivo como parâmetro ao Intent Digital Hub é necessário enviar
  // no parâmetro que iria o XML:
  // 'path=<caminho do arquivo>'
  function printCouponTest(couponType: string) {
    if (updateBridgeServer()) {
      if (couponType === 'nfce') {
        doBridgeXmlNFCe();
      } else if (couponType === 'sat') {
        doBridgeXmlSat();
      } else {
        doBridgeXmlSatCancel();
      }
    }
  }

  function doBridgeXmlNFCe() {
    const directory = RNFS.ExternalDirectoryPath;
    const fileName = XmlFile.XML_NFCE.xmlArchiveName + '.xml';
    const path =
      directory.substring(directory.indexOf('/Android')) + '/' + fileName;

    const indexcsc = 1;
    const csc = 'CODIGO-CSC-CONTRIBUINTE-36-CARACTERES';

    const imprimirCupomNfceCommand = new ImprimirCupomNfce(
      `path=${path}`,
      indexcsc,
      csc,
    );
    startBridgeCommand(imprimirCupomNfceCommand, resultString => {
      const result = JSON.parse(resultString)[2].resultado;
      Alert.alert('Retorno E1 - BRIDGE', result, [
        {text: 'OK', onPress: () => setIsCuponDialogVisible(false)},
      ]);
    });
  }

  function doBridgeXmlSat() {
    const directory = RNFS.ExternalDirectoryPath;
    const fileName = XmlFile.XML_SAT.xmlArchiveName + '.xml';
    const path =
      directory.substring(directory.indexOf('/Android')) + '/' + fileName;

    const imprimirCupomSatCommand = new ImprimirCupomSat(`path=${path}`);
    startBridgeCommand(imprimirCupomSatCommand, resultString => {
      const result = JSON.parse(resultString)[2].resultado;
      Alert.alert('Retorno E1 - BRIDGE', result, [
        {text: 'OK', onPress: () => setIsCuponDialogVisible(false)},
      ]);
    });
  }

  function doBridgeXmlSatCancel() {
    const directory = RNFS.ExternalDirectoryPath;
    const fileName = XmlFile.XML_SAT_CANCELAMENTO.xmlArchiveName + '.xml';
    const path =
      directory.substring(directory.indexOf('/Android')) + '/' + fileName;
    const assQRCode = new XmlSatCancelamento().getQrCodeSatCancelamento();

    const imprimirCupomSatCancelamentoCommand =
      new ImprimirCupomSatCancelamento(`path=${path}`, assQRCode);
    startBridgeCommand(imprimirCupomSatCancelamentoCommand, resultString => {
      const result = JSON.parse(resultString)[2].resultado;
      Alert.alert('Retorno E1 - BRIDGE', result, [
        {text: 'OK', onPress: () => setIsCuponDialogVisible(false)},
      ]);
    });
  }

  function checkTerminalStatus() {
    if (updateBridgeServer()) {
      const consultarStatusCommand = new ConsultarStatus();
      startBridgeCommand(consultarStatusCommand, resultString => {
        const result = JSON.parse(resultString)[2].resultado;
        Alert.alert('Retorno E1 - BRIDGE', result, [
          {text: 'OK', onPress: () => null},
        ]);
      });
    }
  }

  function checkConfiguredTimeout() {
    if (updateBridgeServer()) {
      const getTimeoutCommand = new GetTimeout();
      startBridgeCommand(getTimeoutCommand, resultString => {
        const result = JSON.parse(resultString)[2].resultado;
        Alert.alert('Retorno E1 - BRIDGE', result, [
          {text: 'OK', onPress: () => null},
        ]);
      });
    }
  }

  function checkLastTransaction() {
    if (updateBridgeServer()) {
      const consultarUltimaTransacaoCommand = new ConsultarUltimaTransacao(PDV);
      startBridgeCommand(consultarUltimaTransacaoCommand, resultString => {
        const result = JSON.parse(resultString)[2].resultado;
        Alert.alert('Retorno E1 - BRIDGE', result, [
          {text: 'OK', onPress: () => null},
        ]);
      });
    }
  }

  function configureTerminalPassword() {
    if (selectedPasswordConfig === 'enablePassword') {
      setIsInputPasswordDialogVisible(true);
    } else {
      if (!sendPassword) {
        Alert.alert(
          'Alerta',
          'Habilite a opção de envio de senha e envie a senha mais atual para desabilitar a senha do terminal',
        );
      } else {
        if (updateBridgeServer()) {
          const setSenhaServerCommand = new SetSenhaServer('', false);
          startBridgeCommand(setSenhaServerCommand, resultString => {
            const result = JSON.parse(resultString)[2].resultado;
            Alert.alert('Retorno E1 - BRIDGE', result, [
              {text: 'OK', onPress: () => null},
            ]);
          });
          closeDialog('passwordConfigDialog');
        }
      }
    }
  }

  function enableTerminalPassword() {
    if (passwordEntered === '') {
      Alert.alert(
        'Erro na Senha',
        'Por favor insira um valor para a senha desejada!',
      );
    } else {
      if (updateBridgeServer()) {
        const setSenhaServerCommand = new SetSenhaServer(passwordEntered, true);
        startBridgeCommand(setSenhaServerCommand, resultString => {
          const result = JSON.parse(resultString)[2].resultado;
          Alert.alert('Retorno E1 - BRIDGE', result, [
            {text: 'OK', onPress: () => null},
          ]);
        });

        closeDialog('successPassword');
      }
    }
  }

  function setTransactionTimeOut() {
    if (newTimeOut === '') {
      Alert.alert('Alerta', 'O valor para o TimeOut não pode ser vazio.');
    } else {
      if (updateBridgeServer()) {
        const setTimeoutCommand = new SetTimeout(Number(newTimeOut));
        startBridgeCommand(setTimeoutCommand, resultString => {
          const result = JSON.parse(resultString)[2].resultado;
          Alert.alert('Retorno E1 - BRIDGE', result, [
            {text: 'OK', onPress: () => null},
          ]);
        });
      }
      setIsTimeOutInputVisible(false);
      setNewTimeOut('');
    }
  }

  //Funções de validação

  function isIpAdressValid(): boolean {
    let ipValid = false;

    if (
      /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]){1}$/.test(
        numIP,
      )
    ) {
      ipValid = true;
      return ipValid;
    } else {
      ipValid = false;
      Alert.alert(
        'Erro de IP',
        'O endereço IP está incorreto, digite um endereço válido',
      );
      return ipValid;
    }
  }

  function isValueValidToElginPay(): boolean {
    if (Number(valor) < 1) {
      Alert.alert(
        'Erro na entrada de valor',
        'O valor mínimo para transações é de R$1.00!',
      );
      return false;
    } else {
      return true;
    }
  }

  function isInstallmentsFieldValid(): boolean {
    if (paymentMethod === 'Crédito') {
      if (
        (installmentType === '2' || installmentType === '3') &&
        Number(numParcelas) < 2
      ) {
        Alert.alert(
          'Erro no parcelamento',
          'O número de parcelas deve ser maior que 2!',
        );
        return false;
      } else if (numParcelas === '') {
        Alert.alert(
          'Erro no parcelamento',
          'O número de parcelas não pode ser vazio!',
        );
        return false;
      } else {
        return true;
      }
    } else {
      return true;
    }
  }

  function isTransactionPortValid(): boolean {
    if (Number(trasactionPort) > 65535) {
      Alert.alert(
        'Erro na Porta de Transação',
        'O valor inserido na porta de transação excede o limite esbelecido de 65535!',
        [{text: 'OK', onPress: () => null}],
      );
      return false;
    } else if (trasactionPort === '') {
      Alert.alert(
        'Erro na Porta de Transação',
        'O valor inserido na porta de transação não pode ser vazio',
      );
      return false;
    } else {
      return true;
    }
  }

  function isStatusPortValid() {
    if (Number(trasactionPort) > 65535) {
      Alert.alert(
        'Erro na Porta de Status',
        'O valor inserido na porta de status excede o limite esbelecido de 65535!',
      );
      return false;
    } else if (trasactionPort === '') {
      Alert.alert(
        'Erro na Porta de Status',
        'O valor inserido na porta de status não pode ser vazio',
      );
      return false;
    } else {
      return true;
    }
  }

  //Funções de configurações do Server Bridge

  function updateBridgeServer() {
    if (isIpAdressValid() && isTransactionPortValid() && isStatusPortValid()) {
      return true;
    } else {
      return false;
    }
  }

  // Outras funções

  function generateRandomForBridgeTransactions(): number {
    return Math.floor(Math.random() * (1000000 - 0)) + 0;
  }

  function closeDialog(wichDialogToClose: string) {
    switch (wichDialogToClose) {
      case 'cancelationDialog':
        setIsCancelationDialogVisible(false);
        setRefCode('');
        break;
      case 'passwordConfigDialog':
        setIsPasswordDialogVisible(false);
        setSelectedPasswordConfig('');
        break;
      case 'passwordInputDialog':
        setPasswordEntered('');
        setIsInputPasswordDialogVisible(false);
        break;
      case 'successPassword':
        setIsPasswordDialogVisible(false);
        setIsInputPasswordDialogVisible(false);
        setPasswordEntered('');
        break;
      case 'timeOutInputDialog':
        setIsTimeOutInputVisible(false);
        setNewTimeOut('');
    }
  }

  return (
    <View style={styles.mainView}>
      <Header textTitle={'E1 - BRIDGE'} />
      <View style={styles.menuView}>
        <View style={styles.configView}>
          <View style={styles.inputContainer}>
            <Text style={styles.labelText}>IP:</Text>
            <TextInput
              placeholder={'000.000.0.000'}
              style={styles.input}
              onChangeText={setNumIP}
              value={numIP}
            />
          </View>

          <View style={styles.inputContainer}>
            <Text style={styles.labelText}>VALOR:</Text>
            <TextInput
              placeholder={'000'}
              style={styles.input}
              keyboardType="numeric"
              onChangeText={setValor}
              value={valor}
            />
          </View>

          <View style={styles.inputContainer}>
            <Text style={styles.labelText}>Nº PARCELAS:</Text>
            <TextInput
              placeholder={'00'}
              style={styles.input}
              editable={paymentMethod === 'Crédito' ? true : false}
              keyboardType="numeric"
              onChangeText={setnumParcelas}
              value={numParcelas}
            />
          </View>

          <View style={styles.paymentView}>
            <Text style={styles.labelText}> FORMAS DE PAGAMENTO </Text>
            <View style={styles.paymentsButtonView}>
              {buttonsPayment.map(({id, icon, textButton, onPress}, index) => (
                <TouchableOpacity
                  style={[
                    styles.paymentButton,
                    id === paymentMethod && styles.paymentButtonSelected,
                  ]}
                  key={index}
                  onPress={onPress}>
                  <Image style={styles.icon} source={icon} />
                  <Text style={styles.buttonText}>{textButton}</Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>

          <View style={styles.paymentView}>
            <Text style={styles.labelText}> TIPO DE PARCELAMENTO </Text>
            <View style={styles.paymentsButtonView}>
              {buttonsInstallment.map(
                ({id, icon, textButton, onPress}, index) => (
                  <TouchableOpacity
                    style={[
                      styles.paymentButton,
                      id === installmentType && styles.paymentButtonSelected,
                    ]}
                    key={index}
                    onPress={onPress}>
                    <Image style={styles.icon} source={icon} />
                    <Text style={styles.buttonText}>{textButton}</Text>
                  </TouchableOpacity>
                ),
              )}
            </View>
          </View>

          <View>
            <Dialog.Container visible={isCancelationDialogVisible}>
              <Dialog.Title>Código de Referência</Dialog.Title>
              <Dialog.Input
                label="Insira o código de referência"
                onChangeText={setRefCode}
                value={refCode}
                keyboardType={'numeric'}
              />
              <Dialog.Button
                label="CANCELAR"
                onPress={() => closeDialog('cancelationDialog')}
              />
              <Dialog.Button label="OK" onPress={() => cancelTransaction()} />
            </Dialog.Container>
          </View>

          <View>
            <Dialog.Container visible={isAdmDialogVisible}>
              <Dialog.Title>Escolha uma Operação Administrativa</Dialog.Title>
              <Picker
                selectedValue={selectedAdmOperation}
                onValueChange={(itemValue, _) =>
                  setSelectedAdmOperation(itemValue)
                }>
                <Picker.Item label="Operação Administrativa" value="0" />
                <Picker.Item label="Operação de Instalação" value="1" />
                <Picker.Item label="Operação de Configuração" value="2" />
                <Picker.Item label="Operação de Manutenção" value="3" />
                <Picker.Item label="Teste de Comunicação" value="4" />
                <Picker.Item label="Reimpressão de Comprovante" value="5" />
              </Picker>

              <Dialog.Button
                label="CANCELAR"
                onPress={() => setIsAdmDialogVisible(false)}
              />
              <Dialog.Button label="OK" onPress={() => admOperation()} />
            </Dialog.Container>
          </View>

          <View>
            <Dialog.Container visible={isCuponDialogVisible}>
              <Dialog.Title>Escolha o Tipo de Cupom</Dialog.Title>
              <Picker
                selectedValue={selectedCoupon}
                onValueChange={(itemValue, _) => setSelectedCoupon(itemValue)}>
                <Picker.Item label="Imprimir Cupom NFCe" value="nfce" />
                <Picker.Item label="Imprimir Cupom Sat" value="sat" />
                <Picker.Item
                  label="Imprimir Cupom Sat Cancelamento"
                  value="cancelCoupon"
                />
              </Picker>

              <Dialog.Button
                label="CANCELAR"
                onPress={() => setIsCuponDialogVisible(false)}
              />
              <Dialog.Button
                label="OK"
                onPress={() => printCouponTest(selectedCoupon)}
              />
            </Dialog.Container>
          </View>

          <View style={styles.submitionButtonsView}>
            <TouchableOpacity
              style={styles.submitionButton}
              onPress={() => sendTransaction()}>
              <Text style={styles.textButton}>ENVIAR TRANSAÇÃO</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.submitionButton}
              onPress={() => setIsCancelationDialogVisible(true)}>
              <Text style={styles.textButton}>CANCELAR TRANSAÇÃO</Text>
            </TouchableOpacity>
          </View>
          <View style={styles.submitionButtonsView}>
            <TouchableOpacity
              style={styles.submitionButton}
              onPress={() => setIsAdmDialogVisible(true)}>
              <Text style={styles.textButton}>OPERAÇÃO ADM</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.submitionButton}
              onPress={() => setIsCuponDialogVisible(true)}>
              <Text style={styles.textButton}>IMPRIMIR CUPOM TESTE</Text>
            </TouchableOpacity>
          </View>
        </View>
        <View style={styles.configView}>
          <View style={styles.portView}>
            <Text style={styles.labelText}>PORTAS TRANSAÇÕES/STATUS:</Text>
            <View style={styles.inputPortView}>
              <TextInput
                placeholder={'3000'}
                style={styles.inputPort}
                keyboardType="numeric"
                onChangeText={setTrasactionPort}
                value={trasactionPort}
              />
              <TextInput
                placeholder={'3000'}
                style={styles.inputPort}
                keyboardType="numeric"
                onChangeText={setStatusPort}
                value={statusPort}
              />
            </View>
          </View>
          <View>
            {checkBoxType.map(({id, textButton, value, setValue}) => (
              <View key={id} style={styles.checkBoxStyleView}>
                <CheckBox
                  disabled={false}
                  value={value}
                  onValueChange={newValue => setValue(newValue)}
                />
                <Text style={styles.optionText}>{textButton}</Text>
              </View>
            ))}
          </View>
          <View style={styles.inputContainer}>
            <Text style={styles.labelText}>SENHA:</Text>
            <TextInput
              editable={sendPassword === true ? true : false}
              placeholder={'insira a senha'}
              secureTextEntry={true}
              style={styles.input}
              onChangeText={setPassword}
              value={password}
            />
          </View>
          <View style={styles.e1BridgeFunctionsView}>
            <Text style={styles.labelText}>FUNÇÕES E1-BRIDGE: </Text>
            <View style={styles.consultButtonsView}>
              {consultButtons.map(({id, textButton, onPress}) => (
                <View key={id}>
                  <TouchableOpacity
                    style={styles.largeButton}
                    onPress={onPress}>
                    <Text style={styles.textButton}>{textButton}</Text>
                  </TouchableOpacity>
                </View>
              ))}
            </View>

            <View style={styles.configButtonsView}>
              {configButtons.map(({id, textButton, onPress}) => (
                <View key={id}>
                  <TouchableOpacity
                    style={styles.largeButton}
                    onPress={onPress}>
                    <Text style={styles.textButton}>{textButton}</Text>
                  </TouchableOpacity>
                </View>
              ))}
            </View>

            <Dialog.Container visible={isPasswordDialogVisible}>
              <Dialog.Title>Escolha como configurar a senha</Dialog.Title>
              <Picker
                selectedValue={selectedPasswordConfig}
                onValueChange={(itemValue, _) =>
                  setSelectedPasswordConfig(itemValue)
                }>
                <Picker.Item
                  label="Habilitar Senha no Terminal"
                  value="enablePassword"
                />
                <Picker.Item
                  label="Desabilitar Senha no Terminal"
                  value="unablePassword"
                />
              </Picker>

              <Dialog.Button
                label="CANCELAR"
                onPress={() => closeDialog('passwordInputDialog')}
              />

              <Dialog.Button
                label="OK"
                onPress={() => configureTerminalPassword()}
              />
            </Dialog.Container>

            <View>
              <Dialog.Container visible={isInputPasswordDialogVisible}>
                <Dialog.Title>Digite a senha a ser habilitada</Dialog.Title>
                <Dialog.Input
                  label="Senha: "
                  onChangeText={setPasswordEntered}
                  secureTextEntry={true}
                  value={passwordEntered}
                  keyboardType={'default'}
                />
                <Dialog.Button
                  label="CANCELAR"
                  onPress={() => closeDialog('passwordInputDialog')}
                />
                <Dialog.Button
                  label="OK"
                  onPress={() => enableTerminalPassword()}
                />
              </Dialog.Container>
            </View>

            <View>
              <Dialog.Container visible={isTimeOutInputVisible}>
                <Dialog.Title>
                  Defina um novo timeout para transação (em segundos)
                </Dialog.Title>
                <Dialog.Input
                  label="Novo TimeOut: "
                  onChangeText={setNewTimeOut}
                  value={newTimeOut}
                  keyboardType={'numeric'}
                />
                <Dialog.Button
                  label="CANCELAR"
                  onPress={() => closeDialog('timeOutInputDialog')}
                />
                <Dialog.Button
                  label="OK"
                  onPress={() => setTransactionTimeOut()}
                />
              </Dialog.Container>
            </View>
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
  labelText: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: 14,
  },
  optionText: {
    fontSize: 14,
    fontWeight: 'bold',
  },
  titleText: {
    textAlign: 'center',
    fontSize: 30,
    fontWeight: 'bold',
  },
  menuView: {
    flexDirection: 'row',
    height: '80%',
    justifyContent: 'space-between',
    paddingHorizontal: 10,
  },
  inputContainer: {
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  input: {
    flexDirection: 'row',
    width: '70%',
    borderBottomWidth: 0.5,
    borderBottomColor: 'black',
    textAlignVertical: 'bottom',
    padding: 0,
    fontSize: 17,
  },
  inputPortView: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
  },
  inputPort: {
    flex: 1,
    flexDirection: 'row',
    borderBottomWidth: 0.5,
    borderBottomColor: 'black',
    textAlignVertical: 'bottom',
    marginHorizontal: 5,
    padding: 0,
    fontSize: 17,
  },
  configView: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-between',
    paddingHorizontal: 10,
  },
  returnView: {
    height: 400,
    padding: 15,
    borderWidth: 3,
    borderRadius: 7,
    borderColor: 'black',
    flexDirection: 'column',
    width: '47%',
    // justifyContent:'space-between'
  },
  paymentView: {
    marginTop: 15,
  },
  paymentsButtonView: {
    flexDirection: 'row',
  },
  portView: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  checkBoxStyleView: {
    flexDirection: 'row',
    alignItems: 'center',

    width: '100%',
  },
  e1BridgeFunctionsView: {
    justifyContent: 'space-between',
    height: '60%',
  },
  consultButtonsView: {
    flexDirection: 'column',

    height: '45%',
    justifyContent: 'space-between',
  },
  configButtonsView: {
    flexDirection: 'column',

    height: '30%',
    justifyContent: 'space-between',
  },
  paymentButton: {
    borderColor: 'black',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderRadius: 15,
    width: 60,
    height: 60,
    marginHorizontal: 5,
  },
  paymentButtonSelected: {
    borderColor: '#23F600',
  },
  typeTEFButton: {
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderRadius: 15,
    width: 100,
    height: 35,
    marginHorizontal: 5,
  },
  icon: {
    width: 30,
    height: 30,
  },
  submitionButtonsView: {
    marginTop: 5,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  submitionButton: {
    width: '48%',
    height: 35,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 10,
    justifyContent: 'center',
  },
  largeButton: {
    width: '100%',
    height: 35,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 10,
    justifyContent: 'center',
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 12,
  },
  titleReturnView: {
    marginBottom: 10,
  },
  buttonText: {
    fontSize: 10,
    fontWeight: 'bold',
  },
});

export default Bridge;
