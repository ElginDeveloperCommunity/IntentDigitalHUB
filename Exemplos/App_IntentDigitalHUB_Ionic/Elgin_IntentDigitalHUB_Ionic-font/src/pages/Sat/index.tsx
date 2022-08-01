import { useEffect, useState } from "react";
import {
  IonButton,
  IonCol,
  IonContent,
  IonGrid,
  IonItem,
  IonLabel,
  IonPage,
  IonRadio,
  IonRadioGroup,
  IonRow,
  IonTextarea,
  IonInput,
  useIonAlert,
} from "@ionic/react";
import { App } from "@capacitor/app";

import Footer from "../../components/Footer";
import Header from "../../components/Header";

import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { AssociarAssinatura } from "../../intentDigitalHubService/SAT/Commands/AssociarAssinatura";
import { AtivarSAT } from "../../intentDigitalHubService/SAT/Commands/AtivarSAT";
import { CancelarUltimaVenda } from "../../intentDigitalHubService/SAT/Commands/CancelarUltimaVenda";
import { ConsultarSAT } from "../../intentDigitalHubService/SAT/Commands/ConsultarSAT";
import { ConsultarStatusOperacional } from "../../intentDigitalHubService/SAT/Commands/ConsultarStatusOperacional";
import { EnviarDadosVenda } from "../../intentDigitalHubService/SAT/Commands/EnviarDadosVenda";
import { ExtrairLogs } from "../../intentDigitalHubService/SAT/Commands/ExtrairLogs";
import XmlFile from "../../XmlStorageService/XmlFile";

import styles from "./index.module.css";

const satOptionsRadioButton = [
  {
    label: "SMART SAT",
    value: "SMART SAT",
  },
  {
    label: "SATGO",
    value: "SATGO",
  },
];

const Sat: React.FC = () => {
  const [cfeCancelamento, setCFeCancelamento] = useState("");
  const [textReturn, setTextReturn] = useState("");
  const [selectedOptionSat, setSelectedOptionSat] = useState("SMART SAT");
  const [activationCode, setActivationCode] = useState("123456789");

  const [appPackageName, setAppPackageName] = useState<string>("");

  const [presentAlert] = useIonAlert();

  useEffect(() => {
    App.getInfo().then((info) => {
      setAppPackageName(info.id);
    });
  }, []);

  const buttons = [
    {
      textButton: "CONSULTAR SAT",
      onClick: () => sendConsultarSat(),
      colSize: "6",
    },
    {
      textButton: "CANCELAMENTO",
      onClick: () => cancelarVendaSat(),
      colSize: "6",
    },
    {
      textButton: "STATUS OPERACIONAL",
      onClick: () => sendStatusOperacional(),
      colSize: "6",
    },
    { textButton: "ATIVAR", onClick: () => sendAtivarSat(), colSize: "6" },
    {
      textButton: "REALIZAR VENDA",
      onClick: () => enviarDadosVendaSat(),
      colSize: "6",
    },
    { textButton: "ASSOCIAR", onClick: () => sendAssociarSat(), colSize: "6" },
    { textButton: "EXTRAIR LOGS", onClick: () => extrairLog(), colSize: "12" },
  ];

  async function sendAtivarSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new AtivarSAT(
      Number(numSessao),
      2,
      activationCode,
      "14200166000166",
      15
    );
    const objIDH = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(objIDH.intentDigitalHubResponse)[0].resultado;
    setTextReturn(result);
  }

  async function sendAssociarSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new AssociarAssinatura(
      Number(numSessao),
      activationCode,
      "16716114000172",
      "SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT"
    );
    const objIDH = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(objIDH.intentDigitalHubResponse)[0].resultado;
    setTextReturn(result);
  }

  async function sendConsultarSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new ConsultarSAT(Number(numSessao));
    const objIDH = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(objIDH.intentDigitalHubResponse)[0].resultado;
    setTextReturn(result);
  }

  async function sendStatusOperacional() {
    const numSessao = Math.floor(Math.random() * 999999).toString();

    const command = new ConsultarStatusOperacional(
      Number(numSessao),
      activationCode
    );
    const objIDH = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(objIDH.intentDigitalHubResponse)[0].resultado;
    setTextReturn(result);
  }

  async function enviarDadosVendaSat() {
    const numSessao = Math.floor(Math.random() * 999999).toString();
    setCFeCancelamento("");
    let fileName = "";
    if (selectedOptionSat === "SMART SAT") {
      fileName = XmlFile.SAT_ENVIAR_DADOS_VENDA.getXmlFileName() + ".xml";
    } else {
      fileName = XmlFile.SAT_GO_ENVIAR_DADOS_VENDA.getXmlFileName() + ".xml";
    }
    const path = "/Android/data/" + appPackageName + "/files/" + fileName;

    const command = new EnviarDadosVenda(
      Number(numSessao),
      activationCode,
      `path=${path}`
    );
    const objIDH = await IntentDigitalHubCommandStarter.startCommand(command);
    const result: string = JSON.parse(objIDH.intentDigitalHubResponse)[0]
      .resultado;

    //TRATAMENTO PARA PEGAR O CÓDIGO CFE ATUAL PARA CANCELAMENTO
    if (result.includes("|")) {
      const listOfReturn = result.split("|");
      const newReturn = listOfReturn.find((value) => value.includes("CFe"));
      if (newReturn) {
        setCFeCancelamento(newReturn);
      }
    }
    setTextReturn(result);
  }

  async function generateXmlForSatCancellation() {
    let xmlCancel = await XmlFile.SAT_CANCELAMENTO.getXmlContentInString();
    xmlCancel = xmlCancel.replace("novoCFe", cfeCancelamento);
    return xmlCancel;
  }

  async function cancelarVendaSat() {
    var numSessao = Math.floor(Math.random() * 999999).toString();
    const xmlCancel = await generateXmlForSatCancellation();

    if (!cfeCancelamento) {
      presentAlert("Não foi feita uma venda para cancelar!");
    }

    const command = new CancelarUltimaVenda(
      Number(numSessao),
      activationCode,
      cfeCancelamento,
      xmlCancel
    );
    const objIDH = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(objIDH.intentDigitalHubResponse)[0].resultado;
    setTextReturn(result);
  }

  async function extrairLog() {
    const numSessao = Math.floor(Math.random() * 999999);

    const command = new ExtrairLogs(numSessao, activationCode);
    const objIDH = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(objIDH.intentDigitalHubResponse)[0].resultado;

    if (result === "DeviceNotFound") {
      setTextReturn(result);
    } else {
      setTextReturn("O log do SAT está salvo em:" + result);
    }
  }

  return (
    <IonPage className="ion-padding-horizontal">
      <IonContent fullscreen>
        <Header title="SAT" />
        <IonGrid>
          <IonRow style={{ display: "flex", width: "100%" }}>
            <IonCol className={styles.customBox}>
              <IonTextarea
                disabled
                rows={18}
                style={{ "text-align": "left", "max-height": "100%" }}
                value={textReturn}
              >
                <IonLabel>Retorno:</IonLabel>
              </IonTextarea>
            </IonCol>

            <IonCol className={styles.buttonsColumn}>
              <div style={{ marginTop: "30px" }}>
                <IonRadioGroup
                  allow-empty-selectIon="false"
                  name="radio-group"
                  value={selectedOptionSat}
                  onIonChange={(e) => setSelectedOptionSat(e.detail.value)}
                >
                  <IonRow>
                    {satOptionsRadioButton.map((option) => (
                      <IonCol key={option.value} offset="1">
                        <IonRadio
                          className={styles.radio}
                          value={option.value}
                        ></IonRadio>
                        <IonLabel style={{ marginLeft: "5px" }}>
                          {option.label}
                        </IonLabel>
                      </IonCol>
                    ))}
                  </IonRow>
                </IonRadioGroup>
              </div>

              <IonRow
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  marginTop: "20px",
                }}
              >
                <IonCol size="auto">
                  <h1 style={{ fontSize: "medium" }}>Código de Ativação:</h1>
                </IonCol>
                <IonCol>
                  <IonItem>
                    <IonInput
                      placeholder="123456789"
                      style={{ alignSelf: "flex-end" }}
                      type="text"
                      value={activationCode}
                      onIonChange={(e) => setActivationCode(e.detail.value!)}
                    ></IonInput>
                  </IonItem>
                </IonCol>
              </IonRow>

              <IonRow>
                {buttons.map(({ textButton, onClick, colSize }, index) => (
                  <IonCol size={colSize} className="ion-no-padding">
                    <IonButton
                      expand="block"
                      size="default"
                      color="primary"
                      mode="ios"
                      key={index}
                      onClick={onClick}
                    >
                      {textButton}
                    </IonButton>
                  </IonCol>
                ))}
              </IonRow>
            </IonCol>
          </IonRow>
        </IonGrid>
        <Footer />
      </IonContent>
    </IonPage>
  );
};

export default Sat;
