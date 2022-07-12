namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica
{
    class Corte : TermicaCommand
    {
        readonly int avanco;

        public Corte(int avanco) : base("Corte")
        {
            this.avanco = avanco;
        }

        protected override string FunctionParameters()
        {
            return "\"avanco\"" + ":" + avanco;
        }
    }
}