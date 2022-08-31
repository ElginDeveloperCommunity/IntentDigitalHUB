using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using AndroidX.Annotations;

/**
 * Protocolos de comunicação com a balança, o código de cada protocolo corresponde ao índice de declaração no enum
 * Ex: Protocolo 4 possui 4 de código correspondente
 */

namespace Xamarin_Android_Intent_Digital_Hub.Balanca
{
    class BalanceProtocol
    {
        private BalanceProtocol(string friendlyName) { this.friendlyName = friendlyName; }

        private String friendlyName;

        public string toString() { return this.friendlyName; }

        public static BalanceProtocol PROTOCOL_0 = new BalanceProtocol("PROTOCOL 0");
        public static BalanceProtocol PROTOCOL_1 = new BalanceProtocol("PROTOCOL 1");
        public static BalanceProtocol PROTOCOL_2 = new BalanceProtocol("PROTOCOL 2");
        public static BalanceProtocol PROTOCOL_3 = new BalanceProtocol("PROTOCOL 3");
        public static BalanceProtocol PROTOCOL_4 = new BalanceProtocol("PROTOCOL 4");
        public static BalanceProtocol PROTOCOL_5 = new BalanceProtocol("PROTOCOL 5");
        public static BalanceProtocol PROTOCOL_6 = new BalanceProtocol("PROTOCOL 6");
        public static BalanceProtocol PROTOCOL_7 = new BalanceProtocol("PROTOCOL 7");
    }

}
