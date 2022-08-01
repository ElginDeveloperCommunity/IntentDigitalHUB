import { useEffect, useState } from "react";
import {
  IonContent,
  IonPage,
  IonRow,
  IonCol,
  IonInput,
  IonButton,
  IonCheckbox,
  IonLabel,
  IonImg,
  useIonAlert,
} from "@ionic/react";
import { App } from "@capacitor/app";

import Footer from "../../components/Footer";
import Header from "../../components/Header";

import IntentDigitalHubCommandStarter from "../../intentDigitalHubService/IntentDigitalHubCommandStarter";
import { BridgeCommand } from "../../intentDigitalHubService/BRIDGE/BridgeCommand";
import { SetSenha } from "../../intentDigitalHubService/BRIDGE/Commands/SetSenha";
import { SetServer } from "../../intentDigitalHubService/BRIDGE/Commands/SetServer";
import { IniciaOperacaoAdministrativa } from "../../intentDigitalHubService/BRIDGE/Commands/IniciaOperacaoAdministrativa";
import { SetSenhaServer } from "../../intentDigitalHubService/BRIDGE/Commands/SetSenhaServer";
import { SetTimeout } from "../../intentDigitalHubService/BRIDGE/Commands/SetTimeout";
import { IniciaVendaDebito } from "../../intentDigitalHubService/BRIDGE/Commands/IniciaVendaDebito";
import { IniciaCancelamentoVenda } from "../../intentDigitalHubService/BRIDGE/Commands/IniciaCancelamentoVenda";
import { ImprimirCupomNfce } from "../../intentDigitalHubService/BRIDGE/Commands/ImprimirCupomNfce";
import { ImprimirCupomSat } from "../../intentDigitalHubService/BRIDGE/Commands/ImprimirCupomSat";
import { ImprimirCupomSatCancelamento } from "../../intentDigitalHubService/BRIDGE/Commands/ImprimirCupomSatCancelamento";
import { ConsultarUltimaTransacao } from "../../intentDigitalHubService/BRIDGE/Commands/ConsultarUltimaTransacao";
import { GetTimeout } from "../../intentDigitalHubService/BRIDGE/Commands/GetTimeout";
import { ConsultarStatus } from "../../intentDigitalHubService/BRIDGE/Commands/ConsultarStatus";
import { IniciaVendaCredito } from "../../intentDigitalHubService/BRIDGE/Commands/IniciaVendaCredito";

import XmlFile from "../../XmlStorageService/XmlFile";

import styles from "./index.module.css";

const Bridge: React.FC = () => {
  const [valor, setValor] = useState("2000");
  const [numParcelas, setNumParcelas] = useState("1");
  const [numIP, setNumIP] = useState("192.168.0.104");
  const [paymentMethod, setPaymentMethod] = useState("Crédito");
  const [installmentType, setInstallmentType] = useState("1");

  const PDV = "PDV1";

  function padTo2Digits(num: number) {
    return num.toString().padStart(2, '0');
  }

  function formatDate(date: Date) {
    return [
      padTo2Digits(date.getDate()),
      padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join('/');
  }
  // Data no formato dd/mm/yyyy
  const todayDate = formatDate(new Date());

  const [trasactionPort, setTrasactionPort] = useState("3000");
  const [statusPort, setStatusPort] = useState("3001");

  const [sendPassword, setSendPassword] = useState(false);
  const [password, setPassword] = useState("");

  const [appPackageName, setAppPackageName] = useState<string>("");

  const [presentAlert] = useIonAlert();

  useEffect(() => {
    App.getInfo().then((info) => {
      setAppPackageName(info.id);
    });
  }, []);

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
      onClick: () => setPaymentMethod("Débito"),
    },
  ];

  const buttonsInstallment = [
    {
      id: "3",
      icon: require("../../icons/store.png"),
      textButton: "LOJA",
      onClick: () => setInstallmentType("3"),
    },
    {
      id: "2",
      icon: require("../../icons/adm.png"),
      textButton: "ADM ",
      onClick: () => setInstallmentType("2"),
    },
    {
      id: "1",
      icon: require("../../icons/card.png"),
      textButton: "A VISTA",
      onClick: () => setInstallmentType("1"),
    },
  ];

  const consultButtons = [
    {
      id: "terminalStatus",
      textButton: "CONSULTAR STATUS DO TERMINAL",
      onClick: () => checkTerminalStatus(),
    },
    {
      id: "timeOutConfig",
      textButton: "CONSULTAR TIMEOUT CONFIGURADO",
      onClick: () => checkConfiguredTimeout(),
    },
    {
      id: "lastTransition",
      textButton: "CONSULTAR ULTIMA TRANSAÇÃO",
      onClick: () => checkLastTransaction(),
    },
  ];

  const configButtons = [
    {
      id: "terminalPassword",
      textButton: "CONFIGURAR SENHA DO TERMINAL",
      onClick: () => configureTerminalPassword(),
    },
    {
      id: "transactionTimeOut",
      textButton: "CONFIGURAR TIMEOUT PARA TRANSAÇÕES",
      onClick: () => setTransactionTimeOut(),
    },
  ];

  //Função utilizada para iniciar quaisquer operações Bridge com o fluxo :
  // recebe um comando e o concatena com as funções SetServer e SetSenha,
  // evitando a repetição em todas as funcionalidades e assegurando que as
  // ultimas alterações nos campos de entrada sejam efetivadas antes da excução da operação
  async function startBridgeCommand(
    bridgeCommand: BridgeCommand,
    callback: (objIDH: string) => void
  ) {
    const commands = [
      new SetServer(numIP, Number(trasactionPort), Number(statusPort)),
      new SetSenha(password, sendPassword),
      bridgeCommand,
    ];
    const objIDH = await IntentDigitalHubCommandStarter.startCommands(commands);
    callback(objIDH.intentDigitalHubResponse);
  }

  //Funções dos botões da aplicação

  function sendTransaction() {
    if (
      updateBridgeServer() &&
      isValueValidToElginPay() &&
      isInstallmentsFieldValid()
    ) {
      if (paymentMethod === "Crédito") {
        const iniciaVendaCreditoCommand = new IniciaVendaCredito(
          generateRandomForBridgeTransactions(),
          PDV,
          valor.replace(/[^\d]+/g, ""),
          Number(installmentType),
          Number(numParcelas)
        );
        startBridgeCommand(iniciaVendaCreditoCommand, (resultString) => {
          const result = JSON.parse(resultString)[2].resultado;
          presentAlert({
            header: "Retorno E1 - BRIDGE",
            message: result,
            buttons: ["OK"],
          });
        });
      } else {
        const iniciaVendaDebitoCommand = new IniciaVendaDebito(
          generateRandomForBridgeTransactions(),
          PDV,
          valor.replace(/[^\d]+/g, "")
        );
        startBridgeCommand(iniciaVendaDebitoCommand, (resultString) => {
          const result = JSON.parse(resultString)[2].resultado;
          presentAlert({
            header: "Retorno E1 - BRIDGE",
            message: result,
            buttons: ["OK"],
          });
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
          handler: (data) => {
            if (data.refCode !== "") {
              if (updateBridgeServer()) {
                const iniciaCancelamentoVendaCommand =
                  new IniciaCancelamentoVenda(
                    generateRandomForBridgeTransactions(),
                    PDV,
                    valor.replace(/[^\d]+/g, ""),
                    todayDate,
                    data.refCode
                  );
                startBridgeCommand(
                  iniciaCancelamentoVendaCommand,
                  (resultString) => {
                    const result = JSON.parse(resultString)[2].resultado;
                    presentAlert({
                      header: "Retorno E1 - BRIDGE",
                      message: result,
                    });
                  }
                );
              }
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
        console.log(123, event);
      },
    });
  }

  function admOperation() {
    presentAlert({
      header: "Escolha uma Operação Administrativa",
      animated: false,
      inputs: [
        {
          label: "Operação Administrativa",
          type: "radio",
          value: "0",
          checked: true,
        },
        {
          label: "Operação de Instalação",
          type: "radio",
          value: "1",
        },
        {
          label: "Operação de Configuração",
          type: "radio",
          value: "2",
        },
        {
          label: "Operação de Manutenção",
          type: "radio",
          value: "3",
        },
        {
          label: "Teste de Comunicação",
          type: "radio",
          value: "4",
        },
        {
          label: "Reimpressão de Comprovante",
          type: "radio",
          value: "5",
        },
      ],
      buttons: [
        {
          text: "OK",
          handler: (selectedAdmOperation) => {
            if (updateBridgeServer()) {
              const iniciaOperacaoAdministrativaCommand =
                new IniciaOperacaoAdministrativa(
                  generateRandomForBridgeTransactions(),
                  PDV,
                  Number(selectedAdmOperation)
                );
              startBridgeCommand(
                iniciaOperacaoAdministrativaCommand,
                (resultString) => {
                  const result = JSON.parse(resultString)[2].resultado;
                  presentAlert({
                    header: "Retorno E1 - BRIDGE",
                    message: result,
                  });
                }
              );
            }
          },
        },
      ],
    });
  }

  // As impressões feitas na aplicação utilizam os arquivos XML salvos previamente no início da aplicação.
  // Para indicar um caminho de arquivo como parâmetro ao Intent Digital Hub é necessário enviar
  // no parâmetro que iria o XML:
  // 'path=<caminho do arquivo>'
  function printCouponTest() {
    presentAlert({
      header: "Escolha o Tipo de Cupom",
      animated: false,
      inputs: [
        {
          label: "Imprimir Cupom NFCe",
          type: "radio",
          value: "nfce",
          checked: true,
        },
        {
          label: "Imprimir Cupom Sat",
          type: "radio",
          value: "sat",
        },
        {
          label: "Imprimir Cupom Sat Cancelamento",
          type: "radio",
          value: "cancelCoupon",
        },
      ],
      buttons: [
        {
          text: "OK",
          handler: (couponType) => {
            if (updateBridgeServer()) {
              if (couponType === "nfce") {
                doBridgeXmlNFCe();
              } else if (couponType === "sat") {
                doBridgeXmlSat();
              } else {
                doBridgeXmlSatCancel();
              }
            }
          },
        },
      ],
    });
  }

  function doBridgeXmlNFCe() {
    const fileName = XmlFile.XML_NFCE.getXmlFileName() + ".xml";
    const path = "/Android/data/" + appPackageName + "/files/" + fileName;

    const indexcsc = 1;
    const csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";

    const imprimirCupomNfceCommand = new ImprimirCupomNfce(
      `path=${path}`,
      indexcsc,
      csc
    );
    startBridgeCommand(imprimirCupomNfceCommand, (resultString) => {
      const result = JSON.parse(resultString)[2].resultado;
      presentAlert({
        header: "Retorno E1 - BRIDGE",
        message: result,
        buttons: ["OK"],
      });
    });
  }

  function doBridgeXmlSat() {
    const fileName = XmlFile.XML_SAT.getXmlFileName() + ".xml";
    const path = "/Android/data/" + appPackageName + "/files/" + fileName;

    const imprimirCupomSatCommand = new ImprimirCupomSat(`path=${path}`);
    startBridgeCommand(imprimirCupomSatCommand, (resultString) => {
      const result = JSON.parse(resultString)[2].resultado;
      presentAlert({
        header: "Retorno E1 - BRIDGE",
        message: result,
        buttons: ["OK"],
      });
    });
  }

  function doBridgeXmlSatCancel() {
    const fileName = XmlFile.XML_SAT_CANCELAMENTO.getXmlFileName() + ".xml";
    const path = "/Android/data/" + appPackageName + "/files/" + fileName;

    const assQRCode = XmlFile.ASS_QR_CODE_SAT_CANCELAMENTO;

    const imprimirCupomSatCancelamentoCommand =
      new ImprimirCupomSatCancelamento(`path=${path}`, assQRCode);
    startBridgeCommand(imprimirCupomSatCancelamentoCommand, (resultString) => {
      const result = JSON.parse(resultString)[2].resultado;
      presentAlert({
        header: "Retorno E1 - BRIDGE",
        message: result,
        buttons: ["OK"],
      });
    });
  }

  function checkTerminalStatus() {
    if (updateBridgeServer()) {
      const consultarStatusCommand = new ConsultarStatus();
      startBridgeCommand(consultarStatusCommand, (resultString) => {
        const result = JSON.parse(resultString)[2].resultado;
        presentAlert({
          header: "Retorno E1 - BRIDGE",
          message: result,
          buttons: ["OK"],
        });
      });
    }
  }

  function checkConfiguredTimeout() {
    if (updateBridgeServer()) {
      const getTimeoutCommand = new GetTimeout();
      startBridgeCommand(getTimeoutCommand, (resultString) => {
        const result = JSON.parse(resultString)[2].resultado;
        presentAlert({
          header: "Retorno E1 - BRIDGE",
          message: result,
          buttons: ["OK"],
        });
      });
    }
  }

  function checkLastTransaction() {
    if (updateBridgeServer()) {
      const consultarUltimaTransacaoCommand = new ConsultarUltimaTransacao(PDV);
      startBridgeCommand(consultarUltimaTransacaoCommand, (resultString) => {
        const result = JSON.parse(resultString)[2].resultado;
        presentAlert({
          header: "Retorno E1 - BRIDGE",
          message: result,
          buttons: ["OK"],
        });
      });
    }
  }

  function configureTerminalPassword() {
    presentAlert({
      header: "Escolha como configurar a senha",
      animated: false,
      inputs: [
        {
          label: "Habilitar Senha no Terminal",
          type: "radio",
          value: "enablePassword",
          name: "selectedPasswordConfig",
          checked: true,
        },
        {
          label: "Desabilitar Senha no Terminal",
          type: "radio",
          value: "unablePassword",
          name: "selectedPasswordConfig",
        },
      ],
      buttons: [
        {
          text: "OK",
          role: "confirm",
          handler: (selectedPasswordConfig) => {
            if (selectedPasswordConfig === "enablePassword") {
              setTimeout(() => {
                presentAlert({
                  header: "Digite a senha a ser habilitada",
                  cssClass: styles.alertPassword,
                  animated: false,
                  inputs: [
                    {
                      type: "password",
                      name: "passwordEntered",
                      placeholder: "Senha: ",
                      cssClass: styles.alertPassword,
                    },
                  ],
                  buttons: [
                    {
                      text: "OK",
                      role: "confirm",
                      handler: (data) => {
                        enableTerminalPassword(data.passwordEntered);
                      },
                    },
                  ],
                });
              }, 0);
            } else {
              if (!sendPassword) {
                setTimeout(() => {
                  presentAlert({
                    header: "Alerta",
                    message:
                      "Habilite a opção de envio de senha e envie a senha mais atual para desabilitar a senha do terminal",
                    buttons: ["OK"],
                  });
                }, 0);
              } else {
                if (updateBridgeServer()) {
                  const setSenhaServerCommand = new SetSenhaServer("", false);
                  startBridgeCommand(setSenhaServerCommand, (resultString) => {
                    const result = JSON.parse(resultString)[2].resultado;
                    presentAlert({
                      header: "Retorno E1 - BRIDGE",
                      message: result,
                      buttons: ["OK"],
                    });
                  });
                }
              }
            }
          },
        },
      ],
    });
  }

  function enableTerminalPassword(passwordEntered: string) {
    if (passwordEntered === "") {
      setTimeout(() => {
        presentAlert({
          header: "Erro na Senha",
          message: "Por favor insira um valor para a senha desejada!",
          buttons: ["OK"],
        });
      }, 100);
    } else {
      if (updateBridgeServer()) {
        const setSenhaServerCommand = new SetSenhaServer(passwordEntered, true);
        startBridgeCommand(setSenhaServerCommand, (resultString) => {
          const result = JSON.parse(resultString)[2].resultado;
          presentAlert({
            header: "Retorno E1 - BRIDGE",
            message: result,
            buttons: ["OK"],
          });
        });
      }
    }
  }

  function setTransactionTimeOut() {
    presentAlert({
      header: "Defina o timeout para transação (em segundos)",
      animated: false,
      cssClass: styles.alertTimeout,
      inputs: [
        {
          placeholder: "Novo TimeOut:",
          type: "text",
          name: "newTimeOut",
        },
      ],
      buttons: [
        {
          text: "OK",
          role: "confirm",
          handler: (data) => {
            if (data.newTimeOut === "") {
              setTimeout(() => {
                presentAlert({
                  header: "Alerta",
                  message: "O valor para o TimeOut não pode ser vazio.",
                  buttons: ["OK"],
                });
              }, 100);
            } else {
              if (updateBridgeServer()) {
                const setTimeoutCommand = new SetTimeout(
                  Number(data.newTimeOut)
                );
                startBridgeCommand(setTimeoutCommand, (resultString) => {
                  const result = JSON.parse(resultString)[2].resultado;
                  presentAlert({
                    header: "Retorno E1 - BRIDGE",
                    message: result,
                    buttons: ["OK"],
                  });
                });
              }
            }
          },
        },
      ],
    });
  }

  //Funções de validação

  function isIpAdressValid(): boolean {
    let ipValid = false;

    if (
      /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]){1}$/.test(
        numIP
      )
    ) {
      ipValid = true;
      return ipValid;
    } else {
      ipValid = false;
      presentAlert({
        header: "Erro de IP",
        message: "O endereço IP está incorreto, digite um endereço válido",
        buttons: ["OK"],
      });
      return ipValid;
    }
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
          message: "O número de parcelas deve ser maior que 2!",
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

  function isTransactionPortValid(): boolean {
    if (Number(trasactionPort) > 65535) {
      presentAlert({
        header: "Erro na Porta de Transação",
        message:
          "O valor inserido na porta de transação excede o limite esbelecido de 65535!",
        buttons: ["OK"],
      });
      return false;
    } else if (trasactionPort === "") {
      presentAlert({
        header: "Erro na Porta de Transação",
        message: "O valor inserido na porta de transação não pode ser vazio",
        buttons: ["OK"],
      });
      return false;
    } else {
      return true;
    }
  }

  function isStatusPortValid() {
    if (Number(trasactionPort) > 65535) {
      presentAlert({
        header: "Erro na Porta de Status",
        message:
          "O valor inserido na porta de status excede o limite esbelecido de 65535!",
        buttons: ["OK"],
      });
      return false;
    } else if (trasactionPort === "") {
      presentAlert({
        header: "Erro na Porta de Status",
        message: "O valor inserido na porta de status não pode ser vazio",
        buttons: ["OK"],
      });
      return false;
    } else {
      return true;
    }
  }

  //Funções de configurações do Server Bridge

  function updateBridgeServer() {
    if (isIpAdressValid() && isTransactionPortValid() && isStatusPortValid()) {
      return true;
    } else {
      return false;
    }
  }

  // Outras funções

  function generateRandomForBridgeTransactions(): number {
    return Math.floor(Math.random() * (1000000 - 0)) + 0;
  }

  return (
    <IonPage className="ion-padding-horizontal">
      <IonContent fullscreen>
        <Header title="E1 Bridge" />
        <IonRow>
          <IonCol size="6">
            <div>
              <IonRow className="ion-no-margin ion-no-padding">
                <IonCol>
                  <h6 className="ion-no-margin">IP</h6>
                </IonCol>

                <IonCol>
                  <IonInput
                    placeholder="192.168.0.104"
                    style={{
                      borderColor: "black",
                      borderBottom: "1px solid black",
                      height: 20,
                    }}
                    type="text"
                    value={numIP}
                    onIonChange={(e) => setNumIP(e.detail.value!)}
                  ></IonInput>
                </IonCol>
              </IonRow>
            </div>

            <div>
              <IonRow>
                <IonCol>
                  <h6 className="ion-no-margin">VALOR</h6>
                </IonCol>

                <IonCol>
                  <IonInput
                    style={{
                      alignSelf: "flex-end",
                      borderColor: "black",
                      borderBottom: "1px solid black",
                      height: 20,
                    }}
                    type="number"
                    value={valor}
                    onIonChange={(e) => setValor(e.detail.value!)}
                  ></IonInput>
                </IonCol>
              </IonRow>
            </div>

            <div>
              <IonRow>
                <IonCol>
                  <h6 className="ion-no-margin">N° PARCELAS</h6>
                </IonCol>

                <IonCol>
                  <IonInput
                    style={{
                      alignSelf: "flex-end",
                      borderColor: "black",
                      borderBottom: "1px solid black",
                      height: 20,
                    }}
                    type="number"
                    disabled={paymentMethod === "Crédito" ? true : false}
                    value={numParcelas}
                    onIonChange={(e) => setNumParcelas(e.detail.value!)}
                  ></IonInput>
                </IonCol>
              </IonRow>
            </div>

            <h6 style={{ marginBottom: "5px" }}>FORMAS DE PAGAMENTO:</h6>

            <div style={{ display: "flex" }}>
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

            <h6 style={{ marginTop: "5px", marginBottom: "5px" }}>
              TIPOS DE PARCELAMENTO:
            </h6>

            <div style={{ display: "flex", marginBottom: "5px" }}>
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

            <IonRow>
              <IonCol>
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

              <IonCol>
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
            </IonRow>

            <IonRow>
              <IonCol>
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

              <IonCol>
                <IonButton
                  className={styles.button}
                  size="small"
                  color="primary"
                  mode="ios"
                  onClick={() => printCouponTest()}
                >
                  IMPRIMIR CUPOM TESTE
                </IonButton>
              </IonCol>
            </IonRow>
          </IonCol>
          <IonCol size="6">
            <div>
              <IonRow>
                <h6>PORTAS TRANSAÇÕES/STATUS:</h6>

                <IonInput
                  placeholder="3000"
                  style={{
                    margin: "5px 10px 5px 0px",
                    width: "80px",
                    background: "transparent",
                    borderColor: "black",
                    borderBottom: "1px solid black",
                  }}
                  type="text"
                  value={trasactionPort}
                  onIonChange={(e) => setTrasactionPort(e.detail.value!)}
                ></IonInput>
                <IonInput
                  style={{
                    margin: "5px 0px 5px 10px",
                    width: "80px",
                    background: "transparent",
                    borderColor: "black",
                    borderBottom: "1px solid black",
                  }}
                  placeholder="3001"
                  type="text"
                  value={statusPort}
                  onIonChange={(e) => setStatusPort(e.detail.value!)}
                ></IonInput>
              </IonRow>

              <IonRow>
                <IonCheckbox
                  checked={sendPassword}
                  onIonChange={(e) => setSendPassword(e.detail.checked!)}
                ></IonCheckbox>
                <IonLabel style={{ marginLeft: "20px" }}>
                  ENVIAR SENHA NAS TRANSAÇÕES
                </IonLabel>
              </IonRow>

              <IonRow style={{ marginTop: 20 }}>
                <h6 className="ion-no-margin">SENHA</h6>
                <IonInput
                  style={{
                    marginLeft: "10px",
                    borderColor: "black",
                    borderBottom: "1px solid black",
                    height: 20,
                  }}
                  type="text"
                  value={password}
                  onIonChange={(e) => setPassword(e.detail.value!)}
                ></IonInput>
              </IonRow>

              <IonRow>
                <IonCol>
                  <h6>FUNÇÕES E1-BRIDGE:</h6>
                </IonCol>
              </IonRow>

              {consultButtons.map(({ id, textButton, onClick }) => (
                <IonButton
                  key={id}
                  className={styles.button}
                  style={{ marginTop: "10px" }}
                  size="small"
                  color="primary"
                  mode="ios"
                  onClick={onClick}
                >
                  {textButton}
                </IonButton>
              ))}

              {configButtons.map(({ id, textButton, onClick }) => (
                <IonButton
                  key={id}
                  className={styles.button}
                  style={{ marginTop: "10px" }}
                  size="small"
                  color="primary"
                  mode="ios"
                  onClick={onClick}
                >
                  {textButton}
                </IonButton>
              ))}
            </div>
          </IonCol>
        </IonRow>
        <Footer />
      </IonContent>
    </IonPage>
  );
};

export default Bridge;
