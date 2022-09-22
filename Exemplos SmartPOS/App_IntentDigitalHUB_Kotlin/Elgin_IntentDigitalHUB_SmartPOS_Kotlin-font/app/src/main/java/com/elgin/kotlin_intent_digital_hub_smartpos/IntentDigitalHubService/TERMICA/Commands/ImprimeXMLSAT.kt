package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject


open class ImprimeXMLSAT(private val dados: String, private val param: Int) :
    TermicaCommand("ImprimeXMLSAT") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("dados", dados)
        functionParametersJson.addProperty("param", param)
        return functionParametersJson
    }
}