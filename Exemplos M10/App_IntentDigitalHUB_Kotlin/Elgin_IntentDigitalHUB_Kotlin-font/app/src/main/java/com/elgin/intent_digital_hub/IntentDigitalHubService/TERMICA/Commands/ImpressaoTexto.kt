package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class ImpressaoTexto(
    private val dados: String,
    private val posicao: Int,
    private val stilo: Int,
    private val tamanho: Int
) : TermicaCommand("ImpressaoTexto") {
    override fun functionParameters(): String {
        return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                "\"posicao\"" + ":" + posicao + "," +
                "\"stilo\"" + ":" + stilo + "," +
                "\"tamanho\"" + ":" + tamanho
    }
}