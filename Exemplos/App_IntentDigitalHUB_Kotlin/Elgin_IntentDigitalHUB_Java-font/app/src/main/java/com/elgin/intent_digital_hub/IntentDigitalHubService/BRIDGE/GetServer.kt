package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class GetServer : BridgeCommand("GetServer") {
    override fun functionParameters(): String {
        return ""
    }
}