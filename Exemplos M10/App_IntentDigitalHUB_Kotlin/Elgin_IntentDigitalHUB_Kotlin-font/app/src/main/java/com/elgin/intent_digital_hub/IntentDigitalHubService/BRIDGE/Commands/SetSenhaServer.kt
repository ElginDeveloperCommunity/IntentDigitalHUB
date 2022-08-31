package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class SetSenhaServer(private val senha: String, private val habilitada: Boolean) :
    BridgeCommand("SetSenhaServer") {
    override fun functionParameters(): String {
        return "\"senha\"" + ":" + "\"" + senha + "\"" + "," +
                "\"habilitada\"" + ":" + habilitada
    }
}