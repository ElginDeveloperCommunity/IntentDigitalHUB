package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import androidx.annotation.NonNull;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

final public class ImprimeImagemMemoria extends TermicaCommand {
    final private String key;
    final private int scala;

    public ImprimeImagemMemoria(@NonNull String key, int scala) {
        super("ImprimeImagemMemoria");
        this.key = key;
        this.scala = scala;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("key", this.key);
        functionParametersJson.addProperty("scala", this.scala);

        return functionParametersJson;
    }
}
