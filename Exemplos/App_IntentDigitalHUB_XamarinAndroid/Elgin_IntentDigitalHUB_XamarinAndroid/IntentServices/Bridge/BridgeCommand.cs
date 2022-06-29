using Newtonsoft.Json;

namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge
{
    /**
     * Classe que generaliza todos os comandos do módulo BRIDGE, definindo o módulo do comando e o seu tipo de retorno
     */
    abstract class BridgeCommand : IntentDigitalHubCommand
    {
        //O retorno dos comandos bridge é sempre um JSON em String
        [JsonProperty(PropertyName = "resultado")]
        private readonly string resultado;

        protected BridgeCommand(string functionName) : base(functionName, IntentDigitalHubModule.BRIDGE) { }

        public string GetResultado()
        {
            return resultado;
        }
    }
}