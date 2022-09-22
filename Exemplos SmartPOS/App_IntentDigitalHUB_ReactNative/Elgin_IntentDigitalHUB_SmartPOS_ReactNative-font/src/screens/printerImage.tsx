import React, {FC, useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  Alert,
} from 'react-native';
import {NativeStackScreenProps} from '@react-navigation/native-stack';
import {Checkbox} from 'react-native-paper';
import RNFS from 'react-native-fs';
import {
  launchImageLibrary,
  Asset,
  MediaType,
  ImageLibraryOptions,
} from 'react-native-image-picker';

import IntentDigitalHubCommandStarter from '../intentDigitalHubService/IntentDigitalHubCommandStarter';
import {TermicaCommand} from '../intentDigitalHubService/TERMICA/TermicaCommand';
import {AvancaPapel} from '../intentDigitalHubService/TERMICA/Commands/AvancaPapel';
import {Corte} from '../intentDigitalHubService/TERMICA/Commands/Corte';
import {ImprimeImagem} from '../intentDigitalHubService/TERMICA/Commands/ImprimeImagem';
import {RootStackParamList} from '../appNavigator';
import Header from '../components/Header';
import Footer from '../components/Footer';

const Logo = require('../icons/elgin_logo_default_print_image.png');

const directory = RNFS.ExternalDirectoryPath;
const fileName = 'ImageToPrint.jpg';
const finalPath = directory + '/' + fileName;

const PrinterImage: FC<
  NativeStackScreenProps<RootStackParamList, 'PrinterText'>
> = ({route}) => {
  // Variáveis de Entrada
  const [isCutPaperActive, setIsCutPaperActive] = useState(false);
  const [image, setImage] = useState<Asset | null>(null);

  const showCutPaper = route.params.connectionType === 'ip';

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
      <Header textTitle="IMPRESSORA" />
      <View style={styles.printerSettingsView}>
        <View style={styles.titleView}>
          <Text style={styles.titleText}>IMPRESSÃO DE IMAGEM</Text>
          <Text style={styles.subTitleText}>PRÉ-VISUALIZAÇÃO</Text>
        </View>

        <View style={styles.uploadedImageView}>
          {image ? (
            <Image
              style={styles.imageZone}
              resizeMode="contain"
              source={image}
            />
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

        {showCutPaper && (
          <View style={styles.imageStyleSettingsView}>
            <Text style={styles.labelText}>ESTILIZAÇÃO</Text>
            <View style={styles.imageStyleOptionsView}>
              <View style={styles.checkBoxStyleView}>
                <Checkbox
                  disabled={false}
                  status={isCutPaperActive ? 'checked' : 'unchecked'}
                  onPress={() => setIsCutPaperActive(!isCutPaperActive)}
                />
                <Text style={styles.optionText}>CUT PAPER</Text>
              </View>
            </View>
          </View>
        )}
        <View style={styles.submitButtonsView}>
          <TouchableOpacity
            style={styles.actionButton}
            onPress={() => chooseImage('photo')}>
            <Text style={styles.textButton}>SELECIONAR</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.actionButton}
            onPress={doPrinterImage}>
            <Text style={styles.textButton}>IMPRIMIR</Text>
          </TouchableOpacity>
        </View>
      </View>
      <Footer />
    </View>
  );
};

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    alignItems: 'stretch',
    backgroundColor: 'white',
  },
  printerSettingsView: {
    flex: 1,
    paddingHorizontal: 10,
  },
  titleText: {
    color: 'black',
    fontSize: 26,
    fontWeight: 'bold',
  },
  subTitleText: {
    color: 'black',
    fontSize: 25,
    fontWeight: 'bold',
  },
  optionText: {
    color: 'black',
    fontSize: 16,
    fontWeight: 'bold',
  },
  labelText: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: 15,
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
    marginTop: 50,
  },
  textButton: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 16,
  },
});

export default PrinterImage;
