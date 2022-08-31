package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class ImprimeImagem(private val path: String) : TermicaCommand("ImprimeImagem") {
    override fun functionParameters(): String {
        return "\"path\"" + ":" + "\"" + path + "\""
    }
}