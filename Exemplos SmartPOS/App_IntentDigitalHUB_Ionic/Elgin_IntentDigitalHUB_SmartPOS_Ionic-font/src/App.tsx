import { Redirect, Route } from "react-router-dom";
import { IonApp, IonRouterOutlet, setupIonicReact } from "@ionic/react";
import { IonReactRouter } from "@ionic/react-router";

/* Core CSS required for Ionic components to work properly */
import "@ionic/react/css/core.css";

/* Basic CSS for apps built with Ionic */
import "@ionic/react/css/normalize.css";
import "@ionic/react/css/structure.css";
import "@ionic/react/css/typography.css";

/* Optional CSS utils that can be commented out */
import "@ionic/react/css/padding.css";
import "@ionic/react/css/float-elements.css";
import "@ionic/react/css/text-alignment.css";
import "@ionic/react/css/text-transformation.css";
import "@ionic/react/css/flex-utils.css";
import "@ionic/react/css/display.css";

/* Theme variables */
import "./theme/variables.css";
import Menu from "./pages/Menu";
import Printer from "./pages/Printer";
import PrinterText from "./pages/PrinterText";
import PrinterImage from "./pages/PrinterImage";
import PrinterBarcode from "./pages/PrinterBarcode";
import ElginPay from "./pages/ElginPay";
import BarcodeReader from "./pages/BarCodeReader";

setupIonicReact();

const App: React.FC = () => (
  <IonApp>
    <IonReactRouter>
      <IonRouterOutlet>
        <Route exact path="/home">
          <Menu />
        </Route>
        <Route exact path="/">
          <Redirect to="/home" />
        </Route>
        <Route exact path="/elgin_pay">
          <ElginPay />
        </Route>
        <Route exact path="/printer">
          <Printer />
        </Route>
        <Route exact path="/printer_text">
          <PrinterText />
        </Route>
        <Route exact path="/printer_image">
          <PrinterImage />
        </Route>
        <Route exact path="/printer_barcode">
          <PrinterBarcode />
        </Route>
        <Route exact path="/barcode_reader">
          <BarcodeReader />
        </Route>
      </IonRouterOutlet>
    </IonReactRouter>
  </IonApp>
);

export default App;
