package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.annotations.SerializedName

/**
 * Classe que generaliza todos os comandos do módulo TERMICA, definindo o módulo do comando e o seu tipo de retorno
 */

abstract class TermicaCommand protected constructor(functionName: String) :
    IntentDigitalHubCommand(functionName, IntentDigitalHubModule.TERMICA) {
    /**
     * O retorno dos comandos TERMICA é sempre um inteiro, as classes de comando são utilizadas, também, para deserializar os JSON de RETORNO
     * do IDH, portanto é definido um objeto para que seja possível serializar o retorno de um comando dentro de um objeto da classe correspondente
     * a esse comando; verifique a impĺementação de @onActivityResult nos módulos implementados no projeto.
     */

    @SerializedName("resultado")
    val resultado = 0
}