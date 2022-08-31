import { useEffect, useState } from "react";
import {
  IonButton,
  IonCheckbox,
  IonCol,
  IonGrid,
  IonImg,
  IonLabel,
  IonRow,
  useIonAlert,
} from "@ionic/react";
import { App } from "@capacitor/app";
import { Filesystem, Directory } from "@capacitor/filesystem";
import { Camera, CameraResultType, CameraSource } from "@capacitor/camera";

import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { TermicaCommand } from "../../intentDigitalHubService/TERMICA/TermicaCommand";
import { ImprimeImagem } from "../../intentDigitalHubService/TERMICA/Commands/ImprimeImagem";
import { AvancaPapel } from "../../intentDigitalHubService/TERMICA/Commands/AvancaPapel";
import { Corte } from "../../intentDigitalHubService/TERMICA/Commands/Corte";

import Logo from "../../icons/elgin_logo_default_print_image.png";

const directory = Directory.External;
const fileName = "ImageToPrint.jpg";

const PrinterImage: React.FC = () => {
  // Variáveis de Entrada
  const [isCutPaperActive, setIsCutPaperActive] = useState(false);
  const [image, setImage] = useState<string | null>(null);
  const [appPackageName, setFilePathIdh] = useState<string>("");

  const [presentAlert] = useIonAlert();

  useEffect(() => {
    App.getInfo().then((info) => {
      setFilePathIdh(info.id);
    });
    Filesystem.writeFile({
      path: fileName,
      data: Logo,
      directory,
    });
  }, []);

  async function base64FromPath(path: string): Promise<string> {
    const response = await fetch(path);
    const blob = await response.blob();
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onerror = reject;
      reader.onload = () => {
        if (typeof reader.result === "string") {
          resolve(reader.result);
        } else {
          reject("method did not return a string");
        }
      };
      reader.readAsDataURL(blob);
    });
  }

  //Abre o picker de imagem
  const chooseImage = async () => {
    const photo = await Camera.getPhoto({
      quality: 100,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      source: CameraSource.Photos,
      height: 300,
    });

    if (!photo.webPath) {
      presentAlert("NÃO FOI ESCOLHIDO NENHUMA IMAGEM");
      return;
    }

    const tempPath = photo.webPath;
    const base64Data = await base64FromPath(tempPath);
    setImage(tempPath);
    await Filesystem.writeFile({
      path: fileName,
      data: base64Data,
      directory,
    });
  };

  function doPrinterImage() {
    //REALIZA A LIMPEZA DO URI PADRÃO REMOVENDO A PARTE INICIAL
    const finalPathIDH = "/Android/data/" + appPackageName + "/files/" + fileName;

    const command = new ImprimeImagem(finalPathIDH);

    const commands: TermicaCommand[] = [command];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    IntentDigitalHubCommandStarter.startCommands(commands);
  }

  return (
    <IonGrid>
      <h4
        style={{
          textAlign: "center",
          fontSize: "medium",
        }}
        className="ion-no-margin"
      >
        IMPRESSÃO DE CÓDIGO DE BARRAS
      </h4>
      <h4 style={{ textAlign: "center", marginTop: "10px", fontSize: "large" }}>
        PRÉ-VISUALIZAÇÃO
      </h4>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "center",
          height: 170,
        }}
      >
        {image ? (
          <IonImg
            src={image!}
            style={{ maxWidth: "350px", maxHeigth: "200px" }}
          />
        ) : (
          <IonImg src={Logo} style={{ width: 350 }} />
        )}
      </div>
      <h4 style={{ margin: "5px 0px 0px 10px" }}>ESTILIZAÇÃO:</h4>
      <div style={{ marginLeft: "15px" }}>
        <IonCheckbox
          checked={isCutPaperActive}
          onIonChange={(e) => setIsCutPaperActive(e.detail.checked)}
        ></IonCheckbox>
        <IonLabel style={{ marginLeft: "5px" }}>CUT PAPER</IonLabel>
      </div>
      <IonRow
        style={{
          display: "flex",
          marginTop: "5px",
          flexDirection: "row",
          justifyContent: "space-around",
          alignItems: "center",
        }}
      >
        <IonCol>
          <IonButton
            expand="block"
            onClick={chooseImage}
            color="primary"
            mode="ios"
          >
            SELECIONAR
          </IonButton>
        </IonCol>
        <IonCol>
          <IonButton
            expand="block"
            onClick={doPrinterImage}
            color="primary"
            mode="ios"
          >
            IMPRIMIR
          </IonButton>
        </IonCol>
      </IonRow>
    </IonGrid>
  );
};

export default PrinterImage;
