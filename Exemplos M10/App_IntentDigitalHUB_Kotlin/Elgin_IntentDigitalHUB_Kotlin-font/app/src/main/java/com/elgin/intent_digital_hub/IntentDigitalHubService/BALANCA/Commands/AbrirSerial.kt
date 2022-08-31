package com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.Commands

import com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.BalancaCommand

class AbrirSerial(val baudrate: Int, val lenght: Int, val parity: Char, val stopbits: Int) :
    BalancaCommand<Int>("AbrirSerial") {
    override fun functionParameters(): String {
        return "\"baudrate\"" + ":" + this.baudrate + "," +
                "\"lenght\"" + ":" + this.lenght + "," +
                "\"parity\"" + ":" + "\"" + this.parity + "\"" + "," +
                "\"stopbits\"" + ":" + this.stopbits
    }
}