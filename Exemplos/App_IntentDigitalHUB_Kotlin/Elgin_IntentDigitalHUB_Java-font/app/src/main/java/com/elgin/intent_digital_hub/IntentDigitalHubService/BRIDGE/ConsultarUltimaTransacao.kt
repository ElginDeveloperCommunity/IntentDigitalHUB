package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class ConsultarUltimaTransacao(private val pdv: String) :
    BridgeCommand("ConsultarUltimaTransacao") {
    override fun functionParameters(): String {
        return "\"pdv\"" + ":" + "\"" + pdv + "\""
    }
}