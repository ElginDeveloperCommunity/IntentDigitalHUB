using Newtonsoft.Json;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica
{
    abstract class TermicaCommand : IntentDigitalHubCommand
    {
        //O retorno dos comandos de impressora é sempre um int, correspondendo a documentação TERMICA
        [JsonProperty(PropertyName = "resultado")]
        private int resultado;

        protected TermicaCommand(string functionName) : base(functionName, IntentDigitalHubModule.TERMICA) { }

        public int GetResultado()
        {
            return resultado;
        }
    }
}