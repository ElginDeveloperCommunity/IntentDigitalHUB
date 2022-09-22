package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService

enum class IntentDigitalHubModule (val intentPath: String){
    ELGINPAY("com.elgin.e1.digitalhub.ELGINPAY"),
    TERMICA("com.elgin.e1.digitalhub.TERMICA"),
    //O Scanner é o único módulo que não possuí comandos, apenas o seu módulo filtro é necessário para o ínicio da operação.
    SCANNER("com.elgin.e1.digitalhub.SCANNER");
}