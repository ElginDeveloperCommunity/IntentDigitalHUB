namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class ConsultarUltimaTransacao : BridgeCommand
    {
        private readonly string pdv;

        public ConsultarUltimaTransacao(string pdv) : base("ConsultarUltimaTransacao")
        {
            this.pdv = pdv;
        }

        protected override string FunctionParameters()
        {
            return "\"pdv\"" + ":" + "\"" + this.pdv + "\"";
        }
    }
}