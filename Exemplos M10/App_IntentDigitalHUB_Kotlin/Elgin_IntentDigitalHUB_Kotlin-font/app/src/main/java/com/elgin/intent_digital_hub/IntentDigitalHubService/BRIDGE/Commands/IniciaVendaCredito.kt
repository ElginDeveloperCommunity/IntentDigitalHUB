package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class IniciaVendaCredito(
    private val idTransacao: Int,
    private val pdv: String,
    private val valorTotal: String,
    private val tipoFinanciamento: Int,
    private val numeroParcelas: Int
) : BridgeCommand("IniciaVendaCredito") {
    override fun functionParameters(): String {
        return "\"idTransacao\"" + ":" + idTransacao + "," +
                "\"pdv\"" + ":" + "\"" + pdv + "\"" + "," +
                "\"valorTotal\"" + ":" + "\"" + valorTotal + "\"" + "," +
                "\"tipoFinanciamento\"" + ":" + tipoFinanciamento + "," +
                "\"numeroParcelas\"" + ":" + numeroParcelas
    }
}