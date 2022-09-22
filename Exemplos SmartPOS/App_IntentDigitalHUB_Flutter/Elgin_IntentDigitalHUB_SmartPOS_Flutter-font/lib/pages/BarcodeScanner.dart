import 'package:flutter/material.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:flutter_smartpos/Widgets/widgets.dart';

class BarcodeScannerPage extends StatefulWidget {
  @override
  _BarcodeScannerPageState createState() => _BarcodeScannerPageState();
}

class _BarcodeScannerPageState extends State<BarcodeScannerPage> {
  TextEditingController barcodeResult = new TextEditingController(text: '');
  TextEditingController typeOfBarcode = new TextEditingController(text: '');

  barCodeRead() async {
    IntentDigitalHubCommandReturn scannerCommandResult =
        await IntentDigitalHubCommandStarter.startScanner();

    setState(() {
      barcodeResult.text = scannerCommandResult.resultado[
          1]; // O resultado do Json de scanner é um array, sendo que a o código lido está na posição 1.
      typeOfBarcode.text = scannerCommandResult
          .resultado[3]; // O tipo de código lido está na posição 3.
    });
  }

//  Limpa os campos para uma nova leitura.
  emptyFields() {
    setState(() {
      barcodeResult.text = "";
      typeOfBarcode.text = "";
    });
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
              GeneralWidgets.headerScreen("CÓDIGO DE BARRAS"),
              Padding(
                padding: const EdgeInsets.all(15.0),
                child: Container(
                  height: 200,
                  width: MediaQuery.of(context).size.width,
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: Colors.black,
                      width: 2,
                    ),
                    borderRadius: BorderRadius.all(Radius.circular(30)),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Container(
                            width: MediaQuery.of(context).size.width - 34,
                            height: 50,
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: Colors.black,
                                width: 2,
                              ),
                              borderRadius:
                                  BorderRadius.all(Radius.circular(30)),
                            ),
                            child: TextFormField(
                              textAlign: TextAlign.center,
                              enabled: false,
                              controller: barcodeResult,
                            ),
                          ),
                        ],
                      ),
                      SizedBox(
                        height: 25,
                      ),
                      Row(
                        children: [
                          Container(
                            width: MediaQuery.of(context).size.width - 34,
                            height: 50,
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: Colors.black,
                                width: 2,
                              ),
                              borderRadius:
                                  BorderRadius.all(Radius.circular(30)),
                            ),
                            child: TextFormField(
                              textAlign: TextAlign.center,
                              enabled: false,
                              controller: typeOfBarcode,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              Container(
                child: Column(
                  children: [
                    GeneralWidgets.personButton(
                      textButton: 'INICIAR LEITURA',
                      width: (MediaQuery.of(context).size.width) - 30,
                      callback: () => {barCodeRead()},
                    ),
                    SizedBox(
                      height: 5,
                    ),
                    GeneralWidgets.personButton(
                      textButton: 'LIMPAR CAMPO',
                      width: (MediaQuery.of(context).size.width) - 30,
                      callback: () => {emptyFields()},
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
}
