import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/avanca_papel.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/imprime_imagem.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:intent_digital_hub/components/components.dart';
import 'package:path_provider/path_provider.dart';
import 'package:image_picker/image_picker.dart';

import '../../IntentDigitalHubService/TERMICA/corte.dart';
import '../../Widgets/widgets.dart';

class PrinterImagePage extends StatefulWidget {
  final double mWidth;
  final double mHeight;

  const PrinterImagePage({Key? key, this.mWidth = 200, this.mHeight = 200})
      : super(key: key);
  @override
  _PrinterImagePageState createState() => _PrinterImagePageState();
}

class _PrinterImagePageState extends State<PrinterImagePage> {
  TextEditingController inputCode = TextEditingController();

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
        '/Android/data/com.elgin.flutter.intent_digital_hub/files/ImageToPrint.jpg';

    await IntentDigitalHubCommandStarter.startCommands([
      ImprimeImagem(imageToPrintPath),
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
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text("IMPRESSÃO DE IMAGEM",
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              preVisuImage(_imageSelected),
              const Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "ESTILIZAÇÃO: ",
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              estiloChecks(),
              const SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  GeneralWidgets.personButton(
                    textButton: 'SELECIONAR',
                    width: widget.mWidth / 2 - 50,
                    callback: () => getImage(),
                  ),
                  GeneralWidgets.personButton(
                    textButton: 'IMPRIMIR',
                    width: widget.mWidth / 2 - 50,
                    callback: () => sendPrinterImage(),
                  )
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget preVisuImage(File? imagePath) {
    return Column(
      children: [
        const Text("PRÉ - VISUALIZAÇÃO", style: TextStyle(fontSize: 15)),
        Padding(
          padding: const EdgeInsets.all(10),
          child: imagePath == null
              ? Image.asset(
                  "assets/images/elgin_logo.png",
                  height: 100,
                  fit: BoxFit.contain,
                )
              : Image.file(
                  imagePath,
                  height: 100,
                ),
        )
      ],
    );
  }

  Widget estiloChecks() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        GeneralWidgets.checkBox('CUT PAPER', cutPaper, (bool value) {
          setState(() {
            cutPaper = value;
          });
        }),
      ],
    );
  }
}
