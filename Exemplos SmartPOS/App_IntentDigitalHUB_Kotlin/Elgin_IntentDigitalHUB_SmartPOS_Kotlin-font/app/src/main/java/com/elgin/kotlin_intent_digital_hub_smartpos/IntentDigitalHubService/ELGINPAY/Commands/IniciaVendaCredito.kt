package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.JsonObject

class IniciaVendaCredito(private val valorTotal: String, private val tipoFinanciamento: Int, private val numeroParcelas: Int ) : ElginPayCommand(functionName ="iniciaVendaCredito") {

    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("valorTotal", this.valorTotal)
        functionParametersJson.addProperty("tipoFinanciamento", this.tipoFinanciamento)
        functionParametersJson.addProperty("numeroParcelas", this.numeroParcelas)
        return functionParametersJson
    }
}