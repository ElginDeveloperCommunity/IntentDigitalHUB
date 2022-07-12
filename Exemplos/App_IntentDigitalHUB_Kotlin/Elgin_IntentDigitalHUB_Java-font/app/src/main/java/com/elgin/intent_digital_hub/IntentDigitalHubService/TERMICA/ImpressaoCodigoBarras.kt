package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class ImpressaoCodigoBarras(
    private val tipo: Int,
    private val dados: String,
    private val altura: Int,
    private val largura: Int,
    private val HRI: Int
) : TermicaCommand("ImpressaoCodigoBarras") {
    override fun functionParameters(): String {
        return "\"tipo\"" + ":" + tipo + "," +
                "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                "\"altura\"" + ":" + altura + "," +
                "\"largura\"" + ":" + largura + "," +
                "\"HRI\"" + ":" + HRI
    }
}