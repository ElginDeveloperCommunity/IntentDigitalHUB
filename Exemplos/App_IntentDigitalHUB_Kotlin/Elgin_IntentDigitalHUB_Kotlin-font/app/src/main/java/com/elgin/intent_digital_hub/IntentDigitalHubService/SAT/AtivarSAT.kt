package com.elgin.intent_digital_hub.IntentDigitalHubService.SAT

import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.SatCommand

class AtivarSAT(
    private val numSessao: Int,
    private val subComando: Int,
    private val codAtivacao: String,
    private val cnpj: String,
    private val cUF: Int
) : SatCommand("AtivarSAT") {
    override fun functionParameters(): String {
        return "\"numSessao\"" + ":" + numSessao + "," +
                "\"subComando\"" + ":" + subComando + "," +
                "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                "\"cnpj\"" + ":" + "\"" + cnpj + "\"" + "," +
                "\"cUF\"" + ":" + cUF
    }
}