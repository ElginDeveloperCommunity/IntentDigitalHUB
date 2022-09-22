import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_smartpos/Widgets/widgets.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';

import '../../IntentDigitalHubService/TERMICA/Commands/avanca_papel.dart';
import '../../IntentDigitalHubService/TERMICA/Commands/corte.dart';
import '../../IntentDigitalHubService/TERMICA/Commands/impressao_imagem.dart';
import '../../IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import '../../components/components.dart';

class PrinteImagePage extends StatefulWidget {
  final double mWidth;
  final double mHeight;
  final String selectedImp;

  PrinteImagePage(
      {required this.selectedImp, this.mWidth = 200, this.mHeight = 200});
  @override
  _PrinteImagePageState createState() => _PrinteImagePageState();
}

class _PrinteImagePageState extends State<PrinteImagePage> {
  bool cutPaper = false;

  @override
  void initState() {
    super.initState();

    _setDefaultPrinterImage();
  }

  //Escreve a imagem de impressão padrão (logo da elgin) na diretório usado para ImpressaoImagem
  Future<void> _setDefaultPrinterImage() async {
    File file =
        File('${(await getExternalStorageDirectory())?.path}/ImageToPrint.jpg');

    final byteData = await rootBundle
        .load('assets/images/elgin_logo_default_print_image.png');
    await file.writeAsBytes(byteData.buffer
        .asUint8List(byteData.offsetInBytes, byteData.lengthInBytes));
  }

  //Utilizado para mudar a imagem na tela ao selecionar uma nova imagem na galeria
  File? _imageSelected;

  //Objeto utilizado para iniciar a abertura para seleção de imagem da galeria
  final picker = ImagePicker();

  //Atualiza a imagem no diretório externo da aplicação, que será utilizada para a impressão
  getImage() async {
    final pickedFile = await picker.getImage(source: ImageSource.gallery);

    if (pickedFile != null) {
      setState(() {
        _imageSelected = File(pickedFile.path);
      });

      final _imageSelectedByteData = _imageSelected?.readAsBytesSync();

      //Cria uma cópia da imagem capturada no diretório externo para impressão, caso selecionado
      File newFile = File(
          '${(await getExternalStorageDirectory())?.path}/ImageToPrint.jpg');

      newFile.writeAsBytesSync(_imageSelectedByteData as List<int>);
    } else {
      Components.infoDialog(
          context: context, message: 'Nenhuma imagem selecionda!');
    }
  }

  //Imprime a imagem no diretório externo da aplicação
  sendPrinterImage() async {
    const String imageToPrintPath =
        '/Android/data/com.elgin.flutter_intent_digital_hub_smartpos/files/ImageToPrint.jpg';

    await IntentDigitalHubCommandStarter.startCommands([
      ImprimeImagem(imageToPrintPath),
      AvancaPapel(10),
      if (cutPaper) ...{Corte(0)}
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
                      Text("IMPRESSÃO DE IMAGEM",
                          style: TextStyle(
                              fontSize: 18, fontWeight: FontWeight.bold))
                    ],
                  ),
                  SizedBox(height: 10),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text("PRÉ-VISUZALIZAÇÃO",
                          style: TextStyle(
                              fontSize: 16, fontWeight: FontWeight.bold))
                    ],
                  ),
                  SizedBox(height: 30),
                  preVisuImage(_imageSelected),
                  SizedBox(
                    height: 10,
                  ),
                  SizedBox(height: 30),
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
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      GeneralWidgets.personButton(
                        textButton: 'SELECIONAR',
                        width: (MediaQuery.of(context).size.width / 2) - 10,
                        callback: () => getImage(),
                      ),
                      GeneralWidgets.personButton(
                        textButton: 'IMPRIMIR',
                        width: (MediaQuery.of(context).size.width / 2) - 10,
                        callback: () => sendPrinterImage(),
                      )
                    ],
                  ),
                ],
              ),
            ),
            GeneralWidgets.baseboard(),
          ],
        ),
      ),
    ));
  }

  Widget preVisuImage(File? imagePath) {
    return Column(
      children: [
        imagePath == null
            ? Image.asset(
                "assets/images/elgin_logo.png",
                height: 100,
                fit: BoxFit.contain,
              )
            : Image.file(
                imagePath,
                height: 100,
              )
      ],
    );
  }
}
