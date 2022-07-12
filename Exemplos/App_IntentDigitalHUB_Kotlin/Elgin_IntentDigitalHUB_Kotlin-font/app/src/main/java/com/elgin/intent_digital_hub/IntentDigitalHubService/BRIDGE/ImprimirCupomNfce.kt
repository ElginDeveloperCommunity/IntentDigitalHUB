package com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE

import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand

class ImprimirCupomNfce(
    private val xml: String,
    private val indexcsc: Int,
    private val csc: String
) : BridgeCommand("ImprimirCupomNfce") {
    override fun functionParameters(): String {
        return "\"xml\"" + ":" + "\"" + xml + "\"" + "," +
                "\"indexcsc\"" + ":" + indexcsc + "," +
                "\"csc\"" + ":" + "\"" + csc + "\""
    }
}