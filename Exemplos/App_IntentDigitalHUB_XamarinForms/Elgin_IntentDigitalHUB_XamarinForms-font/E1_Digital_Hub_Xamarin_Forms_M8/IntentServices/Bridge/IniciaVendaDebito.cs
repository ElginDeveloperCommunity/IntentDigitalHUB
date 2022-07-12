namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class IniciaVendaDebito : BridgeCommand
    {
        readonly int idTransacao;
        readonly string pdv;
        readonly string valorTotal;

        public IniciaVendaDebito(int idTransacao, string pdv, string valorTotal) : base("IniciaVendaDebito")
        {
            this.idTransacao = idTransacao;
            this.pdv = pdv;
            this.valorTotal = valorTotal;
        }

        protected override string FunctionParameters()
        {
            return "\"idTransacao\"" + ":" + idTransacao + "," +
                    "\"pdv\"" + ":" + "\"" + pdv + "\"" + "," +
                    "\"valorTotal\"" + ":" + "\"" + valorTotal + "\"";
        }
    }
}