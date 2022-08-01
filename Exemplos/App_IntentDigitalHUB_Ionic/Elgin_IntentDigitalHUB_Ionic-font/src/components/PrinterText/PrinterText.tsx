import { useEffect, useState } from "react";
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
} from "@ionic/react";
import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { AvancaPapel } from "../../intentDigitalHubService/TERMICA/Commands/AvancaPapel";
import { Corte } from "../../intentDigitalHubService/TERMICA/Commands/Corte";
import { ImprimeXMLNFCe } from "../../intentDigitalHubService/TERMICA/Commands/ImprimeXMLNFCe";
import { TermicaCommand } from "../../intentDigitalHubService/TERMICA/TermicaCommand";
import styles from "./index.module.css";
import { ImpressaoTexto } from "../../intentDigitalHubService/TERMICA/Commands/ImpressaoTexto";
import { ImprimeXMLSAT } from "../../intentDigitalHubService/TERMICA/Commands/ImprimeXMLSAT";
import XmlFile from "../../XmlStorageService/XmlFile";
import { App } from "@capacitor/app";

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
  const [appPackageName, setAppPackageName] = useState<string>("");

  const [presentAlert] = useIonAlert();

  useEffect(() => {
    App.getInfo().then((info) => {
      setAppPackageName(info.id);
    });
  });

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
    {
      id: "CUT-PAPER",
      textButton: "CUT PAPER",
      value: isCutPaperActive,
      setValue: (value: boolean) => setIsCutPaperActive(value),
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
    const fileName = XmlFile.XML_SAT.getXmlFileName() + ".xml";
    //REALIZA A LIMPEZA DO URI PADRÃO REMOVENDO A PARTE INICIAL
    const finalPathIDH =
      "path=/Android/data/" + appPackageName + "/files/" + fileName;

    const command = new ImprimeXMLSAT(finalPathIDH, 0);
    const commands: TermicaCommand[] = [command];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    await IntentDigitalHubCommandStarter.startCommands(commands);
  }

  async function doPrinterXmlNFCe() {
    const fileName = XmlFile.XML_NFCE.getXmlFileName() + ".xml";
    //REALIZA A LIMPEZA DO URI PADRÃO REMOVENDO A PARTE INICIAL
    const finalPathIDH =
      "path=/Android/data/" + appPackageName + "/files/" + fileName;

    const command = new ImprimeXMLNFCe(
      finalPathIDH,
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
    <IonGrid className="ion-padding-horizontal">
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

        <IonRow>
          <IonCol size="auto">
            <h6>MENSAGEM: </h6>
          </IonCol>
          <IonCol>
            <IonInput
              value={text}
              onIonChange={(e) => setText(e.detail.value!)}
              placeholder={"Insira sua mensagem aqui"}
              style={{
                borderColor: "black",
                borderBottom: "1px solid black",
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
                <IonLabel style={{ marginLeft: "5px" }}>{item.label}</IonLabel>
              </div>
            ))}
          </div>
        </IonRadioGroup>

        <h6>ESTILIZAÇÃO:</h6>

        <IonRow className="ion-justify-content-center ion-align-items-center">
          <IonCol>
            <h6 style={{ margin: 0 }}>FONT FAMILY:</h6>
          </IonCol>

          <IonCol>
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

          <IonCol>
            <h6 style={{ margin: 0 }}>FONT SIZE:</h6>
          </IonCol>

          <IonCol>
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
          style={{
            display: "flex",
            justifyContent: "space-between",
            margin: "0px 0px 10px 10px",
          }}
        >
          {checkBoxType.map((item, index) => (
            <div key={item.id}>
              <IonCheckbox
                checked={item.value}
                onIonChange={(e) => item.setValue(e.detail.checked)}
              ></IonCheckbox>
              <IonLabel style={{ marginLeft: "5px" }}>
                {item.textButton}
              </IonLabel>
            </div>
          ))}
        </div>

        <div>
          <IonButton
            onClick={doPrinterText}
            className={styles.button}
            color="primary"
            mode="ios"
          >
            IMPRIMIR TEXTO
          </IonButton>
        </div>

        <IonRow>
          <IonCol>
            <IonButton
              onClick={doPrinterXmlNFCe}
              className={styles.button}
              color="primary"
              mode="ios"
            >
              NFCE
            </IonButton>
          </IonCol>
          <IonCol>
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
  );
};

export default PrinterText;
