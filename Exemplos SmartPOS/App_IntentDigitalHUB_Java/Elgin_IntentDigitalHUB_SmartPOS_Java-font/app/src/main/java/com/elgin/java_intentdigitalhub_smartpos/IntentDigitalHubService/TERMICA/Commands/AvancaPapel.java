package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

public class AvancaPapel extends TermicaCommand {
    final private int linhas;

    public AvancaPapel(int linhas) {
        super("AvancaPapel");
        this.linhas = linhas;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("linhas", this.linhas);

        return functionParametersJson;
    }
}
