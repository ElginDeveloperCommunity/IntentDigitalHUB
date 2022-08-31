package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.TermicaCommand;
import com.google.gson.JsonObject;

public class AbreConexaoImpressora extends TermicaCommand {
    final private int tipo;
    final private String modelo;
    final private String conexao;
    final private int parametro;

    public AbreConexaoImpressora(int tipo, String modelo, String conexao, int parametro) {
        super("AbreConexaoImpressora");
        this.tipo = tipo;
        this.modelo = modelo;
        this.conexao = conexao;
        this.parametro = parametro;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("tipo", this.tipo);
        functionParametersJson.addProperty("modelo", this.modelo);
        functionParametersJson.addProperty("conexao", this.conexao);
        functionParametersJson.addProperty("parametro", parametro);

        return functionParametersJson;
    }
}
