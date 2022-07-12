package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class IniciaOperacaoAdministrativa(
    private val idTransacao: Int,
    private val pdv: String,
    private val operacao: Int
) : BridgeCommand("IniciaOperacaoAdministrativa") {
    override fun functionParameters(): String {
        return "\"idTransacao\"" + ":" + idTransacao + "," +
                "\"pdv\"" + ":" + "\"" + pdv + "\"" + "," +
                "\"operacao\"" + ":" + operacao
    }
}