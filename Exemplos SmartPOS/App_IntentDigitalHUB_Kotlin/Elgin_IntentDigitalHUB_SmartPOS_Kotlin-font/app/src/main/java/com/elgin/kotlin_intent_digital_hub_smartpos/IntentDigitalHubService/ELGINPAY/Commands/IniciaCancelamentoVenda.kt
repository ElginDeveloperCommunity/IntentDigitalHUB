package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand
import com.google.gson.JsonObject

class IniciaCancelamentoVenda(
    private val valorTotal: String,
    private val ref: String,
    private val data: String
) :
    ElginPayCommand("iniciaCancelamentoVenda") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("valorTotal", valorTotal)
        functionParametersJson.addProperty("ref", ref)
        functionParametersJson.addProperty("data", data)
        return functionParametersJson
    }
}

