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
import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { AbreConexaoImpressora } from "../../intentDigitalHubService/TERMICA/Commands/AbreConexaoImpressora";
import { FechaConexaoImpressora } from "../../intentDigitalHubService/TERMICA/Commands/FechaConexaoImpressora";
import { StatusImpressora } from "../../intentDigitalHubService/TERMICA/Commands/StatusImpressora";
import styles from "./index.module.css";

const Printer: React.FC = () => {
  const [selectedConnection, setSelectedConnection] = useState("interna");
  const [selectedPrinterModel, setSelectedPrinterModel] = useState("");
  const [ipConection, setIpConection] = useState("192.168.0.31:9100");

  const [presentAlert] = useIonAlert();

  const buttonsPrinter = [
    {
      id: "TEXT",
      icon: require("../../icons/printer_text.png"),
      textButton: "IMPRESSÃO\nDE TEXTO",
      routerLink: `/printer_text?connectionType=${selectedConnection}`,
    },
    {
      id: "BARCODE",
      icon: require("../../icons/printer_bar_code.png"),
      textButton: "IMPRESSÃO DE\nCÓDIGO DE BARRAS",
      routerLink: `/printer_barcode?connectionType=${selectedConnection}`,
    },
    {
      id: "IMAGE",
      icon: require("../../icons/printer_image.png"),
      textButton: "IMPRESSÃO\nDE IMAGEM",
      routerLink: `/printer_image?connectionType=${selectedConnection}`,
    },
  ];

  useEffect(() => {
    startConnectPrinterIntern();

    return () => {
      stopConnectPrinter();
    };
  }, []);

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

  async function changePrinterChoose(value: string) {
    if (value === "ip") {
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

  function stopConnectPrinter() {
    const command = new FechaConexaoImpressora();
    IntentDigitalHubCommandStarter.startCommand(command);
  }

  async function startConnectPrinterIP(model: string) {
    if (ipConection !== "") {
      var ip = ipConection.split(":")[0];
      var port = ipConection.split(":")[1];
      console.log(ip, port);

      if (isIpAdressValid()) {
        const command = new AbreConexaoImpressora(3, model, ip, Number(port));
        const { intentDigitalHubResponse } =
          await IntentDigitalHubCommandStarter.startCommand(command);
        const result = JSON.parse(intentDigitalHubResponse)[0].resultado;
        setSelectedConnection("ip");
        setSelectedPrinterModel(model);
        if (Number(result) !== 0) {
          await presentAlert({
            header: "ERRO",
            message:
              "Não foi possível realizar a conexão por IP. Inciando conexão com impressora interna",
            buttons: ["OK"],
            backdropDismiss: false,
          });
          startConnectPrinterIntern();
        }
      } else {
        presentAlert("Digíte um endereço e porta IP válido!");
      }
    } else {
      presentAlert("Digíte um endereço e porta IP válido!");
    }
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
        <Header title="IMPRESSORA" />
        <IonGrid className="ion-no-padding ion-margin-top">
          <IonRow>
            <IonCol size="12" className="ion-no-padding ion-margin-top">
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
                    <IonLabel
                      style={{
                        marginLeft: "5px",
                        fontSize: "small",
                        fontWeight: "bold",
                      }}
                    >
                      IMP.INTERNA
                    </IonLabel>
                  </div>

                  <div>
                    <IonRadio className={styles.radio} value="ip" />
                    <IonLabel
                      style={{
                        marginLeft: "5px",
                        fontSize: "small",
                        fontWeight: "bold",
                      }}
                    >
                      IMP.EXTERNA {selectedPrinterModel}
                    </IonLabel>
                  </div>
                </div>
              </IonRadioGroup>
            </IonCol>
            <IonCol size="12">
              <IonInput
                type="text"
                value={ipConection}
                onIonChange={(e) => setIpConection(e.detail.value!)}
              ></IonInput>
            </IonCol>
            <IonCol size="12" className="ion-no-padding">
              {buttonsPrinter.map((button) => (
                <IonButton
                  key={button.id}
                  className={[styles.box, styles.menuButton].join(" ")}
                  type="button"
                  routerLink={button.routerLink}
                >
                  <div>
                    <IonImg
                      className={styles.printerOptionImg}
                      src={button.icon}
                    />
                    <h4 className="ion-no-margin">{button.textButton}</h4>
                  </div>
                </IonButton>
              ))}
              <IonButton
                className={[styles.box, styles.menuButtonSmall].join(" ")}
                onClick={actualStatusPrinter}
                type="button"
              >
                <IonImg
                  className={styles.printerOptionImg}
                  src={require("../../icons/status.png")}
                />
                <h4 style={{ marginLeft: "2px" }}>STATUS IMPRESSORA</h4>
              </IonButton>
            </IonCol>
          </IonRow>
        </IonGrid>
      </IonContent>
      <Footer />
    </IonPage>
  );
};

export default Printer;
