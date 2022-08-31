namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class ImprimirCupomNfce : BridgeCommand
    {
        readonly string xml;
        readonly int indexcsc;
        readonly string csc;

        public ImprimirCupomNfce(string xml, int indexcsc, string csc) : base("ImprimirCupomNfce")
        {
            this.xml = xml;
            this.indexcsc = indexcsc;
            this.csc = csc;
        }

        protected override string FunctionParameters()
        {
            return "\"xml\"" + ":" + "\"" + xml + "\"" + "," +
                    "\"indexcsc\"" + ":" + indexcsc + "," +
                    "\"csc\"" + ":" + "\"" + csc + "\"";
        }
    }
}