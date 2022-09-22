package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands

import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubModule
import com.google.gson.JsonObject


class SetPersonalizacao(
    private val iconeToolbar: String?,
    private val fonte: String?,
    private val corFonte: String?,
    private val corFonteTeclado: String?,
    private val corFundoToolbar: String?,
    private val corFundoTela: String?,
    private val corTeclaLiberadaTeclado: String?,
    private val corFundoTeclado: String?,
    private val corTextoCaixaEdicao: String?,
    private val corSeparadorMenu: String?
): ElginPayCommand("setPersonalizacao") {


    override fun functionParametersJson(): JsonObject? {
        val functionParametersJson = JsonObject()
        functionParametersJson.addProperty("iconeToolbar", iconeToolbar)
        functionParametersJson.addProperty("fonte", fonte)
        functionParametersJson.addProperty("corFonte", corFonte)
        functionParametersJson.addProperty("corFonteTeclado", corFonteTeclado)
        functionParametersJson.addProperty("corFundoToolbar", corFundoToolbar)
        functionParametersJson.addProperty("corFundoTela", corFundoTela)
        functionParametersJson.addProperty("corTeclaLiberadaTeclado", corTeclaLiberadaTeclado)
        functionParametersJson.addProperty("corFundoTeclado", corFundoTeclado)
        functionParametersJson.addProperty("corTextoCaixaEdicao", corTextoCaixaEdicao)
        functionParametersJson.addProperty("corSeparadorMenu", corSeparadorMenu)
        return functionParametersJson
    }
}