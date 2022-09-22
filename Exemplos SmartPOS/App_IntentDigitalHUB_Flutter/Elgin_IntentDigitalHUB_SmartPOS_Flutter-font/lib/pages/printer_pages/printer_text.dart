import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/Commands/avanca_papel.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/Commands/impressao_texto.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/Commands/imprime_xml_nfce.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:flutter_smartpos/Widgets/widgets.dart';
import 'package:flutter_smartpos/components/components.dart';

import '../../IntentDigitalHubService/TERMICA/Commands/corte.dart';
import '../../IntentDigitalHubService/TERMICA/Commands/imprime_xml_sat.dart';

class PrinterTextPage extends StatefulWidget {
  final double mWidth;
  final double mHeight;
  final String selectedImp;

  PrinterTextPage(
      {Key? key,
      required this.selectedImp,
      this.mWidth = 200,
      this.mHeight = 200})
      : super(key: key);

  //PrinterTextPage({this.mWidth = 200, this.mHeight = 200});

  @override
  _PrinterTextPageState createState() => _PrinterTextPageState();
}

class _PrinterTextPageState extends State<PrinterTextPage>
    with WidgetsBindingObserver {
  TextEditingController inputMessage =
      new TextEditingController(text: "ELGIN DEVELOPERS COMMUNITY");

  String alignPrint = "Centralizado";

  String fontFamily = "FONT A";
  String fontSize = "17";

  bool isImpExternal = true;

  bool isNegrito = false;
  bool isItalic = false;
  bool isUnderline = false;
  bool cutPaper = false;

  onChangeAlign(String value) {
    setState(() {
      alignPrint = value;
    });
  }

  @override
  void initState() {
    super.initState();
    print(widget.selectedImp);
  }

  //Retorna o valor para o tipo de alinhamento selecionado.
  int getPosicaoValue() {
    if (alignPrint == "Esquerda") {
      return 0;
    } else if (alignPrint == "Centralizado") {
      return 1;
    } else {
      return 2;
    }
  }

  //Retorna o valor de acordo com as opções de estilização selecionadas.
  int getStilo() {
    int stilo = 0;

    if (fontFamily == "FONT B") {
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
      Components.infoDialog(
          context: context,
          message: "A entrada de Texto não pode estar vazia!");
      return;
    }

    List<IntentDigitalHubCommandReturn> printTextCommandReturn =
        await IntentDigitalHubCommandStarter.startCommands([
      new ImpressaoTexto(inputMessage.text, getPosicaoValue(), getStilo(),
          int.parse(fontSize)),
      new AvancaPapel(10),
      if (cutPaper) ...{new Corte(0)}
    ]);

    print("aqui" + printTextCommandReturn.toString());
  }

  sendPrinterNFCe() async {
    final String dados = await rootBundle.loadString('assets/xml/xmlNFCe.xml');
    final int indexcsc = 1;
    final String csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";
    final int param = 0;

    List<IntentDigitalHubCommandReturn> printNFCeCommandReturn =
        await IntentDigitalHubCommandStarter.startCommands([
      new ImprimeXMLNFCe(dados, indexcsc, csc, param),
      new AvancaPapel(10),
      if (cutPaper) ...{new Corte(0)}
    ]);
  }

  sendPrinterSAT() async {
    final String dados = await rootBundle.loadString('assets/xml/xmlSAT.xml');
    final int param = 0;

    List<IntentDigitalHubCommandReturn> printSATCommandReturn =
        await IntentDigitalHubCommandStarter.startCommands([
      new ImprimeXMLSAT(dados, param),
      new AvancaPapel(10),
      if (cutPaper) ...{new Corte(0)}
    ]);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Container(
          height: MediaQuery.of(context).size.height,
          width: MediaQuery.of(context).size.width,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              GeneralWidgets.headerScreen("IMPRESSORA"),
              Container(
                height: 520,
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text("IMPRESSÃO DE TEXTO",
                            style: TextStyle(
                                fontSize: 18, fontWeight: FontWeight.bold))
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        GeneralWidgets.inputField(
                          inputMessage,
                          'MENSAGEM: ',
                          iWidht: MediaQuery.of(context).size.width - 20,
                          textSize: 16,
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 10,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        Text(
                          "ALINHAMENTO: ",
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        GeneralWidgets.radioButton(
                            'Esquerda', alignPrint, onChangeAlign),
                        GeneralWidgets.radioButton(
                            'Centralizado', alignPrint, onChangeAlign),
                        GeneralWidgets.radioButton(
                            'Direita', alignPrint, onChangeAlign)
                      ],
                    ),
                    SizedBox(height: 80),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        Text(
                          "ESTILIZAÇÃO: ",
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 10),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [dropDownFontFamily()],
                    ),
                    Row(
                      children: [dropDownFontSize()],
                    ),
                    Container(
                      width: MediaQuery.of(context).size.width,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        children: [
                          if (fontFamily == 'FONT A') ...{
                            GeneralWidgets.checkBox("NEGRITO", isNegrito,
                                (bool value) {
                              setState(() {
                                isNegrito = value;
                              });
                            })
                          },
                          GeneralWidgets.checkBox("SUBLINHADO", isUnderline,
                              (bool value) {
                            setState(() {
                              isUnderline = value;
                            });
                          }),
                          if (widget.selectedImp == "IMP. EXTERNA") ...{
                            GeneralWidgets.checkBox("CUT PAPER", cutPaper,
                                (bool value) {
                              setState(() {
                                cutPaper = value;
                              });
                            })
                          }
                        ],
                      ),
                    ),
                    Row(
                      children: [
                        GeneralWidgets.personButton(
                          textButton: 'IMPRIMIR TEXTO',
                          width: MediaQuery.of(context).size.width,
                          height: 35,
                          callback: () => sendPrinterText(),
                        )
                      ],
                    ),
                    SizedBox(
                      height: 5,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        GeneralWidgets.personButton(
                          textButton: 'NFCE',
                          height: 35,
                          width: (MediaQuery.of(context).size.width / 2) - 10,
                          callback: () => sendPrinterNFCe(),
                        ),
                        GeneralWidgets.personButton(
                          textButton: 'SAT',
                          height: 35,
                          width: (MediaQuery.of(context).size.width / 2) - 10,
                          callback: () => sendPrinterSAT(),
                        ),
                      ],
                    )
                  ],
                ),
              ),
              GeneralWidgets.baseboard(),
            ],
          ),
        ),
      ),
    );
  }

  Widget dropDownFontFamily() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            "FONT FAMILY: ",
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          GeneralWidgets.dropDown(fontFamily, ['FONT B', 'FONT A'],
              (String value) {
            setState(() {
              fontFamily = value;
              if (fontFamily == 'FONT B') {
                isNegrito = false;
              }
            });
          })
        ],
      ),
    );
  }

  Widget dropDownFontSize() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            "FONT SIZE: ",
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          GeneralWidgets.dropDown(fontSize, ["17", "34", "51", "68"],
              (String value) {
            setState(() {
              fontSize = value;
            });
          })
        ],
      ),
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
        SizedBox(height: 10),
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
