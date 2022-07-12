package com.elgin.intent_digital_hub.IntentDigitalHubService

import android.app.Activity
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import android.content.Intent
import kotlin.Throws
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubModule
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

/**
 * Classe service utilizada para iniciar os comandos do Intent Digital Hub
 */
object IntentDigitalHubCommandStarter {
    /**
     * Função que inicializa um comando do IDH, reduz a repetição no start da intent
     *
     * @param activity                Contexto necessário da atividade que irá invocar a intent
     * @param intentDigitalHubCommand O comando parâmetrizado a ser iniciado
     * @param requestCode             O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
     */
    fun startHubCommandActivity(
        activity: Activity,
        intentDigitalHubCommand: IntentDigitalHubCommand,
        requestCode: Int
    ) {
        //Captura o módulo intent correspondente da função
        val modulePathOfCommand = intentDigitalHubCommand.correspondingIntentModule.intentPath
        val intent = Intent(modulePathOfCommand)
        intent.putExtra("comando", intentDigitalHubCommand.commandJSON)
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Overload da função de inicio de comando, permitindo a inicialização de vários comandos, basta fornecer uma List com todos os comandos
     *
     * @param activity                    Contexto necessário da atividade que irá invocar a intent
     * @param intentDigitalHubCommandList A lista de comandos que será tranformada em um só comando através da concatenação de todos os comandos da lista formando um arrayJSon contendo todos os comandos enviados
     * @param requestCode                 O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun startHubCommandActivity(
        activity: Activity,
        intentDigitalHubCommandList: List<IntentDigitalHubCommand>,
        requestCode: Int
    ) {
        //A lista de comandos não pode estar vazia
        require(!intentDigitalHubCommandList.isEmpty()) { "A lista de comandos a serem concatenadas não pode estar vazia!" }
        require(validateCommandList(intentDigitalHubCommandList)) { "Todos os comandos da lista devem pertencer ao mesmo módulo!" }

        //Verifica de qual modulo são os comandos da lista
        val modulePathOfCommand =
            intentDigitalHubCommandList[0].correspondingIntentModule.intentPath
        val digitalHubCommandJSON = concatenateDigitalHubCommands(intentDigitalHubCommandList)
        val intent = Intent(modulePathOfCommand)
        intent.putExtra("comando", digitalHubCommandJSON)
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Cria o comando JSON com todos os comandos da lista formatado
     */
    private fun concatenateDigitalHubCommands(intentDigitalHubCommandList: List<IntentDigitalHubCommand>): String {
        val concatenatedDigitalHubCommand = StringBuilder()
        for (intentDigitalHubCommand in intentDigitalHubCommandList) {
            //Remove o fechamento de parênteses de todos os comandos
            val actualDigitalHubCommandJSON = intentDigitalHubCommand.commandJSON.substring(
                1,
                intentDigitalHubCommand.commandJSON.length - 1
            )
            //Adiciona uma virgula para separar uma nova função
            concatenatedDigitalHubCommand.append(actualDigitalHubCommandJSON).append(",")
        }

        //Remove a ultima vírgula inserida
        concatenatedDigitalHubCommand.deleteCharAt(concatenatedDigitalHubCommand.length - 1)

        //Fecha o JSON concatenado com os parênteses []
        concatenatedDigitalHubCommand.insert(0, "[")
        concatenatedDigitalHubCommand.insert(concatenatedDigitalHubCommand.length, "]")
        return concatenatedDigitalHubCommand.toString().trim { it <= ' ' }
    }

    /**
     * Os comandos na lista a serem concatenados em um só comando não podem diferir entre módulo, um comando com múltiplas funções devem sempre fazer parte do mesmo módulo
     */
    private fun validateCommandList(intentDigitalHubCommandList: List<IntentDigitalHubCommand>): Boolean {
        //Checa o modulo do primeiro comando da lista para comparação com todos posteriores
        val intentDigitalHubModuleBase = intentDigitalHubCommandList[0].correspondingIntentModule
        for (intentDigitalHubCommand in intentDigitalHubCommandList) if (intentDigitalHubCommand.correspondingIntentModule != intentDigitalHubModuleBase) return false
        return true
    }
}