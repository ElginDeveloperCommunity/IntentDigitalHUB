namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class SetSenhaServer : BridgeCommand
    {
        readonly string senha;
        readonly bool habilitada;

        public SetSenhaServer(string senha, bool habilitada) : base("SetSenhaServer")
        {
            this.senha = senha;
            this.habilitada = habilitada;
        }

        protected override string FunctionParameters()
        {
            return "\"senha\"" + ":" + "\"" + senha + "\"" + "," +
                    "\"habilitada\"" + ":" + habilitada;
        }
    }
}