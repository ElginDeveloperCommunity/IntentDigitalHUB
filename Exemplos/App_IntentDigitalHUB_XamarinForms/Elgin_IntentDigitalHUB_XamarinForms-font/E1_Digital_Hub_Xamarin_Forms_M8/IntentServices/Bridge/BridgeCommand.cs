using Newtonsoft.Json;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge
{
    abstract class BridgeCommand : IntentDigitalHubCommand
    {
        //O retorno dos comandos bridge é um JSON em String, que possui o código de retorno ("e1_bridge_code") e a mensagem ("e1_bridge_msg")
        [JsonProperty(PropertyName = "resultado")]
        private string resultado;

        protected BridgeCommand(string functionName) : base(functionName, IntentDigitalHubModule.BRIDGE) { }

        public string GetResultado()
        {
            return resultado;
        }
    }
}