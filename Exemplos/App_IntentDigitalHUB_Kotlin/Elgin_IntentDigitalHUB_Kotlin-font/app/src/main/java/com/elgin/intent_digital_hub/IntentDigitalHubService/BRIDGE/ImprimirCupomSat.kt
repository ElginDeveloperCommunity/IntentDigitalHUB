package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class ImprimirCupomSat(private val xml: String) : BridgeCommand("ImprimirCupomSat") {
    override fun functionParameters(): String {
        return "\"xml\"" + ":" + "\"" + xml + "\""
    }
}