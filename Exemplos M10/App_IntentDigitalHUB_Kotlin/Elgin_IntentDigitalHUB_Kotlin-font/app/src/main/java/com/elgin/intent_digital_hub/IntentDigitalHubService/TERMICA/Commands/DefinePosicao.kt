package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class DefinePosicao(private val posicao: Int) : TermicaCommand("DefinePosicao") {
    override fun functionParameters(): String {
        return "\"posicao\"" + ":" + posicao
    }
}