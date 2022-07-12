package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class Corte(private val avanco: Int) : TermicaCommand("Corte") {
    override fun functionParameters(): String {
        return "\"avanco\"" + ":" + avanco
    }
}