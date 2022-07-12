package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class AvancaPapel(private val linhas: Int) : TermicaCommand("AvancaPapel") {
    override fun functionParameters(): String {
        return "\"linhas\"" + ":" + linhas
    }
}