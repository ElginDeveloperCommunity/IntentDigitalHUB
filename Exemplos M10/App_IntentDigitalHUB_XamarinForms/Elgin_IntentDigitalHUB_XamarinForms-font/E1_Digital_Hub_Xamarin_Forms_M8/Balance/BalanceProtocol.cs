using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.Balance
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
