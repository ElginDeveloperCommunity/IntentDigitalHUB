namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class DefinePosicao : TermicaCommand
    {
        readonly int posicao;

        public DefinePosicao(int posicao) : base("DefinePosicao")
        {
            this.posicao = posicao;
        }

        protected override string FunctionParameters()
        {
            return "\"posicao\"" + ":" + posicao;
        }
    }
}