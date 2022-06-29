namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class ImprimeImagem : TermicaCommand
    {
        readonly string path;

        public ImprimeImagem(string path) : base("ImprimeImagem")
        {
            this.path = path;
        }

        protected override string FunctionParameters()
        {
            return "\"path\"" + ":" + "\"" + path + "\"";
        }
    }
}