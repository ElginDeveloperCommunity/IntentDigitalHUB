namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class ImprimeCupomTEF : TermicaCommand
    {
        readonly string dados;

        public ImprimeCupomTEF(string dados) : base("ImprimeCupomTEF")
        {
            this.dados = dados;
        }

        protected override string FunctionParameters()
        {
            return null;
        }
    }
}