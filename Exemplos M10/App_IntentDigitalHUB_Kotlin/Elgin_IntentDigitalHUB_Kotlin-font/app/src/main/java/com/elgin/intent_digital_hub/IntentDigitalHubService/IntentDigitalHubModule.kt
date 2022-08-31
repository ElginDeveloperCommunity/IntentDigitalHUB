package com.elgin.intent_digital_hub.IntentDigitalHubService

/**
 * Módulo disponíveis no Intent Digital Hub
 */
enum class IntentDigitalHubModule(val intentPath: String) {
    BALANCA("com.elgin.e1.digitalhub.BALANCA"),
    BRIDGE("com.elgin.e1.digitalhub.BRIDGE"),
    SAT("com.elgin.e1.digitalhub.SAT"),
    TERMICA("com.elgin.e1.digitalhub.TERMICA");
}