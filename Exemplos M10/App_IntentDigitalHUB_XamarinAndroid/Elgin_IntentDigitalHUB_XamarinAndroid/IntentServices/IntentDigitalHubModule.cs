namespace Xamarin_Android_Intent_Digital_Hub.IntentServices
{
    public class IntentDigitalHubModule
    {
        private IntentDigitalHubModule(string value) { Value = value; }

        public string Value { get; private set; }

        public static IntentDigitalHubModule BRIDGE = new IntentDigitalHubModule("com.elgin.e1.digitalhub.BRIDGE");
        public static IntentDigitalHubModule SAT = new IntentDigitalHubModule("com.elgin.e1.digitalhub.SAT");
        public static IntentDigitalHubModule TERMICA = new IntentDigitalHubModule("com.elgin.e1.digitalhub.TERMICA");
    }
}