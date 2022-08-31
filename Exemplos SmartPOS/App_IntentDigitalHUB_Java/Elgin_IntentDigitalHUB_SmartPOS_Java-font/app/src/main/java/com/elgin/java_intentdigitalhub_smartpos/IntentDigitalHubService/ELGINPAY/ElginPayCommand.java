package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY;

import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubModule;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class ElginPayCommand extends IntentDigitalHubCommand implements Serializable {

    protected ElginPayCommand(String functionName) {
        super(functionName, IntentDigitalHubModule.ELGINPAY);
    }

    /**
     * O retorno dos comandos ELGINPAY é sempre uma string, as classes de comando são utilizadas, também, para deserializar os JSON de RETORNO
     * do IDH, portanto é definido um objeto para que seja possível serializar o retorno de um comando dentro de um objeto da classe correspondente
     * a esse comando; verifique a impĺementação de @onActivityResult nos módulos implementados no projeto.
     */
    @SerializedName("resultado")
    private String resultado;

    public String getResultado() {
        return resultado;
    }
}
