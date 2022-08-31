using Newtonsoft.Json;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices
{
    public abstract class IntentDigitalHubCommand
    {
        protected string functionName;
        public IntentDigitalHubModule correspondingIntentModule;

        [JsonProperty(PropertyName = "funcao")]
        private string funcao;

        [JsonProperty(PropertyName = "mensagem")]
        private string mensagem;

        //O código de sucesso de um comando no DigitalHub é 0
        readonly static public int RESULT_OK = 0;

        protected IntentDigitalHubCommand(string functionName, IntentDigitalHubModule correspondingIntentModule)
        {
            this.functionName = functionName;
            this.correspondingIntentModule = correspondingIntentModule;
        }

        //Formata o JSON de acordo com os parâmetros definidos por cada subclasse, o modificador de acesso protected impede a exposição do método que sera usado somente em DigitalHubUtils para o start da intent
        public string GetCommandJSON()
        {
            return "[{" +
                    "\"funcao\"" + ":" + "\"" + functionName + "\"" + "," +
                    "\"parametros\"" + ":" + "{" + FunctionParameters() + "}" +
                    "}]";
        }

        //Função que deve ser implementada por cada subclasse definindo a formatação dos parâmetros da função específica
        protected abstract string FunctionParameters();

        //Getters do Serializable
        public string GetRetornoFuncao()
        {
            return funcao;
        }

        public string GetRetornoMensagem()
        {
            return mensagem;
        }
    }
}
