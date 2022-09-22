package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.JsonObject

class IniciaVendaDebito( private val valorTotal: String) : ElginPayCommand(functionName = "iniciaVendaDebito") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("valorTotal", valorTotal)
        return functionParametersJson
    }
}