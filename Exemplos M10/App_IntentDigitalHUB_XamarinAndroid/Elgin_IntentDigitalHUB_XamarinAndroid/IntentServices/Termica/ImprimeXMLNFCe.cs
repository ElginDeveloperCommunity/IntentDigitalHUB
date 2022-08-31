namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class ImprimeXMLNFCe : TermicaCommand
    {
        readonly string dados;
        readonly int indexcsc;
        readonly string csc;
        readonly int param;

        public ImprimeXMLNFCe(string dados, int indexcsc, string csc, int param) : base("ImprimeXMLNFCe")
        {
            this.dados = dados;
            this.indexcsc = indexcsc;
            this.csc = csc;
            this.param = param;
        }

        protected override string FunctionParameters()
        {
            return "\"dados\"" + ":" + "\"" + dados + "\"" + "," +
                    "\"indexcsc\"" + ":" + indexcsc + "," +
                    "\"csc\"" + ":" + "\"" + csc + "\"" + "," +
                    "\"param\"" + ":" + param;
        }
    }
}