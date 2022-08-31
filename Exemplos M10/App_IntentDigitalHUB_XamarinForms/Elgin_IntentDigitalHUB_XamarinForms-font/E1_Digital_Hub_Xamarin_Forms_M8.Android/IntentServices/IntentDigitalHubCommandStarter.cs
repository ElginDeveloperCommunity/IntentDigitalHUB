using Android.Content;
using Android.Util;
using System.Collections.Generic;
using System.Text;
using Xamarin.Forms;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin.Essentials;
using System;

[assembly: Dependency(typeof(Xamarin_Forms_Intent_Digital_Hub.Droid.IntentDigitalHubCommandStarter))]
namespace Xamarin_Forms_Intent_Digital_Hub.Droid
{
    class IntentDigitalHubCommandStarter : IIntentDigitalHubCommandStarter
    {
        public IntentDigitalHubCommandStarter()
        {
        }

        public void StartHubCommandActivity(IntentDigitalHubCommand digitalHubCommand, int requestCode)
        {
            //Captura o módulo intent correspondente da função
            string modulePathOfCommand = digitalHubCommand.correspondingIntentModule.Value;

            Intent intent = new Intent(modulePathOfCommand);
            intent.PutExtra("comando", digitalHubCommand.GetCommandJSON());

            Log.Debug("command_json", digitalHubCommand.GetCommandJSON());

            Platform.CurrentActivity.StartActivityForResult(intent, requestCode);
        }

        public void LongLog(string str)
        {
            if (str.Length > 4000)
            {
                Log.Debug("BIG_COMMAND", str.Substring(0, 4000));
                LongLog(str[4000..]);
            }
            else
                Log.Debug("BIG_COMMAND", str);
        }

        //@Overload da função utilitária para iniciar uma atividade do digitalhub com vários comandos de uma vez só
        //basta apenas criar uma List<> com todos os comandos a serem inseridos
        public void StartHubCommandActivity(List<IntentDigitalHubCommand> digitalHubCommandList, int requestCode)
        {
            //A lista de comandos não pode estar vazia
            if (digitalHubCommandList.Count == 0)
                throw new Java.Lang.IllegalArgumentException("A lista de comandos a serem concatenadas não pode estar vazia!");

            if (!ValidateCommandList(digitalHubCommandList))
                throw new Java.Lang.IllegalArgumentException("Todos os comandos da lista devem pertencer ao mesmo módulo!");

            //Verifica de qual modulo são os comandos da lista
            string modulePathOfCommand = digitalHubCommandList[0].correspondingIntentModule.Value;

            string digitalHubCommandJSON = ConcatenateDigitalHubCommands(digitalHubCommandList);

            Intent intent = new Intent(modulePathOfCommand);
            intent.PutExtra("comando", digitalHubCommandJSON);

            LongLog(digitalHubCommandJSON);
            Platform.CurrentActivity.StartActivityForResult(intent, requestCode);
        }

        //Cria o comando JSON com todos os comandos da lista formatado
        public string ConcatenateDigitalHubCommands(List<IntentDigitalHubCommand> digitalHubCommandList)
        {
            StringBuilder concatenatedDigitalHubCommand = new StringBuilder();

            foreach (IntentDigitalHubCommand digitalHubCommand in digitalHubCommandList)
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

        //Os comandos na lista a serem concatenados em um só comando não podem diferir entre módulo, um comando com múltiplas funções devem sempre fazer parte do mesmo módulo
        public bool ValidateCommandList(List<IntentDigitalHubCommand> digitalHubCommandList)
        {
            //Checa o modulo do primeiro comando da lista para comparação com todos posteriores
            IntentDigitalHubModule digitalHubIntentBase = digitalHubCommandList[0].correspondingIntentModule;

            foreach (IntentDigitalHubCommand digitalHubCommand in digitalHubCommandList)
            {
                if (digitalHubCommand.correspondingIntentModule != digitalHubIntentBase)
                    return false;
            }
            return true;
        }    
    }
}   