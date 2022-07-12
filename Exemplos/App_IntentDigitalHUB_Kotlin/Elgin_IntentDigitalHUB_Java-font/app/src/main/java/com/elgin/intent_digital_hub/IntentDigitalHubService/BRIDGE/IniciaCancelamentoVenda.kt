package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class IniciaCancelamentoVenda(
    private val idTransacao: Int,
    private val pdv: String,
    private val valorTotal: String,
    private val dataHora: String,
    private val nsu: String
) : BridgeCommand("IniciaCancelamentoVenda") {
    override fun functionParameters(): String {
        return "\"idTransacao\"" + ":" + idTransacao + "," +
                "\"pdv\"" + ":" + "\"" + pdv + "\"" + "," +
                "\"valorTotal\"" + ":" + "\"" + valorTotal + "\"" + "," +
                "\"dataHora\"" + ":" + "\"" + dataHora + "\"" + "," +
                "\"nsu\"" + ":" + "\"" + nsu + "\""
    }
}