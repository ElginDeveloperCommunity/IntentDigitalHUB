//Classe que manterá a referência a todos os arquivos XMls que serão guardados no projeto

import 'dart:io';

import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

part 'xml_file.dart';

//Classe service que ao inicio da aplicação salva todos os XMLs presentes em assets/xml dentro do diretório raiz da aplicação, o enumerator fornece o path relativo dos
//arquivos salvos dentro do diretório da aplicação com o formato correto para as funções do Intent Digital Hub
class XmlDataBaseService {
  //Função que salva todos os XMLs do projeto(presentes em assets/xml) dentro do diretório da aplicação
  static allocateXmls() async {
    for (var xml in XmlFile.values) {
      //Cria o arquvo que será salvo no diretório da aplicação
      final File file = File(await xml._absolutePath);

      //Evita de criar os arquivos novamente
      if (!await file.exists()) {
        //Lê o asset do projeto e o guarda em formato ByteData
        final byteData =
            await rootBundle.load('assets/xml/' + getXmlArchiveName(xml));

        //Escreve no arquivo todo o conteúdo lido do asset
        await file.writeAsBytes(byteData.buffer
            .asUint8List(byteData.offsetInBytes, byteData.lengthInBytes));
      }
    }
  }
}
