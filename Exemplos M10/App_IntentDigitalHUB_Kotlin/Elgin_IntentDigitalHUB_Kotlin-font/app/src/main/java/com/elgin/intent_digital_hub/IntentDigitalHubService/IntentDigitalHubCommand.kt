package com.elgin.intent_digital_hub.IntentDigitalHubService

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubModule

/**
 * Classe abstrata que generaliza as carecterísticas comuns à todos os comandos do Intent Digital Hub, as classes que herdam desta implementam cada comando de maneira específica servindo como Wrapper
 */
abstract class IntentDigitalHubCommand protected constructor(//Nome da função
    protected val functionName: String?, //Módulo a qual a função pertence
    val correspondingIntentModule: IntentDigitalHubModule
) {
    //Formata o JSON de acordo com os parâmetros definidos por cada subclasse, o modificador de acesso protected impede a exposição do método que sera usado somente em DigitalHubUtils para o start da intent
    val commandJSON: String
        get() = "[{" +
                "\"funcao\"" + ":" + "\"" + functionName + "\"" + "," +
                "\"parametros\"" + ":" + "{" + functionParameters() + "}" +
                "}]"

    //Função que deve ser implementada por cada subclasse definindo a formatação dos parâmetros da função específica
    protected abstract fun functionParameters(): String
}