package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject


class ImprimeImagemMemoria(private val key: String, private val scala: Int) :
    TermicaCommand("ImprimeImagemMemoria") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("key", key)
        functionParametersJson.addProperty("scala", scala)
        return functionParametersJson
    }
}