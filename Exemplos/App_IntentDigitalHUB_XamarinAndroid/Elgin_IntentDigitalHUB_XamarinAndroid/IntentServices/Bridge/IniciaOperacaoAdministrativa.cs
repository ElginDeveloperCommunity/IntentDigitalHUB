namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge
{
    class IniciaOperacaoAdministrativa : BridgeCommand
    {
        readonly int idTransacao;
        readonly string pdv;
        readonly int operacao;

        public IniciaOperacaoAdministrativa(int idTransacao, string pdv, int operacao) : base("IniciaOperacaoAdministrativa")
        {
            this.idTransacao = idTransacao;
            this.pdv = pdv;
            this.operacao = operacao;
        }

        protected override string FunctionParameters()
        {
            return "\"idTransacao\"" + ":" + idTransacao + "," +
                    "\"pdv\"" + ":" + "\"" + pdv + "\"" + "," +
                    "\"operacao\"" + ":" + operacao;
        }
    }
}