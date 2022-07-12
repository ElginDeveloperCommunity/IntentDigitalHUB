package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class ImpressaoQRCode(
    private val dados: String,
    private val tamanho: Int,
    private val nivelCorrecao: Int
) : TermicaCommand("ImpressaoQRCode") {
    override fun functionParameters(): String {
        return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                "\"tamanho\"" + ":" + tamanho + "," +
                "\"nivelCorrecao\"" + ":" + nivelCorrecao
    }
}