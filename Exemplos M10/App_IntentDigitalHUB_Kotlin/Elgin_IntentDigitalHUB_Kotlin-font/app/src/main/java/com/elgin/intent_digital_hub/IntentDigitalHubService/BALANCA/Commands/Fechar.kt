package com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.BalancaCommand

class Fechar : BalancaCommand<Int>("Fechar") {
    override fun functionParameters(): String {
        return ""
    }
}