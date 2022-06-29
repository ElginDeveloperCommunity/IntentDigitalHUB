namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Sat
{
    class AtivarSAT : SatCommand
    {
        readonly int numSessao;
        readonly int subComando;
        readonly string codAtivacao;
        readonly string cnpj;
        readonly int cUF;

        public AtivarSAT(int numSessao, int subComando, string codAtivacao, string cnpj, int cUF) : base("AtivarSAT")
        {
            this.numSessao = numSessao;
            this.subComando = subComando;
            this.codAtivacao = codAtivacao;
            this.cnpj = cnpj;
            this.cUF = cUF;
        }

        protected override string FunctionParameters()
        {
            return "\"numSessao\"" + ":" + numSessao + "," +
                    "\"subComando\"" + ":" + subComando + "," +
                    "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                    "\"cnpj\"" + ":" + "\"" + cnpj + "\"" + "," +
                    "\"cUF\"" + ":" + cUF;
        }
    }
}