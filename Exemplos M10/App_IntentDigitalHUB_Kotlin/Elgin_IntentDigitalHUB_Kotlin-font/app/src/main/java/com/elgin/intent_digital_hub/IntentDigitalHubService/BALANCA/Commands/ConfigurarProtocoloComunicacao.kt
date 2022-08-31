package com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.BalancaCommand

class ConfigurarProtocoloComunicacao(val protocoloComunicacao: Int) :
    BalancaCommand<Int>("ConfigurarProtocoloComunicacao") {
    override fun functionParameters(): String {
        return "\"protocoloComunicacao\"" + ":" + protocoloComunicacao
    }
}