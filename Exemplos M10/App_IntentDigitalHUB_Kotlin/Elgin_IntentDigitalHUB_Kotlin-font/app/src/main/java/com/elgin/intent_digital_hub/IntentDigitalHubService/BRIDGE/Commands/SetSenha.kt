package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class SetSenha(private val senha: String, private val habilitada: Boolean) :
    BridgeCommand("SetSenha") {
    override fun functionParameters(): String {
        return "\"senha\"" + ":" + "\"" + senha + "\"" + "," +
                "\"habilitada\"" + ":" + habilitada
    }
}