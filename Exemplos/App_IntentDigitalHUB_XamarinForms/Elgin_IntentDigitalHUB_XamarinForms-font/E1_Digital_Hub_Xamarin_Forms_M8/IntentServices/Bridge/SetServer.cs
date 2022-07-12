namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class SetServer : BridgeCommand
    {
        readonly string ipTerminal;
        readonly int portaTransacao;
        readonly int portaStatus;

        public SetServer(string ipTerminal, int portaTransacao, int portaStatus) : base("SetServer")
        {
            this.ipTerminal = ipTerminal;
            this.portaTransacao = portaTransacao;
            this.portaStatus = portaStatus;
        }

        protected override string FunctionParameters()
        {
            return "\"ipTerminal\"" + ":" + "\"" + ipTerminal + "\"" + "," +
                    "\"portaTransacao\"" + ":" + portaTransacao + "," +
                    "\"portaStatus\"" + ":" + portaStatus;
        }
    }
}