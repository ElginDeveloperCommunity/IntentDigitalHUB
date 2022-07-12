namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica
{
    class ImpressaoTexto : TermicaCommand
    {
        readonly string dados;
        readonly int posicao;
        readonly int stilo;
        readonly int tamanho;

        public ImpressaoTexto(string dados, int posicao, int stilo, int tamanho) : base("ImpressaoTexto")
        {
            this.dados = dados;
            this.posicao = posicao;
            this.stilo = stilo;
            this.tamanho = tamanho;
        }

        protected override string FunctionParameters()
        {
            return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                    "\"posicao\"" + ":" + posicao + "," +
                    "\"stilo\"" + ":" + stilo + "," +
                    "\"tamanho\"" + ":" + tamanho;
        }
    }
}