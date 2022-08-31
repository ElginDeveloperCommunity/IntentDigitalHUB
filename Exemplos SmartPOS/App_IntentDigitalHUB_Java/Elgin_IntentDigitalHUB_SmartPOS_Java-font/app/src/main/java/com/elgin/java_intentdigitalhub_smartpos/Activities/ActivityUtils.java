package com.elgin.java_intentdigitalhub_smartpos.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Classe que utilidades que todas as atividades podem utilizar, reduzindo a repetição de código em funcionalidades com processos similares
 */

final public class ActivityUtils {
    //Classe utilitária, não deve ser possível instãnciar
    private ActivityUtils() {
    }

    /**
     * Função utilitária que inicia uma nova atividade
     *
     * @param sourceActivity       Contexto necessário da atividade que irá invocar a atividade alvo
     * @param activityClassToStart Classe que representa a Ativity alvos
     */
    public static void startNewActivity(Activity sourceActivity, Class<?> activityClassToStart) {
        final Intent intent = new Intent(sourceActivity, activityClassToStart);
        sourceActivity.startActivity(intent);
    }

    /**
     * Função utilitária que cria um alert e os mostra
     *
     * @param activityContext Contexto necessário para a função
     * @param alertTitle      Título do Alert
     * @param alertMessage    Texto corpo do Alert
     */

    public static void showAlertMessage(Context activityContext, String alertTitle, String alertMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(activityContext).create();
        alertDialog.setTitle(alertTitle);
        alertDialog.setMessage(alertMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    /**
     * Cria, caso não exista, o diretório raiz da aplicação que será ultilizado para salvar  a imagem no módulo de impressão de imagem, fornece como retorno do path do diretório. (Android/data/com.elgin.intent_digital_hub/files/)
     *
     * @param activity Contexto necessário para a função
     * @return String path do diretório da aplicação
     */
    public static String getRootDirectoryPATH(Activity activity) {
        final File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
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

    //Arquivos XML disṕoníveis do projeto.
    public enum ProjectXml {
        XML_NFCE("xmlnfce"),
        XML_SAT("xmlsat");

        //Nome do arquivo XML do projeto, localizados em res/raw.
        final private String archiveNameInProject;

        ProjectXml(String archiveNameInProject) {
           this.archiveNameInProject = archiveNameInProject;
        }
    }

    /**
     * Lẽ os XMLs do projeto, que estão salvos em res/raw, e retorna o seu conteúdo em String.
     *
     * @param activityForReference Contexto necessário para a função
     * @param selectedXmlFromProject Arquivo xml a ser lido do projeto.
     * @return xmlReadInString String contendo o texto do arquivo XMl lido
     */
    public static String readXmlFileFromProjectAsString(Activity activityForReference, ProjectXml selectedXmlFromProject) {
        final String xmlReadInString;

        //Todos os .XMLs advindos do projeto estão em res/raw
        InputStream ins = activityForReference.getResources().openRawResource(
                activityForReference.getResources().getIdentifier(
                        selectedXmlFromProject.archiveNameInProject,
                        "raw",
                        activityForReference.getPackageName()
                )
        );

        BufferedReader br = new BufferedReader(new InputStreamReader(ins));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        xmlReadInString = sb.toString();

        return xmlReadInString;
    }
}
