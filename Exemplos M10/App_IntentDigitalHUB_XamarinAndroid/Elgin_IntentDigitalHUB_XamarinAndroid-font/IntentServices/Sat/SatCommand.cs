using Newtonsoft.Json;

namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Sat
{
    abstract class SatCommand : IntentDigitalHubCommand
    {
        //O retorno dos comandos SAT é sempre uma String
        [JsonProperty(PropertyName = "resultado")]
        private readonly string resultado;

        protected SatCommand(string functionName) : base(functionName, IntentDigitalHubModule.SAT) { }

        public string GetResultado()
        {
            return resultado;
        }
    }
}