package com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.SatCommand

class ExtrairLogs(private val numSessao: Int, private val codAtivacao: String) :
    SatCommand("ExtrairLogs") {
    override fun functionParameters(): String {
        return "\"numSessao\"" + ":" + numSessao + "," +
                "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\""
    }
}