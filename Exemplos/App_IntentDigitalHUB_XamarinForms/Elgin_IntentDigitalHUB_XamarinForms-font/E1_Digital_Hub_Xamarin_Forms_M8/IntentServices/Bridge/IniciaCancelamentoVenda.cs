namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class IniciaCancelamentoVenda : BridgeCommand
    {
        readonly int idTransacao;
        readonly string pdv;
        readonly string valorTotal;
        readonly string dataHora;
        readonly string nsu;

        public IniciaCancelamentoVenda(int idTransacao, string pdv, string valorTotal, string dataHora, string nsu) : base("IniciaCancelamentoVenda")
        {
            this.idTransacao = idTransacao;
            this.pdv = pdv;
            this.valorTotal = valorTotal;
            this.dataHora = dataHora;
            this.nsu = nsu;
        }

        protected override string FunctionParameters()
        {
            return "\"idTransacao\"" + ":" + idTransacao + "," +
                    "\"pdv\"" + ":" + "\"" + pdv + "\"" + "," +
                    "\"valorTotal\"" + ":" + "\"" + valorTotal + "\"" + "," +
                    "\"dataHora\"" + ":" + "\"" + dataHora + "\"" + "," +
                    "\"nsu\"" + ":" + "\"" + nsu + "\"";
        }
    }
}