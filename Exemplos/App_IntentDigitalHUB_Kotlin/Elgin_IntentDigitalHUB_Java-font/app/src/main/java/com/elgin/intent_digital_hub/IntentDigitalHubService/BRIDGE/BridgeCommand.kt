package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Classe que generaliza todos os comandos do módulo BRIDGE, definindo o módulo do comando e o seu tipo de retorno
 */
abstract class BridgeCommand protected constructor(functionName: String?) :
    IntentDigitalHubCommand(functionName, IntentDigitalHubModule.BRIDGE), Serializable {
    //O retorno dos comandos bridge é sempre um JSON em String
    @SerializedName("resultado")
    val resultado: String? = null
}