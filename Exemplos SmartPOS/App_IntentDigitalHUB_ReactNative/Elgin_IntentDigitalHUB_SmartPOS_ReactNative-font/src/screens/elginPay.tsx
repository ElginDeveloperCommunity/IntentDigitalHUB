import React, {useEffect, useState} from 'react';

import {
  StyleSheet,
  Text,
  View,
  Image,
  TouchableOpacity,
  Alert,
  TextInput,
} from 'react-native';
import {Checkbox, Portal, Modal, Button} from 'react-native-paper';
import Header from '../components/Header';
import Footer from '../components/Footer';
import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {IniciaVendaCredito} from '../intentDigitalHubService/ELGINPAY/Commands/IniciaVendaCredito';
import {IniciaVendaDebito} from '../intentDigitalHubService/ELGINPAY/Commands/IniciaVendaDebito';
import {IniciaCancelamentoVenda} from '../intentDigitalHubService/ELGINPAY/Commands/IniciaCancelamentoVenda';
import {IniciaOperacaoAdministrativa} from '../intentDigitalHubService/ELGINPAY/Commands/IniciaOperacaoAdministrativa';
import {SetPersonalizacao} from '../intentDigitalHubService/ELGINPAY/Commands/SetPersonalizacao';

const YELLOW = '#FED20B';
const BLACK = '#050609';
const ELGINPAY_BLUE = '#0864a4';
const WHITE = '#FFFFFF';

const ElginPay = () => {
  const [valor, setValor] = useState('2000');
  const [numParcelas, setnumParcelas] = useState('1');
  const [paymentMethod, setPaymentMethod] = useState('Crédito');
  const [installmentType, setInstallmentType] = useState('1');
  const [customLayout, setCustomLayout] = useState(false);

  const [refCode, setRefCode] = useState('');

  const [isCancelationDialogVisible, setIsCancelationDialogVisible] =
    useState(false);

  useEffect(() => {
    let setPersonalizacaoCommand = new SetPersonalizacao(
      '',
      '',
      ELGINPAY_BLUE,
      WHITE,
      ELGINPAY_BLUE,
      WHITE,
      ELGINPAY_BLUE,
      WHITE,
      ELGINPAY_BLUE,
      ELGINPAY_BLUE,
    );
    if (customLayout) {
      setPersonalizacaoCommand = new SetPersonalizacao(
        '',
        '',
        YELLOW,
        BLACK,
        YELLOW,
        BLACK,
        YELLOW,
        BLACK,
        YELLOW,
        YELLOW,
      );
    }
    const commands = [setPersonalizacaoCommand];
    IntentDigitalHubCommandStarter.startCommands(commands, _ => {});
  }, [customLayout]);

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
      onPress: () => {
        setnumParcelas('2');
        setInstallmentType('3');
      },
    },
    {
      id: '2',
      icon: require('../icons/adm.png'),
      textButton: 'ADM ',
      onPress: () => {
        setnumParcelas('2');
        setInstallmentType('2');
      },
    },
    {
      id: '1',
      icon: require('../icons/card.png'),
      textButton: 'A VISTA',
      onPress: () => {
        setnumParcelas('1');
        setInstallmentType('1');
      },
    },
  ];

  //Funções dos botões da aplicação

  function sendTransaction() {
    if (isValueValidToElginPay() && isInstallmentsFieldValid()) {
      if (paymentMethod === 'Crédito') {
        const iniciaVendaCreditoCommand = new IniciaVendaCredito(
          valor.replace(/[^\d]+/g, ''),
          Number(installmentType),
          Number(numParcelas),
        );
        IntentDigitalHubCommandStarter.startCommand(
          iniciaVendaCreditoCommand,
          resultString => {
            const result = JSON.parse(resultString)[0].resultado;
            Alert.alert('Retorno E1 - ELGINPAY', result, [
              {text: 'OK', onPress: () => null},
            ]);
          },
        );
      } else {
        const iniciaVendaDebitoCommand = new IniciaVendaDebito(
          valor.replace(/[^\d]+/g, ''),
        );
        IntentDigitalHubCommandStarter.startCommand(
          iniciaVendaDebitoCommand,
          resultString => {
            const result = JSON.parse(resultString)[0].resultado;
            Alert.alert('Retorno E1 - ELGINPAY', result, [
              {text: 'OK', onPress: () => null},
            ]);
          },
        );
      }
    }
  }

  function cancelTransaction() {
    if (refCode !== '') {
      setIsCancelationDialogVisible(false);

      const iniciaCancelamentoVendaCommand = new IniciaCancelamentoVenda(
        valor.replace(/[^\d]+/g, ''),
        todayDate,
        refCode,
      );
      IntentDigitalHubCommandStarter.startCommand(
        iniciaCancelamentoVendaCommand,
        resultString => {
          const result = JSON.parse(resultString)[0].resultado;
          Alert.alert('Retorno E1 - ELGINPAY', result, [
            {text: 'OK', onPress: () => null},
          ]);
        },
      );
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
    const iniciaOperacaoAdministrativaCommand =
      new IniciaOperacaoAdministrativa();
    IntentDigitalHubCommandStarter.startCommand(
      iniciaOperacaoAdministrativaCommand,
      resultString => {
        const result = JSON.parse(resultString)[0].resultado;
        Alert.alert('Retorno E1 - ELGINPAY', result, [
          {text: 'OK', onPress: () => null},
        ]);
      },
    );
  }

  //Funções de validação

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
          'O número mínimo de parcelas para esse tipo de parcelamento é 2!',
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

  return (
    <View style={styles.mainView}>
      <Header textTitle={'ELGIN PAY'} />
      <View style={styles.configView}>
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

        {paymentMethod === 'Crédito' && (
          <View style={styles.inputContainer}>
            <Text style={styles.labelText}>Nº PARCELAS:</Text>
            <TextInput
              placeholder={'00'}
              style={styles.input}
              keyboardType="numeric"
              editable={installmentType !== '1'}
              onChangeText={setnumParcelas}
              value={numParcelas}
            />
          </View>
        )}

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

        <View
          style={[
            styles.paymentView,
            paymentMethod === 'Débito' && styles.paymentViewHidden,
          ]}
          pointerEvents={paymentMethod === 'Débito' ? 'none' : 'auto'}>
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

        <Portal>
          <Modal
            visible={isCancelationDialogVisible}
            dismissable={false}
            contentContainerStyle={styles.modal}>
            <Text>Código de Referência</Text>
            <TextInput
              placeholder="Insira o código de referência"
              onChangeText={setRefCode}
              value={refCode}
              keyboardType={'numeric'}
            />
            <View style={styles.submitionButtonsView}>
              <Button onPress={() => setIsCancelationDialogVisible(false)}>
                CANCELAR
              </Button>
              <Button onPress={() => cancelTransaction()}>OK</Button>
            </View>
          </Modal>
        </Portal>

        <View style={styles.checkBoxStyleView}>
          <Checkbox
            disabled={false}
            status={customLayout ? 'checked' : 'unchecked'}
            onPress={() => setCustomLayout(!customLayout)}
          />
          <Text style={styles.optionText}>LAYOUT PERSONALIZADO</Text>
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
            style={styles.largeButton}
            onPress={() => admOperation()}>
            <Text style={styles.textButton}>
              INICIAR OPERAÇÃO ADMINISRATIVA
            </Text>
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
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'flex-start',
    paddingHorizontal: 10,
    marginTop: 20,
  },
  labelText: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: 16,
  },
  optionText: {
    fontSize: 14,
    fontWeight: 'bold',
  },
  menuView: {
    flexDirection: 'row',
    height: '80%',
    justifyContent: 'space-between',
  },
  inputContainer: {
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 15,
  },
  input: {
    flexDirection: 'row',
    width: '70%',
    borderBottomWidth: 1,
    borderBottomColor: 'black',
    textAlignVertical: 'bottom',
    textAlign: 'center',
    padding: 0,
    fontSize: 17,
  },
  paymentView: {
    marginTop: 15,
  },
  paymentViewHidden: {
    opacity: 0,
  },
  paymentsButtonView: {
    flexDirection: 'row',
    marginTop: 10,
  },
  checkBoxStyleView: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 20,
    marginBottom: 10,
  },
  paymentButton: {
    borderColor: 'black',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderRadius: 15,
    width: 90,
    height: 90,
    marginHorizontal: 5,
  },
  icon: {
    width: 50,
    height: 50,
  },
  paymentButtonSelected: {
    borderColor: '#23F600',
  },
  buttonText: {
    fontSize: 10,
    fontWeight: 'bold',
  },
  submitionButtonsView: {
    marginTop: 5,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  submitionButton: {
    width: '48%',
    height: 50,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 10,
    justifyContent: 'center',
  },
  largeButton: {
    width: '100%',
    height: 50,
    backgroundColor: '#0069A5',
    alignItems: 'center',
    borderRadius: 10,
    justifyContent: 'center',
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 14,
  },
  modal: {
    backgroundColor: 'white',
    padding: 20,
    marginHorizontal: 20,
  },
});

export default ElginPay;
