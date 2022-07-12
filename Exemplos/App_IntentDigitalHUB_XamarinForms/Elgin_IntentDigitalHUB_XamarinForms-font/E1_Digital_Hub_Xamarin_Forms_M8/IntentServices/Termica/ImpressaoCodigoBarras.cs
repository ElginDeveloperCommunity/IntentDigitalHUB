namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica
{
    class ImpressaoCodigoBarras : TermicaCommand
    {
        readonly int tipo;
        readonly string dados;
        readonly int altura;
        readonly int largura;
        readonly int HRI;

        public ImpressaoCodigoBarras(int tipo, string dados, int altura, int largura, int HRI) : base("ImpressaoCodigoBarras")
        {
            this.tipo = tipo;
            this.dados = dados;
            this.altura = altura;
            this.largura = largura;
            this.HRI = HRI;
        }

        protected override string FunctionParameters()
        {
            return "\"tipo\"" + ":" + tipo + "," +
                    "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                    "\"altura\"" + ":" + altura + "," +
                    "\"largura\"" + ":" + largura + "," +
                    "\"HRI\"" + ":" + HRI;
        }
    }
}