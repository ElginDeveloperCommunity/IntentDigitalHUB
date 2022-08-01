import { useState } from "react";
import {
  IonButton,
  IonCol,
  IonContent,
  IonGrid,
  IonLabel,
  IonPage,
  IonRadio,
  IonRadioGroup,
  IonRow,
  IonTextarea,
  IonSelect,
  IonSelectOption,
} from "@ionic/react";
import { Toast } from "@capacitor/toast";

import Footer from "../../components/Footer";
import Header from "../../components/Header";

import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";

import { ConfigurarModeloBalanca } from "../../intentDigitalHubService/BALANCA/Commands/ConfigurarModeloBalanca";
import { ConfigurarProtocoloComunicacao } from "../../intentDigitalHubService/BALANCA/Commands/ConfigurarProtocoloComunicacao";
import { AbrirSerial } from "../../intentDigitalHubService/BALANCA/Commands/AbrirSerial";
import { LerPeso } from "../../intentDigitalHubService/BALANCA/Commands/LerPeso";
import { Fechar } from "../../intentDigitalHubService/BALANCA/Commands/Fechar";

import styles from "./index.module.css";

const modelOptionsData = [
  {
    label: "DP3005",
    value: "0",
  },
  {
    label: "SA110",
    value: "1",
  },
  {
    label: "DPSC",
    value: "2",
  },
  {
    label: "DP30CK",
    value: "3",
  },
];

const protocolOptionsData = [
  {
    label: "PROTOCOL 0",
    value: "0",
  },
  {
    label: "PROTOCOL 1",
    value: "1",
  },
  {
    label: "PROTOCOL 2",
    value: "2",
  },
  {
    label: "PROTOCOL 3",
    value: "3",
  },
  {
    label: "PROTOCOL 4",
    value: "4",
  },
  {
    label: "PROTOCOL 5",
    value: "5",
  },
  {
    label: "PROTOCOL 6",
    value: "6",
  },
  {
    label: "PROTOCOL 7",
    value: "7",
  },
];

const Balanca: React.FC = () => {
  const [weigthValue, setWeigthValue] = useState("00.00");
  const [selectedModel, setSelectaedModel] = useState("0");
  const [selectedProtocol, setSelectedProtocol] = useState("0");

  async function sendConfigBalanca() {
    const commands = [
      new ConfigurarModeloBalanca(Number(selectedModel)),
      new ConfigurarProtocoloComunicacao(Number(selectedProtocol)),
    ];
    const objIDH = await IntentDigitalHubCommandStarter.startCommands(commands);
    const configurarModeloBalancaResult = JSON.parse(
      objIDH.intentDigitalHubResponse
    )[0].resultado;
    const configurarProtocoloComunicacao = JSON.parse(
      objIDH.intentDigitalHubResponse
    )[1].resultado;

    Toast.show({
      text: `ConfigurarModeloBalanca: ${configurarModeloBalancaResult}\nConfigurarProtocoloComunicacao: ${configurarProtocoloComunicacao}`,
      duration: "long",
      position: "bottom",
    });
  }

  async function sendLerPeso() {
    const commands = [
      new AbrirSerial(2400, 8, "N", 1),
      new LerPeso(1),
      new Fechar(),
    ];
    const objIDH = await IntentDigitalHubCommandStarter.startCommands(commands);
    const abrirSerialResult = JSON.parse(objIDH.intentDigitalHubResponse)[0]
      .resultado;
    const lerPesoReturn = JSON.parse(objIDH.intentDigitalHubResponse)[1]
      .resultado;
    const fecharReturn = JSON.parse(objIDH.intentDigitalHubResponse)[2]
      .resultado;
    Toast.show({
      text: `AbrirSerial: ${abrirSerialResult}\nLerPeso: ${lerPesoReturn}\nFechar: ${fecharReturn}`,
      duration: "long",
      position: "bottom",
    });

    if (lerPesoReturn > 0.0) {
      setWeigthValue(String(lerPesoReturn / 1000));
    }
  }
  return (
    <IonPage className="ion-padding-horizontal">
      <IonContent fullscreen>
        <Header title="Balança" />

        <IonGrid style={{marginTop: 80, marginBottom: 60}}>
          <IonRow>
            <IonCol>
              <h4 className="ion-no-margin">VALOR BALANÇA:</h4>
            </IonCol>
            <IonCol>
              <IonTextarea
                disabled
                rows={1}
                placeholder="00.00"
                value={weigthValue}
                onIonChange={(e) => setWeigthValue(e.detail.value!)}
                style={{
                  height: "min-content",
                  fontSize: "x-large",
                }}
              ></IonTextarea>
            </IonCol>
          </IonRow>

          <IonRow>
            <IonCol>
              <h3 style={{ margin: "0px", fontSize: "large" }}>MODELOS:</h3>
            </IonCol>
          </IonRow>

          <IonRow>
            <IonCol>
              <IonRadioGroup
                allow-empty-selection="false"
                name="radio-group"
                value={selectedModel}	
                onIonChange={(e) => setSelectaedModel(e.detail.value)}
                className="ion-no-margin"
              >
                <IonRow class="ion-justify-content-around">
                  {modelOptionsData.map((item, index) => (
                    <IonCol size="auto" key={index}>
                      <IonRadio className={styles.radio} value={item.value} />
                      <IonLabel style={{ "margin-left": "5px" }}>
                        {item.label}
                      </IonLabel>
                    </IonCol>
                  ))}
                </IonRow>
              </IonRadioGroup>
            </IonCol>
          </IonRow>

          <IonRow className="ion-align-items-center">
            <IonCol>
              <h3 style={{ fontSize: "large" }}>PROTOCOLOS</h3>
            </IonCol>
            <IonCol>
              <IonSelect
                mode="ios"
                value={selectedProtocol}
                onIonChange={(e) => setSelectedProtocol(e.detail.value)}
                interface="action-sheet"
              >
                {protocolOptionsData.map((item, index) => (
                  <IonSelectOption key={item.value} value={item.value}>
                    {item.label}
                  </IonSelectOption>
                ))}
              </IonSelect>
            </IonCol>
          </IonRow>

          <IonRow>
            <IonCol>
              <IonButton
                onClick={sendConfigBalanca}
                expand="block"
                color="primary"
                mode="ios"
              >
                CONFIGURAR MODELO BALANÇA
              </IonButton>
            </IonCol>

            <IonCol>
              <IonButton
                onClick={sendLerPeso}
                expand="block"
                color="primary"
                mode="ios"
              >
                LER PESO
              </IonButton>
            </IonCol>
          </IonRow>
        </IonGrid>

        <Footer />
      </IonContent>
    </IonPage>
  );
};

export default Balanca;
