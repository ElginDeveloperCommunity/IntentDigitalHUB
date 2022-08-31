import NativeServiceInterface from "../../Wrapper_idh_capacitor_methods";
import { useEffect } from "react";
import XmlStoreService from "../../XmlStorageService/XmlStoreService";
import { Toast } from '@capacitor/toast';


import {
  IonButton,
  IonContent,
  IonPage,
  IonRow,
  IonCol,
  IonLabel,
  IonImg,
} from "@ionic/react";
import Footer from "../../components/Footer";
import styles from "./index.module.css";
import { App } from "@capacitor/app";

const Menu: React.FC = () => {
  //Ao entrar na aplicação, para prosseguir, é necessário a permissão de acesso ao armazenamento externo.
  useEffect(() => {
    //Pede a permissão de armazenamento
    NativeServiceInterface.askWriteExternalStoragePermission().then(
      async (responseJson) => {
        let permissionRequestResponse: boolean =
          responseJson["permissionRequestResponse"];

        if (!permissionRequestResponse) {
          await Toast.show({
             text: 'É necessário conceder a permissão para várias funcionalidades da aplicação!',
             duration: 'long',
             position: 'bottom',
          });
          //Sair da aplicação aqui
          App.exitApp();
        } else {
          //Após a permissão ser concedida, salva no diretório do dispositivo todos os XMLs que serão utilizados.
          await XmlStoreService.allocateXmls();
        }
      }
    );
    return () => {};
  }, []);

  return (
    <IonPage>
      <IonContent fullscreen>
        <IonRow class="ion-justify-content-center">
          <IonImg className={styles.menuLogo} src={require('../../icons/elgin_logo.png')} />
        </IonRow>
        <IonRow class="ion-justify-content-center">
          <IonCol size="auto">
            <IonButton class={styles.menuButton} type="button" routerLink="/bridge" color="light">
              <div>
                <IonImg src={require('../../icons/elginpay_logo.png')} />
                <IonLabel>
                  <h4>BRIDGE</h4>
                </IonLabel>
              </div>
            </IonButton>
          </IonCol>

          <IonCol size="auto">
            <IonButton class={styles.menuButton} type="button" routerLink="/printer" color="light">
              <div>
                <IonImg src={require('../../icons/printer.png')} />
                <IonLabel>
                  <h4>IMPRESSORA</h4>
                </IonLabel>
              </div>
            </IonButton>
          </IonCol>
        </IonRow>

        <IonRow class="ion-justify-content-center">
          <IonCol size="auto">
            <IonButton class={styles.menuButton} type="button" routerLink="/balanca" color="light">
              <div>
                <IonImg src={require('../../icons/balanca.png')} />
                <IonLabel>
                  <h4>BALANÇA</h4>
                </IonLabel>
              </div>
            </IonButton>
          </IonCol>

          <IonCol size="auto">
            <IonButton class={styles.menuButton} type="button" routerLink="/sat" color="light">
              <div>
                <IonImg src={require('../../icons/sat.png')} />
                <IonLabel>
                  <h4>SAT</h4>
                </IonLabel>
              </div>
            </IonButton>
          </IonCol>
        </IonRow>
      </IonContent>
      <Footer />
    </IonPage>
  );
};

export default Menu;
