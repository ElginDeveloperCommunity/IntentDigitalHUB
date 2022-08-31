package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

public class Corte extends TermicaCommand {
    final private int avanco;

    public Corte(int avanco) {
        super("Corte");
        this.avanco = avanco;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("avanco", this.avanco);

        return functionParametersJson;
    }
}
