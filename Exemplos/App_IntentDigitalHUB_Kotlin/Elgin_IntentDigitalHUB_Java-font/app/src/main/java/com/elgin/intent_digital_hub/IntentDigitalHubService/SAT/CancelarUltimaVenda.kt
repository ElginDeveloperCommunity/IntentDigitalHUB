package com.elgin.intent_digital_hub.IntentDigitalHubService.SAT

import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.SatCommand

class CancelarUltimaVenda(
    private val numSessao: Int,
    private val codAtivacao: String,
    private val numeroCFe: String,
    private val dadosCancelamento: String
) : SatCommand("CancelarUltimaVenda") {
    override fun functionParameters(): String {
        return "\"numSessao\"" + ":" + numSessao + "," +
                "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                "\"numeroCFe\"" + ":" + "\"" + numeroCFe + "\"" + "," +
                "\"dadosCancelamento\"" + ":" + "\"" + dadosCancelamento + "\""
    }
}