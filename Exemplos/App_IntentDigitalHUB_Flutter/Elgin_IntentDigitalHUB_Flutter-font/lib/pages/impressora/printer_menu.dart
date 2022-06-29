// ignore_for_file: constant_identifier_names

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/abre_gaveta_elgin.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/fecha_conexao_impressora.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/status_impressora.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';

import '../../IntentDigitalHubService/TERMICA/abre_conexao_impressora.dart';
import '../../Utils/utils.dart';
import '../../Widgets/widgets.dart';
import '../../components/components.dart';
import 'printer_barcode.dart';
import 'printer_image.dart';
import 'printer_text.dart';

class PrinterMenutPage extends StatefulWidget {
  const PrinterMenutPage({Key? key}) : super(key: key);

  @override
  PrinterMenutPageState createState() => PrinterMenutPageState();
}

class PrinterMenutPageState extends State<PrinterMenutPage> {
  TextEditingController inputIp =
      TextEditingController(text: "192.168.0.104:9100");

  String selectedModulePrinter = "text";
  static String selectedImp = "IMP. INTERNA";

  double mBoxScreenSizeW = 580;
  double mBoxScreenSizeH = 450;

  Widget? mActualScreenSelected;

  //Modelos de impressora
  static const String EXTERNAL_PRINTER_MODEL_I9 = "i9";
  static const String EXTERNAL_PRINTER_MODEL_I8 = "i8";

  @override
  void initState() {
    super.initState();
    mActualScreenSelected = PrinterTextPage(
      mWidth: mBoxScreenSizeW,
      mHeight: mBoxScreenSizeH,
    );
    connectInternalImp();
  }

  //Fecha a conexão com a impressora ao sair da tela
  @override
  void dispose() async {
    super.dispose();
    await IntentDigitalHubCommandStarter.startCommand(FechaConexaoImpressora());
  }

  onChangeModulePrinter(String selected) {
    setState(() {
      selectedModulePrinter = selected;
    });
    setState(() {
      switch (selectedModulePrinter) {
        case "text":
          mActualScreenSelected = PrinterTextPage(
            mWidth: mBoxScreenSizeW,
            mHeight: mBoxScreenSizeH,
          );
          break;
        case "barcode":
          mActualScreenSelected = PrinterBarCodePage(
            mWidth: mBoxScreenSizeW,
            mHeight: mBoxScreenSizeH,
          );
          break;
        case "image":
          mActualScreenSelected = PrinterImagePage(
            mWidth: mBoxScreenSizeW,
            mHeight: mBoxScreenSizeH,
          );
          break;
        default:
      }
    });
  }

  onChangedImp(String value) async {
    //Se qualquer erro ocorrer durante a tentativa de conexão com uma impressora externa: retorne o tipo de impressão pra impressora interna
    if (value != "IMP. INTERNA") {
      List<String> externalPrinterModels = [
        EXTERNAL_PRINTER_MODEL_I9,
        EXTERNAL_PRINTER_MODEL_I8
      ];
      String selectedModel = '';

      onExternalPrinterModelTapped(int indexOfTapped) async {
        const int MODEL_I9_TAPPED = 0;
        Navigator.of(context).pop();
        selectedModel = (indexOfTapped == MODEL_I9_TAPPED)
            ? EXTERNAL_PRINTER_MODEL_I9
            : EXTERNAL_PRINTER_MODEL_I8;
      }

      await GeneralWidgets.showAlertDialogWithSelectableOptions(
          mainWidgetContext: context,
          alertTitle: 'Selecione o modelo de impressora a ser conectado',
          listOfOptions: externalPrinterModels,
          onTap: onExternalPrinterModelTapped);
      if (value == "IMP. EXTERNA - USB") {
        //Tenta a conexão por impressora externa via USB
        if (await connectExternalImpByUSB(selectedModel)) {
          setState(() {
            selectedImp = 'IMP. EXTERNA - USB';
          });
        } else {
          Components.infoDialog(
              context: context,
              message: 'A tentativa de conexão por USB não foi bem sucedida!');
          await connectInternalImp();
        }
      } else {
        //Valida IP
        if (Utils.validaIpWithPort(inputIp.text)) {
          //Tenta a conexão por impressora externa via IP
          if (await connectExternalImpByIP(selectedModel)) {
            setState(() {
              selectedImp = 'IMP. EXTERNA - IP';
            });
          } else {
            Components.infoDialog(
                context: context,
                message: 'A tentativa de conexão por IP não foi bem sucedida!');
            await connectInternalImp();
          }
        } else {
          Components.infoDialog(
              context: context, message: "Digite um IP valido.");
          await connectInternalImp();
        }
      }
    } else {
      await connectInternalImp();
    }
  }

  getImpStatus() async {
    final String termicaResponseJson =
        await IntentDigitalHubCommandStarter.startCommand(StatusImpressora(3));

    final int result = _termicaCommandResponse(termicaResponseJson);

    final String resultMessage;
    switch (result) {
      case 5:
        resultMessage = "Papel está presente e não está próximo do fim!";
        break;
      case 6:
        resultMessage = "Papel próximo do fim!";
        break;
      case 7:
        resultMessage = "Papel ausente!";
        break;
      default:
        resultMessage = "Status Desconhecido!";
    }

    Components.infoDialog(
        context: context, message: resultMessage.toUpperCase());
  }

  getGavetaStatus() async {
    final String termicaResponseJson =
        await IntentDigitalHubCommandStarter.startCommand(StatusImpressora(1));

    final int result = _termicaCommandResponse(termicaResponseJson);

    final String resultMessage;
    switch (result) {
      case 1:
        resultMessage = "Gaveta aberta!";
        break;
      case 2:
        resultMessage = "Gaveta fechada";
        break;
      default:
        resultMessage = "Status Desconhecido!";
    }

    Components.infoDialog(context: context, message: resultMessage);
  }

  sendAbrirGaveta() async {
    await IntentDigitalHubCommandStarter.startCommand(AbreGavetaElgin());
  }

  //Inica a conexão com a impressora interna
  connectInternalImp() async {
    await IntentDigitalHubCommandStarter.startCommand(
        AbreConexaoImpressora(6, "M8", "", 0));
    setState(() {
      selectedImp = 'IMP. INTERNA';
    });
  }

  //Tenta a conexão com a impressora externa via IP, retorna true caso a tentativa de conexão tenha sido bem sucedida
  Future<bool> connectExternalImpByIP(String model) async {
    final String ip = inputIp.text.substring(0, inputIp.text.indexOf(":"));
    final String port = inputIp.text.substring(ip.length + 1);

    final String termicaResponseJson =
        await IntentDigitalHubCommandStarter.startCommand(
            AbreConexaoImpressora(3, model, ip, int.parse(port)));

    final int result = _termicaCommandResponse(termicaResponseJson);

    return result == 0;
  }

  //Tenta a conexão com a impressora externa via USB, retorna true caso a tentativa de conexão tenha sido bem sucedida
  Future<bool> connectExternalImpByUSB(String model) async {
    final String termicaResponseJson =
        await IntentDigitalHubCommandStarter.startCommand(
            AbreConexaoImpressora(1, model, 'USB', 0));

    final int result = _termicaCommandResponse(termicaResponseJson);

    return result == 0;
  }

  //Captura o resultado do JSON de retorno dos comandos Termica
  int _termicaCommandResponse(String termicaResponseJson) =>
      jsonDecode(termicaResponseJson)[0]['resultado'];

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
              GeneralWidgets.headerScreen("IMPRESSORA"),
              Row(
                children: [
                  Padding(
                    padding: const EdgeInsets.only(left: 20),
                    child: SizedBox(
                      height: mBoxScreenSizeH - 50,
                      child: Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 8),
                        child: modulesPrinter(),
                      ),
                    ),
                  ),
                  Column(
                    children: [
                      SizedBox(
                        width: mBoxScreenSizeW,
                        child: rowExternalImp(),
                      ),
                      SizedBox(
                        height: 350,
                        width: mBoxScreenSizeW,
                        child: boxScreen(),
                      ),
                    ],
                  ),
                ],
              ),
              GeneralWidgets.baseboard(),
            ],
          ),
        ),
      ),
    );
  }

  Widget rowExternalImp() {
    return IntrinsicWidth(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              Radio<String>(
                value: 'IMP. INTERNA',
                groupValue: selectedImp,
                onChanged: (String? value) => onChangedImp(value!),
              ),
              const Text('IMP. INTERNA',
                  style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
              Radio<String>(
                value: 'IMP. EXTERNA - USB',
                groupValue: selectedImp,
                onChanged: (String? value) => onChangedImp(value!),
              ),
              const Text('IMP. EXTERNA - USB',
                  style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
              Radio<String>(
                value: 'IMP. EXTERNA - IP',
                groupValue: selectedImp,
                onChanged: (String? value) => onChangedImp(value!),
              ),
              const Text('IMP. EXTERNA - IP',
                  style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
            ],
          ),
          SizedBox(
            width: 170,
            child: TextFormField(
              textAlign: TextAlign.center,
              controller: inputIp,
              style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
              keyboardType: TextInputType.text,
              decoration: const InputDecoration(
                prefixText: 'IP: ',
                isDense: true,
                hintStyle: TextStyle(fontSize: 14),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(
                    Radius.circular(10.0),
                  ),
                ),
                filled: false,
                contentPadding: EdgeInsets.all(10),
              ),
            ),
          )
        ],
      ),
    );
  }

  Widget boxScreen() {
    return Container(
      height: mBoxScreenSizeH,
      width: mBoxScreenSizeW,
      decoration: BoxDecoration(
        border: Border.all(color: Colors.black, width: 3),
        borderRadius: const BorderRadius.all(Radius.circular(16)),
      ),
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: mActualScreenSelected,
      ),
    );
  }

  Widget modulesPrinter() {
    return Column(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        GeneralWidgets.personSelectedButton(
          nameButton: 'IMPRESSÃO\nDE TEXTO',
          assetImage: 'assets/images/printerText.png',
          mHeight: 80,
          iconSize: 40,
          mWidth: 140,
          fontLabelSize: 12,
          color: const Color(0xFF0069A5),
          isSelectedBtn: selectedModulePrinter == "text",
          onSelected: () => onChangeModulePrinter("text"),
        ),
        GeneralWidgets.personSelectedButton(
          nameButton: 'IMPRESSÃO DE\nCÓDIGO DE BARRAS',
          assetImage: 'assets/images/printerBarCode.png',
          mHeight: 80,
          mWidth: 140,
          iconSize: 40,
          fontLabelSize: 12,
          color: const Color(0xFF0069A5),
          isSelectedBtn: selectedModulePrinter == "barcode",
          onSelected: () => onChangeModulePrinter("barcode"),
        ),
        GeneralWidgets.personSelectedButton(
          nameButton: 'IMPRESSÃO\nDE IMAGEM',
          mHeight: 80,
          mWidth: 140,
          iconSize: 40,
          fontLabelSize: 12,
          color: const Color(0xFF0069A5),
          assetImage: 'assets/images/printerImage.png',
          isSelectedBtn: selectedModulePrinter == "image",
          onSelected: () => onChangeModulePrinter("image"),
        ),
        GeneralWidgets.personSelectedButtonStatus(
          nameButton: "STATUS IMPRESSORA",
          mHeight: 45,
          mWidth: 140,
          onSelected: () => getImpStatus(),
        ),
        GeneralWidgets.personSelectedButtonStatus(
          nameButton: "STATUS GAVETA",
          mHeight: 45,
          mWidth: 140,
          onSelected: () => getGavetaStatus(),
        ),
        GeneralWidgets.personButton(
          textButton: 'ABRIR GAVETA',
          height: 35,
          width: 140,
          callback: () => sendAbrirGaveta(),
        ),
      ],
    );
  }
}
