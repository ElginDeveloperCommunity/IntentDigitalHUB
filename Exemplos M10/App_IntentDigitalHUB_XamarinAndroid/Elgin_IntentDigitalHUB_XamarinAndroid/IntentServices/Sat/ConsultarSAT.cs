namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Sat
{
    class ConsultarSAT : SatCommand
    {
        readonly int numSessao;

        public ConsultarSAT(int numSessao) : base("ConsultarSat")
        {
            this.numSessao = numSessao;
        }

        protected override string FunctionParameters()
        {
            return "\"numSessao\"" + ":" + numSessao;
        }
    }
}