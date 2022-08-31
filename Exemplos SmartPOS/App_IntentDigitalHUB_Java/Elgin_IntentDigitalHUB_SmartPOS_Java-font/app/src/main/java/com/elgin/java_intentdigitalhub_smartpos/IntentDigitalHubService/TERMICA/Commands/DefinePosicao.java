package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

public class DefinePosicao extends TermicaCommand {
    final private int posicao;

    public DefinePosicao(int posicao) {
        super("DefinePosicao");
        this.posicao = posicao;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("posicao", this.posicao);

        return functionParametersJson;
    }
}
