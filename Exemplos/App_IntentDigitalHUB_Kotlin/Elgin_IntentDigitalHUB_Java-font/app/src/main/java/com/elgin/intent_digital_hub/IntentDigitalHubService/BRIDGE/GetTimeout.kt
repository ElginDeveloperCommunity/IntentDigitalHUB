package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class GetTimeout : BridgeCommand("GetTimeout") {
    override fun functionParameters(): String {
        return ""
    }
}