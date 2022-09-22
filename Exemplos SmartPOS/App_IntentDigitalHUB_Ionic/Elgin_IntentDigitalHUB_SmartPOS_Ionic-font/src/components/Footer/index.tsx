import {
  IonRow,
  IonCol,
} from "@ionic/react";
import "./index.css";

const Footer: React.FC = () => {
  return (
    <IonRow class="ion-justify-content-end" style={{marginTop: 5, marginBottom: 5}}>
      <IonCol size="auto">
        <h6 style={{fontWeight: "bold", margin: 0, color: "gray"}}>Ionic - Intent Digital Hub 1.0.0</h6>
      </IonCol>
    </IonRow>
  );
};

export default Footer;
