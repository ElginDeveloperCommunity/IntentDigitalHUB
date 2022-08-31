package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands;

import androidx.annotation.NonNull;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand;
import com.google.gson.JsonObject;

public class IniciaCancelamentoVenda extends ElginPayCommand {
    final private String valorTotal;
    final private String ref;
    final private String data;

    public IniciaCancelamentoVenda(@NonNull String valorTotal, @NonNull String ref, @NonNull String data) {
        super("iniciaCancelamentoVenda");
        this.valorTotal = valorTotal;
        this.ref = ref;
        this.data = data;
    }

    @Override
    protected JsonObject functionParametersJson() {
        JsonObject functionParametersJson = new JsonObject();

        functionParametersJson.addProperty("valorTotal", this.valorTotal);
        functionParametersJson.addProperty("ref", this.ref);
        functionParametersJson.addProperty("data", this.data);

        return functionParametersJson;
    }
}
