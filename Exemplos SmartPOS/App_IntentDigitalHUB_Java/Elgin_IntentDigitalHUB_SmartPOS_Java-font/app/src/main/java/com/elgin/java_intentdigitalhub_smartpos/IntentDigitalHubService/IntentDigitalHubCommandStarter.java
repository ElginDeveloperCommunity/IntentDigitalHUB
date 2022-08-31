package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonArray;

import java.util.List;

/**
 * Classe service utilizada para iniciar os comandos do Intent Digital Hub
 */
final public class IntentDigitalHubCommandStarter {
    //Classe service, não deve ser possível instânciar
    private IntentDigitalHubCommandStarter() {}

    /**
     * Inicia um comando IDH, auxilia no uso dos comandos, evitando a repetição de código em todas as Activities que utilizam o IDH.
     *
     * @param activity                Contexto necessário da atividade que irá invocar a intent
     * @param intentDigitalHubCommand O comando parâmetrizado a ser iniciado
     * @param requestCode             O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
     */
    public static void startIDHCommandForResult(Activity activity, IntentDigitalHubCommand intentDigitalHubCommand, int requestCode) {
        //Captura o módulo filtro do correspondete do comando;
        final String modulePathOfCommand = intentDigitalHubCommand.correspondingIntentModule.getIntentPath();

        //Captura o json do comando correspondente em string.
        final String commandJson = intentDigitalHubCommand.getCommandJSON().toString();

        //Fornece à intent o path do comando.
        Intent intent = new Intent(modulePathOfCommand);

        //Insere, como extra, na intent o json de comando do comando correspondente, sob a chave "comando". Diferente da função abaixo, que já considera o comando como um Array, nesta deve ser inserido a formatação de array no comando, uma vez que
        //para o IDH é necessário enviar, sempre, um Array de Json, corresponde aos comandos, por este motivo é adicionado "[]" formatando este único comando que é inciado por essa função.
        intent.putExtra("comando", "[" + commandJson + "]");
        //Inicia a atividade com base na referência da atividade passada, enviando a intent configurada e o requestCode é utilizado para filtrar o retorno em @onActivityResult.
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Similar a função acima, porém esta função concatena uma lista de comandos IDH em um só comando, e o inicia.
     *
     * @param activity                    Contexto necessário da atividade que irá invocar a intent
     * @param intentDigitalHubCommandList A lista de comandos que será tranformada em um só comando através da concatenação de todos os comandos da lista formando um arrayJSon contendo todos os comandos enviados
     * @param requestCode                 O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
     */
    public static void startIDHCommandForResult(Activity activity, List<IntentDigitalHubCommand> intentDigitalHubCommandList, int requestCode) throws IllegalArgumentException {
        //A lista de comandos não pode estar vazia
        if (intentDigitalHubCommandList.isEmpty())
            throw new IllegalArgumentException("A lista de comandos a serem concatenadas não pode estar vazia!");

        if (!isIDHCommandListOfSameModule(intentDigitalHubCommandList))
            throw new IllegalArgumentException("Todos os comandos da lista devem pertencer ao mesmo módulo!");

        //Captura o módulo filtro do correspondete do comando, através de quaisquer elemento da lista.
        final String modulePathOfCommand = intentDigitalHubCommandList.get(0).correspondingIntentModule.getIntentPath();

        //Cria o json do comando correspondente em string, a partir da concatenação dos comandos da lista de comandos.
        final String commandJson = concatenateIDHCommandList(intentDigitalHubCommandList);

        //Fornece à intent o path do comando.
        Intent intent = new Intent(modulePathOfCommand);

        //Insere, como extra, na intent o json de comando do comando correspondente, sob a chave "comando".
        intent.putExtra("comando", commandJson);

        //Inicia a atividade com base na referência da atividade passada, enviando a intent configurada e o requestCode é utilizado para filtrar o retorno em @onActivityResult.
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Valida se a lista de comandos IDH pertencem ao mesmo módulo, uma vez que, uma lista de comandos com módulos (ELGINPAY, SCANNER, TERMICA...) não formaria um comando válido.
     *
     * @param intentDigitalHubCommandList A lista a ser validada.
     * @return boolean True caso a lista seja válida, false caso não seja.
     */
    private static boolean isIDHCommandListOfSameModule(List<IntentDigitalHubCommand> intentDigitalHubCommandList) {
        //Checa o modulo do primeiro comando da lista para comparação com todos posteriores
        IntentDigitalHubModule intentDigitalHubModuleBase = intentDigitalHubCommandList.get(0).correspondingIntentModule;

        for (IntentDigitalHubCommand intentDigitalHubCommand : intentDigitalHubCommandList)
            if (intentDigitalHubCommand.correspondingIntentModule != intentDigitalHubModuleBase)
                return false;
        return true;
    }

    /**
     * Insere todos os comandos IDH (da lista de comandos) em um só Array de comando, e retorna este Array como String.
     *
     * @param intentDigitalHubCommandList A lista a ser concatenada.
     * @return String array de comando concatenados, como string.
     */
    private static String concatenateIDHCommandList(List<IntentDigitalHubCommand> intentDigitalHubCommandList) {
        JsonArray commandArray = new JsonArray();

        for (IntentDigitalHubCommand intentDigitalHubCommand : intentDigitalHubCommandList) {
            commandArray.add(intentDigitalHubCommand.getCommandJSON());
        }

        return commandArray.toString();
    }
}