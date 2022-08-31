package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class SetTimeout(private val timeout: Int) : BridgeCommand("SetTimeout") {
    override fun functionParameters(): String {
        return "\"timeout\"" + ":" + timeout
    }
}