package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

public class ImpressaoQRCode extends TermicaCommand {
    final private String dados;
    final private int tamanho;
    final private int nivelCorrecao;

    public ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao) {
        super("ImpressaoQRCode");
        this.dados = dados;
        this.tamanho = tamanho;
        this.nivelCorrecao = nivelCorrecao;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("dados", this.dados);
        functionParametersJson.addProperty("tamanho", this.tamanho);
        functionParametersJson.addProperty("nivelCorrecao", this.nivelCorrecao);

        return functionParametersJson;
    }
}
