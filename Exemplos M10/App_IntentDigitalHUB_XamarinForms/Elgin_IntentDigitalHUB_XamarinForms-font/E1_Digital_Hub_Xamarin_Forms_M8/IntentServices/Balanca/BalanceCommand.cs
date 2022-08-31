using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Balanca
{
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
