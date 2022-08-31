package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import androidx.annotation.NonNull;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

final public class ImprimeImagem extends TermicaCommand {
    final private String path;

    public ImprimeImagem(@NonNull String path) {
        super("ImprimeImagem");
        this.path = path;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("path", this.path);

        return functionParametersJson;
    }
}
