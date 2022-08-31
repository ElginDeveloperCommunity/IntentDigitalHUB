package com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Classe que generaliza todos os comandos do módulo BALANCA, definindo o módulo do comando e o seu tipo de retorno
 */

//O módulo balança possuí diferentes tipos de retorno para cada função, por isto é utilizado Generics para o serializável

abstract class BalancaCommand<T> protected constructor(functionName: String?) : IntentDigitalHubCommand(functionName, IntentDigitalHubModule.BALANCA), Serializable  {
    @SerializedName("resultado")
    val resultado : T? = null;
}