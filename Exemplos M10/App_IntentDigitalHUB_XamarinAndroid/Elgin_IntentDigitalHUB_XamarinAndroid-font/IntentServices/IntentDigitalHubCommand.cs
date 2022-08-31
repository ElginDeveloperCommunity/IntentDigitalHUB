namespace Xamarin_Android_Intent_Digital_Hub.IntentServices
{
    /**
     * Classe abstrata que generaliza as carecterísticas comuns à todos os comandos do Intent Digital Hub, as classes que herdam desta implementam cada comando de maneira específica servindo como Wrapper
     */
    public abstract class IntentDigitalHubCommand
    {
        //Nome da função
        protected readonly string functionName;

        //Módulo a qual a função pertence
        public readonly IntentDigitalHubModule correspondingIntentModule;

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
    }
}