package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Classe que generaliza todos os comandos do módulo TERMICA, definindo o módulo do comando e o seu tipo de retorno
 */
abstract class TermicaCommand protected constructor(functionName: String?) :
    IntentDigitalHubCommand(functionName, IntentDigitalHubModule.TERMICA) {
    //O retorno dos comandos de impressora é sempre um int
    @SerializedName("resultado")
    private var resultado: Int = 0

    open fun getResultado(): Int {
        return resultado
    }
}

