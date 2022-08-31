using Newtonsoft.Json;

namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    /**
     * Classe que generaliza todos os comandos do módulo TERMICA, definindo o módulo do comando e o seu tipo de retorno
     */
    abstract class TermicaCommand : IntentDigitalHubCommand
    {
        //O retorno dos comandos de impressora é sempre um int
        [JsonProperty(PropertyName = "resultado")]
        private readonly int resultado;

        protected TermicaCommand(string functionName) : base(functionName, IntentDigitalHubModule.TERMICA) { }

        public int GetResultado()
        {
            return resultado;
        }
    }
}