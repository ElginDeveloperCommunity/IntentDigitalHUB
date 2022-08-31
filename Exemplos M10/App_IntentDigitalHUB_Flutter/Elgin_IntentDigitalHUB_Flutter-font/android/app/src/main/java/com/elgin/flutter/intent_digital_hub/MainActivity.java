package com.elgin.flutter.intent_digital_hub;

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

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    //Nome do CHANNEL utilizado para comunicação com o nativo
    private static final String CHANNEL = "elgin.intent_digital_hub";
    //Inteiro utilizado para filtrar o código da intent em @OnActivityResult pela chamada atual
    private static final int IDH_INTENTS_REQUESTCODE = 1000;
    //Inteiro utilizado para filtrar a requisição de permissão
    private final static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2000;
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

                            if (call.method.equals("startIntent"))
                                startIntent(call.argument("args"));
                            else if (call.method.equals("askExternalStoragePermission"))
                                askWriteExternalStoragePermission();

                        }
                );
    }

    private void startIntent(Map<String, Object> argumentsMap) {
        final String commandJSONString = (String) argumentsMap.get("commandJSON");
        final String intentPathString = (String) argumentsMap.get("intentPath");

        //Cria a intent com o path do respectivo módulo
        final Intent intent = new Intent(intentPathString);

        //Insere como 'extra' na intent, o comando
        intent.putExtra("comando", commandJSONString);

        startActivityForResult(intent, IDH_INTENTS_REQUESTCODE);
    }

    //Pede a permissão de acesso ao diretório externo
    private void askWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Impede que a aplicação continue caso a permissão seja negada, uma vez que vários módulos dependem da permissão de acesso ao armazenamento
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            //Envia a resposta da requisição de volta ao Flutter
            methodChannelResult.success(false);
        } else if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            methodChannelResult.success(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IDH_INTENTS_REQUESTCODE) {
            //O retorno é sempre um JSONArray, no para a aplicação implementada, apenas retornos com somente um comando terão seus dados em tela, portanto apenas o primeiro json é enviado de volta ao Flutter
            try {
                final String retorno = data.getStringExtra("retorno");

                Log.d("retorno", data.getStringExtra("retorno"));

                JSONArray jsonArray = new JSONArray(retorno);
                JSONObject jsonObjectReturn = jsonArray.getJSONObject(0);

                methodChannelResult.success(jsonArray.toString());
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                methodChannelResult.error("JSON_FORMAT_ERROR", "O formato do JSON enviado está invalido ou o app DigitalHub não está instalado!", null);
            }
        }
    }
}