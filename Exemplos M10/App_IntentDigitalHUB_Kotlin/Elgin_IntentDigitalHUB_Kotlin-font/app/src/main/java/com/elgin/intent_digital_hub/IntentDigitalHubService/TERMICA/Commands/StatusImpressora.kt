package com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.TermicaCommand

class StatusImpressora(private val param: Int) : TermicaCommand("StatusImpressora") {
    override fun functionParameters(): String {
        return "\"param\"" + ":" + param
    }
}