package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand
import com.google.gson.JsonObject

class AbreConexaoImpressora(
    private val tipo: Int,
    private val modelo: String,
    private val conexao: String,
    private val parametro: Int) :
    TermicaCommand("AbreConexaoImpressora") {
    override fun functionParametersJson(): JsonObject {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("tipo", tipo)
        functionParametersJson.addProperty("modelo", modelo)
        functionParametersJson.addProperty("conexao", conexao)
        functionParametersJson.addProperty("parametro", parametro)
        return functionParametersJson
    }
}