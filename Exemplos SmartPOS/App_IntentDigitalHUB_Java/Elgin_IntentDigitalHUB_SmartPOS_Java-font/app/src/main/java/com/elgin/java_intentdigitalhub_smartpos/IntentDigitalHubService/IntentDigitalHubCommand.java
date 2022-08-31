package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService;

import com.google.gson.JsonObject;

/**
 * Classe abstrata que generaliza as carecterísticas comuns à todos os comandos do Intent Digital Hub, as classes que herdam desta implementam cada comando de maneira específica servindo como Wrapper
 */
public abstract class IntentDigitalHubCommand {
    //Nome da função
    final protected String functionName;
    //Módulo a qual a função pertence
    final protected IntentDigitalHubModule correspondingIntentModule;

    /**
     * As classes que herdam desta, ou seja, classes que implementam um comando, devem, em seu construtor, fornecer o nome da função, e o módulo filtro (caminho da intent) ao qual aquele comando pertence.
     */
    protected IntentDigitalHubCommand(String functionName, IntentDigitalHubModule correspondingIntentModule) {
        this.functionName = functionName;
        this.correspondingIntentModule = correspondingIntentModule;
    }

    /**
     * Um comando do IDH deve ser um JSON com o seguinte formato: {funcao:"nomeDaFuncao", parametros:{}}.
     *
     * O subjson que deve ser inserido na chave "parametros" corresponde aos parametros específicos de cada função, caso a classe que herde desta (ou seja, representa um comando) possua parâmetros,
     * é necessário fazer o @override da função que define esses parâmetros {@link #functionParametersJson()}.
     * Exemplo: {funcao:"Corte",parametros:{linhas:10}}.
     *
     * Caso a classe comando não possua parâmetros, não é necessário realizar nenhuma implementação adicional, pois a implementação padrão de {@link #functionParametersJson()} oferece um json vazio,
     * fornecendo corretamente a chave "parametros" com um json vazio "{}", necessário para a formatação de comandos sem parâmetros.
     * Exemplo: {funcao:"FechaConexaoImpressora",parametros:{}}.
     */
    final protected JsonObject getCommandJSON() {
        final JsonObject commandJson = new JsonObject();

        commandJson.addProperty("funcao", this.functionName);
        commandJson.add("parametros", this.functionParametersJson());

        return commandJson;
    }

    /**
     * Função que define os parametros especificos de cada comando, é utilizada para montagem do json de comando em getCommandJSON().
     */
    protected JsonObject functionParametersJson() {
        return new JsonObject();
    }
}
