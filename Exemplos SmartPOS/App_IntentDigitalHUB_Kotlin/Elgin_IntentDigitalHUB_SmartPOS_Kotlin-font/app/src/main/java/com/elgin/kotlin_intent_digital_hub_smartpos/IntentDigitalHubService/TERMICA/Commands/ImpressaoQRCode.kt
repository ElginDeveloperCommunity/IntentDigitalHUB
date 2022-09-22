package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject


open class ImpressaoQRCode(
    private val dados: String,
    private val tamanho: Int,
    private val nivelCorrecao: Int)
    : TermicaCommand("ImpressaoQRCode") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("dados", dados)
        functionParametersJson.addProperty("tamanho", tamanho)
        functionParametersJson.addProperty("nivelCorrecao", nivelCorrecao)
        return functionParametersJson
    }
}
