package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class ImprimeXMLNFCe(
    private val dados: String,
    private val indexcsc: Int,
    private val csc: String,
    private val param: Int
) : TermicaCommand("ImprimeXMLNFCe") {
    override fun functionParameters(): String {
        return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                "\"indexcsc\"" + ":" + indexcsc + "," +
                "\"csc\"" + ":" + "\"" + csc + "\"" + "," +
                "\"param\"" + ":" + param
    }
}