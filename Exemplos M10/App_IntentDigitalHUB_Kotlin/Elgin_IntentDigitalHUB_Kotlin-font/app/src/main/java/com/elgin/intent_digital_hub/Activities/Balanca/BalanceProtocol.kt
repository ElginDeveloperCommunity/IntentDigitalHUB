package com.elgin.intent_digital_hub.Activities.Balanca

/**
 * Protocolos de comunicação com a balança, o código de cada protocolo corresponde ao índice de declaração no enum
 * Ex: Protocolo 4 possui 4 de código correspondente
 */
enum class BalanceProtocol(val friendlyName: String) {
    PROTOCOL_0("PROTOCOL 0"),
    PROTOCOL_1("PROTOCOL 1"),
    PROTOCOL_2("PROTOCOL 2"),
    PROTOCOL_3("PROTOCOL 3"),
    PROTOCOL_4("PROTOCOL 4"),
    PROTOCOL_5("PROTOCOL 5"),
    PROTOCOL_6("PROTOCOL 6"),
    PROTOCOL_7("PROTOCOL 7");

    //Nome legível, utilizado para a montagem do Spinner/Dropdown
    override fun toString(): String {
        return this.friendlyName
    }
}