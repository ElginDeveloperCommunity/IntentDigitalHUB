package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;


import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

final public class ImprimeXMLSAT extends TermicaCommand {
    final private String dados;
    final private int param;

    public ImprimeXMLSAT(String dados, int param) {
        super("ImprimeXMLSAT");
        this.dados = dados;
        this.param = param;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("dados", this.dados);
        functionParametersJson.addProperty("param", this.param);

        return functionParametersJson;
    }
}
