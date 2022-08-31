namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Sat
{
    class ConsultarStatusOperacional : SatCommand
    {
        readonly int numSessao;
        readonly string codAtivacao;

        public ConsultarStatusOperacional(int numSessao, string codAtivacao) : base("ConsultarStatusOperacional")
        {
            this.numSessao = numSessao;
            this.codAtivacao = codAtivacao;
        }

        protected override string FunctionParameters()
        {
            return "\"numSessao\"" + ":" + numSessao + "," +
                    "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"";
        }
    }
}