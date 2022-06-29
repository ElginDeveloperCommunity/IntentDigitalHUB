import 'dart:convert';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/SAT/associar_assinatura.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/SAT/ativar_sat.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/SAT/cancelar_ultima_venda.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/SAT/consultar_sat.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/SAT/consultar_status_operacional.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/SAT/enviar_dados_venda.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/SAT/extrair_logs.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:intent_digital_hub/XmStorageService/xml_database_service.dart';

import '../../Widgets/widgets.dart';
import '../../components/components.dart';

class SatPage extends StatefulWidget {
  const SatPage({Key? key}) : super(key: key);

  @override
  _SatPageState createState() => _SatPageState();
}

class _SatPageState extends State<SatPage> {
  TextEditingController inputCodeAtivacao =
      TextEditingController(text: "123456789");

  String cfeCancelamento = "";
  String modelSAT = "SMART SAT";

  onChangeTypeSAT(String value) {
    setState(() {
      modelSAT = value;
    });
  }

  String resultSAT = "";

  //Gera um número aleatório para as operações SAT
  int _generateRandomNumberForSatSessions() {
    return Random().nextInt(1000000);
  }

  //Extrai o resultado do JSON de retorno das operações SAT
  String _getSatResponse(String satJsonResponse) =>
      jsonDecode(satJsonResponse)[0]['resultado'];

  sendAtivarSAT() async {
    if (isInputCodeAtivacaoEmpty()) {
      Components.infoDialog(
        context: context,
        message: "A entrada de Texto não pode estar vazia!",
      );
      return;
    }

    final String result = await IntentDigitalHubCommandStarter.startCommand(
        AtivarSAT(_generateRandomNumberForSatSessions(), 2,
            inputCodeAtivacao.text, '14200166000166', 15));

    formatResponseSAT(_getSatResponse(result));
  }

  sendAssociarSAT() async {
    if (isInputCodeAtivacaoEmpty()) {
      Components.infoDialog(
        context: context,
        message: "A entrada de Texto não pode estar vazia!",
      );
      return;
    }

    final String result = await IntentDigitalHubCommandStarter.startCommand(
        AssociarAssinatura(
            _generateRandomNumberForSatSessions(),
            inputCodeAtivacao.text,
            '16716114000172',
            'SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT'));

    formatResponseSAT(_getSatResponse(result));
  }

  sendConsultarSAT() async {
    if (isInputCodeAtivacaoEmpty()) {
      Components.infoDialog(
        context: context,
        message: "A entrada de Texto não pode estar vazia!",
      );
      return;
    }

    final String result = await IntentDigitalHubCommandStarter.startCommand(
        ConsultarSAT(_generateRandomNumberForSatSessions()));

    formatResponseSAT(_getSatResponse(result));
  }

  sendStatusOperacional() async {
    if (isInputCodeAtivacaoEmpty()) {
      Components.infoDialog(
        context: context,
        message: "A entrada de Texto não pode estar vazia!",
      );
      return;
    }

    final String result = await IntentDigitalHubCommandStarter.startCommand(
        ConsultarStatusOperacional(
            _generateRandomNumberForSatSessions(), inputCodeAtivacao.text));

    formatResponseSAT(_getSatResponse(result));
  }

  sendEnviarVendasSAT() async {
    if (isInputCodeAtivacaoEmpty()) {
      Components.infoDialog(
        context: context,
        message: "A entrada de Texto não pode estar vazia!",
      );
      return;
    }

    final String dadosVenda = (modelSAT == 'SMART SAT')
        ? XmlFile.SAT_ENVIAR_DADOS_VENDA.idhRelativePathForCommand
        : XmlFile.SAT_GO_ENVIAR_DADOS_VENDA.idhRelativePathForCommand;

    final String result = await IntentDigitalHubCommandStarter.startCommand(
        EnviarDadosVenda(_generateRandomNumberForSatSessions(),
            inputCodeAtivacao.text, dadosVenda));

    final String satResponse = _getSatResponse(result);

    //Se a compra tiver sido realizada com sucesso, atualize o cfeCancelamento que será utilizado para o cancelamento de venda sat
    List<String> satResponseSplitted = satResponse.split('|');

    if (satResponseSplitted.length > 8) {
      cfeCancelamento = satResponseSplitted[8];
    }

    formatResponseSAT(satResponse);
  }

  sendCancelarVendaSAT() async {
    if (isInputCodeAtivacaoEmpty()) {
      Components.infoDialog(
        context: context,
        message: "A entrada de Texto não pode estar vazia!",
      );
      return;
    }

    if (cfeCancelamento == '') {
      Components.infoDialog(
        context: context,
        message: "Não foi feita uma venda para cancelar!",
      );
      return;
    }

    //Carrega o xml base para o cancelamento
    String dadosCancelamento =
        await rootBundle.loadString('assets/xml/sat_cancelamento.xml');

    //Aplica o cfe da ultima venda para cancelar a mesma
    dadosCancelamento =
        dadosCancelamento.replaceAll("novoCFe", cfeCancelamento);

    //No envio direto de XML, é necessário escapar as aspas para não quebrar a formatação do JSON
    dadosCancelamento = dadosCancelamento.replaceAll('"', '\\"');

    final String result = await IntentDigitalHubCommandStarter.startCommand(
        CancelarUltimaVenda(_generateRandomNumberForSatSessions(),
            inputCodeAtivacao.text, cfeCancelamento, dadosCancelamento));

    formatResponseSAT(_getSatResponse(result));
  }

  sendExtrairLog() async {
    if (isInputCodeAtivacaoEmpty()) {
      Components.infoDialog(
        context: context,
        message: "A entrada de Texto não pode estar vazia!",
      );
      return;
    }

    final String result = await IntentDigitalHubCommandStarter.startCommand(
        ExtrairLogs(
            _generateRandomNumberForSatSessions(), inputCodeAtivacao.text));

    var satResponse = _getSatResponse(result);

    if (satResponse == 'DeviceNotFound') {
      formatResponseSAT(satResponse);
    } else {
      formatResponseSAT('Log SAT salvo em $satResponse');
    }
  }

  formatResponseSAT(String result) {
    var dt = DateTime.now();
    String dateFormat =
        "Data e Hora: ${dt.day}/${dt.month}/${dt.year} ${dt.hour}:${dt.minute}:${dt.second}\n\n";

    setState(() {
      resultSAT = dateFormat +
          result +
          "\n" +
          "-----------------------------\n\n" +
          resultSAT;
    });
  }

  isInputCodeAtivacaoEmpty() {
    return inputCodeAtivacao.text.isEmpty;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: SizedBox(
          height: MediaQuery.of(context).size.height,
          width: MediaQuery.of(context).size.width,
          child: Column(
            children: [
              const SizedBox(height: 30),
              GeneralWidgets.headerScreen("SAT Homologação"),
              const SizedBox(height: 30),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  boxScreen(),
                  const SizedBox(width: 20),
                  buttons(),
                ],
              ),
              GeneralWidgets.baseboard(),
            ],
          ),
        ),
      ),
    );
  }

  Widget buttons() {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        typesModelsRadios(),
        const SizedBox(height: 10),
        GeneralWidgets.inputField(
          inputCodeAtivacao,
          'Código ativação: ',
          textSize: 14,
          iWidht: 340,
          textInputType: TextInputType.number,
        ),
        const SizedBox(height: 20),
        Row(
          children: [
            Column(
              children: [
                GeneralWidgets.personButton(
                  textButton: "CONSULTAR SAT",
                  width: 170,
                  callback: sendConsultarSAT,
                ),
                const SizedBox(height: 10),
                GeneralWidgets.personButton(
                  textButton: "STATUS OPERACIONAL",
                  width: 170,
                  callback: sendStatusOperacional,
                ),
                const SizedBox(height: 10),
                GeneralWidgets.personButton(
                  textButton: "REALIZAR VENDA",
                  width: 170,
                  callback: sendEnviarVendasSAT,
                ),
              ],
            ),
            const SizedBox(width: 10),
            Column(
              children: [
                GeneralWidgets.personButton(
                  textButton: "CANCELAMENTO",
                  width: 170,
                  callback: sendCancelarVendaSAT,
                ),
                const SizedBox(height: 10),
                GeneralWidgets.personButton(
                  textButton: "ATIVAR",
                  width: 170,
                  callback: sendAtivarSAT,
                ),
                const SizedBox(height: 10),
                GeneralWidgets.personButton(
                  textButton: "ASSOCIAR",
                  width: 170,
                  callback: sendAssociarSAT,
                ),
              ],
            ),
          ],
        ),
        const SizedBox(
          height: 10,
        ),
        Row(
          children: [
            Column(
              children: [
                GeneralWidgets.personButton(
                  textButton: "EXTRAIR LOG",
                  width: 170,
                  callback: sendExtrairLog,
                ),
              ],
            )
          ],
        )
      ],
    );
  }

  Widget boxScreen() {
    return Container(
      height: 370,
      width: 400,
      alignment: Alignment.topLeft,
      decoration: BoxDecoration(
        border: Border.all(color: Colors.black, width: 3),
        borderRadius: const BorderRadius.all(Radius.circular(20)),
      ),
      child: Padding(
        padding: const EdgeInsets.all(10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text("RETORNO:"),
            SizedBox(
              height: 310,
              width: 500,
              child: Padding(
                padding: const EdgeInsets.only(top: 5),
                child: SingleChildScrollView(child: Text(resultSAT)),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget typesModelsRadios() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        SizedBox(
          width: 160,
          child: GeneralWidgets.radioBtn(
            'SMART SAT',
            modelSAT,
            onChangeTypeSAT,
          ),
        ),
        GeneralWidgets.radioBtn(
          'SATGO',
          modelSAT,
          onChangeTypeSAT,
        ),
      ],
    );
  }
}
