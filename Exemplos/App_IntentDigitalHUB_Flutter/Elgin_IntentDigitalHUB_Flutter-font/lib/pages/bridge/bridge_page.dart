// ignore_for_file: constant_identifier_names, non_constant_identifier_names
import 'dart:async';
import 'dart:convert';
import 'dart:math';
import 'package:currency_text_input_formatter/currency_text_input_formatter.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/consultar_status.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/consultar_ultima_transacao.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/get_timeout.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/imprimir_cupom_nfce.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/imprimir_cupom_sat.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/imprimir_cupom_sat_cancelamento.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/inicia_cancelamento_venda.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/inicia_operacao_administrativa.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/inicia_venda_credito.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/inicia_venda_debito.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/set_senha.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/set_senha_server.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/set_server.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/set_timeout.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:intent_digital_hub/XmStorageService/xml_database_service.dart';
import 'package:intl/intl.dart';

import '../../Widgets/widgets.dart';

part 'installment_method.dart';
part 'payment_method.dart';

class BridgePage extends StatefulWidget {
  const BridgePage({Key? key}) : super(key: key);

  @override
  _BridgePageState createState() => _BridgePageState();
}

class _BridgePageState extends State<BridgePage> {
  //Formatar do plugin currencyInputFormatter para a máscara de valor da transação
  final CurrencyTextInputFormatter _formatter = CurrencyTextInputFormatter(
      decimalDigits: 2, locale: 'pt_BR', symbol: '', turnOffGrouping: true);

  //Controllers para os formsFields
  TextEditingController controllerIpBridge =
      TextEditingController(text: "192.168.0.100");
  TextEditingController controllerTransactionPort =
      TextEditingController(text: "3000");
  TextEditingController controllerStatusPort =
      TextEditingController(text: "3001");
  TextEditingController controllerTransactionValue =
      TextEditingController(text: "20,00");
  TextEditingController controllerNumberOfInstallments =
      TextEditingController(text: "1");
  TextEditingController controllerPassword = TextEditingController(text: "");

  //Se a forma de pagamento for por crédito
  bool isCreditPaymentMethodSelected = true;

  //Se deve enviar senha nas transações
  bool sendPassword = false;

  //Forma de pagemento selecionada inicialmente
  PaymentMethod selectedPaymentMethod = PaymentMethod.CREDITO;

  //Forma de parcelamento selecionada inicialmente
  InstallmentMethod selectedInstallmentMethod =
      InstallmentMethod.FINANCIAMENTO_A_VISTA;

  //String utilizada nas transações para identificar o PVD
  static const String PDV_CODE = "PDV";

  ///A implementação da remoção de foco foi feita para que os dialogs que aparecerão na tela não invoquem novamente o teclado após o seu término
  ///pois nenhum input terá o foco após o teclado ser fechado com o pressionamento do botão de voltar (backButton)

  //Parametros para implementação da remoção do focus() em inputs quando a tecla backButton for pressionada no teclado
  late final KeyboardVisibilityController _keyboardVisibilityController;
  late StreamSubscription<bool> keyboardSubscription;

  //Quando a página iniciar adiciona um listener que escutará quando a tecla backButton for apertada e removerá o focos de qualquer input que ainda esteja com o focus()
  @override
  void initState() {
    super.initState();
    _keyboardVisibilityController = KeyboardVisibilityController();
    keyboardSubscription =
        _keyboardVisibilityController.onChange.listen((isVisible) {
      if (!isVisible) FocusManager.instance.primaryFocus?.unfocus();
    });
  }

  @override
  void dispose() {
    keyboardSubscription.cancel();
    super.dispose();
  }

  //Mudança de forma de pagamento
  onPaymentMethodChanged(PaymentMethod paymentMethodSelected) {
    setState(() => {
          selectedPaymentMethod = paymentMethodSelected,
          isCreditPaymentMethodSelected =
              (paymentMethodSelected == PaymentMethod.CREDITO)
        });
  }

  //Mudança de forma de parcelamento
  onInstallmentMethodChanged(InstallmentMethod installmentMethodSelected) {
    setState(() => {
          selectedInstallmentMethod = installmentMethodSelected,
          if (installmentMethodSelected ==
              InstallmentMethod.FINANCIAMENTO_A_VISTA)
            {
              controllerNumberOfInstallments.text = '1',
            }
          else
            controllerNumberOfInstallments.text = '2'
        });
  }

  //Checkbox de envio de senha
  void onShouldSendPasswordChanged(bool value) {
    sendPassword = value;
    if (!sendPassword) controllerPassword.text = '';
    setState(() {
      sendPassword = value;
    });
  }

  //Antes de quaisquer operações Bridge, atualizar o Ip e portas por onde a conexão será feita tryToUpdateBridgeServer(), validando todos os campos e retornando se foi possível atualizar com os dados valores.
  //Logo em seguida, verificar se o envio de senha deve ser feito shouldSendPassword()
  //'await' deve ser sempre utilizado para esperar que a resposta da intent chegue antes de um novo comando
  onConsultTerminalStatusPressed() async {
    //Variavel utilizada para receber o retorno do Bridge
    String bridgeJsonReturn = '';

    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      bridgeJsonReturn =
          await IntentDigitalHubCommandStarter.startCommand(ConsultarStatus());

      _showBridgeResponse(bridgeJsonReturn);
    }
  }

  onConsultConfiguredTimeoutPressed() async {
    String bridgeJsonReturn = '';

    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      bridgeJsonReturn =
          await IntentDigitalHubCommandStarter.startCommand(GetTimeout());

      _showBridgeResponse(bridgeJsonReturn);
    }
  }

  onConsultLastTransactionPressed() async {
    String bridgeJsonReturn = '';

    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      bridgeJsonReturn = await IntentDigitalHubCommandStarter.startCommand(
          ConsultarUltimaTransacao(PDV_CODE));

      _showBridgeResponse(bridgeJsonReturn);
    }
  }

  //Sempre que uma operação que envolva o campo de valor for chamada, validar este campo para o pagamento com isValueValiToElginPay()
  onSendTransactionPressed() async {
    if (await tryToUpdateBridgeServer() && isValueValidToElginPay()) {
      await shouldSendPassword();

      //O valor da transação deve ser enviado ao Bridge em centavos (ex R$ 20,00 deve ser passado 2000), portanto removemos a ',' antes da passagem do parametro valorTotal
      String transactionValueFormatted =
          controllerTransactionValue.text.replaceAll(',', '');

      String bridgeJsonReturn = '';

      if (selectedPaymentMethod == PaymentMethod.DEBITO) {
        bridgeJsonReturn = await IntentDigitalHubCommandStarter.startCommand(
            IniciaVendaDebito(generateRandomIntForBridgeTransaction(), PDV_CODE,
                transactionValueFormatted));
      } else {
        //Se o pagamento for por crédito, valida o número de parcelas inserido:
        if (isInstallmentsFieldValid()) {
          bridgeJsonReturn = await IntentDigitalHubCommandStarter.startCommand(
              IniciaVendaCredito(
                  generateRandomIntForBridgeTransaction(),
                  PDV_CODE,
                  transactionValueFormatted,
                  selectedInstallmentMethod.installmentMethodCode,
                  int.parse(controllerNumberOfInstallments.text)));
        }
      }

      _showBridgeResponse(bridgeJsonReturn);
    }
  }

  onCancelTransactionPressed() async {
    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      String inputRef = '';
      //aceitar apenas caractéres numéricos de 0 a 9
      FilteringTextInputFormatter cancelTrasanctionFilterToOnlyDigits =
          FilteringTextInputFormatter.allow(RegExp('[0-9]'));

      OnInputRefChanged(String newInputRef) {
        inputRef = newInputRef;
      }

      OnPressedAction(int pressedAction) async {
        Navigator.of(context).pop();

        //Se a opção OK foi selecionada
        if (pressedAction == 1) {
          //No app APP Experience, para fins de simplificação, faremos cancelamento apenas de vendas do mesmo dia, mas como pode ser inspecionado abaixo e na função no methodChannel no Java, podemos passar a data de cancelamento diretamente pra função de cancelamento
          DateTime now = DateTime.now();
          String formattedDate = DateFormat('dd/MM/yy').format(now);

          //O valor da transação deve ser enviado ao Bridge em centavos (ex R$ 20,00 deve ser passado 2000), portanto removemos a ',' antes da passagem do parametro valorTotal
          String transactionValueFormatted =
              controllerTransactionValue.text.replaceAll(',', '');

          String bridgeJsonReturn = '';

          bridgeJsonReturn = await IntentDigitalHubCommandStarter.startCommand(
              IniciaCancelamentoVenda(
                  generateRandomIntForBridgeTransaction(),
                  PDV_CODE,
                  transactionValueFormatted,
                  formattedDate,
                  inputRef));

          _showBridgeResponse(bridgeJsonReturn);
        }
      }

      GeneralWidgets.showAlertDialogWithInputField(
          mainWidgetContext: context,
          dialogTitle: 'Código de Referência: ',
          onTextInput: OnInputRefChanged,
          onPressedAction: OnPressedAction,
          textInputType: TextInputType.number,
          filteringTextInputFormatter: cancelTrasanctionFilterToOnlyDigits);
    }
  }

  onSetTerminalPasswordPressed() async {
    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      List<String> enableOrDisablePasswordOptions = [
        "Habilitar Senha no Terminal",
        "Desabilitar Senha no Terminal"
      ];

      onEnableOrDisablePasswordOptionTapped(int indexOfTapped) async {
        Navigator.of(context).pop();

        const int PRESSED_ACTION_ENABLEPASSWORD = 0;

        //Função e String utilizada para o recebimento do input no campo de diálogo
        String passwordToBeSet = '';
        onPasswordToBeSetChanged(String newPasswordToBeSet) {
          passwordToBeSet = newPasswordToBeSet;
        }

        //Se a opção escolhida foi Habilitar senha no terminal
        if (indexOfTapped == PRESSED_ACTION_ENABLEPASSWORD) {
          onPressedAction(int pressedAction) async {
            Navigator.of(context).pop();
            if (pressedAction == 1) {
              const bool HABILITAR_SENHA_TERMINAL = true;

              //Valida se o usuario não entrou com uma senha vazia
              if (passwordToBeSet.isEmpty) {
                GeneralWidgets.showAlertDialog(
                    mainWidgetContext: context,
                    dialogTitle: 'ALERTA',
                    dialogText:
                        'O campo de senha a ser habilitada não pode ser vazio!');
              } else {
                String bridgeJsonReturn = '';

                bridgeJsonReturn =
                    await IntentDigitalHubCommandStarter.startCommand(
                        SetSenhaServer(
                            passwordToBeSet, HABILITAR_SENHA_TERMINAL));

                _showBridgeResponse(bridgeJsonReturn);
              }
            }
          }

          GeneralWidgets.showAlertDialogWithInputField(
              mainWidgetContext: context,
              dialogTitle: 'DIGITE A SENHA A SER HABILITADA',
              onTextInput: onPasswordToBeSetChanged,
              onPressedAction: onPressedAction);
        }
        //Se a opção selecionada for de desabilite de senha
        else {
          //Para o desabilite de senha no terminal deve ser passado o booleano false
          const bool DESABILITAR_SENHA_TERMINAL = false;
          //Para apagar a senha no terminal deve-se enviar uma String vazia
          const String ERASE_TERMINAL_PASSWORD = '';

          String bridgeJsonReturn = '';

          bridgeJsonReturn = await IntentDigitalHubCommandStarter.startCommand(
              SetSenhaServer(
                  ERASE_TERMINAL_PASSWORD, DESABILITAR_SENHA_TERMINAL));

          _showBridgeResponse(bridgeJsonReturn);
        }
      }

      GeneralWidgets.showAlertDialogWithSelectableOptions(
          mainWidgetContext: context,
          alertTitle: 'ESCOLHA COMO CONFIGURAR A SENHA',
          listOfOptions: enableOrDisablePasswordOptions,
          onTap: onEnableOrDisablePasswordOptionTapped);
    }
  }

  onAdministrativeOperationPressed() async {
    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      List<String> administrativeOperationsList = [
        "Operação Administrativa",
        "Operação de Instalação",
        "Operação de Configuração",
        "Operação de Manutenção",
        "Teste de Comunicação",
        "Reimpressão de Comprovante"
      ];

      onAdministrativeOptionTapped(int indexOfTapped) async {
        Navigator.of(context).pop();
        //Neste caso o indice da opção escolhida corresponde diretamente ao valor que deve ser enviado para a escolha da operação a ser executada, pois a lista esta na ordem como especificada na documentação da Lib E1 (0-5)
        String bridgeJsonReturn = '';

        bridgeJsonReturn = await IntentDigitalHubCommandStarter.startCommand(
            IniciaOperacaoAdministrativa(
                generateRandomIntForBridgeTransaction(),
                PDV_CODE,
                indexOfTapped));

        _showBridgeResponse(bridgeJsonReturn);
      }

      GeneralWidgets.showAlertDialogWithSelectableOptions(
          mainWidgetContext: context,
          alertTitle: 'ESCOLHA A OPERAÇÃO ADMINISTRATIVA',
          listOfOptions: administrativeOperationsList,
          onTap: onAdministrativeOptionTapped);
    }
  }

  onPrintTestCouponPressed() async {
    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      List<String> couponOptions = [
        "Imprimir Cupom NFCe",
        "Imprimir Cupom Sat",
        "Imprimir Cupom Sat Cancelamento"
      ];
      onCouponOptionTapped(int indexOfTapped) async {
        Navigator.of(context).pop();

        String bridgeJsonReturn = '';

        //Todos os valores abaixo são exemplos
        switch (indexOfTapped) {
          case 0:
            final String xml = XmlFile.XML_NFCE.idhRelativePathForCommand;
            const int indexcsc = 1;
            const String csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";

            bridgeJsonReturn =
                await IntentDigitalHubCommandStarter.startCommand(
                    ImprimirCupomNfce(xml, indexcsc, csc));

            break;
          case 1:
            final String xml = XmlFile.XML_SAT.idhRelativePathForCommand;

            bridgeJsonReturn =
                await IntentDigitalHubCommandStarter.startCommand(
                    ImprimirCupomSat(xml));

            break;
          case 2:
            final String xml =
                XmlFile.XML_SAT_CANCELAMENTO.idhRelativePathForCommand;
            const String assQRCode =
                "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

            bridgeJsonReturn =
                await IntentDigitalHubCommandStarter.startCommand(
                    ImprimirCupomSatCancelamento(xml, assQRCode));

            break;
        }
        _showBridgeResponse(bridgeJsonReturn);
      }

      GeneralWidgets.showAlertDialogWithSelectableOptions(
          mainWidgetContext: context,
          alertTitle: "ESCOLHA O TIPO DE CUPOM",
          listOfOptions: couponOptions,
          onTap: onCouponOptionTapped);
    }
  }

  onSetTransactionTimeoutPressed() async {
    if (await tryToUpdateBridgeServer()) {
      await shouldSendPassword();

      String transactionTimeoutInSeconds = '';
      //aceitar apenas caractéres numéricos de 0 a 9
      FilteringTextInputFormatter setTrasanctionTimeoutFilterToOnlyDigits =
          FilteringTextInputFormatter.allow(RegExp('[0-9]'));

      onTransactionTimeoutInSecondsChanged(
          String newTransactionTimeoutInSeconds) {
        transactionTimeoutInSeconds = newTransactionTimeoutInSeconds;
      }

      onPressedAction(int pressedAction) async {
        Navigator.of(context).pop();

        const int PRESSED_ACTION_OK = 1;
        if (pressedAction == PRESSED_ACTION_OK) {
          //Valida se foi inserido um valor válido para timeout
          try {
            int newTransactionTimetouInSeconds =
                int.parse(transactionTimeoutInSeconds);

            String bridgeJsonReturn = '';

            bridgeJsonReturn =
                await IntentDigitalHubCommandStarter.startCommand(
                    SetTimeout(newTransactionTimetouInSeconds));

            _showBridgeResponse(bridgeJsonReturn);
          } on FormatException {
            GeneralWidgets.showAlertDialog(
                mainWidgetContext: context,
                dialogTitle: 'Alerta',
                dialogText:
                    'Insira um valor válido para a configuração do novo timeout!');
          }
        }
      }

      GeneralWidgets.showAlertDialogWithInputField(
          mainWidgetContext: context,
          dialogTitle: 'DEFINA UM NOVO TIMEOUT PARA TRANSAÇÃO (em segundos):',
          onTextInput: onTransactionTimeoutInSecondsChanged,
          onPressedAction: onPressedAction,
          textInputType: TextInputType.number,
          filteringTextInputFormatter: setTrasanctionTimeoutFilterToOnlyDigits);
    }
  }

//Função que valida o IP e as portas de comunicação e transação do Bridge
  Future<bool> tryToUpdateBridgeServer() async {
    //Verifica se todos os campos inseridos estão em um formato válido antes de tentar atualizar o servidor por onde o Bridge se conectará
    if (isIpValid() && isTransactionPortValid() && isStatusPortValid()) {
      await IntentDigitalHubCommandStarter.startCommand(SetServer(
          controllerIpBridge.text,
          int.parse(controllerTransactionPort.text),
          int.parse(controllerStatusPort.text)));
      return true;
    }
    return false;
  }

  //Trata da validação de IP
  bool isIpValid() {
    RegExp regexIpValidation = RegExp(
        r"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]){1}$");

    bool isIpValid = regexIpValidation.hasMatch(controllerIpBridge.text);

    if (!isIpValid) {
      GeneralWidgets.showAlertDialog(
          mainWidgetContext: context,
          dialogTitle: 'ALERTA',
          dialogText: 'Insira um IP válido para a conexão Bridge!');
      return false;
    }
    return true;
  }

  //Trata da validação do valor inserido na porta de transação
  bool isTransactionPortValid() {
    try {
      int portValueInInteger = int.parse(controllerTransactionPort.text);

      if (portValueInInteger < 65535) return true;
      GeneralWidgets.showAlertDialog(
          mainWidgetContext: context,
          dialogTitle: 'ALERTA',
          dialogText:
              'O valor inserido na porta de transação excede o limite esbelecido de 65535!');
      return false;
    } on FormatException catch (e) {
      if (kDebugMode) {
        print(e);
      }
    }
    GeneralWidgets.showAlertDialog(
        mainWidgetContext: context,
        dialogTitle: 'ALERTA',
        dialogText:
            'O valor inserido na porta de transação não pode estar vazio!');
    return false;
  }

  //Trata da validação do valor inserido na porta de transação
  bool isStatusPortValid() {
    try {
      int portValueInInteger = int.parse(controllerStatusPort.text);

      if (portValueInInteger < 65535) return true;
      GeneralWidgets.showAlertDialog(
          mainWidgetContext: context,
          dialogTitle: 'ALERTA',
          dialogText:
              'O valor inserido na porta de status excede o limite esbelecido de 65535!');
      return false;
    } on FormatException catch (e) {
      if (kDebugMode) {
        print(e);
      }
    }
    GeneralWidgets.showAlertDialog(
        mainWidgetContext: context,
        dialogTitle: 'ALERTA',
        dialogText:
            'O valor inserido na porta de status não pode estar vazio!');
    return false;
  }

  //Trata da validação de valor (O valor para transação não deve ser inferior a R$ 1,00)
  bool isValueValidToElginPay() {
    try {
      //A máscara utilizada para o campo valor utilizar o padrão ',' para representação monetária, o que deve ser substiuído por '.' antes da conversão para Double
      double transactionValueInInteger =
          double.parse(controllerTransactionValue.text.replaceAll(',', '.'));

      if (transactionValueInInteger < 1.00) {
        GeneralWidgets.showAlertDialog(
            mainWidgetContext: context,
            dialogTitle: 'ALERTA',
            dialogText: 'O valor mínimo para a transação é de 1.00!');
        return false;
      }
      return true;
    } on FormatException catch (e) {
      if (kDebugMode) {
        print(e);
      }
    }
    GeneralWidgets.showAlertDialog(
        mainWidgetContext: context,
        dialogTitle: 'ALERTA',
        dialogText: 'O valor para a transação não pode estar vazio!');
    return false;
  }

  //Trata da validação do número de parcelas (Caso o parcelamento por ADM ou LOJA estejam selecionados, uma vez que para o parcelamento a vista o número de parcelas é travado em 1)
  bool isInstallmentsFieldValid() {
    try {
      int numberOfInstallmentsInInteger =
          int.parse(controllerNumberOfInstallments.text);

      if ((selectedInstallmentMethod !=
              InstallmentMethod.FINANCIAMENTO_A_VISTA) &&
          (numberOfInstallmentsInInteger < 2)) {
        GeneralWidgets.showAlertDialog(
            mainWidgetContext: context,
            dialogTitle: 'ALERTA',
            dialogText:
                'O número mínimo de parcelas para esse tipo de parcelamento é 2!');
        return false;
      }
      return true;
    } on FormatException catch (e) {
      if (kDebugMode) {
        print(e);
      }
    }
    GeneralWidgets.showAlertDialog(
        mainWidgetContext: context,
        dialogTitle: 'ALERTA',
        dialogText: 'O campo de parcelas não pode estar vazio!');
    return false;
  }

  shouldSendPassword() async {
    //Se a opção de envio estiver marcada, enviar a senha inserida, caso contrário desabilite o envio de senha
    String passwordEntered = controllerPassword.text.toString();
    if (kDebugMode) {
      print(passwordEntered);
    }

    if (sendPassword) {
      const bool ENABLE_PASSWORD_SUBMISSION = true;

      await IntentDigitalHubCommandStarter.startCommand(
          SetSenha(passwordEntered, ENABLE_PASSWORD_SUBMISSION));
    } else {
      const bool DISABLE_PASSWORD_SUBMISSION = false;

      await IntentDigitalHubCommandStarter.startCommand(
          SetSenha(passwordEntered, DISABLE_PASSWORD_SUBMISSION));
    }
  }

  //Função que gera um Int aleatório para alimentar as transações do Bridge
  int generateRandomIntForBridgeTransaction() {
    var random = Random();
    return random.nextInt(999999);
  }

  //Invoca um alertDialog com a resposta do bridge extraída do JSON de retorno completo do comando
  _showBridgeResponse(String bridgeJsonReturn) => GeneralWidgets.showAlertDialog(
      mainWidgetContext: context,
      dialogTitle: 'Retorno E1 - BRIDGE',
      //O retorno do IDH é um JSONArray, como apenas uma chama é feita por vez, o json só possuí uma posição
      dialogText: jsonDecode(bridgeJsonReturn)[0]['resultado'].toString());

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Column(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const SizedBox(height: 25),
        GeneralWidgets.headerScreen("E1 - BRIDGE"),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10),
          child: Column(
            children: [
              Row(
                children: [
                  Expanded(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Expanded(
                          child: Text(
                            'IP:',
                            style: TextStyle(
                                fontWeight: FontWeight.bold, fontSize: 16),
                          ),
                        ),
                        Expanded(
                          child: TextFormField(
                            controller: controllerIpBridge,
                            keyboardType: TextInputType.number,
                            textAlign: TextAlign.center,
                          ),
                        )
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        const Text(
                          'PORTAS TRANSAÇÕES/STATUS:',
                          overflow: TextOverflow.clip,
                          style: TextStyle(
                              fontWeight: FontWeight.bold, fontSize: 16),
                        ),
                        SizedBox(
                          width: 50,
                          child: TextFormField(
                            controller: controllerTransactionPort,
                            inputFormatters: [
                              FilteringTextInputFormatter.allow(RegExp('[0-9]'))
                            ],
                            keyboardType: TextInputType.number,
                            textAlign: TextAlign.center,
                          ),
                        ),
                        SizedBox(
                          width: 50,
                          child: TextFormField(
                            controller: controllerStatusPort,
                            inputFormatters: [
                              FilteringTextInputFormatter.allow(RegExp('[0-9]'))
                            ],
                            keyboardType: TextInputType.number,
                            textAlign: TextAlign.center,
                          ),
                        )
                      ],
                    ),
                  )
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Expanded(
                          child: Row(
                            children: [
                              const Expanded(
                                child: Text(
                                  'VALOR:',
                                  style: TextStyle(
                                      fontWeight: FontWeight.bold,
                                      fontSize: 16),
                                ),
                              ),
                              Expanded(
                                child: TextFormField(
                                  controller: controllerTransactionValue,
                                  keyboardType: TextInputType.number,
                                  inputFormatters: [_formatter],
                                  textAlign: TextAlign.center,
                                ),
                              )
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Expanded(
                          child: ListTile(
                            title: const Text(
                              "ENVIAR SENHA NAS TRANSAÇÕES",
                              style: TextStyle(
                                  fontWeight: FontWeight.bold, fontSize: 16),
                            ),
                            leading: Checkbox(
                              value: sendPassword,
                              onChanged: (bool? value) =>
                                  onShouldSendPasswordChanged(value!),
                            ),
                          ),
                        )
                      ],
                    ),
                  )
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Row(
                      children: [
                        if (selectedPaymentMethod == PaymentMethod.CREDITO) ...{
                          const Expanded(
                            child: Text(
                              'N° PARCELAS:',
                              style: TextStyle(
                                  fontWeight: FontWeight.bold, fontSize: 16),
                            ),
                          ),
                          Expanded(
                            child: TextFormField(
                              controller: controllerNumberOfInstallments,
                              inputFormatters: [
                                FilteringTextInputFormatter.allow(
                                    RegExp('[0-9]'))
                              ],
                              textAlign: TextAlign.center,
                              keyboardType: TextInputType.number,
                              enabled: isCreditPaymentMethodSelected &&
                                  (selectedInstallmentMethod !=
                                      InstallmentMethod.FINANCIAMENTO_A_VISTA),
                            ),
                          )
                        }
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        const Text(
                          'SENHA:',
                          style: TextStyle(
                              fontWeight: FontWeight.bold, fontSize: 16),
                        ),
                        SizedBox(
                          width: 250,
                          child: Align(
                            alignment: Alignment.topRight,
                            child: TextFormField(
                              controller: controllerPassword,
                              textAlign: TextAlign.center,
                              obscureText: true,
                              enableSuggestions: false,
                              autocorrect: false,
                              enabled: sendPassword,
                            ),
                          ),
                        )
                      ],
                    ),
                  )
                ],
              ),
              const SizedBox(
                height: 5,
              ),
              Row(
                children: [
                  Expanded(
                    child: Row(
                      children: const [
                        Text(
                          'FORMAS DE PAGAMENTO:',
                          style: TextStyle(
                              fontWeight: FontWeight.bold, fontSize: 16),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: const [
                        Text(
                          'FUNÇÕES E1-BRIDGE:',
                          style: TextStyle(
                              fontWeight: FontWeight.bold, fontSize: 16),
                        ),
                      ],
                    ),
                  )
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Row(
                      children: [
                        GeneralWidgets.personSelectedButton(
                          nameButton: 'Crédito',
                          fontLabelSize: 12,
                          assetImage: 'assets/images/card.png',
                          isSelectedBtn:
                              selectedPaymentMethod == PaymentMethod.CREDITO,
                          onSelected: () =>
                              onPaymentMethodChanged(PaymentMethod.CREDITO),
                        ),
                        GeneralWidgets.personSelectedButton(
                          nameButton: 'Débito',
                          fontLabelSize: 12,
                          assetImage: 'assets/images/card.png',
                          isSelectedBtn:
                              selectedPaymentMethod == PaymentMethod.DEBITO,
                          onSelected: () =>
                              onPaymentMethodChanged(PaymentMethod.DEBITO),
                        )
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Expanded(
                          child: ElevatedButton(
                            onPressed: () => onConsultTerminalStatusPressed(),
                            child: const Text("CONSULTAR STATUS DO TERMINAL"),
                          ),
                        )
                      ],
                    ),
                  )
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Row(
                      children: [
                        if (selectedPaymentMethod == PaymentMethod.CREDITO) ...{
                          const Text(
                            'FORMAS DE PARCELAMENTO:',
                            style: TextStyle(
                                fontWeight: FontWeight.bold, fontSize: 16),
                          ),
                        }
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Expanded(
                          child: ElevatedButton(
                            onPressed: () =>
                                onConsultConfiguredTimeoutPressed(),
                            child: const Text("CONSULTAR TIMEOUT CONFIGURADO"),
                          ),
                        )
                      ],
                    ),
                  )
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Row(
                      children: [
                        if (selectedPaymentMethod == PaymentMethod.CREDITO) ...{
                          GeneralWidgets.personSelectedButton(
                            nameButton: 'Loja',
                            fontLabelSize: 12,
                            isSelectedBtn: selectedInstallmentMethod ==
                                InstallmentMethod
                                    .FINANCIAMENTO_PARCELADO_ESTABELECIMENTO,
                            assetImage: 'assets/images/store.png',
                            onSelected: () => onInstallmentMethodChanged(
                                InstallmentMethod
                                    .FINANCIAMENTO_PARCELADO_ESTABELECIMENTO),
                          ),
                          GeneralWidgets.personSelectedButton(
                            nameButton: 'Adm',
                            fontLabelSize: 12,
                            isSelectedBtn: selectedInstallmentMethod ==
                                InstallmentMethod
                                    .FINANCIAMENTO_PARCELADO_EMISSOR,
                            assetImage: 'assets/images/adm.png',
                            onSelected: () => onInstallmentMethodChanged(
                                InstallmentMethod
                                    .FINANCIAMENTO_PARCELADO_EMISSOR),
                          ),
                          GeneralWidgets.personSelectedButton(
                            nameButton: 'A vista',
                            fontLabelSize: 12,
                            isSelectedBtn: selectedInstallmentMethod ==
                                InstallmentMethod.FINANCIAMENTO_A_VISTA,
                            assetImage: 'assets/images/card.png',
                            onSelected: () => onInstallmentMethodChanged(
                                InstallmentMethod.FINANCIAMENTO_A_VISTA),
                          ),
                        }
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Expanded(
                          child: ElevatedButton(
                            onPressed: () => onConsultLastTransactionPressed(),
                            child: const Text("CONSULTAR ULTIMA TRANSAÇÃO"),
                          ),
                        )
                      ],
                    ),
                  )
                ],
              ),
              const SizedBox(
                height: 5,
              ),
              Row(
                children: [
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 3),
                      child: Row(
                        children: [
                          Expanded(
                            child: ElevatedButton(
                              onPressed: () => onSendTransactionPressed(),
                              child: const Text("ENVIAR TRANSAÇÃO"),
                            ),
                          ),
                          const SizedBox(
                            width: 5,
                          ),
                          Expanded(
                            child: ElevatedButton(
                              onPressed: () => onCancelTransactionPressed(),
                              child: const Text("CANCELAR TRANSAÇÃO"),
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Expanded(
                          child: ElevatedButton(
                            onPressed: () => onSetTerminalPasswordPressed(),
                            child: const Text("CONFIGURAR SENHA DO TERMINAL"),
                          ),
                        )
                      ],
                    ),
                  )
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 3),
                      child: Row(
                        children: [
                          Expanded(
                            child: ElevatedButton(
                              onPressed: () =>
                                  onAdministrativeOperationPressed(),
                              child: const Text("OPERAÇÃO ADM"),
                            ),
                          ),
                          const SizedBox(
                            width: 5,
                          ),
                          Expanded(
                            child: ElevatedButton(
                              onPressed: () => onPrintTestCouponPressed(),
                              child: const Text(
                                "IMPRIMIR CUPOM TESTE",
                                textAlign: TextAlign.center,
                              ),
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Expanded(
                          child: ElevatedButton(
                            onPressed: () => onSetTransactionTimeoutPressed(),
                            child: const Text(
                                "CONFIGURAR TIMEOUT PARA TRANSAÇÕES"),
                          ),
                        )
                      ],
                    ),
                  )
                ],
              )
            ],
          ),
        ),
        Padding(
            padding: const EdgeInsets.only(bottom: 5),
            child: GeneralWidgets.baseboard()),
      ],
    ));
  }
}
