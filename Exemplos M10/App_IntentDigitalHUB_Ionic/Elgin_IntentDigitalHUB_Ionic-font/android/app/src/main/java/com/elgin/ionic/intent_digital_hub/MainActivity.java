package com.elgin.ionic.intent_digital_hub;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Registra o plugin na inicialização da ponte com o lado nativo da aplicação
        registerPlugin(IntentDigitalHubPlugin.class);
    }
}
