namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class StatusImpressora : TermicaCommand
    {
        readonly int param;

        public StatusImpressora(int param) : base("StatusImpressora")
        {
            this.param = param;
        }

        protected override string FunctionParameters()
        {
            return "\"param\"" + ":" + param;
        }
    }
}