import 'package:flutter/material.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/Commands/define_posicao.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/Commands/impressao_qr_code.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:flutter_smartpos/Widgets/widgets.dart';
import 'package:flutter_smartpos/components/components.dart';

import '../../IntentDigitalHubService/TERMICA/Commands/avanca_papel.dart';
import '../../IntentDigitalHubService/TERMICA/Commands/corte.dart';
import '../../IntentDigitalHubService/TERMICA/Commands/impressao_codigo_barras.dart';

class PrinterBarCodePage extends StatefulWidget {
  final double mWidth;
  final double mHeight;
  final String selectedImp;

  PrinterBarCodePage(
      {required this.selectedImp, this.mWidth = 200, this.mHeight = 200});
  @override
  _PrinterBarCodePageState createState() => _PrinterBarCodePageState();
}

class _PrinterBarCodePageState extends State<PrinterBarCodePage> {
  TextEditingController inputCode = new TextEditingController(text: '40170725');

  bool cutPaper = false;

  String alignPrint = "Centralizado";
  String heightImp = '120';
  String widthImp = '6';
  String barCodeType = 'EAN 8';

  onChangeAlign(String value) {
    setState(() {
      alignPrint = value;
    });
  }

  sendPrinterBarCode() async {
    // VALIDA ENTRADA DE TEXTO
    if (inputCode.text.isEmpty) {
      Components.infoDialog(
          context: context,
          message: "A entrada de código não pode estar vazia!");
      return;
    }
    final int tipo = getBarcodeTypeValue();

    final String dados = inputCode.text;

    final int altura = int.parse(heightImp);

    final int largura = int.parse(widthImp);

    final int HRI = 4;

    IntentDigitalHubCommandStarter.startCommands([
      new DefinePosicao(getAlignValue()),
      new ImpressaoCodigoBarras(tipo, dados, altura, largura, HRI),
      new AvancaPapel(10),
      if (cutPaper) ...{new Corte(0)}
    ]);
  }

  sendPrinterQrCode() async {
    // VALIDA ENTRADA DE TEXTO
    if (inputCode.text.isEmpty) {
      Components.infoDialog(
          context: context,
          message: "A entrada de código não pode estar vazia!");
      return;
    }

    final String dados = inputCode.text;

    final int tamanho = int.parse(widthImp);

    final int nivelCorrecao = 2;

    IntentDigitalHubCommandStarter.startCommands([
      new DefinePosicao(getAlignValue()),
      new ImpressaoQRCode(dados, tamanho, nivelCorrecao),
      new AvancaPapel(10),
      if (cutPaper) ...{new Corte(0)}
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

//Retorna o código correspondente ao tipo de código de barras selecionado.
  getBarcodeTypeValue() {
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
    }
  }

  //Retorna o valor de posição para cada tipo de alinhamento.
  getAlignValue() {
    switch (alignPrint) {
      case 'Centralizado':
        return 1;
      case 'Esquerda':
        return 0;
      case 'Direita':
        return 2;
    }
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
                        Text("IMPRESSÃO DE CÓDIGO DE BARRAS",
                            style: TextStyle(
                                fontSize: 18, fontWeight: FontWeight.bold))
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        GeneralWidgets.inputField(
                          inputCode,
                          'CÓDIGO: ',
                          iWidht: MediaQuery.of(context).size.width - 120,
                          textSize: 16,
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 10,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [dropDownBarcodeType()],
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
                    SizedBox(height: 20),
                    //Estilização de código de barras, para o dispositivo SmartPOS só está disponível para o CODE 128 e QR_CODE.
                    if (barCodeType == 'QR_CODE' ||
                        barCodeType == 'CODE 128') ...{
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
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [dropDownWidth()],
                      ),
                      //A altura só é configurável para o código de barras CODE 128.
                      if (barCodeType == 'CODE 128') ...{
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [dropDownHeight()],
                        ),
                      }
                    },

                    Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
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
                    SizedBox(
                      height: 10,
                    ),
                    Row(
                      children: [
                        GeneralWidgets.personButton(
                          textButton: 'IMPRIMIR CÓDIGO DE BARRAS',
                          width: MediaQuery.of(context).size.width,
                          callback: () {
                            if (barCodeType == "QR_CODE") {
                              sendPrinterQrCode();
                            } else {
                              sendPrinterBarCode();
                            }
                          },
                        )
                      ],
                    )
                  ],
                ),
              ),
              GeneralWidgets.baseboard()
            ],
          ),
        ),
      ),
    );
  }

  Widget alignRadios() {
    return Column(
      children: [
        GeneralWidgets.radioBtn('Esquerda', alignPrint, onChangeAlign),
        GeneralWidgets.radioBtn('Centralizado', alignPrint, onChangeAlign),
        GeneralWidgets.radioBtn('Direita', alignPrint, onChangeAlign),
      ],
    );
  }

  Widget dropDownBarcodeType() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            "TIPO DE CÓDIGO DE BARRAS: ",
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          GeneralWidgets.dropDown(barCodeType, [
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
            setState(() {
              barCodeType = value;
            });
            onChangeBarCodeType(value);
          })
        ],
      ),
    );
  }

  Widget dropDownWidth() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            //Para o QR_CODE o nome da largura, deve ser "square", uma vez que um qr_code tem largura e altura iguais..
            (barCodeType == 'CODE 128') ? "WIDTH: " : "SQUARE: ",
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          GeneralWidgets.dropDown(widthImp, ['1', '2', '3', '4', '5', '6'],
              (String value) {
            setState(() {
              widthImp = value;
            });
          })
        ],
      ),
    );
  }

  Widget dropDownHeight() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            "HEIGHT: ",
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          GeneralWidgets.dropDown(heightImp, ['20', '60', '120', '200'],
              (String value) {
            setState(() {
              heightImp = value;
            });
          })
        ],
      ),
    );
  }
  // Widget estiloChecks() {
  // return Row(
  // mainAxisAlignment: MainAxisAlignment.spaceAround,
  //     children: [
  //     if (barCodeType != "QR_CODE") ...{
  //    GeneralWidgets.dropDown(
  //      'HEIGHT: ', heightImp, ['20', '60', '120', '200'],
  //       (String value) {
  //       setState(() {
  //       heightImp = value;
  //      });
  //    }),
  //  },
  //   GeneralWidgets.dropDown(
  //       'WIDTH: ', widthImp, ['1', '2', '3', '4', '5', '6'],
  //        (String value) {
  //     setState(() {
  //         widthImp = value;
  //     });
  //  }),
  //     GeneralWidgets.checkBox('CUT PAPER', cutPaper, (bool value) {
  //       setState(() {
  //        cutPaper = value;
  //    });
  //   }),
  // ],
  // );
//  }
//}
}
