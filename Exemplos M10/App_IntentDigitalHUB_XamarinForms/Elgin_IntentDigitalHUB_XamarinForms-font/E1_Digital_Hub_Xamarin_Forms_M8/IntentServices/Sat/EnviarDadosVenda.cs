namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Sat
{
    class EnviarDadosVenda : SatCommand
    {
        readonly int numSessao;
        readonly string codAtivacao;
        readonly string dadosVenda;

        public EnviarDadosVenda(int numSessao, string codAtivacao, string dadosVenda) : base("EnviarDadosVenda")
        {
            this.numSessao = numSessao;
            this.codAtivacao = codAtivacao;
            this.dadosVenda = dadosVenda;
        }

        protected override string FunctionParameters()
        {
            return "\"numSessao\"" + ":" + numSessao + "," +
                    "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                    "\"dadosVenda\"" + ":" + "\"" + dadosVenda + "\"";
        }
    }
}