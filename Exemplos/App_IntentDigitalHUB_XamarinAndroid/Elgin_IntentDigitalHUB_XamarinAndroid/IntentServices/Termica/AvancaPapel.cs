namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class AvancaPapel : TermicaCommand
    {
        readonly int linhas;

        public AvancaPapel(int linhas) : base("AvancaPapel")
        {
            this.linhas = linhas;
        }

        protected override string FunctionParameters()
        {
            return "\"linhas\"" + ":" + linhas;
        }
    }
}