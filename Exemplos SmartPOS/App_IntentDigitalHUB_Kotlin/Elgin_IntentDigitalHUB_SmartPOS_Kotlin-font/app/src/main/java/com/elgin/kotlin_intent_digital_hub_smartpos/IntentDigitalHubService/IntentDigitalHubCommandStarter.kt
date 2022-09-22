package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.gson.JsonArray
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand

object IntentDigitalHubCommandStarter {

    /**
     * Inicia um comando IDH, auxilia no uso dos comandos, evitando a repetição de código em todas as Activities que utilizam o IDH.
     *
     * @param activity                Contexto necessário da atividade que irá invocar a intent
     * @param intentDigitalHubCommand O comando parâmetrizado a ser iniciado
     * @param requestCode             O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
     */
    fun startIDHCommandForResult(
        activity: Activity,
        intentDigitalHubCommand: IntentDigitalHubCommand,
        requestCode: Int
    ) {
        //Captura o módulo filtro do correspondete do comando;
        val modulePathOfCommand: String =
            intentDigitalHubCommand.correspondingIntentModule.intentPath

        //Captura o json do comando correspondente em string.
        val commandJson: String = intentDigitalHubCommand.getCommandJSON().toString()

        //Fornece à intent o path do comando.
        val intent = Intent(modulePathOfCommand)

        //Insere, como extra, na intent o json de comando do comando correspondente, sob a chave "comando". Diferente da função abaixo, que já considera o comando como um Array, nesta deve ser inserido a formatação de array no comando, uma vez que
        //para o IDH é necessário enviar, sempre, um Array de Json, corresponde aos comandos, por este motivo é adicionado "[]" formatando este único comando que é inciado por essa função.
        intent.putExtra("comando", "[$commandJson]")

        Log.d("comando", "[$commandJson]");

        //Inicia a atividade com base na referência da atividade passada, enviando a intent configurada e o requestCode é utilizado para filtrar o retorno em @onActivityResult.
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Similar a função acima, porém esta função concatena uma lista de comandos IDH em um só comando, e o inicia.
     *
     * @param activity                    Contexto necessário da atividade que irá invocar a intent
     * @param intentDigitalHubCommandList A lista de comandos que será tranformada em um só comando através da concatenação de todos os comandos da lista formando um arrayJSon contendo todos os comandos enviados
     * @param requestCode                 O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
     */
    @Throws(IllegalArgumentException::class)
    fun startIDHCommandForResult(
        activity: Activity,
        intentDigitalHubCommandList: List<IntentDigitalHubCommand>,
        requestCode: Int
    ) {
        //A lista de comandos não pode estar vazia
        require(!intentDigitalHubCommandList.isEmpty()) { "A lista de comandos a serem concatenadas não pode estar vazia!" }
        require(isIDHCommandListOfSameModule(intentDigitalHubCommandList)) { "Todos os comandos da lista devem pertencer ao mesmo módulo!" }

        //Captura o módulo filtro do correspondete do comando, através de quaisquer elemento da lista.
        val modulePathOfCommand: String? =
            intentDigitalHubCommandList[0].correspondingIntentModule.intentPath

        //Cria o json do comando correspondente em string, a partir da concatenação dos comandos da lista de comandos.
        val commandJson = concatenateIDHCommandList(intentDigitalHubCommandList)

        //Fornece à intent o path do comando.
        val intent = Intent(modulePathOfCommand)

        //Insere, como extra, na intent o json de comando do comando correspondente, sob a chave "comando".
        intent.putExtra("comando", commandJson)

        Log.d("comando", commandJson);

        //Inicia a atividade com base na referência da atividade passada, enviando a intent configurada e o requestCode é utilizado para filtrar o retorno em @onActivityResult.
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Valida se a lista de comandos IDH pertencem ao mesmo módulo, uma vez que, uma lista de comandos com módulos (ELGINPAY, SCANNER, TERMICA...) não formaria um comando válido.
     *
     * @param intentDigitalHubCommandList A lista a ser validada.
     * @return boolean True caso a lista seja válida, false caso não seja.
     */
    private fun isIDHCommandListOfSameModule(intentDigitalHubCommandList: List<IntentDigitalHubCommand>): Boolean {
        //Checa o modulo do primeiro comando da lista para comparação com todos posteriores
        val intentDigitalHubModuleBase = intentDigitalHubCommandList[0].correspondingIntentModule
        for (intentDigitalHubCommand in intentDigitalHubCommandList) if (intentDigitalHubCommand.correspondingIntentModule !== intentDigitalHubModuleBase) return false
        return true
    }

    /**
     * Insere todos os comandos IDH (da lista de comandos) em um só Array de comando, e retorna este Array como String.
     *
     * @param intentDigitalHubCommandList A lista a ser concatenada.
     * @return String array de comando concatenados, como string.
     */
    private fun concatenateIDHCommandList(intentDigitalHubCommandList: List<IntentDigitalHubCommand>): String {
        val commandArray = JsonArray()
        for (intentDigitalHubCommand in intentDigitalHubCommandList) {
            commandArray.add(intentDigitalHubCommand.getCommandJSON())
        }
        return commandArray.toString()
    }
}