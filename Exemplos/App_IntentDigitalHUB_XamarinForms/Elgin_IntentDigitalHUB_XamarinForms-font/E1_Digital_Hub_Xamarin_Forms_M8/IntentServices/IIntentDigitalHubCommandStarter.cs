using System.Collections.Generic;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices
{
    public interface IIntentDigitalHubCommandStarter
    {
        void StartHubCommandActivity(IntentDigitalHubCommand digitalHubCommand, int requestCode);

        void LongLog(string str);

        void StartHubCommandActivity(List<IntentDigitalHubCommand> digitalHubCommandList, int requestCode);

        string ConcatenateDigitalHubCommands(List<IntentDigitalHubCommand> digitalHubCommandList);

        bool ValidateCommandList(List<IntentDigitalHubCommand> digitalHubCommandList);
    }
}
