namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge
{
    class ImprimirCupomSat : BridgeCommand
    {
        readonly string xml;

        public ImprimirCupomSat(string xml) : base("ImprimirCupomSat")
        {
            this.xml = xml;
        }

        protected override string FunctionParameters()
        {
            return "\"xml\"" + ":" + "\"" + xml + "\"";
        }
    }
}