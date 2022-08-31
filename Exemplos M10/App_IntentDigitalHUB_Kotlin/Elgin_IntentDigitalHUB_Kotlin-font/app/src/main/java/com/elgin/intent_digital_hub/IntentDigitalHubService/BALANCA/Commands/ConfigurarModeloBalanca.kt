package com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.BalancaCommand

class ConfigurarModeloBalanca(val modeloBalanca: Int) : BalancaCommand<Int>("ConfigurarModeloBalanca") {
    override fun functionParameters(): String {
        return "\"modeloBalanca\"" + ":" + modeloBalanca
    }
}