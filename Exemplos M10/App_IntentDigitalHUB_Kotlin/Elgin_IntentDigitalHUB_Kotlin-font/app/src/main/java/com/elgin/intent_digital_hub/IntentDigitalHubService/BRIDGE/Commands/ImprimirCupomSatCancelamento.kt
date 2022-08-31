package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class ImprimirCupomSatCancelamento(private val xml: String, private val assQRCode: String) :
    BridgeCommand("ImprimirCupomSatCancelamento") {
    override fun functionParameters(): String {
        return "\"xml\"" + ":" + "\"" + xml + "\"" + "," +
                "\"assQRCode\"" + ":" + "\"" + assQRCode + "\""
    }
}