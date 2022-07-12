package com.elgin.intent_digital_hub.IntentDigitalHubService.SAT

import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.SatCommand

class AssociarAssinatura(
    private val numSessao: Int,
    private val codAtivacao: String,
    private val cnpjSH: String,
    private val assinaturaAC: String
) : SatCommand("AssociarAssinatura") {
    override fun functionParameters(): String {
        return "\"numSessao\"" + ":" + numSessao + "," +
                "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                "\"cnpjSH\"" + ":" + "\"" + cnpjSH + "\"" + "," +
                "\"assinaturaAC\"" + ":" + "\"" + assinaturaAC + "\""
    }
}