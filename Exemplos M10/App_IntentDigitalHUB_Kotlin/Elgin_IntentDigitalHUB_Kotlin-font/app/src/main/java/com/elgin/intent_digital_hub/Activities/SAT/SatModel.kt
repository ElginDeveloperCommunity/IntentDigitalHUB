package com.elgin.intent_digital_hub.Activities.SAT


/**
 * Os dois modelos diferentes de SAT possuem xmls diferentes no envio de venda, o enumerator facilita guardando também o nome dos arquivos xml de envio de envio de venda para cada modelo, encontrados em res/raw/
 */
enum class SatModel(val SALE_XML_ARCHIVE_NAME: String) {
    SMART_SAT("sat_enviar_dados_venda"),
    SAT_GO("satgo_enviar_dados_venda");


    open fun SALE_XML_ARCHIVE_NAME(): String? {
        return SALE_XML_ARCHIVE_NAME
    }

}