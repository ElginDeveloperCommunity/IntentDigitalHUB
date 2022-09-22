package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.annotations.SerializedName
import java.io.Serializable


abstract class ElginPayCommand protected constructor(functionName: String?) :
    IntentDigitalHubCommand(functionName!!, IntentDigitalHubModule.ELGINPAY), Serializable {
    /**
     * O retorno dos comandos ELGINPAY é sempre uma string, as classes de comando são utilizadas, também, para deserializar os JSON de RETORNO
     * do IDH, portanto é definido um objeto para que seja possível serializar o retorno de um comando dentro de um objeto da classe correspondente
     * a esse comando; verifique a impĺementação de @onActivityResult nos módulos implementados no projeto.
     */
    @SerializedName("resultado")
    val resultado: String? = null



}
