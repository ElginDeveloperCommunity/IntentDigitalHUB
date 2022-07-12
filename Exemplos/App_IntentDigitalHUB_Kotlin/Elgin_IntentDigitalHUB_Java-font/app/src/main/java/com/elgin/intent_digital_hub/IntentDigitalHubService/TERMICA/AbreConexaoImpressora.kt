package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class AbreConexaoImpressora(
    private val tipo: Int,
    private val modelo: String,
    private val conexao: String,
    private val parametro: Int
) : TermicaCommand("AbreConexaoImpressora") {
    override fun functionParameters(): String {
        return "\"tipo\"" + ":" + tipo + "," +
                "\"modelo\"" + ":" + "\"" + modelo + "\"" + "," +
                "\"conexao\"" + ":" + "\"" + conexao + "\"" + "," +
                "\"parametro\"" + ":" + parametro
    }
}