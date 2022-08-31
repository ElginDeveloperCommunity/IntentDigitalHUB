namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class ImprimeXMLSAT : TermicaCommand
    {
        readonly string dados;
        readonly int param;

        public ImprimeXMLSAT(string dados, int param) : base("ImprimeXMLSAT")
        {
            this.dados = dados;
            this.param = param;
        }

        protected override string FunctionParameters()
        {
            return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                    "\"param\"" + ":" + param;
        }
    }
}