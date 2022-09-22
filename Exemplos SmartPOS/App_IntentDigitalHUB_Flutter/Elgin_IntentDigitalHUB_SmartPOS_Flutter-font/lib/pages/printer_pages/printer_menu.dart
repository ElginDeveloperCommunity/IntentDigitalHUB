import 'package:flutter/material.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/Commands/status_impressora.dart';
import 'package:flutter_smartpos/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:flutter_smartpos/Utils/utils.dart';
import 'package:flutter_smartpos/Widgets/widgets.dart';
import 'package:flutter_smartpos/components/components.dart';
import 'package:flutter_smartpos/pages/printer_pages/printer_barCode.dart';
import 'package:flutter_smartpos/pages/printer_pages/printer_image.dart';
import 'package:flutter_smartpos/pages/printer_pages/printer_text.dart';

import '../../IntentDigitalHubService/TERMICA/Commands/abre_conexao_impressora.dart';
import '../../IntentDigitalHubService/TERMICA/Commands/fecha_conexao_impressora.dart';

class PrinterMenuPage extends StatefulWidget {
  @override
  _PrinterMenuPageState createState() => _PrinterMenuPageState();
}

class _PrinterMenuPageState extends State<PrinterMenuPage>
    with WidgetsBindingObserver {
  TextEditingController inputIp =
      new TextEditingController(text: "192.168.0.31:9100");

  @mustCallSuper
  @protected
  Future<void> dispose() async {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
    //Termina a conexão coma impressora ao sair da tela.
    await IntentDigitalHubCommandStarter.startCommand(FechaConexaoImpressora());
  }

  String selectedModulePrinter = "text";
  String selectedImp = "IMP. INTERNA";

  //Modelos de impressora externa.
  static const String EXTERNAL_PRINTER_MODEL_I9 = "i9";
  static const String EXTERNAL_PRINTER_MODEL_I8 = "i8";

  double mBoxScreenSizeW = 580;
  double mBoxScreenSizeH = 450;

  Widget? mActualScreenSelected;

  @override
  void initState() {
    super.initState();

    connectInternalImp();
  }

  selectScreen(Widget selected) {
    Components.goToScreen(context, selected);
  }

  onChangeModulePrinter(String selected) {
    setState(() {
      selectedModulePrinter = selected;
    });
    setState(() {
      switch (selectedModulePrinter) {
        case "text":
          mActualScreenSelected = PrinterTextPage(
            selectedImp: this.selectedImp,
            mWidth: mBoxScreenSizeW,
            mHeight: mBoxScreenSizeH,
          );
          break;
        case "barcode":
          mActualScreenSelected = PrinterBarCodePage(
            selectedImp: this.selectedImp,
            mWidth: mBoxScreenSizeW,
            mHeight: mBoxScreenSizeH,
          );
          break;
        case "image":
          mActualScreenSelected = PrinteImagePage(
            selectedImp: this.selectedImp,
            mWidth: mBoxScreenSizeW,
            mHeight: mBoxScreenSizeH,
          );
          break;
        default:
      }
    });
  }

  onChangedImp(String value) async {
    if (value == "IMP. INTERNA") {
      connectInternalImp();
    } else {
      //Valida se o ip está corretamente formatado.
      if (Utils.validaIpWithPort(inputIp.text)) {
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
          await connectExternalImp(selectedModel);
        }

        await GeneralWidgets.showAlertDialogWithSelectableOptions(
            mainWidgetContext: context,
            alertTitle: 'Selecione o modelo de impressora a ser conectado',
            listOfOptions: externalPrinterModels,
            onTap: onExternalPrinterModelTapped);
      } else {
        Components.infoDialog(
            context: context, message: "Digite um IP valido.");
        await connectInternalImp();
      }
    }
  }

  getImpStatus() async {
    final IntentDigitalHubCommandReturn statusCommandReturn =
        await IntentDigitalHubCommandStarter.startCommand(
            new StatusImpressora(3));

    int resultado = statusCommandReturn.resultado;

    String message;
    if (resultado == 5) {
      message = "Papel está presente e não está próximo do fim!";
    } else if (resultado == 6) {
      message = "Papel próximo do fim!";
    } else if (resultado == 7) {
      message = "Papel ausente!";
    } else {
      message = "Status Desconhecido!";
    }

    Components.infoDialog(context: context, message: message);
  }

  connectInternalImp() async {
    setState(() {
      selectedImp = "IMP. INTERNA";
    });

    IntentDigitalHubCommandStarter.startCommand(
        new AbreConexaoImpressora(5, "SMARTPOS", "", 0));
  }

  connectExternalImp(String selectedPrinterModel) async {
    String ip = inputIp.text.substring(0, inputIp.text.indexOf(":"));
    String port = inputIp.text.substring(ip.length + 1);

    IntentDigitalHubCommandReturn startPrinterStartReturn =
        await IntentDigitalHubCommandStarter.startCommand(
            new AbreConexaoImpressora(
                3, selectedPrinterModel, ip, int.parse(port)));

    //Se a conexão com a impressora externa não for bem sucedida, retorne a impressora interna.
    if (startPrinterStartReturn.resultado != 0) {
      connectInternalImp();
    } else {
      setState(() {
        selectedImp = "IMP. EXTERNA";
      });
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
                child: Column(
                  children: [
                    rowExternalImp(),
                    modulesPrinter(),
                  ],
                ),
              ),
              Container(
                child: Column(
                  children: [GeneralWidgets.baseboard()],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  Widget rowExternalImp() {
    return Container(
      child: Column(
        children: [
          Container(
              child: Row(
            children: [
              GeneralWidgets.radioBtn(
                  'IMP. INTERNA', selectedImp, onChangedImp),
              GeneralWidgets.radioBtn(
                  'IMP. EXTERNA', selectedImp, onChangedImp),
            ],
          )),
          GeneralWidgets.formFieldPerson(
            inputIp,
            width: 250,
            label: "IP: ",
          ),
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
        borderRadius: BorderRadius.all(Radius.circular(16)),
      ),
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: mActualScreenSelected,
      ),
    );
  }

  Widget modulesPrinter() {
    return Container(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          GeneralWidgets.personSelectedButton(
            nameButton: 'IMPRESSÃO\nDE TEXTO',
            assetImage: 'assets/images/printerText.png',
            mHeight: 90,
            iconSize: 40,
            mWidth: 250,
            fontLabelSize: 12,
            onSelected: () => selectScreen(PrinterTextPage(
              selectedImp: this.selectedImp,
            )),
          ),
          GeneralWidgets.personSelectedButton(
            nameButton: 'IMPRESSÃO DE\nCÓDIGO DE BARRAS',
            assetImage: 'assets/images/printerBarCode.png',
            mHeight: 90,
            mWidth: 250,
            iconSize: 40,
            fontLabelSize: 12,
            onSelected: () => selectScreen(PrinterBarCodePage(
              selectedImp: this.selectedImp,
            )),
          ),
          GeneralWidgets.personSelectedButton(
            nameButton: 'IMPRESSÃO\nDE IMAGEM',
            mHeight: 90,
            mWidth: 250,
            iconSize: 40,
            fontLabelSize: 12,
            assetImage: 'assets/images/printerImage.png',
            onSelected: () => selectScreen(PrinteImagePage(
              selectedImp: this.selectedImp,
            )),
          ),
          GeneralWidgets.personSelectedButtonStatus(
            nameButton: "STATUS IMPRESSORA",
            mHeight: 45,
            mWidth: 250,
            onSelected: () => getImpStatus(),
          )
        ],
      ),
    );
  }
}
