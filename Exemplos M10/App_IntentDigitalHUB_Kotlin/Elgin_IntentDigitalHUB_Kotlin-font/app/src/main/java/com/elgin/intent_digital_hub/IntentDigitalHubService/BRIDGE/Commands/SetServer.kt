package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class SetServer(val ipTerminal: String, val portaTransacao: Int, val portaStatus: Int) :
    BridgeCommand("SetServer") {
    override fun functionParameters(): String {
        return "\"ipTerminal\"" + ":" + "\"" + ipTerminal + "\"" + "," +
                "\"portaTransacao\"" + ":" + portaTransacao + "," +
                "\"portaStatus\"" + ":" + portaStatus
    }
}