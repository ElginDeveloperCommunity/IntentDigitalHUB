package com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.SatCommand

class EnviarDadosVenda(
    private val numSessao: Int,
    private val codAtivacao: String,
    private val dadosVenda: String
) : SatCommand("EnviarDadosVenda") {
    override fun functionParameters(): String {
        return "\"numSessao\"" + ":" + numSessao + "," +
                "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                "\"dadosVenda\"" + ":" + "\"" + dadosVenda + "\""
    }
}