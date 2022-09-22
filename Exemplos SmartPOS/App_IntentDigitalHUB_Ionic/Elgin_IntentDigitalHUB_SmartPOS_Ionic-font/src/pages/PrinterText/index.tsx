import { useState } from "react";
import {
  IonButton,
  IonCheckbox,
  IonGrid,
  IonInput,
  IonRow,
  IonCol,
  IonLabel,
  IonRadio,
  IonRadioGroup,
  IonSelect,
  IonSelectOption,
  useIonAlert,
  IonPage,
  IonContent,
} from "@ionic/react";
import { useLocation } from "react-router";

import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { AvancaPapel } from "../../intentDigitalHubService/TERMICA/Commands/AvancaPapel";
import { Corte } from "../../intentDigitalHubService/TERMICA/Commands/Corte";
import { ImprimeXMLNFCe } from "../../intentDigitalHubService/TERMICA/Commands/ImprimeXMLNFCe";
import { TermicaCommand } from "../../intentDigitalHubService/TERMICA/TermicaCommand";
import styles from "./index.module.css";
import { ImpressaoTexto } from "../../intentDigitalHubService/TERMICA/Commands/ImpressaoTexto";
import { ImprimeXMLSAT } from "../../intentDigitalHubService/TERMICA/Commands/ImprimeXMLSAT";
import XmlFile from "../../XmlStorageService/XmlFile";

import Footer from "../../components/Footer";
import Header from "../../components/Header";

const alignTextOptionData = [
  {
    label: "ESQUERDA",
    value: "0",
  },
  {
    label: "CENTRALIZADO",
    value: "1",
  },
  {
    label: "DIREITA",
    value: "2",
  },
];

const PrinterText: React.FC = () => {
  const [text, setText] = useState("ELGIN DEVELOPER COMMNUNITY");
  const [selectedFontFamily, setSelectedFontFamily] = useState("FONT A");
  const [selectedFontSize, setSelectedFontSize] = useState(17);
  const [optionTextAlign, setOptionTextAlign] = useState("0");

  const [isBold, setIsBold] = useState(false);
  const [isUnderline, setIsUnderline] = useState(false);
  const [isCutPaperActive, setIsCutPaperActive] = useState(false);

  const [presentAlert] = useIonAlert();

  // Checando o tipo de conexão selecionada
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const showCutPaper = params.get("connectionType") === "ip";

  const checkBoxType = [
    {
      id: "NEGRITO",
      textButton: "NEGRITO",
      value: isBold,
      setValue: (value: boolean) => setIsBold(value),
    },
    {
      id: "SUBLINHADO",
      textButton: "SUBLINHADO",
      value: isUnderline,
      setValue: (value: boolean) => setIsUnderline(value),
    },
  ];

  function getStiloValue() {
    let stilo = 0;

    if (selectedFontFamily === "FONT B") {
      stilo += 1;
    }
    if (isUnderline) {
      stilo += 2;
    }
    if (isBold) {
      stilo += 8;
    }
    return stilo;
  }

  async function doPrinterText() {
    if (text === "") {
      presentAlert("Campo mensagem vazio!");
    } else {
      const stilo = getStiloValue();
      const command = new ImpressaoTexto(
        text,
        Number(optionTextAlign),
        stilo,
        Number(selectedFontSize)
      );
      const commands: TermicaCommand[] = [command];
      commands.push(new AvancaPapel(10));
      if (isCutPaperActive) {
        commands.push(new Corte(0));
      }
      await IntentDigitalHubCommandStarter.startCommands(commands);
    }
  }

  async function doPrinterXmlSAT() {
    const content = await XmlFile.XML_SAT.getXmlContentInString();
    const command = new ImprimeXMLSAT(content, 0);
    const commands: TermicaCommand[] = [command];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    await IntentDigitalHubCommandStarter.startCommands(commands);
  }

  async function doPrinterXmlNFCe() {
    const content = await XmlFile.XML_NFCE.getXmlContentInString();
    const command = new ImprimeXMLNFCe(
      content,
      1,
      "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES",
      0
    );
    const commands: TermicaCommand[] = [command];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    await IntentDigitalHubCommandStarter.startCommands(commands);
  }

  return (
    <IonPage className="ion-padding-horizontal">
      <IonContent fullscreen>
        <Header title="IMPRESSORA" />
        <IonGrid>
          <div style={{ textAlign: "start" }} className="ion-no-margin">
            <h6
              style={{
                textAlign: "center",
                fontSize: "large",
              }}
              className="ion-no-margin"
            >
              IMPRESSÃO DE TEXTO
            </h6>

            <IonRow className="ion-justify-content-center ion-align-items-center ion-margin-vertical">
              <IonCol size="auto" className="ion-no-padding">
                <h6 className="ion-no-margin" style={{ fontSize: 13 }}>
                  MENSAGEM:{" "}
                </h6>
              </IonCol>
              <IonCol>
                <IonInput
                  value={text}
                  onIonChange={(e) => setText(e.detail.value!)}
                  placeholder={"Insira sua mensagem aqui"}
                  className="ion-no-margin"
                  style={{
                    borderColor: "black",
                    borderBottom: "1px solid black",
                    fontSize: 13,
                  }}
                  type="text"
                  id="mensagem"
                ></IonInput>
              </IonCol>
            </IonRow>

            <h6 style={{ marginTop: 0 }}>ALINHAMENTO</h6>

            <IonRadioGroup
              allow-empty-selectIon="false"
              name="alinhamento"
              value={optionTextAlign}
              onIonChange={(e) => setOptionTextAlign(e.detail.value)}
              className="ion-margin-vertical"
            >
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-around",
                  alignItems: "center",
                }}
              >
                {alignTextOptionData.map((item, index) => (
                  <div key={index}>
                    <IonRadio
                      className={styles.radio}
                      value={item.value}
                    ></IonRadio>
                    <IonLabel style={{ marginLeft: "5px", fontSize: 14 }}>
                      {item.label}
                    </IonLabel>
                  </div>
                ))}
              </div>
            </IonRadioGroup>

            <h6>ESTILIZAÇÃO:</h6>

            <IonRow className="ion-justify-content-center ion-align-items-center">
              <IonCol size="6" className="ion-no-padding">
                <h6 style={{ margin: 0 }}>FONT FAMILY:</h6>
              </IonCol>

              <IonCol size="6" className="ion-no-padding">
                <IonSelect
                  mode="ios"
                  value={selectedFontFamily}
                  cancelText="Cancelar"
                  interface="popover"
                  onIonChange={(e) => setSelectedFontFamily(e.detail.value)}
                >
                  <IonSelectOption value="FONT A">Fonte A</IonSelectOption>
                  <IonSelectOption value="FONT B">Fonte B</IonSelectOption>
                </IonSelect>
              </IonCol>

              <IonCol size="6" className="ion-no-padding">
                <h6 style={{ margin: 0 }}>FONT SIZE:</h6>
              </IonCol>

              <IonCol size="6" className="ion-no-padding">
                <IonSelect
                  mode="ios"
                  value={selectedFontSize}
                  cancelText="Cancelar"
                  interface="popover"
                  onIonChange={(e) => setSelectedFontSize(e.detail.value)}
                >
                  <IonSelectOption value="17">17</IonSelectOption>
                  <IonSelectOption value="34">34</IonSelectOption>
                  <IonSelectOption value="51">51</IonSelectOption>
                  <IonSelectOption value="68">68</IonSelectOption>
                </IonSelect>
              </IonCol>
            </IonRow>

            <div
              className="ion-margin-top"
              style={{
                display: "flex",
                alignItems: "center",
              }}
            >
              {checkBoxType.map((item, index) => (
                <div
                  key={item.id}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    marginRight: 5,
                  }}
                >
                  <IonCheckbox
                    checked={item.value}
                    onIonChange={(e) => item.setValue(e.detail.checked)}
                  ></IonCheckbox>
                  <IonLabel style={{ marginLeft: "5px", fontSize: 14 }}>
                    {item.textButton}
                  </IonLabel>
                </div>
              ))}

              {showCutPaper && (
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    marginRight: 5,
                  }}
                >
                  <IonCheckbox
                    checked={isCutPaperActive}
                    onIonChange={(e) => setIsCutPaperActive(e.detail.checked)}
                  ></IonCheckbox>
                  <IonLabel style={{ marginLeft: "5px", fontSize: 14 }}>
                    CUT PAPER
                  </IonLabel>
                </div>
              )}
            </div>
            <IonRow className="ion-margin-top">
              <IonCol className="ion-no-padding" size="12">
                <IonButton
                  onClick={doPrinterText}
                  className={styles.button}
                  color="primary"
                  mode="ios"
                >
                  IMPRIMIR TEXTO
                </IonButton>
              </IonCol>
              <IonCol className="ion-no-padding" size="6">
                <IonButton
                  onClick={doPrinterXmlNFCe}
                  className={styles.button}
                  color="primary"
                  mode="ios"
                >
                  NFCE
                </IonButton>
              </IonCol>
              <IonCol className="ion-no-padding" size="6">
                <IonButton
                  onClick={doPrinterXmlSAT}
                  className={styles.button}
                  color="primary"
                  mode="ios"
                >
                  SAT
                </IonButton>
              </IonCol>
            </IonRow>
          </div>
        </IonGrid>
      </IonContent>
      <Footer />
    </IonPage>
  );
};

export default PrinterText;
