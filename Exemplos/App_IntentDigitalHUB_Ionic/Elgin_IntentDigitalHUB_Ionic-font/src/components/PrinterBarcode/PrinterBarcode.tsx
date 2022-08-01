import { useState } from "react";
import {
  IonButton,
  IonCheckbox,
  IonCol,
  IonGrid,
  IonInput,
  IonLabel,
  IonList,
  IonRadio,
  IonRadioGroup,
  IonRow,
  IonSelect,
  IonSelectOption,
  useIonAlert,
} from "@ionic/react";
import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import styles from "./index.module.css";
import { TermicaCommand } from "../../intentDigitalHubService/TERMICA/TermicaCommand";
import { AvancaPapel } from "../../intentDigitalHubService/TERMICA/Commands/AvancaPapel";
import { Corte } from "../../intentDigitalHubService/TERMICA/Commands/Corte";
import { DefinePosicao } from "../../intentDigitalHubService/TERMICA/Commands/DefinePosicao";
import { ImpressaoQRCode } from "../../intentDigitalHubService/TERMICA/Commands/ImpressaoQRCode";
import { ImpressaoCodigoBarras } from "../../intentDigitalHubService/TERMICA/Commands/ImpressaoCodigoBarras";

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

const barcodeTypes = [
  {
    label: "EAN_8",
    typeValue: 3,
    defaultMessage: "40170725",
  },
  {
    label: "EAN_13",
    typeValue: 2,
    defaultMessage: "0123456789012",
  },
  {
    label: "QR_CODE",
    typeValue: null,
    defaultMessage: "ELGIN DEVELOPERS COMMUNITY",
  },
  {
    label: "UPC_A",
    typeValue: 0,
    defaultMessage: "123601057072",
  },
  {
    label: "CODE_39",
    typeValue: 4,
    defaultMessage: "CODE39",
  },
  {
    label: "ITF",
    typeValue: 5,
    defaultMessage: "05012345678900",
  },
  {
    label: "CODE_BAR",
    typeValue: 6,
    defaultMessage: "A3419500A",
  },
  {
    label: "CODE_93",
    typeValue: 7,
    defaultMessage: "CODE93",
  },
  {
    label: "CODE_128",
    typeValue: 8,
    defaultMessage: "{C1233",
  },
];

const PrinterBarcode: React.FC = () => {
  //Variáveis de entrada
  const [codigo, setCodigo] = useState("40170725");
  const [selectedCodeTypeIdx, setSelectedCodeTypeIdx] = useState(0);
  const [selectedCodeWidth, setSelectedCodeWidth] = useState("1");
  const [selectedHeigthCode, setSelectedHeigthCode] = useState("20");
  const [optionTextAlign, setOptionTextAlign] = useState("0");
  const [isCutPaperActive, setIsCutPaperActive] = useState(false);

  const [presentAlert] = useIonAlert();

  //CHAMADA A FUNÇÃO DE TIPO DE BARCODE ESCOLHIDO - DEFAULT E QR CODE
  function doAllTypesOfBarCodes() {
    if (codigo === "") {
      presentAlert("Campo código vazio!");
    } else {
      if (barcodeTypes[selectedCodeTypeIdx].label === "QR_CODE") {
        doPrinterQrCode();
      } else {
        doPrinterBarCodeDefault();
      }
    }
  }

  async function doPrinterBarCodeDefault() {
    const HRI = 4;
    const mainCommand = new ImpressaoCodigoBarras(
      barcodeTypes[selectedCodeTypeIdx].typeValue!,
      codigo,
      Number(selectedHeigthCode),
      Number(selectedCodeWidth),
      HRI
    );

    const posicaoCommand = new DefinePosicao(Number(optionTextAlign));
    const commands: TermicaCommand[] = [posicaoCommand, mainCommand];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    await IntentDigitalHubCommandStarter.startCommands(commands);
  }

  async function doPrinterQrCode() {
    const mainCommand = new ImpressaoQRCode(
      codigo,
      Number(selectedCodeWidth),
      2
    );

    const posicaoCommand = new DefinePosicao(Number(optionTextAlign));
    const commands: TermicaCommand[] = [posicaoCommand, mainCommand];
    commands.push(new AvancaPapel(10));
    if (isCutPaperActive) {
      commands.push(new Corte(0));
    }
    await IntentDigitalHubCommandStarter.startCommands(commands);
  }

  return (
    <IonGrid className="ion-padding-horizontal">
      <div>
        <h6
          style={{
            textAlign: "center",
            fontSize: "large",
            marginTop: 0,
          }}
        >
          IMPRESSÃO DE CÓDIGO DE BARRAS
        </h6>

        <IonRow>
          <IonCol size="auto">
            <h6 className="ion-no-margin">CÓDIGO: </h6>
          </IonCol>
          <IonCol>
            <IonInput
              style={{
                borderColor: "black",
                borderBottom: "1px solid black",
                height: 20,
              }}
              type="text"
              id="codigo"
              value={codigo}
              onIonChange={(e) => setCodigo(e.detail.value!)}
            ></IonInput>
          </IonCol>
        </IonRow>
      </div>

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
        }}
        className="ion-no-margin"
      >
        <div style={{ display: "flex" }}>
          <h6>TIPO DE CÓDIGO DE BARRAS:</h6>
        </div>

        <div>
          <IonList>
            <IonSelect
              mode="ios"
              cancelText="Cancelar"
              interface="popover"
              value={selectedCodeTypeIdx}
              onIonChange={(e) => {
                setSelectedCodeTypeIdx(e.detail.value);
                setCodigo(barcodeTypes[e.detail.value].defaultMessage);
              }}
            >
              {barcodeTypes.map((option, index) => (
                <IonSelectOption key={option.label} value={index}>
                  {option.label}
                </IonSelectOption>
              ))}
            </IonSelect>
          </IonList>
        </div>
      </div>

      <h6 style={{ marginTop: 0 }}>ALINHAMENTO</h6>

      <IonRadioGroup
        className="ion-no-margin"
        allow-empty-selection="false"
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
              <IonRadio className={styles.radio} value={item.value}></IonRadio>
              <IonLabel style={{ marginLeft: "5px" }}>{item.label}</IonLabel>
            </div>
          ))}
        </div>
      </IonRadioGroup>

      <h6>ESTILIZAÇÃO</h6>

      <IonRow
        style={{
          display: "flex",
          margin: "0px 0px 0px 10px",
        }}
        className="ion-align-items-center"
      >
        <IonCol size="2">
          <h6>
            {barcodeTypes[selectedCodeTypeIdx].label === "QR_CODE"
              ? "SQUARE:"
              : "WIDTH:"}
          </h6>
        </IonCol>

        <IonCol size="2">
          <IonSelect
            mode="ios"
            cancelText="Cancelar"
            interface="popover"
            value={selectedCodeWidth}
            onIonChange={(e) => setSelectedCodeWidth(e.detail.value)}
          >
            <IonSelectOption value="1">1</IonSelectOption>
            <IonSelectOption value="2">2</IonSelectOption>
            <IonSelectOption value="3">3</IonSelectOption>
            <IonSelectOption value="4">4</IonSelectOption>
            <IonSelectOption value="5">5</IonSelectOption>
            <IonSelectOption value="6">6</IonSelectOption>
          </IonSelect>
        </IonCol>

        {barcodeTypes[selectedCodeTypeIdx].label !== "QR_CODE" && (
          <>
            <IonCol size="2">
              <h6>HEIGHT:</h6>
            </IonCol>
            <IonCol size="2">
              <IonSelect
                mode="ios"
                cancelText="Cancelar"
                interface="popover"
                value={selectedHeigthCode}
                onIonChange={(e) => setSelectedHeigthCode(e.detail.value)}
              >
                <IonSelectOption value="20">20</IonSelectOption>
                <IonSelectOption value="60">60</IonSelectOption>
                <IonSelectOption value="120">120</IonSelectOption>
                <IonSelectOption value="200">200</IonSelectOption>
              </IonSelect>
            </IonCol>
          </>
        )}

        <IonCol
          style={{
            display: "flex",
            justifyContent: "end",
          }}
        >
          <IonCheckbox
            checked={isCutPaperActive}
            onIonChange={(e) => setIsCutPaperActive(e.detail.checked)}
          ></IonCheckbox>
          <IonLabel style={{ marginLeft: "5px" }}>CUT PAPER</IonLabel>
        </IonCol>
      </IonRow>

      <IonButton
        onClick={doAllTypesOfBarCodes}
        expand="block"
        color="primary"
        mode="ios"
      >
        IMPRIMIR CÓDIGO DE BARRAS
      </IonButton>
    </IonGrid>
  );
};

export default PrinterBarcode;
