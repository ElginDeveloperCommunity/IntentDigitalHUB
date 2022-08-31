namespace Xamarin_Android_Intent_Digital_Hub.Sat
{
    /**
     * Os dois modelos diferentes de SAT possuem xmls diferentes no envio de venda, o enumerator facilita guardando também o nome dos arquivos xml de envio de envio de venda para cada modelo, encontrados em res/raw/
     */
    class SatModel
    {
        private SatModel(string SALE_XML_ARCHIVE_NAME) { this.SALE_XML_ARCHIVE_NAME = SALE_XML_ARCHIVE_NAME; }

        public string SALE_XML_ARCHIVE_NAME { get; private set; }

        public static SatModel SMART_SAT = new SatModel("sat_enviar_dados_venda");
        public static SatModel SAT_GO = new SatModel("satgo_enviar_dados_venda");
    }
}