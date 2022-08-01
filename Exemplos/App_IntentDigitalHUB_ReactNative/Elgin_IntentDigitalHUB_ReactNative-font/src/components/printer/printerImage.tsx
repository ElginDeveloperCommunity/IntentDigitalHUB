import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  Alert,
} from 'react-native';
import CheckBox from '@react-native-community/checkbox';
import RNFS from 'react-native-fs';
import {
  launchImageLibrary,
  Asset,
  MediaType,
  ImageLibraryOptions,
} from 'react-native-image-picker';

import IntentDigitalHubCommandStarter from '../../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {TermicaCommand} from '../../intentDigitalHubService/TERMICA/TermicaCommand';
import {AvancaPapel} from '../../intentDigitalHubService/TERMICA/AvancaPapel';
import {Corte} from '../../intentDigitalHubService/TERMICA/Corte';
import {ImprimeImagem} from '../../intentDigitalHubService/TERMICA/ImprimeImagem';

const Logo = require('../../icons/elgin_logo_default_print_image.png');

const directory = RNFS.ExternalDirectoryPath;
const fileName = 'ImageToPrint.jpg';
const finalPath = directory + '/' + fileName;

const PrinterImage = () => {
  // Variáveis de Entrada
  const [isCutPaperActive, setIsCutPaperActive] = useState(false);
  const [image, setImage] = useState<Asset | null>(null);

  useEffect(() => {
    const saveDefaultImage = async () => {
      RNFS.readFileRes('src_icons_elgin_logo_default_print_image.png', 'base64')
        .then(async data => {
          await RNFS.writeFile(finalPath, data, 'base64');
        })
        .catch(err => {
          console.log(err);
        });
    };
    saveDefaultImage();
  }, []);

  //Abre o picker de imagem
  const chooseImage = (type: MediaType) => {
    let options: ImageLibraryOptions = {
      quality: 1,
      mediaType: type,
      maxWidth: 300,
      maxHeight: 550,
    };

    launchImageLibrary(options, response => {
      if (response.didCancel) {
        Alert.alert('NÃO FOI ESCOLHIDO NENHUMA IMAGEM');
        return;
      } else if (response.errorCode === 'camera_unavailable') {
        Alert.alert('CÂMERA NÃO DISPONÍVEL');
        return;
      } else if (response.errorCode === 'permission') {
        Alert.alert('PERMISSÃO NÃO CONCEDIDA');
        return;
      } else if (response.errorCode === 'others') {
        Alert.alert(response.errorMessage || 'ERRO DESCONHECIDO');
        return;
      }

      const tempPath = response.assets![0].uri!.split('file://')[1];
      RNFS.copyFile(tempPath, finalPath);

      setImage(response.assets![0]);
    });
  };

  function doPrinterImage() {
    //REALIZA A LIMPEZA DO URI PADRÃO REMOVENDO A PARTE INICIAL
    const finalPathIDH =
      directory.substring(directory.indexOf('/Android')) + '/' + fileName;

    const command = new ImprimeImagem(finalPathIDH);

    const commands: TermicaCommand[] = [command];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    IntentDigitalHubCommandStarter.startCommands(commands, _ => null);
  }

  return (
    <View style={styles.mainView}>
      <View style={styles.titleView}>
        <Text style={styles.titleText}>IMPRESSÃO DE IMAGEM</Text>
        <Text style={styles.subTitleText}>PRÉ-VISUALIZAÇÃO</Text>
      </View>

      <View style={styles.uploadedImageView}>
        {image ? (
          <Image style={styles.imageZone} resizeMode="contain" source={image} />
        ) : (
          <>
            <Image
              style={styles.imageZone}
              resizeMode="contain"
              source={Logo}
            />
          </>
        )}
      </View>

      <View style={styles.imageStyleSettingsView}>
        <Text style={styles.labelText}>ESTILIZAÇÃO</Text>
        <View style={styles.imageStyleOptionsView}>
          <View style={styles.checkBoxStyleView}>
            <CheckBox
              disabled={false}
              value={isCutPaperActive}
              onValueChange={newValue => setIsCutPaperActive(newValue)}
            />
            <Text style={styles.optionText}>CUT PAPER</Text>
          </View>
        </View>
      </View>
      <View style={styles.submitButtonsView}>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => chooseImage('photo')}>
          <Text style={styles.textButton}>SELECIONAR</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.actionButton} onPress={doPrinterImage}>
          <Text style={styles.textButton}>IMPRIMIR</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  titleText: {
    color: 'black',
    fontSize: 30,
    fontWeight: 'bold',
  },
  subTitleText: {
    color: 'black',
    fontSize: 25,
    fontWeight: 'bold',
  },
  optionText: {
    color: 'black',
    fontSize: 14,
    fontWeight: 'bold',
  },
  labelText: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: 15,
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
  },
  mainView: {
    flex: 1,
    paddingHorizontal: 20,
    alignItems: 'stretch',
  },
  titleView: {
    height: 80,
    alignItems: 'center',
    justifyContent: 'center',
  },
  uploadedImageView: {
    alignItems: 'center',
    height: 150,
    justifyContent: 'center',
  },
  imageZone: {
    maxWidth: 300,
    height: 150,
  },
  actionButton: {
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderRadius: 5,
    borderColor: '#0069A5',
    backgroundColor: '#0069A5',
    height: 50,
    width: '46%',
    marginVertical: 5,
  },
  imageStyleSettingsView: {
    flexDirection: 'column',
  },
  imageStyleOptionsView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  imageTypePicker: {
    width: 150,
    height: 50,
  },
  imageStylePicker: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  checkBoxStyleView: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  submitButtonsView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
});

export default PrinterImage;
