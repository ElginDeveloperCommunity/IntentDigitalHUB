import { useEffect, useState } from "react";
import {
  IonContent,
  IonPage,
  IonRow,
  IonCol,
  IonInput,
  IonButton,
  IonImg,
  useIonAlert,
  IonCheckbox,
  IonLabel,
} from "@ionic/react";

import Footer from "../../components/Footer";
import Header from "../../components/Header";

import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { IniciaOperacaoAdministrativa } from "../../intentDigitalHubService/ELGINPAY/Commands/IniciaOperacaoAdministrativa";
import { IniciaVendaDebito } from "../../intentDigitalHubService/ELGINPAY/Commands/IniciaVendaDebito";
import { IniciaCancelamentoVenda } from "../../intentDigitalHubService/ELGINPAY/Commands/IniciaCancelamentoVenda";
import { IniciaVendaCredito } from "../../intentDigitalHubService/ELGINPAY/Commands/IniciaVendaCredito";
import { SetPersonalizacao } from "../../intentDigitalHubService/ELGINPAY/Commands/SetPersonalizacao";

import styles from "./index.module.css";

const YELLOW = "#FED20B";
const BLACK = "#050609";
const ELGINPAY_BLUE = "#0864a4";
const WHITE = "#FFFFFF";

const ElginPay: React.FC = () => {
  const [valor, setValor] = useState("2000");
  const [numParcelas, setNumParcelas] = useState("1");
  const [paymentMethod, setPaymentMethod] = useState("Crédito");
  const [installmentType, setInstallmentType] = useState("1");
  const [customLayout, setCustomLayout] = useState(false);

  useEffect(() => {
    let setPersonalizacaoCommand = new SetPersonalizacao(
      "",
      "",
      ELGINPAY_BLUE,
      WHITE,
      ELGINPAY_BLUE,
      WHITE,
      ELGINPAY_BLUE,
      WHITE,
      ELGINPAY_BLUE,
      ELGINPAY_BLUE
    );
    if (customLayout) {
      setPersonalizacaoCommand = new SetPersonalizacao(
        "",
        "",
        YELLOW,
        BLACK,
        YELLOW,
        BLACK,
        YELLOW,
        BLACK,
        YELLOW,
        YELLOW
      );
    }
    IntentDigitalHubCommandStarter.startCommand(setPersonalizacaoCommand);
  }, [customLayout]);

  function padTo2Digits(num: number) {
    return num.toString().padStart(2, "0");
  }

  function formatDate(date: Date) {
    return [
      padTo2Digits(date.getDate()),
      padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join("/");
  }
  // Data no formato dd/mm/yyyy
  const todayDate = formatDate(new Date());

  const [presentAlert] = useIonAlert();

  const buttonsPayment = [
    {
      id: "Crédito",
      icon: require("../../icons/card.png"),
      textButton: "CRÉDITO",
      onClick: () => setPaymentMethod("Crédito"),
    },
    {
      id: "Débito",
      icon: require("../../icons/card.png"),
      textButton: "DÉBITO",
      onClick: () => {
        setPaymentMethod("Débito");
      },
    },
  ];

  const buttonsInstallment = [
    {
      id: "3",
      icon: require("../../icons/store.png"),
      textButton: "LOJA",
      onClick: () => {
        setNumParcelas("2");
        setInstallmentType("3");
      },
    },
    {
      id: "2",
      icon: require("../../icons/adm.png"),
      textButton: "ADM ",
      onClick: () => {
        setNumParcelas("2");
        setInstallmentType("2");
      },
    },
    {
      id: "1",
      icon: require("../../icons/card.png"),
      textButton: "A VISTA",
      onClick: () => {
        setNumParcelas("1");
        setInstallmentType("1");
      },
    },
  ];

  //Funções dos botões da aplicação

  async function sendTransaction() {
    if (isValueValidToElginPay() && isInstallmentsFieldValid()) {
      if (paymentMethod === "Crédito") {
        const iniciaVendaCreditoCommand = new IniciaVendaCredito(
          valor.replace(/[^\d]+/g, ""),
          Number(installmentType),
          Number(numParcelas)
        );
        const { intentDigitalHubResponse } =
          await IntentDigitalHubCommandStarter.startCommand(
            iniciaVendaCreditoCommand
          );

        const result = JSON.parse(intentDigitalHubResponse)[0].resultado;
        presentAlert({
          header: "Retorno E1 - ELGINPAY",
          message: result,
          buttons: ["OK"],
        });
      } else {
        const iniciaVendaDebitoCommand = new IniciaVendaDebito(
          valor.replace(/[^\d]+/g, "")
        );
        const { intentDigitalHubResponse } =
          await IntentDigitalHubCommandStarter.startCommand(
            iniciaVendaDebitoCommand
          );
        const result = JSON.parse(intentDigitalHubResponse)[0].resultado;
        presentAlert({
          header: "Retorno E1 - ELGINPAY",
          message: result,
          buttons: ["OK"],
        });
      }
    }
  }

  function cancelTransaction() {
    presentAlert({
      header: "Código de Referência",
      animated: false,
      inputs: [
        {
          name: "refCode",
          type: "text",
          placeholder: "Insira o código de referência",
        },
      ],
      buttons: [
        {
          text: "OK",
          handler: async (data) => {
            if (data.refCode !== "") {
              const iniciaCancelamentoVendaCommand =
                new IniciaCancelamentoVenda(
                  valor.replace(/[^\d]+/g, ""),
                  todayDate,
                  data.refCode
                );
              const { intentDigitalHubResponse } =
                await IntentDigitalHubCommandStarter.startCommand(
                  iniciaCancelamentoVendaCommand
                );
              const result = JSON.parse(intentDigitalHubResponse)[0].resultado;
              presentAlert({
                header: "Retorno E1 - ELGINPAY",
                message: result,
              });
            } else {
              setTimeout(() => {
                presentAlert({
                  header: "Código de Referência Vazio",
                  message: "Por favor, insira um código de referência válido",
                  buttons: ["OK"],
                });
              }, 0);
            }
          },
        },
      ],
      onDidDismiss(event) {
        console.log("DISMISS", event);
      },
    });
  }

  async function admOperation() {
    const iniciaOperacaoAdministrativaCommand =
      new IniciaOperacaoAdministrativa();
    const { intentDigitalHubResponse } =
      await IntentDigitalHubCommandStarter.startCommand(
        iniciaOperacaoAdministrativaCommand
      );
    const result = JSON.parse(intentDigitalHubResponse)[0].resultado;
    presentAlert({
      header: "Retorno E1 - ELGINPAY",
      message: result,
      buttons: ["OK"],
    });
  }

  function isValueValidToElginPay(): boolean {
    if (Number(valor) < 1) {
      presentAlert({
        header: "Erro na entrada de valor",
        message: "O valor mínimo para transações é de R$1.00!",
        buttons: ["OK"],
      });
      return false;
    } else {
      return true;
    }
  }

  function isInstallmentsFieldValid(): boolean {
    if (paymentMethod === "Crédito") {
      if (
        (installmentType === "2" || installmentType === "3") &&
        Number(numParcelas) < 2
      ) {
        presentAlert({
          header: "Erro no parcelamento",
          message:
            "O número mínimo de parcelas para esse tipo de parcelamento é 2!",
          buttons: ["OK"],
        });
        return false;
      } else if (numParcelas === "") {
        presentAlert({
          header: "Erro no parcelamento",
          message: "O número de parcelas não pode ser vazio!",
          buttons: ["OK"],
        });
        return false;
      } else {
        return true;
      }
    } else {
      return true;
    }
  }

  return (
    <IonPage className="ion-padding-horizontal">
      <IonContent fullscreen>
        <Header title="E1 ElginPay" />
        <IonRow>
          <IonCol size="12">
            <div>
              <IonRow className="ion-justify-content-center ion-align-items-center">
                <IonCol>
                  <h6 className="ion-no-margin">VALOR</h6>
                </IonCol>

                <IonCol>
                  <IonInput
                    className="ion-no-margin"
                    type="number"
                    value={valor}
                    onIonChange={(e) => setValor(e.detail.value!)}
                  ></IonInput>
                </IonCol>
              </IonRow>
            </div>

            <div>
              <IonRow className="ion-justify-content-center ion-align-items-center">
                <IonCol>
                  <h6 className="ion-no-margin">N° PARCELAS</h6>
                </IonCol>

                <IonCol>
                  <IonInput
                    className="ion-no-margin"
                    type="number"
                    disabled={installmentType === "1"}
                    value={numParcelas}
                    onIonChange={(e) => setNumParcelas(e.detail.value!)}
                  ></IonInput>
                </IonCol>
              </IonRow>
            </div>

            <h6 className="ion-margin-top">FORMAS DE PAGAMENTO:</h6>

            <div className="ion-margin-bottom" style={{ display: "flex" }}>
              {buttonsPayment.map(({ id, icon, textButton, onClick }) => (
                <div
                  className={[
                    styles.customBox2,
                    id === paymentMethod && styles.paymentButtonSelected,
                  ].join(" ")}
                  key={id}
                >
                  <button
                    className={styles.menuButton}
                    onClick={onClick}
                    type="button"
                  >
                    <IonImg className={styles.paymentImg} src={icon} />
                    {textButton}
                  </button>
                </div>
              ))}
            </div>

            <div
              style={{
                opacity: Number(paymentMethod === "Crédito"),
                pointerEvents: paymentMethod === "Crédito" ? "auto" : "none",
              }}
            >
              <h6 className="ion-margin-top">TIPOS DE PARCELAMENTO:</h6>

              <div className="ion-margin-bottom" style={{ display: "flex" }}>
                {buttonsInstallment.map(({ id, icon, textButton, onClick }) => (
                  <div
                    className={[
                      styles.customBox2,
                      id === installmentType && styles.paymentButtonSelected,
                    ].join(" ")}
                    key={id}
                  >
                    <button
                      className={styles.menuButton}
                      onClick={onClick}
                      type="button"
                    >
                      <IonImg className={styles.paymentImg} src={icon} />
                      {textButton}
                    </button>
                  </div>
                ))}
              </div>
            </div>

            <div
              className="ion-padding-vertical"
              style={{
                display: "flex",
                alignItems: "center",
              }}
            >
              <IonCheckbox
                checked={customLayout}
                onIonChange={(e) => setCustomLayout(e.detail.checked)}
              ></IonCheckbox>
              <IonLabel style={{ marginLeft: "5px" }}>
                LAYOUT PERSONALIZADO
              </IonLabel>
            </div>

            <IonRow>
              <IonCol size="6">
                <IonButton
                  className={styles.button}
                  size="small"
                  color="primary"
                  mode="ios"
                  onClick={() => sendTransaction()}
                >
                  ENVIAR TRANSAÇÃO
                </IonButton>
              </IonCol>

              <IonCol size="6">
                <IonButton
                  className={styles.button}
                  size="small"
                  color="primary"
                  mode="ios"
                  onClick={() => cancelTransaction()}
                >
                  CANCELAR TRANSAÇÃO
                </IonButton>
              </IonCol>
              <IonCol size="12">
                <IonButton
                  className={styles.button}
                  size="small"
                  color="primary"
                  mode="ios"
                  onClick={() => admOperation()}
                >
                  OPERAÇÃO ADM
                </IonButton>
              </IonCol>
            </IonRow>
          </IonCol>
        </IonRow>
      </IonContent>
      <Footer />
    </IonPage>
  );
};

export default ElginPay;
