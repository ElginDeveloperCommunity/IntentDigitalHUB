namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge
{
    class ConsultarStatus : BridgeCommand
    {
        public ConsultarStatus() : base("ConsultarStatus") { }

        protected override string FunctionParameters()
        {
            return "";
        }
    }
}