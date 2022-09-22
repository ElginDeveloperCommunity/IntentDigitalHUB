package com.elgin.flutter_intent_digital_hub_smartpos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    //Nome do CHANNEL utilizado para comunicação com o nativo
    private static final String CHANNEL = "elgin.intent_digital_hub";
    //Inteiro utilizado para filtrar o código da intent em @OnActivityResult pela chamada atual
    private static final int IDH_INTENTS_REQUESTCODE = 1000;
    //O scanner é a o único comando Intent Digital Hub que não é necessário enviar json, pois se trata apenas do ínicio da atividade de leitor de ćodigo de baaras.
    private final static int SCANNER_REQUESTCODE = 2000;
    //Variável que controlará o retorno da chamada
    private static MethodChannel.Result methodChannelResult;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            //Captura a referência ao retorno da chamada atual
                            methodChannelResult = result;

                            if (call.method.equals("startIntent")) {
                                startIntent(Objects.requireNonNull(call.argument("args")));
                            }
                        }
                );
    }

    private void startIntent(Map<String, String> argumentsMap) {
        final String commandJSONString = argumentsMap.get("commandJSON");
        final String intentPathString = argumentsMap.get("intentPath");

        //Cria a intent com o path do respectivo módulo
        final Intent intent = new Intent(intentPathString);

        //Como comentado no código na plataforma Flutter, o scanner é a único módulo do Intent Digital Hub que não possuí json de comando, pois a única atividade é a de ínicio do scan.

        if (commandJSONString != null) { // Se o o json de comando estiver null, a função iniciada foi a de scanner.
            Log.d("comando", commandJSONString);

            //Insere como 'extra' na intent, o comando
            intent.putExtra("comando", commandJSONString);

            startActivityForResult(intent, IDH_INTENTS_REQUESTCODE);
        } else { //Inicia a atividade de scanner com um código de requestcode diferente, para filtro em @onAcitivityResult.
            startActivityForResult(intent, SCANNER_REQUESTCODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IDH_INTENTS_REQUESTCODE) { //Captura o retorno do comando e devolve à plataforma Flutter.
            //O retorno é sempre um JSONArray, no para a aplicação implementada, apenas retornos com somente um comando terão seus dados em tela, portanto apenas o primeiro json é enviado de volta ao Flutter

            final String retorno = data.getStringExtra("retorno");

            Log.d("retorno", data.getStringExtra("retorno"));

            methodChannelResult.success(retorno);
        } else if (resultCode == 0 && requestCode == SCANNER_REQUESTCODE) { //O código de retorno de sucesso exclusivamente para a atividade scanner é 0;
            methodChannelResult.success(data.getStringExtra("retorno"));
        }
    }
}