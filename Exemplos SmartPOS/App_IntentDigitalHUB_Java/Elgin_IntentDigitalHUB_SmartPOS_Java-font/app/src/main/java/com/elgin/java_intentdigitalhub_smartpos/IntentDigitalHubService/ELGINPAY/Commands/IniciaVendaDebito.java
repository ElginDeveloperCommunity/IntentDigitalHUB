package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands;

import androidx.annotation.NonNull;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand;
import com.google.gson.JsonObject;

public class IniciaVendaDebito extends ElginPayCommand {
    final private String valorTotal;

    public IniciaVendaDebito(@NonNull String valorTotal) {
        super("iniciaVendaDebito");
        this.valorTotal = valorTotal;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("valorTotal", this.valorTotal);

        return functionParametersJson;
    }
}
