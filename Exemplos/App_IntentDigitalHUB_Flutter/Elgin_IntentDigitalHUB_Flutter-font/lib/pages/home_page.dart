import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:intent_digital_hub/Widgets/widgets.dart';
import 'package:intent_digital_hub/XmStorageService/xml_database_service.dart';
import 'package:intent_digital_hub/pages/bridge/bridge_page.dart';
import 'package:intent_digital_hub/pages/impressora/printer_menu.dart';
import 'package:intent_digital_hub/pages/sat/sat.dart';

import '../components/components.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  //Pede a permissão ao ínicio da aplicação
  @override
  void initState() {
    super.initState();

    askExternalStoragePermission();
  }

  //Assinatura do methodChannel
  static const _platform = MethodChannel('elgin.intent_digital_hub');

  //Se a permissão for negada, a aplicação deve ser encerrada, uma vez que vários módulos dependem da permissão de acesso ao armazenamento
  askExternalStoragePermission() async {
    bool wasPermissionGranted =
        await _platform.invokeMethod("askExternalStoragePermission");

    if (!wasPermissionGranted) {
      Fluttertoast.showToast(
          msg:
              'É necessário conceder a permissão para várias funcionalidades da aplicação!',
          toastLength: Toast.LENGTH_LONG,
          gravity: ToastGravity.BOTTOM,
          timeInSecForIosWeb: 1,
          backgroundColor: Colors.red,
          textColor: Colors.white,
          fontSize: 16.0);
      //Encerra a aplicação
      SystemChannels.platform.invokeMethod('SystemNavigator.pop');
    } else {
      //Salva todos os xmls dentro do diretório da aplicação, na memória do dispositivo
      XmlDataBaseService.allocateXmls();
    }
  }

  selectScreen(Widget selected) {
    Components.goToScreen(context, selected);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 150, vertical: 25),
            child: Image.asset("assets/images/elgin_logo.png"),
          ),
          Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  moduleButton(
                      nameButton: 'BRIDGE',
                      assetImage: 'assets/images/elginpay_logo.png',
                      widgetScreen: const BridgePage()),
                  moduleButton(
                      nameButton: 'IMPRESSORA',
                      assetImage: 'assets/images/printer.png',
                      widgetScreen: const PrinterMenutPage())
                ],
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  moduleButton(
                      nameButton: 'SAT',
                      assetImage: 'assets/images/sat.png',
                      widgetScreen: const SatPage())
                ],
              )
            ],
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 20),
            child: GeneralWidgets.baseboard(),
          )
        ],
      ),
    );
  }

  Widget moduleButton(
      {
      //Nome do módulo
      required final String nameButton,
      //Caminho da image que decora o button
      required final String assetImage,
      //Padding horizontal opcional na image
      final double horizontalPadding = 0,
      //Tela-Widget do módulo - change to required
      final Widget? widgetScreen,
      //Altura do módulo
      final double height = 130,
      //Largura do módulo
      final double width = 230}) {
    return TextButton(
      onPressed: (() => selectScreen(widgetScreen!)),
      child: Container(
        height: height,
        width: width,
        decoration: BoxDecoration(
          border: Border.all(color: Colors.black, width: 3),
          borderRadius: const BorderRadius.all(Radius.circular(20)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const SizedBox(
              height: 5,
            ),
            Padding(
                padding: EdgeInsets.symmetric(horizontal: horizontalPadding),
                child: Image.asset(assetImage, height: 50)),
            const SizedBox(
              height: 5,
            ),
            Text(
              nameButton,
              textAlign: TextAlign.center,
              style: const TextStyle(
                  fontSize: 16,
                  color: Colors.black,
                  fontWeight: FontWeight.bold),
            ),
            const SizedBox(
              height: 5,
            )
          ],
        ),
      ),
    );
  }
}
