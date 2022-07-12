package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class ConsultarStatus : BridgeCommand("ConsultarStatus") {
    override fun functionParameters(): String {
        return ""
    }
}