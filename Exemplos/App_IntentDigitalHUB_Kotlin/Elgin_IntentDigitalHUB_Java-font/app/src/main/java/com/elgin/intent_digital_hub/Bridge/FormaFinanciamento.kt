package com.elgin.intent_digital_hub.Bridge

/**
 * Formas de financiamento e o valor a ser enviado em cada opção para os comandos
 */
enum class FormaFinanciamento(val codigoFormaParcelamento: Int) {
    FINANCIAMENTO_A_VISTA(1), FINANCIAMENTO_PARCELADO_EMISSOR(2), FINANCIAMENTO_PARCELADO_ESTABELECIMENTO(
        3
    );

}