namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge
{
    class SetTimeout : BridgeCommand
    {
        private readonly int timeout;

        public SetTimeout(int timeout) : base("SetTimeout")
        {
            this.timeout = timeout;
        }
        protected override string FunctionParameters()
        {
            return "\"timeout\"" + ":" + timeout;
        }
    }
}