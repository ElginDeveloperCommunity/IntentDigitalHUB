import 'package:flutter/material.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/avanca_papel.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/corte.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/impressao_texto.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/imprime_xml_nfce.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/imprime_xml_sat.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:intent_digital_hub/XmStorageService/xml_database_service.dart';

import '../../Widgets/widgets.dart';

class PrinterTextPage extends StatefulWidget {
  final double mWidth;
  final double mHeight;

  const PrinterTextPage({Key? key, this.mWidth = 200, this.mHeight = 200})
      : super(key: key);

  @override
  _PrinterTextPageState createState() => _PrinterTextPageState();
}

class _PrinterTextPageState extends State<PrinterTextPage> {
  TextEditingController inputMessage =
      TextEditingController(text: "ELGIN DEVELOPERS COMMUNITY");

  String alignPrint = "Esquerda";

  String fontFamily = "FONT A";
  String fontSize = "34";

  bool isNegrito = false;
  bool isItalic = false;
  bool isUnderline = false;
  bool cutPaper = false;

  onChangeAlign(String value) {
    setState(() {
      alignPrint = value;
    });
  }

  //Retorna o valor do alinhamento
  _getSelectedAlignmentValue() {
    if (alignPrint == 'Esquerda') {
      return 0;
    } else if (alignPrint == 'Centralizado') {
      return 1;
    } else {
      return 0;
    }
  }

  //Retorna o parametro 'stilo' usado nas impressões de texto
  _getStilo() {
    int stilo = 0;

    if (fontFamily == 'FONT B') {
      stilo += 1;
    }
    if (isUnderline) {
      stilo += 2;
    }
    if (isNegrito) {
      stilo += 8;
    }

    return stilo;
  }

  sendPrinterText() async {
    // VALIDA ENTRADA DE TEXTO
    if (inputMessage.text.isEmpty) {
      GeneralWidgets.showAlertDialog(
          mainWidgetContext: context,
          dialogTitle: 'Alerta',
          dialogText: 'A entrada de Texto não pode estar vazia!');
      return;
    }

    await IntentDigitalHubCommandStarter.startCommands([
      ImpressaoTexto(inputMessage.text, _getSelectedAlignmentValue(),
          _getStilo(), int.parse(fontSize)),
      AvancaPapel(10),
      if (cutPaper) ...{Corte(0)}
    ]);
  }

  sendPrinterNFCe() async {
    final String xml = XmlFile.XML_NFCE.idhRelativePathForCommand;

    await IntentDigitalHubCommandStarter.startCommands([
      ImprimeXMLNFCe(xml, 1, 'CODIGO-CSC-CONTRIBUINTE-36-CARACTERES', 0),
      AvancaPapel(10),
      if (cutPaper) ...{Corte(0)}
    ]);
  }

  sendPrinterSAT() async {
    final String xml = XmlFile.XML_SAT.idhRelativePathForCommand;

    await IntentDigitalHubCommandStarter.startCommands([
      ImprimeXMLSAT(xml, 0),
      AvancaPapel(10),
      if (cutPaper) ...{Corte(0)}
    ]);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SizedBox(
          height: widget.mHeight,
          width: widget.mWidth - 20,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const SizedBox(height: 5),
              const Text("IMPRESSÃO DE TEXTO",
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              GeneralWidgets.inputField(
                inputMessage,
                'MENSAGEM: ',
                iWidht: widget.mWidth - 20,
                textSize: 16,
              ),
              const Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "ALINHAMENTO: ",
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
                  "ESTILIZAÇÃO: ",
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              dropDownsEstilo(),
              estiloChecks(),
              buttonsActionWidget(),
            ],
          ),
        ),
      ),
    );
  }

  Widget alignRadios() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        SizedBox(
          width: 165,
          child: GeneralWidgets.radioBtn('Esquerda', alignPrint, onChangeAlign),
        ),
        GeneralWidgets.radioBtn('Centralizado', alignPrint, onChangeAlign),
        SizedBox(
          width: 165,
          child: GeneralWidgets.radioBtn('Direita', alignPrint, onChangeAlign),
        ),
      ],
    );
  }

  Widget dropDownsEstilo() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        GeneralWidgets.dropDown(
            'FONT FAMILY: ', fontFamily, ['FONT B', 'FONT A'], (String value) {
          setState(() {
            fontFamily = value;
          });
        }),
        const SizedBox(width: 20),
        GeneralWidgets.dropDown(
            "FONT SIZE: ", fontSize, ["17", "34", "51", "68"], (String value) {
          setState(() {
            fontSize = value;
          });
        }),
      ],
    );
  }

  Widget estiloChecks() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        SizedBox(
          width: 165,
          child: GeneralWidgets.checkBox('NEGRITO', isNegrito, (bool value) {
            setState(() {
              isNegrito = value;
            });
          }),
        ),
        SizedBox(
          width: 200,
          child:
              GeneralWidgets.checkBox('SUBLINHADO', isUnderline, (bool value) {
            setState(() {
              isUnderline = value;
            });
          }),
        ),
        SizedBox(
          width: 180,
          child: GeneralWidgets.checkBox('CUT PAPER', cutPaper, (bool value) {
            setState(() {
              cutPaper = value;
            });
          }),
        ),
      ],
    );
  }

  Widget buttonsActionWidget() {
    return Column(
      children: [
        GeneralWidgets.personButton(
          textButton: 'IMPRIMIR TEXTO',
          width: widget.mWidth,
          height: 35,
          callback: () => sendPrinterText(),
        ),
        const SizedBox(height: 10),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            GeneralWidgets.personButton(
              textButton: 'NFCE',
              height: 35,
              width: widget.mWidth / 2 - 30,
              callback: () => sendPrinterNFCe(),
            ),
            GeneralWidgets.personButton(
              textButton: 'SAT',
              height: 35,
              width: widget.mWidth / 2 - 30,
              callback: () => sendPrinterSAT(),
            ),
          ],
        )
      ],
    );
  }
}
