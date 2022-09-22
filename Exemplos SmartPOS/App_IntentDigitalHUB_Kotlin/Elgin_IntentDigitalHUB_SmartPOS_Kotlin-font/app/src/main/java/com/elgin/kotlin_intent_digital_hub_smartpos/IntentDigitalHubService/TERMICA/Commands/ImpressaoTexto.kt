package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand

import com.google.gson.JsonObject


class ImpressaoTexto(
    private val dados: String,
    private val posicao: Int,
    private val stilo: Int,
    private val tamanho: Int)
    : TermicaCommand("ImpressaoTexto") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("dados", dados)
        functionParametersJson.addProperty("posicao", posicao)
        functionParametersJson.addProperty("stilo", stilo)
        functionParametersJson.addProperty("tamanho", tamanho)
        return functionParametersJson
    }
}
