package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class ImprimeXMLSAT(private val dados: String, private val param: Int) :
    TermicaCommand("ImprimeXMLSAT") {
    override fun functionParameters(): String {
        return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                "\"param\"" + ":" + param
    }
}