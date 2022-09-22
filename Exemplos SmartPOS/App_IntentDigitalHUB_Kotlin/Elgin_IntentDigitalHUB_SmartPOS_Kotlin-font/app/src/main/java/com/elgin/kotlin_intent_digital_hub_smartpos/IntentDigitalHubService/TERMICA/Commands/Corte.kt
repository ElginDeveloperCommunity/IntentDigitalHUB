package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject


open class Corte(private val avanco: Int) : TermicaCommand("Corte") {

    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("avanco", avanco)
        return functionParametersJson
    }
}