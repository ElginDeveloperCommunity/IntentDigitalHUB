namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class ImpressaoQRCode : TermicaCommand
    {
        readonly string dados;
        readonly int tamanho;
        readonly int nivelCorrecao;

        public ImpressaoQRCode(string dados, int tamanho, int nivelCorrecao) : base("ImpressaoQRCode")
        {
            this.dados = dados;
            this.tamanho = tamanho;
            this.nivelCorrecao = nivelCorrecao;
        }

        protected override string FunctionParameters()
        {
            return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                    "\"tamanho\"" + ":" + tamanho + "," +
                    "\"nivelCorrecao\"" + ":" + nivelCorrecao;
        }
    }
}