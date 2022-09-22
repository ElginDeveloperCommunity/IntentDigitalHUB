import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

object ActivityUtils {
    /**
     * Função utilitária que inicia uma nova atividade
     *
     * @param sourceActivity       Contexto necessário da atividade que irá invocar a atividade alvo
     * @param activityClassToStart Classe que representa a Ativity alvos
     */
    fun startNewActivity(sourceActivity: Activity, activityClassToStart: Class<*>?) {
        val intent = Intent(sourceActivity, activityClassToStart)
        sourceActivity.startActivity(intent)
    }

    /**
     * Função utilitária que cria um alert e os mostra
     *
     * @param activityContext Contexto necessário para a função
     * @param alertTitle      Título do Alert
     * @param alertMessage    Texto corpo do Alert
     */
    fun showAlertMessage(activityContext: Context?, alertTitle: String?, alertMessage: String?) {
        val alertDialog = AlertDialog.Builder(activityContext).create()
        alertDialog.setTitle(alertTitle)
        alertDialog.setMessage(alertMessage)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog.show()
    }

    /**
     * Cria, caso não exista, o diretório raiz da aplicação que será ultilizado para salvar  a imagem no módulo de impressão de imagem, fornece como retorno do path do diretório. (Android/data/com.elgin.intent_digital_hub/files/)
     *
     * @param activity Contexto necessário para a função
     * @return String path do diretório da aplicação
     */
    fun getRootDirectoryPATH(activity: Activity): String {
        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/Android/data/"
                    + activity.applicationContext.packageName
                    + "/files"
        )

        //Cria o diretório que a aplicação utilizara para salvar as mídias, caso não exista
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Se não foi possível criar o diretório, a exceção será lançada
                throw SecurityException("Permissão não garantida para a criação do diretório externo da aplicação!")
            }
        }
        return mediaStorageDir.path
    }

    /**
     * Lẽ os XMLs do projeto, que estão salvos em res/raw, e retorna o seu conteúdo em String.
     *
     * @param activityForReference Contexto necessário para a função
     * @param selectedXmlFromProject Arquivo xml a ser lido do projeto.
     * @return xmlReadInString String contendo o texto do arquivo XMl lido
     */
    fun readXmlFileFromProjectAsString(
        activityForReference: Activity,
        selectedXmlFromProject: ProjectXml
    ): String {
        val xmlReadInString: String

        //Todos os .XMLs advindos do projeto estão em res/raw
        val ins = activityForReference.resources.openRawResource(
            activityForReference.resources.getIdentifier(
                selectedXmlFromProject.archiveNameInProject,
                "raw",
                activityForReference.packageName
            )
        )
        val br = BufferedReader(InputStreamReader(ins))
        val sb = StringBuilder()
        var line: String? = null
        try {
            line = br.readLine()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        while (line != null) {
            sb.append(line)
            sb.append(System.lineSeparator())
            try {
                line = br.readLine()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        xmlReadInString = sb.toString()
        return xmlReadInString
    }

    //Arquivos XML disṕoníveis do projeto.
    enum class ProjectXml(  //Nome do arquivo XML do projeto, localizados em res/raw.
        val archiveNameInProject: String
    ) {
        XML_NFCE("xmlnfce"), XML_SAT("xmlsat");

    }
}
