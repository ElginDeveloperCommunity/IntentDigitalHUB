namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class IniciaVendaCredito : BridgeCommand
    {
        readonly int idTransacao;
        readonly string pdv;
        readonly string valorTotal;
        readonly int tipoFinanciamento;
        readonly int numeroParcelas;

        public IniciaVendaCredito(int idTransacao, string pdv, string valorTotal, int tipoFinanciamento, int numeroParcelas) : base("IniciaVendaCredito")
        {
            this.idTransacao = idTransacao;
            this.pdv = pdv;
            this.valorTotal = valorTotal;
            this.tipoFinanciamento = tipoFinanciamento;
            this.numeroParcelas = numeroParcelas;
        }

        protected override string FunctionParameters()
        {
            return "\"idTransacao\"" + ":" + this.idTransacao + "," +
                    "\"pdv\"" + ":" + "\"" + this.pdv + "\"" + "," +
                    "\"valorTotal\"" + ":" + "\"" + this.valorTotal + "\"" + "," +
                    "\"tipoFinanciamento\"" + ":" + this.tipoFinanciamento + "," +
                    "\"numeroParcelas\"" + ":" + this.numeroParcelas;
        }
    }
}