package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject


class ImpressaoCodigoBarras(
    private val tipo: Int,
    private val dados: String,
    private val altura: Int,
    private val largura: Int,
    private val HRI: Int
) :
    TermicaCommand("ImpressaoCodigoBarras") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("tipo", tipo)
        functionParametersJson.addProperty("dados", dados)
        functionParametersJson.addProperty("altura", altura)
        functionParametersJson.addProperty("largura", largura)
        functionParametersJson.addProperty("HRI", HRI)
        return functionParametersJson
    }
}