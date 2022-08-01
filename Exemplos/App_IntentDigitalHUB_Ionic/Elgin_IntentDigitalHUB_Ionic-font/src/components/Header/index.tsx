import { IonRow, IonCol, IonImg } from "@ionic/react";
import "./index.css";

type Props = {
  title: string;
};

const Header: React.FC<Props> = ({ title }) => {
  return (
    <IonRow class="ion-align-items-center" style={{marginTop: 10, marginBottom: 10}}>
      <IonCol>
        <h1 style={{fontWeight: "bold", margin: 0}}>{title}</h1>
      </IonCol>
      <IonImg style={{height: 50}} src={require('../../icons/elgin_logo.png')} />
    </IonRow>
  );
};

export default Header;
