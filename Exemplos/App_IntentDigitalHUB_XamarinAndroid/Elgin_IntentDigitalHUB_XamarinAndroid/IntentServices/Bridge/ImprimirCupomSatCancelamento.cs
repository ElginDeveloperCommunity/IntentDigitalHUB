namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge
{
    class ImprimirCupomSatCancelamento : BridgeCommand
    {
        readonly string xml;
        readonly string assQRCode;

        public ImprimirCupomSatCancelamento(string xml, string assQRCode) : base("ImprimirCupomSatCancelamento")
        {
            this.xml = xml;
            this.assQRCode = assQRCode;
        }

        protected override string FunctionParameters()
        {
            return "\"xml\"" + ":" + "\"" + xml + "\"" + "," +
                    "\"assQRCode\"" + ":" + "\"" + assQRCode + "\"";
        }
    }
}