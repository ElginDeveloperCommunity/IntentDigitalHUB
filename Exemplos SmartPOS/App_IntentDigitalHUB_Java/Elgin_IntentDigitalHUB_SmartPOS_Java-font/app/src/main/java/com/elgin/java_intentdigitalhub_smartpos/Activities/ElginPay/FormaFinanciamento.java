package com.elgin.java_intentdigitalhub_smartpos.Activities.ElginPay;

public enum FormaFinanciamento {
    FINANCIAMENTO_A_VISTA(1),
    FINANCIAMENTO_PARCELADO_EMISSOR(2),
    FINANCIAMENTO_PARCELADO_ESTABELECIMENTO(3);

    private final int codigoFormaParcelamento;

    FormaFinanciamento(int codigoFormaParcelamento) {
        this.codigoFormaParcelamento = codigoFormaParcelamento;
    }

    public int getCodigoFormaParcelamento() {
        return codigoFormaParcelamento;
    }
}
