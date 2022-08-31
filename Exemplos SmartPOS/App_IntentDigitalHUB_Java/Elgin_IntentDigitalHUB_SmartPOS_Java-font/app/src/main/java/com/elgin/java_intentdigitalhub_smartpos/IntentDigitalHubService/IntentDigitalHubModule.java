package com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService;

/**
 * Módulos necessário que são utilizados pelos comandos do IDH, para a ciração da intent com o filtro correto.
 */
public enum IntentDigitalHubModule {
    ELGINPAY("com.elgin.e1.digitalhub.ELGINPAY"),
    TERMICA("com.elgin.e1.digitalhub.TERMICA"),
    //O Scanner é o único módulo que não possuí comandos, apenas o seu módulo filtro é necessário para o ínicio da operação.
    SCANNER("com.elgin.e1.digitalhub.SCANNER");


    private final String intentPath;

    IntentDigitalHubModule(String intentPath) {
        this.intentPath = intentPath;
    }

    public String getIntentPath() {
        return this.intentPath;
    }
}
