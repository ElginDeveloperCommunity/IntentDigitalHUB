package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject


class StatusImpressora(private val param: Int) : TermicaCommand("StatusImpressora") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("param", param)
        return functionParametersJson
    }
}