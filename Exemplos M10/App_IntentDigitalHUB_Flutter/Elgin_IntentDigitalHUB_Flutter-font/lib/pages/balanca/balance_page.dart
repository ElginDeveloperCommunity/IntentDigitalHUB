import 'dart:convert';

import 'package:currency_text_input_formatter/currency_text_input_formatter.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/abrir_serial.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/configurar_modelo_balanca.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/configurar_protocolo_comunicacao.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/fechar.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/ler_peso.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:intl/intl.dart';

import '../../Widgets/widgets.dart';

part 'balance_model.dart';
part 'balance_protocol.dart';

class BalancePage extends StatefulWidget {
  const BalancePage({Key? key}) : super(key: key);

  @override
  createState() => _BalancePageState();
}

class _BalancePageState extends State<BalancePage> {
  TextEditingController valueBalance = TextEditingController(text: "0.00");

  //Modelo e protocolo de balança escolhidos inicialmente
  BalanceModel selectedBalanceModel = BalanceModel.DP3005;
  BalanceProtocol selectedBalanceProtocol = BalanceProtocol.PROTOCOL_0;

  onChangeTypeBalance(String balanceModelName) {
    setState(() {
      if (balanceModelName == BalanceModel.DP3005.name) {
        selectedBalanceModel = BalanceModel.DP3005;
      } else if (balanceModelName == BalanceModel.SA110.name) {
        selectedBalanceModel = BalanceModel.SA110;
      } else if (balanceModelName == BalanceModel.DPSC.name) {
        selectedBalanceModel = BalanceModel.DPSC;
      } else {
        selectedBalanceModel = BalanceModel.DP30CK;
      }
    });
  }

  sendConfigBalance() async {
    final String balanceModelConfigurationCommandReturn =
        await IntentDigitalHubCommandStarter.startCommand(
            ConfigurarModeloBalanca(selectedBalanceModel.balanceCode));

    final String protocolConfigurationCommandReturn =
        await IntentDigitalHubCommandStarter.startCommand(
            ConfigurarProtocoloComunicacao(
                selectedBalanceProtocol.protocolCode));

    Fluttertoast.showToast(
        msg:
            '''ConfigurarModeloBalanca: ${jsonDecode(balanceModelConfigurationCommandReturn)[0]['resultado']}\nConfigurarProtocoloComunicacao: ${jsonDecode(protocolConfigurationCommandReturn)[0]['resultado']}''',
        toastLength: Toast.LENGTH_LONG,
        gravity: ToastGravity.BOTTOM,
        timeInSecForIosWeb: 1,
        backgroundColor: Colors.blueGrey,
        textColor: Colors.white,
        fontSize: 16.0);
  }

  sendLerPeso() async {
    final String openSerialCommandReturn =
        await IntentDigitalHubCommandStarter.startCommand(
            AbrirSerial(2400, 8, 'N', 1));

    final String readWeightCommandReturn =
        await IntentDigitalHubCommandStarter.startCommand(LerPeso(1));

    final String closeBalanceCommandReturn =
        await IntentDigitalHubCommandStarter.startCommand(Fechar());

    await Fluttertoast.showToast(
        msg:
            '''AbrirSerial: ${jsonDecode(openSerialCommandReturn)[0]['resultado']}\nLerPeso: ${jsonDecode(readWeightCommandReturn)[0]['resultado']}\nFechar: ${jsonDecode(closeBalanceCommandReturn)[0]['resultado']}''',
        toastLength: Toast.LENGTH_LONG,
        gravity: ToastGravity.BOTTOM,
        timeInSecForIosWeb: 1,
        backgroundColor: Colors.blueGrey,
        textColor: Colors.white,
        fontSize: 16.0);

    //Se a leitura de peso obtiver um resultado correto, inserir o valor lido no campo
    if (int.parse(jsonDecode(readWeightCommandReturn)[0]['resultado']) > 0) {
      final double readWeight =
          double.parse(jsonDecode(readWeightCommandReturn)[0]['resultado']) /
              1000;

      valueBalance.text = readWeight.toString();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SizedBox(
        height: MediaQuery.of(context).size.height,
        width: MediaQuery.of(context).size.width,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 30),
            GeneralWidgets.headerScreen("BALANÇA"),
            const SizedBox(height: 30),
            SizedBox(
              height: 350,
              width: MediaQuery.of(context).size.width - 150,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  SizedBox(
                    width: 600,
                    child: Column(
                      children: [
                        const Align(
                          alignment: Alignment.centerLeft,
                          child: Text(
                            "VALOR BALANÇA: ",
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                        GeneralWidgets.formFieldPerson(
                          valueBalance,
                          width: 600,
                          isEnable: false,
                          label: "",
                        ),
                      ],
                    ),
                  ),
                  SizedBox(
                      width: 600,
                      child: Column(
                        children: [
                          const Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              "MODELOS: ",
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                          typesModelsRadios(),
                        ],
                      )),
                  SizedBox(
                      width: 600,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              "PROTOCOLOS: ",
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                          dropDownProtocol(),
                        ],
                      )),
                  SizedBox(
                    width: 600,
                    child: buttonsOptionBalance(),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 10),
            GeneralWidgets.baseboard(),
          ],
        ),
      ),
    );
  }

  Widget typesModelsRadios() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        for (BalanceModel balanceModel in BalanceModel.values) ...{
          SizedBox(
              width: 130,
              child: GeneralWidgets.radioBtn(balanceModel.name,
                  selectedBalanceModel.name, onChangeTypeBalance))
        },
      ],
    );
  }

  Widget dropDownProtocol() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        GeneralWidgets.dropDown(
            '',
            //Nome do protocolo selecionado
            selectedBalanceProtocol.name,
            //Mapeia todos os protocolos disponíveis para montar a lista dropdown
            BalanceProtocol.values.map((e) => e.name).toList(), (String value) {
          //Para efetivar a seleção do protocolo, é feito um loop que busca qual o protocolo escolhido de acordo com o nome
          for (BalanceProtocol balanceProtocol in BalanceProtocol.values) {
            if (value == balanceProtocol.name) {
              setState(() {
                selectedBalanceProtocol = balanceProtocol;
              });
              break;
            }
          }
        }),
      ],
    );
  }

  Widget buttonsOptionBalance() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(
          children: [
            GeneralWidgets.personButton(
              textButton: "CONFIGURAR MODELO BALANÇA",
              width: 250,
              callback: () => sendConfigBalance(),
            ),
          ],
        ),
        const SizedBox(height: 10),
        Column(
          children: [
            GeneralWidgets.personButton(
              textButton: "LER PESO",
              width: 250,
              callback: () => sendLerPeso(),
            ),
          ],
        ),
      ],
    );
  }
}
