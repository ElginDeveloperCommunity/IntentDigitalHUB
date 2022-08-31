package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand;
import com.google.gson.JsonObject;

public class SetPersonalizacao extends ElginPayCommand {
    final private String iconeToolbar;
    final private String fonte;
    final private String corFonte;
    final private String corFonteTeclado;
    final private String corFundoToolbar;
    final private String corFundoTela;
    final private String corTeclaLiberadaTeclado;
    final private String corFundoTeclado;
    final private String corTextoCaixaEdicao;
    final private String corSeparadorMenu;


    public SetPersonalizacao(String iconeToolbar, String fonte, String corFonte, String corFonteTeclado, String corFundoToolbar, String corFundoTela, String corTeclaLiberadaTeclado, String corFundoTeclado, String corTextoCaixaEdicao, String corSeparadorMenu) {
        super("setPersonalizacao");
        this.iconeToolbar = iconeToolbar;
        this.fonte = fonte;
        this.corFonte = corFonte;
        this.corFonteTeclado = corFonteTeclado;
        this.corFundoToolbar = corFundoToolbar;
        this.corFundoTela = corFundoTela;
        this.corTeclaLiberadaTeclado = corTeclaLiberadaTeclado;
        this.corFundoTeclado = corFundoTeclado;
        this.corTextoCaixaEdicao = corTextoCaixaEdicao;
        this.corSeparadorMenu = corSeparadorMenu;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("iconeToolbar", this.iconeToolbar);
        functionParametersJson.addProperty("fonte", this.fonte);
        functionParametersJson.addProperty("corFonte", this.corFonte);
        functionParametersJson.addProperty("corFonteTeclado", this.corFonteTeclado);
        functionParametersJson.addProperty("corFundoToolbar", this.corFundoToolbar);
        functionParametersJson.addProperty("corFundoTela", this.corFundoTela);
        functionParametersJson.addProperty("corTeclaLiberadaTeclado", this.corTeclaLiberadaTeclado);
        functionParametersJson.addProperty("corFundoTeclado", this.corFundoTeclado);
        functionParametersJson.addProperty("corTextoCaixaEdicao", this.corTextoCaixaEdicao);
        functionParametersJson.addProperty("corSeparadorMenu", this.corSeparadorMenu);

        return functionParametersJson;
    }
}
