package com.reactnative_intentdigitalhub;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * É necessário implementar @ActivityEventListener para que o módulo consiga receber os resultado de uma intent iniciada (https://reactnative.dev/docs/native-modules-android#getting-activity-result-from-startactivityforresult)
 */
public class IntentDigitalHubNativeModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    IntentDigitalHubNativeModule(ReactApplicationContext context) {
        super(context);
        //Registra este módulo como ActivityResultListener
        context.addActivityEventListener(this);
    }

    //Inteiro utilizado para filtrar o código da intent em @OnActivityResult pela chamada atual
    private static final int IDH_INTENTS_REQUESTCODE = 1000;

    //Inteiro utilizado para a requisição de permissão de acesso ao diretório externo da aplicação
    private final static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;

    //Utilizado para referenciar a ultima função chamada
    private Callback lastCallback = null;

    @NonNull
    @Override
    public String getName() {
        return "IntentDigitalHubNativeModule";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        return super.getConstants();
    }


    @ReactMethod
    public void startIntentForResult(String commandJson, String intentPath, Callback callback) {
        //Guarda a referência do callback atual, para emitir o retorno corretamente em onActivityResult
        lastCallback = callback;

        Log.d("DEBUG_1", commandJson + " " + intentPath);

        //Cria a intent com o path do respectivo módulo
        final Intent intent = new Intent(intentPath);

        //Insere como 'extra' na intent, o comando
        intent.putExtra("comando", commandJson);

        getCurrentActivity().startActivityForResult(intent, IDH_INTENTS_REQUESTCODE);
    }

    /**
     * Cria arquivo .XML no diretório raiz da aplicação
     *
     * @param xmlContentInString Conteúdo do .XML a ser salvo
     * @param xmlFileName        Nome do arquivo xml
     */
    @ReactMethod
    public void storeXmlFile(String xmlContentInString, String xmlFileName) {
        File newXmlArchive = new File(getRootDirectoryPATH() + File.separator + xmlFileName + ".xml");

        //Não é necessário criar novamente o arquivo, caso o mesmo já exista
        if (!newXmlArchive.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(newXmlArchive);
                fileWriter.append(xmlContentInString);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == IDH_INTENTS_REQUESTCODE) {
            //O retorno é sempre um JSONArray, no para a aplicação implementada, apenas retornos com somente um comando terão seus dados em tela, portanto apenas o primeiro json é enviado de volta ao Flutter
            try {
                final String retorno = data.getStringExtra("retorno");
                Log.d("DEBUG_2", retorno);

                JSONArray jsonArray = new JSONArray(retorno);
                JSONObject jsonObjectReturn = jsonArray.getJSONObject(0);

                lastCallback.invoke(jsonArray.toString());
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                lastCallback.invoke("O formato do JSON enviado está inválido ou o app DigitalHub não está instalado!");
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
    }


    /**
     * Cria, caso não exista, o diretório raiz da aplicação que será ultilizado para salvar os XMLs e a imagem no módulo de impressão de imagem, fornece como retorno do path do diretório (Android/data/com.elgin.intent_digital_hub/files/)
     *
     * @return String path do diretório da aplicação
     */
    private String getRootDirectoryPATH() {
        final File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getCurrentActivity().getApplicationContext().getPackageName()
                + "/files");

        //Cria o diretório que a aplicação utilizara para salvar as mídias, caso não exista
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Se não foi possível criar o diretório, a exceção será lançada
                throw new SecurityException("Permissão não garantida para a criação do diretório externo da aplicação!");
            }
        }

        return mediaStorageDir.getPath();
    }

}
