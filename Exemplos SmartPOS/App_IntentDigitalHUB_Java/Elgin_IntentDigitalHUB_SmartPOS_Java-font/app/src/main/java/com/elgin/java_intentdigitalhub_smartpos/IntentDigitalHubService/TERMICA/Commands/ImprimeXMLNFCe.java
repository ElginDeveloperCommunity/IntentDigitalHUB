package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import androidx.annotation.NonNull;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;


public class ImprimeXMLNFCe extends TermicaCommand {
    final private String dados;
    final private int indexcsc;
    final private String csc;
    final private int param;

    public ImprimeXMLNFCe(@NonNull String dados, int indexcsc, @NonNull String csc, int param) {
        super("ImprimeXMLNFCe");
        this.dados = dados;
        this.indexcsc = indexcsc;
        this.csc = csc;
        this.param = param;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("dados", this.dados);
        functionParametersJson.addProperty("indexcsc", this.indexcsc);
        functionParametersJson.addProperty("csc", csc);
        functionParametersJson.addProperty("param", this.param);

        return functionParametersJson;
    }
}
