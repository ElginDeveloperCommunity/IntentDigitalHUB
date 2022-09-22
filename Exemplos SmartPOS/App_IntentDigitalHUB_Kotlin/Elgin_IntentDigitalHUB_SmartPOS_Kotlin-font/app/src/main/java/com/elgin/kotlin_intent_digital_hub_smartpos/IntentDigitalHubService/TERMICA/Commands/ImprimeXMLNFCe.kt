package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands


import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject


open class ImprimeXMLNFCe(
    private val dados: String,
    private val indexcsc: Int,
    private val csc: String,
    private val param: Int
) :
    TermicaCommand("ImprimeXMLNFCe") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("dados", dados)
        functionParametersJson.addProperty("indexcsc", indexcsc)
        functionParametersJson.addProperty("csc", csc)
        functionParametersJson.addProperty("param", param)
        return functionParametersJson
    }
}
