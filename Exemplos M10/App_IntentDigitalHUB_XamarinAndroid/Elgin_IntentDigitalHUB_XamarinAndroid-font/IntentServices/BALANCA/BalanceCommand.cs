using Newtonsoft.Json;

namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.BALANCA
{
    /**
     * Classe que generaliza todos os comandos do módulo BRIDGE, definindo o módulo do comando e o seu tipo de retorno
     */
    abstract class BalanceCommand : IntentDigitalHubCommand
    {
        //O retorno dos comandos bridge é sempre um JSON em String
        [JsonProperty(PropertyName = "resultado")]
        private string resultado;

        protected BalanceCommand(string functionName) : base(functionName, IntentDigitalHubModule.BALANCA) { }

        public string GetResultado()
        {
            return resultado;
        }
    }
}