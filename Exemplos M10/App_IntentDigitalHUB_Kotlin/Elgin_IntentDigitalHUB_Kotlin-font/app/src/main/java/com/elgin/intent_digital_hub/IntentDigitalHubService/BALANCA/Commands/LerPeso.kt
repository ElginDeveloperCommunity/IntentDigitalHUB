package com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.BalancaCommand

class LerPeso(val qtdLeituras: Int) : BalancaCommand<Int>("LerPeso") {
    override fun functionParameters(): String {
        return "\"qtdLeituras\"" + ":" + this.qtdLeituras
    }
}