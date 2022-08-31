package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;


import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

final public class StatusImpressora extends TermicaCommand {
    final private int param;

    public StatusImpressora(int param) {
        super("StatusImpressora");
        this.param = param;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("param", this.param);

        return functionParametersJson;
    }
}
