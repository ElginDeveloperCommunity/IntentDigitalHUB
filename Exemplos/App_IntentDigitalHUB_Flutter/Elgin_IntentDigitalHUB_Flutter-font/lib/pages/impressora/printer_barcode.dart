import 'package:flutter/material.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/define_posicao.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/impressao_codigo_barras.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/impressao_qr_code.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';

import '../../IntentDigitalHubService/TERMICA/avanca_papel.dart';
import '../../IntentDigitalHubService/TERMICA/corte.dart';
import '../../Widgets/widgets.dart';

class PrinterBarCodePage extends StatefulWidget {
  final double mWidth;
  final double mHeight;

  const PrinterBarCodePage({Key? key, this.mWidth = 200, this.mHeight = 200})
      : super(key: key);
  @override
  _PrinterBarCodePageState createState() => _PrinterBarCodePageState();
}

class _PrinterBarCodePageState extends State<PrinterBarCodePage> {
  TextEditingController inputCode = TextEditingController(text: '40170725');

  //PrinterService printerService = new PrinterService();

  bool cutPaper = false;

  String alignPrint = "Esquerda";
  String heightImp = '120';
  String widthImp = '6';
  String barCodeType = 'EAN 8';

  //Retorna o valor para cada tipo de código de barras
  int _getBarcodeTypeValue() {
    switch (barCodeType) {
      case 'EAN 8':
        return 3;
      case 'EAN 13':
        return 2;
      case 'UPC-A':
        return 0;
      case 'CODE 39':
        return 4;
      case 'ITF':
        return 5;
      case 'CODE BAR':
        return 6;
      case 'CODE 93':
        return 7;
      case 'CODE 128':
        return 8;
      default:
        return -9999;
    }
  }

  //Retorna o valor do alinhamento
  int _getAlignmentValue() {
    switch (alignPrint) {
      case 'Esquerda':
        return 0;
      case 'Centralizado':
        return 1;
      case 'Direita':
        return 2;
      default:
        return -9999;
    }
  }

  onChangeAlign(String value) {
    setState(() {
      alignPrint = value;
    });
  }

  sendPrinterBarCode() async {
    // VALIDA ENTRADA DE TEXTO
    if (inputCode.text.isEmpty) {
      GeneralWidgets.showAlertDialog(
          mainWidgetContext: context,
          dialogTitle: 'Alerta',
          dialogText: 'A entrada de código não pode estar vazia!');
      return;
    }

    await IntentDigitalHubCommandStarter.startCommands([
      DefinePosicao(_getAlignmentValue()),
      ImpressaoCodigoBarras(_getBarcodeTypeValue(), inputCode.text,
          int.parse(heightImp), int.parse(widthImp), 4),
      AvancaPapel(10),
      if (cutPaper) ...{Corte(0)}
    ]);
  }

  sendPrinterQrCode() async {
    if (inputCode.text.isEmpty) {
      GeneralWidgets.showAlertDialog(
          mainWidgetContext: context,
          dialogTitle: 'Alerta',
          dialogText: 'A entrada de código não pode estar vazia!');
      return;
    }

    await IntentDigitalHubCommandStarter.startCommands([
      DefinePosicao(_getAlignmentValue()),
      ImpressaoQRCode(inputCode.text, int.parse(widthImp), 2),
      AvancaPapel(10),
      if (cutPaper) ...{Corte(0)}
    ]);
  }

  onChangeBarCodeType(String barCodeType) {
    String defaultValueInput = "";
    switch (barCodeType) {
      case 'EAN 8':
        defaultValueInput = '40170725';
        break;
      case 'EAN 13':
        defaultValueInput = '0123456789012';
        break;
      case 'QR_CODE':
        defaultValueInput = 'ELGIN DEVELOPERS COMMUNITY';
        break;
      case 'UPC-A':
        defaultValueInput = '123601057072';
        break;
      case 'CODE 39':
        defaultValueInput = 'CODE39';
        break;
      case 'ITF':
        defaultValueInput = '05012345678900';
        break;
      case 'CODE BAR':
        defaultValueInput = 'A3419500A';
        break;
      case 'CODE 93':
        defaultValueInput = 'CODE93';
        break;
      case 'CODE 128':
        defaultValueInput = '{C1233';
        break;
      default:
        defaultValueInput = '';
    }
    setState(() {
      inputCode.text = defaultValueInput;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SizedBox(
          height: widget.mHeight,
          width: widget.mWidth - 20,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const SizedBox(height: 5),
              const Text(
                "IMPRESSÃO DE CÓDIGO DE BARRAS",
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              GeneralWidgets.inputField(
                inputCode,
                'CÓDIGO: ',
                iWidht: widget.mWidth - 20,
                textSize: 16,
              ),
              const SizedBox(height: 5),
              GeneralWidgets.dropDown(
                  'TIPO DE CÓDIGO DE BARRAS: ', barCodeType, [
                'EAN 8',
                'EAN 13',
                'QR_CODE',
                'UPC-A',
                'CODE 39',
                'ITF',
                'CODE BAR',
                'CODE 93',
                'CODE 128',
              ], (String value) {
                barCodeType = value;
                onChangeBarCodeType(value);
              }),
              const SizedBox(height: 5),
              const Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "ALINHAMENTO",
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              alignRadios(),
              const Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "ESTILIZAÇÃO",
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              estiloChecks(),
              const SizedBox(height: 5),
              GeneralWidgets.personButton(
                textButton: 'IMPRIMIR CÓDIGO DE BARRAS',
                width: widget.mWidth,
                callback: () {
                  if (barCodeType == "QR_CODE") {
                    sendPrinterQrCode();
                  } else {
                    sendPrinterBarCode();
                  }
                },
              )
            ],
          ),
        ),
      ),
    );
  }

  Widget alignRadios() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        SizedBox(
          width: 165,
          child: GeneralWidgets.radioBtn('Esquerda', alignPrint, onChangeAlign),
        ),
        GeneralWidgets.radioBtn('Centralizado', alignPrint, onChangeAlign),
        SizedBox(
          width: 160,
          child: GeneralWidgets.radioBtn('Direita', alignPrint, onChangeAlign),
        ),
      ],
    );
  }

  Widget estiloChecks() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        if (barCodeType != "QR_CODE") ...{
          GeneralWidgets.dropDown(
              'HEIGHT: ', heightImp, ['20', '60', '120', '200'],
              (String value) {
            setState(() {
              heightImp = value;
            });
          }),
        },
        if (barCodeType == "QR_CODE") ...{
          GeneralWidgets.dropDown(
              'SQUARE: ', widthImp, ['1', '2', '3', '4', '5', '6'],
              (String value) {
            setState(() {
              widthImp = value;
            });
          }),
        },
        if (barCodeType != "QR_CODE") ...{
          GeneralWidgets.dropDown(
              'WIDTH: ', widthImp, ['1', '2', '3', '4', '5', '6'],
              (String value) {
            setState(() {
              widthImp = value;
            });
          }),
        },
        GeneralWidgets.checkBox('CUT PAPER', cutPaper, (bool value) {
          setState(() {
            cutPaper = value;
          });
        }),
      ],
    );
  }
}
