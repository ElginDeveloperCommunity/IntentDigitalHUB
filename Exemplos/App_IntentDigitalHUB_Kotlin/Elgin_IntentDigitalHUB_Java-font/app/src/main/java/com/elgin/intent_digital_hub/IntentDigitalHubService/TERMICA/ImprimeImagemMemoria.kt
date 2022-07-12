package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class ImprimeImagemMemoria(private val key: String, private val scala: Int) :
    TermicaCommand("ImprimeImagemMemoria") {
    override fun functionParameters(): String {
        return "\"key\"" + ":" + "\"" + key + "\"" + "," +
                "\"scala\"" + ":" + scala
    }
}