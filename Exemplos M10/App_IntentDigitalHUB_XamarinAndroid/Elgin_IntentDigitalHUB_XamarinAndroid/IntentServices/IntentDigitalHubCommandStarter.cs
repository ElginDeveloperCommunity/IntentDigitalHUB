using Android.App;
using Android.Content;
using Android.Util;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Android_Intent_Digital_Hub.IntentServices
{
    /**
     * Classe service utilizada para iniciar os comandos do Intent Digital Hub
    */
    public sealed class IntentDigitalHubCommandStarter
    {
        //Classe service, não deve ser possível instânciar
        private IntentDigitalHubCommandStarter()
        {
        }

        /**
         * Função que inicializa um comando do IDH, reduz a repetição no start da intent
         *
         * @param activity                Contexto necessário da atividade que irá invocar a intent
         * @param intentDigitalHubCommand O comando parâmetrizado a ser iniciado
         * @param requestCode             O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
        */
        public static void StartHubCommandActivity(Activity activity, IntentDigitalHubCommand intentDigitalHubCommand, int requestCode)
        {
            //Captura o módulo intent correspondente da função
            string modulePathOfCommand = intentDigitalHubCommand.correspondingIntentModule.Value;

            Intent intent = new Intent(modulePathOfCommand);
            intent.PutExtra("comando", intentDigitalHubCommand.GetCommandJSON());

            Log.Debug("command_json", intentDigitalHubCommand.GetCommandJSON());

            activity.StartActivityForResult(intent, requestCode);
        }

        public static void LongLog(string str)
        {
            if (str.Length > 4000)
            {
                Log.Debug("BIG_COMMAND", str.Substring(0, 4000));
                LongLog(str[4000..]);
            }
            else
                Log.Debug("BIG_COMMAND", str);
        }

        /**
         * Overload da função de inicio de comando, permitindo a inicialização de vários comandos, basta fornecer uma List com todos os comandos
         *
         * @param activity                    Contexto necessário da atividade que irá invocar a intent
         * @param intentDigitalHubCommandList A lista de comandos que será tranformada em um só comando através da concatenação de todos os comandos da lista formando um arrayJSon contendo todos os comandos enviados
         * @param requestCode                 O código de requisição que será utilizado para iniciar a intent, para filtro de retorno numa atividade com múltiplos comandos separados iniciados inidividualmente
        */
        public static void StartHubCommandActivity(Activity activity, List<IntentDigitalHubCommand> intentDigitalHubCommandList, int requestCode) {
            //A lista de comandos não pode estar vazia
            if (intentDigitalHubCommandList.Count == 0)
                throw new Java.Lang.IllegalArgumentException("A lista de comandos a serem concatenadas não pode estar vazia!");

            if (!ValidateCommandList(intentDigitalHubCommandList))
                throw new Java.Lang.IllegalArgumentException("Todos os comandos da lista devem pertencer ao mesmo módulo!");

            //Verifica de qual modulo são os comandos da lista
            string modulePathOfCommand = intentDigitalHubCommandList[0].correspondingIntentModule.Value;

            string digitalHubCommandJSON = ConcatenateDigitalHubCommands(intentDigitalHubCommandList);

            Intent intent = new Intent(modulePathOfCommand);
            intent.PutExtra("comando", digitalHubCommandJSON);

            LongLog(digitalHubCommandJSON);

            activity.StartActivityForResult(intent, requestCode);
        }

        /**
         * Cria o comando JSON com todos os comandos da lista formatado
        */
        private static string ConcatenateDigitalHubCommands(List<IntentDigitalHubCommand> intentDigitalHubCommandList)
        {
            StringBuilder concatenatedDigitalHubCommand = new StringBuilder();

            foreach (IntentDigitalHubCommand digitalHubCommand in intentDigitalHubCommandList)
            {
                //Remove o fechamento de parênteses de todos os comandos
                string actualDigitalHubCommandJSON = digitalHubCommand.GetCommandJSON()[1..^1];

                //Adiciona uma virgula para separar uma nova função
                concatenatedDigitalHubCommand.Append(actualDigitalHubCommandJSON).Append(",");
            }

            //Remove a ultima vírgula inserida
            concatenatedDigitalHubCommand.Remove(concatenatedDigitalHubCommand.Length - 1, 1);

            //Fecha o JSON concatenado com os parênteses []
            concatenatedDigitalHubCommand.Insert(0, "[");
            concatenatedDigitalHubCommand.Insert(concatenatedDigitalHubCommand.Length, "]");

            return concatenatedDigitalHubCommand.ToString().Trim();
        }

        /**
         * Os comandos na lista a serem concatenados em um só comando não podem diferir entre módulo, um comando com múltiplas funções devem sempre fazer parte do mesmo módulo
        */
        private static bool ValidateCommandList(List<IntentDigitalHubCommand> intentDigitalHubCommandList)
        {
            //Checa o modulo do primeiro comando da lista para comparação com todos posteriores
            IntentDigitalHubModule digitalHubIntentBase = intentDigitalHubCommandList[0].correspondingIntentModule;

            foreach (IntentDigitalHubCommand digitalHubCommand in intentDigitalHubCommandList)
                if (digitalHubCommand.correspondingIntentModule != digitalHubIntentBase)
                    return false;
            return true;
        }
    }
}