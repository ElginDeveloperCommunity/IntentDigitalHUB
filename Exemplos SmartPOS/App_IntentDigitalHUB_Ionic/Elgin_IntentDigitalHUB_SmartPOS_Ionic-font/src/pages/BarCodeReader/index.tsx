import { useState } from "react";
import {
  IonButton,
  IonCol,
  IonContent,
  IonGrid,
  IonInput,
  IonLabel,
  IonPage,
  IonRow,
} from "@ionic/react";

import Footer from "../../components/Footer";
import Header from "../../components/Header";

import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { IntentDigitalHubModule } from "../../intentDigitalHubService/IntentDigitalHubModule";

import styles from "./index.module.css";

const BarcodeReader: React.FC = () => {
  const [barCode, setBarCode] = useState("");
  const [barCodeType, setBarCodeType] = useState("");

  const handleIniciarLeitura = async () => {
    const { intentDigitalHubResponse } =
      await IntentDigitalHubCommandStarter.startIntent(
        IntentDigitalHubModule.SCANNER
      );
    const result = JSON.parse(intentDigitalHubResponse)[0].resultado;
    console.log(result);
    setBarCode(result[1]);
    setBarCodeType(result[3]);
  };

  const handleLimparCampos = () => {
    setBarCode("");
    setBarCodeType("");
  };

  return (
    <IonPage className="ion-padding-horizontal">
      <IonContent fullscreen>
        <Header title="CÓDIGO DE BARRAS" />

        <IonGrid style={{ marginTop: 80, marginBottom: 60, flex: 1 }}>
          <IonRow
            className={[styles.resultContainer, styles.bordered].join(" ")}
          >
            <IonCol size="12">
              <IonLabel className="ion-no-margin">COD.</IonLabel>
              <IonInput
                value={barCode}
                disabled
                className={styles.bordered}
                type="text"
                id="barcode"
              ></IonInput>
            </IonCol>
            <IonCol size="12">
              <IonLabel className="ion-no-margin">TYPE</IonLabel>
              <IonInput
                value={barCodeType}
                disabled
                className={styles.bordered}
                type="text"
                id="barcodetype"
              ></IonInput>
            </IonCol>
          </IonRow>

          <IonRow>
            <IonCol size="12">
              <IonButton
                onClick={handleIniciarLeitura}
                expand="block"
                color="primary"
                mode="ios"
              >
                INICIAR LEITURA
              </IonButton>
            </IonCol>

            <IonCol size="12">
              <IonButton
                onClick={handleLimparCampos}
                expand="block"
                color="primary"
                mode="ios"
              >
                LIMPAR CAMPO
              </IonButton>
            </IonCol>
          </IonRow>
        </IonGrid>

      </IonContent>
      <Footer />
    </IonPage>
  );
};

export default BarcodeReader;
