package com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.SatCommand

class ConsultarSAT(private val numSessao: Int) : SatCommand("ConsultarSat") {
    override fun functionParameters(): String {
        return "\"numSessao\"" + ":" + numSessao
    }
}