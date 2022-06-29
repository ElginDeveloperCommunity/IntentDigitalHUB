namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Sat
{
    class ExtrairLogs : SatCommand
    {
        readonly int numSessao;
        readonly string codAtivacao;

        public ExtrairLogs(int numSessao, string codAtivacao) : base("ExtrairLogs")
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