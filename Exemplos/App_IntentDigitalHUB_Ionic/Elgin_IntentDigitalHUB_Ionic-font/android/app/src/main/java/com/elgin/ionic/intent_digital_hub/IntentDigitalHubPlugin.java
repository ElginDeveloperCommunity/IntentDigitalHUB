package com.elgin.ionic.intent_digital_hub;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe que cria o plugin que será exportado para o Ionic durante e a inicialização, contém os métodos nativos.
 * A anotação @CapacitorPlugin define o nome com o qual o plugin será esportado (para ser importador atraǘes de Capacitor.Plugins).
 */
@CapacitorPlugin(name = "IntentDigitalHubPlugin",
        permissions = {
                @Permission(alias = "storage",
                        strings = {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        })
        })
public class IntentDigitalHubPlugin extends Plugin {
    //Keys utilizadas para os Json de resposta
    private final static String PERMISSION_REQUEST_RESPONSE_KEY = "permissionRequestResponse";
    private final static String INTENT_DIGITAL_HUB_RESPONSE_KEY = "intentDigitalHubResponse";

    /**
     * Pede a permissão de acesso ao diretório externo da aplicação, necessária para salvar os arquivos XMLs e de imagem dentro
     * do diretório da aplicação, para que posteriormente seja enviado o comando via PATH. O resultado do request de permissão é resolvido em {@link #storagePermsCallback(PluginCall)}.
     */
    @PluginMethod
    public void askWriteExternalStoragePermission(PluginCall call) {
        Log.d("DEBUG", "called!");
        if (getPermissionState("storage") != PermissionState.GRANTED) {
            requestPermissionForAlias("storage", call, "storagePermsCallback");
        } else {
            call.resolve(new JSObject().put(PERMISSION_REQUEST_RESPONSE_KEY, true));
        }
    }

    /**
     * Lida com o resultado do request de permissão que a função {@link #askWriteExternalStoragePermission(PluginCall)} executa e devolve ao Ionic o resultado.
     */
    @PermissionCallback
    private void storagePermsCallback(PluginCall call) {
        if (getPermissionState("storage") == PermissionState.GRANTED) {
            call.resolve(new JSObject().put(PERMISSION_REQUEST_RESPONSE_KEY, true));
        } else {
            call.resolve(new JSObject().put(PERMISSION_REQUEST_RESPONSE_KEY, false));
        }
    }


    /**
     * Salva, no diretório externo da aplicação, que é criado caso ainda não exista em {@link #getRootDirectoryPATH()}.
     *
     * @param call Json que deve conter:
     *             String xmlContentInString: o conteúdo do xml a ser salvo, ou seja, o XMl em string.
     *             String xmlFileName: o nome com o qual o xml será salvo no diretório da aplicação.
     */
    @PluginMethod
    public void storeXmlFile(PluginCall call) {
        final String xmlContentInString = call.getString("xmlContentInString");
        final String xmlFileName = call.getString("xmlFileName");

        File newXmlArchive = new File(getRootDirectoryPATH() + File.separator + xmlFileName + ".xml");

        //Não é necessário criar novamente o arquivo, caso o mesmo já exista.
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

        call.resolve();
    }

    /**
     * Cria, caso não exista, o diretório raiz da aplicação que será ultilizado para salvar os XMLs e a imagem no módulo de impressão de imagem, fornece como retorno do path do diretório (Android/data/com.elgin.intent_digital_hub/files/)
     *
     * @return String path do diretório da aplicação
     */
    private String getRootDirectoryPATH() {
        final File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + this.getActivity().getApplicationContext().getPackageName()
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

    /**
     * Inicia uma intent por resultado, construíndo a intent com o padrão para o Intent Digital Hub, o resultado é devolvido em {@link #handleIntentDigitalHubResult(PluginCall, ActivityResult)}.
     *
     * @param call Json que deve conter:
     *             String commandJson: O JSON de comando correspondente ao comando que deverá ser executado pelo Intent Digital Hub.
     *             String idhModuleFilter: O filtro de módulo que a intent receberá, cada comando pertence à um módulo(Ex: TERMICA, BRIDGE, SAT..) e este deve ser inserido como path da intent.
     */
    @PluginMethod
    public void startDigitalHubIntent(PluginCall call) {
        final String commandJson = call.getString("commandJson");
        final String idhModuleFilter = call.getString("idhModuleFilter");

        Log.d("DEBUG_1", commandJson + " " + idhModuleFilter);

        //Instancia a intent com o filtro de módulo.
        Intent intentForDigitalHub = new Intent(idhModuleFilter);

        //Adiciona o comando à intent com a key padrão "comando".
        intentForDigitalHub.putExtra("comando", commandJson);

        startActivityForResult(call, intentForDigitalHub, "handleIntentDigitalHubResult");
    }

    /**
     * Função que lida com o resultado da intent iniciada em {@link #startDigitalHubIntent(PluginCall)}.
     * Devolve ao Ionic o json-resultado do comando IDH resultado.
     */
    @ActivityCallback
    private void handleIntentDigitalHubResult(PluginCall call, ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultIntent = result.getData();

            Log.d("DEBUG_2", resultIntent.getStringExtra("retorno"));
            //O resultado estará sempre no extra da intent de resultado, com a key "retorno".
            call.resolve(new JSObject().put(INTENT_DIGITAL_HUB_RESPONSE_KEY, resultIntent.getStringExtra("retorno")));
        }
    }

}
