package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import androidx.annotation.NonNull;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

final public class ImpressaoTexto extends TermicaCommand {
    final private String dados;
    final private int posicao;
    final private int stilo;
    final private int tamanho;

    public ImpressaoTexto(@NonNull String dados, int posicao, int stilo, int tamanho) {
        super("ImpressaoTexto");
        this.dados = dados;
        this.posicao = posicao;
        this.stilo = stilo;
        this.tamanho = tamanho;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("dados", this.dados);
        functionParametersJson.addProperty("posicao", this.posicao);
        functionParametersJson.addProperty("stilo", this.stilo);
        functionParametersJson.addProperty("tamanho", this.tamanho);

        return functionParametersJson;
    }
}
