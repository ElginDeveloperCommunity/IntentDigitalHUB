namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    class SetSenha : BridgeCommand
    {
        private readonly string senha;
        private readonly bool habilitada;

        public SetSenha(string senha, bool habilitada) : base("SetSenha")
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