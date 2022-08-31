package com.elgin.java_intentdigitalhub_smartpos.Activities.BarCodeReader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubModule;
import com.elgin.java_intentdigitalhub_smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BarCodeReaderActivity extends AppCompatActivity {

    //Campos onde o resultado da leitura será exposto.
    private EditText editTextBarCode;
    private EditText editTextBarCodeType;

    private Button buttonInitializeReading;
    private Button buttonClearField;

    private final int SCANNER_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_reader);

        editTextBarCode = findViewById(R.id.editTextCodeBar);
        editTextBarCodeType = findViewById(R.id.editTextCodeBarType);

        //Os campos não devem receber input por parte do usuário.
        editTextBarCode.setFocusable(false);
        editTextBarCodeType.setFocusable(false);

        buttonInitializeReading = findViewById(R.id.buttonInitializeReading);
        buttonClearField = findViewById(R.id.buttonClearField);

        buttonInitializeReading.setOnClickListener(v -> {
            startScannerReading();
        });

        buttonClearField.setOnClickListener(v -> {
            editTextBarCode.setText("");
            editTextBarCodeType.setText("");
        });
    }

    private void startScannerReading() {
        //Cria o Intent para o scanner com o módulo filtro do SCANNER.
        Intent scannerIntent = new Intent(IntentDigitalHubModule.SCANNER.getIntentPath());

        //Inicia o scanner.
        this.startActivityForResult(scannerIntent, 1234);
    }

    //Captura o resultado do scanner.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCANNER_REQUEST_CODE) {
            if (resultCode == 0) {
                //O resultado, assim como todos os comandos do IDH, é um array de json (como string) sob a chave "retorno" no extra da intent, que neste caso possuí um único elemento.
                //Exemplo : Exemplo de retorno: [{"funcao":"getScanner","mensagem":"Método executado","resultado":["1","7909189189069","7909189189069","EAN13"]}]
                final String result = data.getStringExtra("retorno");

                try {
                    //Capturando o resultado dentro de objeto JSONArray, para capturar o seu único elemento, que possuí o resultado da leitura.
                    JSONArray jsonArrayReturn = new JSONArray(result);

                    //Capturando o json de retorno.
                    JSONObject jsonObjectReturn = jsonArrayReturn.getJSONObject(0);

                    //O resultado do código de barras e o tipo de código de barras estão presente em um Array de String.
                    final JSONArray barCodeReturns = jsonObjectReturn.getJSONArray("resultado");

                    //Atualiza na tela o código de barras capturado.
                    editTextBarCode.setText(barCodeReturns.getString(1));

                    //Atualiza na tela o tipo de código de barras capturado.
                    editTextBarCodeType.setText(barCodeReturns.getString(3));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}