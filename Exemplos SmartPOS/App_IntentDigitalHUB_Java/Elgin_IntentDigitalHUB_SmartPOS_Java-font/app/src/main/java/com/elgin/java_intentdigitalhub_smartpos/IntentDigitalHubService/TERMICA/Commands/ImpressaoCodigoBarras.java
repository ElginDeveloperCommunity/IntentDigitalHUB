package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

public class ImpressaoCodigoBarras extends TermicaCommand {
    final private int tipo;
    final private String dados;
    final private int altura;
    final private int largura;
    final private int HRI;

    public ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int hri) {
        super("ImpressaoCodigoBarras");
        this.tipo = tipo;
        this.dados = dados;
        this.altura = altura;
        this.largura = largura;
        HRI = hri;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("tipo", this.tipo);
        functionParametersJson.addProperty("dados", this.dados);
        functionParametersJson.addProperty("altura", this.altura);
        functionParametersJson.addProperty("largura", this.largura);
        functionParametersJson.addProperty("HRI", this.HRI);

        return functionParametersJson;
    }
}
