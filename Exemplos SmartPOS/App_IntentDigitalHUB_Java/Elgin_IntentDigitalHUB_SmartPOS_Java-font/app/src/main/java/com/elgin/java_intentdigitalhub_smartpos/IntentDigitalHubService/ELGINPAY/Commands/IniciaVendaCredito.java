package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands;

import androidx.annotation.NonNull;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand;
import com.google.gson.JsonObject;

public class IniciaVendaCredito extends ElginPayCommand {
    final private String valorTotal;
    final private int tipoFinanciamento;
    final private int numeroParcelas;

    public IniciaVendaCredito(@NonNull String valorTotal, int tipoFinanciamento, int numeroParcelas) {
        super("iniciaVendaCredito");
        this.valorTotal = valorTotal;
        this.tipoFinanciamento = tipoFinanciamento;
        this.numeroParcelas = numeroParcelas;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("valorTotal", this.valorTotal);
        functionParametersJson.addProperty("tipoFinanciamento", this.tipoFinanciamento);
        functionParametersJson.addProperty("numeroParcelas", this.numeroParcelas);

        return functionParametersJson;
    }
}
