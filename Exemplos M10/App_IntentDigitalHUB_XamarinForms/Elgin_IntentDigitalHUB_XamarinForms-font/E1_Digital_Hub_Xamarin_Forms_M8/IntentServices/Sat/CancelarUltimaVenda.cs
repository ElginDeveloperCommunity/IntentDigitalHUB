namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Sat
{
    class CancelarUltimaVenda : SatCommand
    {
        readonly int numSessao;
        readonly string codAtivacao;
        readonly string numeroCFe;
        readonly string dadosCancelamento;

        public CancelarUltimaVenda(int numSessao, string codAtivacao, string numeroCFe, string dadosCancelamento) : base("CancelarUltimaVenda")
        {
            this.numSessao = numSessao;
            this.codAtivacao = codAtivacao;
            this.numeroCFe = numeroCFe;
            this.dadosCancelamento = dadosCancelamento;
        }

        protected override string FunctionParameters()
        {
            return "\"numSessao\"" + ":" + numSessao + "," +
                    "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                    "\"numeroCFe\"" + ":" + "\"" + numeroCFe + "\"" + "," +
                    "\"dadosCancelamento\"" + ":" + "\"" + dadosCancelamento + "\"";
        }
    }
}