package com.elgin.intent_digital_hub.IntentDigitalHubService.SAT

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Classe que generaliza todos os comandos SAT definindo o módulo do comando e o seu tipo de retorno
 */
abstract class SatCommand protected constructor(functionName: String?) :
    IntentDigitalHubCommand(functionName, IntentDigitalHubModule.SAT), Serializable {
    //O retorno dos comandos SAT é sempre uma String
    @SerializedName("resultado")
    val resultado: String? = null
}