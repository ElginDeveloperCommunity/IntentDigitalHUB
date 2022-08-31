namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge
{
    class GetTimeout : BridgeCommand
    {
        public GetTimeout() : base("GetTimeout")
        {
        }

        protected override string FunctionParameters()
        {
            return "";
        }
    }
}