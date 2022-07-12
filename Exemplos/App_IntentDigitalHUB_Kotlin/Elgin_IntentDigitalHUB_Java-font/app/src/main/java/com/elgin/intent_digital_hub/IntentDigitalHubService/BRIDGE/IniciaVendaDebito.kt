package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class IniciaVendaDebito(
    private val idTransacao: Int,
    private val pdv: String,
    private val valorTotal: String
) : BridgeCommand("IniciaVendaDebito") {
    override fun functionParameters(): String {
        return "\"idTransacao\"" + ":" + idTransacao + "," +
                "\"pdv\"" + ":" + "\"" + pdv + "\"" + "," +
                "\"valorTotal\"" + ":" + "\"" + valorTotal + "\""
    }
}