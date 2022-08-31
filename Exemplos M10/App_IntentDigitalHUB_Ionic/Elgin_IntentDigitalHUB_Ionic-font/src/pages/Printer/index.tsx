import { useEffect, useState } from "react";
import {
  IonRow,
  IonCol,
  IonButton,
  IonContent,
  IonGrid,
  IonImg,
  IonInput,
  IonLabel,
  IonPage,
  IonRadio,
  IonRadioGroup,
  useIonAlert,
} from "@ionic/react";
import Footer from "../../components/Footer";
import Header from "../../components/Header";
import PrinterBarcode from "../../components/PrinterBarcode/PrinterBarcode";
import PrinterImage from "../../components/PrinterImage/PrinterImage";
import PrinterText from "../../components/PrinterText/PrinterText";
import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { AbreConexaoImpressora } from "../../intentDigitalHubService/TERMICA/Commands/AbreConexaoImpressora";
import { AbreGavetaElgin } from "../../intentDigitalHubService/TERMICA/Commands/AbreGavetaElgin";
import { StatusImpressora } from "../../intentDigitalHubService/TERMICA/Commands/StatusImpressora";
import styles from "./index.module.css";

const Printer: React.FC = () => {
  const [typePrinter, setTypePrinter] = useState("TEXT");

  const [selectedConnection, setSelectedConnection] = useState("interna");

  const [selectedPrinterIp, setSelectedPrinterIp] = useState("");
  const [selectedPrinterUsb, setSelectedPrinterUsb] = useState("");

  const [ipConection, setIpConection] = useState("192.168.0.31:9100");

  const [presentAlert] = useIonAlert();

  const buttonsPrinter = [
    {
      id: "TEXT",
      icon: require("../../icons/printer_text.png"),
      textButton: "IMPRESSÃO\nDE TEXTO",
      onPress: () => setTypePrinter("TEXT"),
    },
    {
      id: "BARCODE",
      icon: require("../../icons/printer_bar_code.png"),
      textButton: "IMPRESSÃO DE\nCÓDIGO DE BARRAS",
      onPress: () => setTypePrinter("BARCODE"),
    },
    {
      id: "IMAGE",
      icon: require("../../icons/printer_image.png"),
      textButton: "IMPRESSÃO\nDE IMAGEM",
      onPress: () => setTypePrinter("IMAGE"),
    },
  ];

  useEffect(() => {
    startConnectPrinterIntern();
  }, []);

  const typePrinterAtual = () => {
    switch (typePrinter) {
      case "TEXT":
        return <PrinterText />;
      case "BARCODE":
        return <PrinterBarcode />;
      case "IMAGE":
        return <PrinterImage />;
    }
  };

  async function actualStatusPrinter() {
    const command = new StatusImpressora(3);
    const idhObj = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(idhObj.intentDigitalHubResponse)[0].resultado;
    if (result === 5) {
      presentAlert("Papel está presente e não está próximo!");
    } else if (result === 6) {
      presentAlert("Papel está próximo do fim!");
    } else if (result === 7) {
      presentAlert("Papel ausente!");
    } else {
      presentAlert("Status Desconhecido");
    }
  }

  async function actualStatusGaveta() {
    const command = new StatusImpressora(1);
    const idhObj = await IntentDigitalHubCommandStarter.startCommand(command);
    const result = JSON.parse(idhObj.intentDigitalHubResponse)[0].resultado;
    if (result === 1) {
      presentAlert("Gaveta aberta!");
    } else if (result === 2) {
      presentAlert("Gaveta fechada!");
    } else {
      presentAlert("Status Desconhecido");
    }
  }

  function sendAbrirGaveta() {
    const command = new AbreGavetaElgin();
    IntentDigitalHubCommandStarter.startCommand(command);
  }

  async function changePrinterChoose(value: string) {
    if (value === "usb") {
      await presentAlert({
        header: "Impressora Externa USB",
        message: "Escolha o modelo da impressora que deseja utilziar",
        buttons: [
          {
            text: "OK",
            role: "confirm",
            handler: (option) => {
              startConnectPrinterUsb(option);
            },
          },
        ],
        backdropDismiss: false,
        inputs: [
          {
            label: "I8",
            type: "radio",
            value: "i8",
          },
          {
            label: "I9",
            type: "radio",
            value: "i9",
          },
        ],
      });
    } else if (value === "ip") {
      await presentAlert({
        header: "Impressora Externa IP",
        message: "Escolha o modelo da impressora que deseja utilziar",
        buttons: [
          {
            text: "OK",
            role: "confirm",
            handler: (option) => {
              startConnectPrinterIP(option);
            },
          },
        ],
        backdropDismiss: false,
        inputs: [
          {
            label: "I8",
            type: "radio",
            value: "i8",
          },
          {
            label: "I9",
            type: "radio",
            value: "i9",
          },
        ],
      });
    } else {
      startConnectPrinterIntern();
    }
  }

  function startConnectPrinterIntern() {
    setSelectedConnection("interna");
    const command = new AbreConexaoImpressora(6, "M8", "", 0);
    IntentDigitalHubCommandStarter.startCommand(command);
  }

  function startConnectPrinterIP(model: string) {
    if (ipConection !== "") {
      var ip = ipConection.split(":")[0];
      var port = ipConection.split(":")[1];
      console.log(ip, port);

      if (isIpAdressValid()) {
        const command = new AbreConexaoImpressora(3, model, ip, Number(port));
        IntentDigitalHubCommandStarter.startCommand(command);
        setSelectedConnection("ip");
        setSelectedPrinterIp(model);
      } else {
        presentAlert("Digíte um endereço e porta IP válido!");
      }
    } else {
      presentAlert("Digíte um endereço e porta IP válido!");
    }
  }

  function startConnectPrinterUsb(model: string) {
    setSelectedConnection("usb");
    setSelectedPrinterUsb(model);
    const command = new AbreConexaoImpressora(1, model, "USB", 0);
    IntentDigitalHubCommandStarter.startCommand(command);
  }

  function isIpAdressValid() {
    let ipValid = false;

    if (
      /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?[0-9][0-9])$/.test(
        ipConection
      )
    ) {
      ipValid = true;
      return ipValid;
    } else {
      ipValid = false;
      return ipValid;
    }
  }
  return (
    <IonPage className="ion-padding-horizontal">
      <IonContent fullscreen>
        <Header title="Impressora" />
        <IonGrid className="ion-no-padding">
          <IonRow>
            <IonCol size="auto" className="ion-no-padding">
              {buttonsPrinter.map((button) => (
                <div className={styles.box} key={button.id}>
                  <button
                    className={styles.menuButton}
                    onClick={button.onPress}
                    type="button"
                  >
                    <IonImg
                      className={styles.printerOptionImg}
                      src={button.icon}
                    />
                    <h4 className="ion-no-margin">{button.textButton}</h4>
                  </button>
                </div>
              ))}
              <div className={styles.box}>
                <button
                  className={styles.menuButtonSmall}
                  onClick={actualStatusPrinter}
                  type="button"
                >
                  <IonImg
                    className={styles.printerOptionImg}
                    src={require("../../icons/status.png")}
                  />
                  <h4 style={{ marginLeft: "2px" }}>STATUS IMPRESSORA</h4>
                </button>
              </div>
              <div className={styles.box}>
                <button
                  onClick={actualStatusGaveta}
                  className={styles.menuButtonSmall}
                  type="button"
                >
                  <IonImg
                    className={styles.printerOptionImg}
                    src={require("../../icons/status.png")}
                  />
                  <h4 style={{ marginLeft: "2px" }}>STATUS GAVETA</h4>
                </button>
              </div>

              <IonButton
                onClick={sendAbrirGaveta}
                color="primary"
                expand="block"
                size="small"
                mode="ios"
              >
                ABRIR GAVETA
              </IonButton>
            </IonCol>

            <IonCol className="ion-no-padding">
              <IonRadioGroup
                allow-empty-selectIon="false"
                value={selectedConnection}
                onIonChange={(e) => changePrinterChoose(e.detail.value)}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-around",
                    alignItems: "center",
                  }}
                >
                  <div>
                    <IonRadio className={styles.radio} value="interna" />
                    <IonLabel style={{ marginLeft: "5px", fontSize: "small" }}>
                      IMP.INTERNA
                    </IonLabel>
                  </div>

                  <div>
                    <IonRadio className={styles.radio} value="usb" />
                    <IonLabel style={{ marginLeft: "5px", fontSize: "small" }}>
                      IMP.EXTERNA-USB {selectedPrinterUsb}
                    </IonLabel>
                  </div>

                  <div>
                    <IonRadio className={styles.radio} value="ip" />
                    <IonLabel style={{ marginLeft: "5px", fontSize: "small" }}>
                      IMP.EXTERNA-IP {selectedPrinterIp}
                    </IonLabel>
                  </div>

                  <div style={{ width: "160px" }} className={styles.box}>
                    <IonInput
                      type="text"
                      value={ipConection}
                      onIonChange={(e) => setIpConection(e.detail.value!)}
                    ></IonInput>
                  </div>
                </div>
              </IonRadioGroup>

              <div className={styles.bigBox}>
                <IonContent className={styles.contentSqueeze}>
                  {typePrinterAtual()}
                </IonContent>
              </div>
            </IonCol>
          </IonRow>
        </IonGrid>
        <Footer />
      </IonContent>
    </IonPage>
  );
};

export default Printer;
