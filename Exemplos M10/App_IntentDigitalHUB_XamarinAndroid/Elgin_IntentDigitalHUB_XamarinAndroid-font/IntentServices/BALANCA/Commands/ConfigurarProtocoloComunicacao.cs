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

namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.BALANCA.Commands
{
    class ConfigurarProtocoloComunicacao : BalanceCommand
    {
        readonly int protocoloComunicacao;
        
        public ConfigurarProtocoloComunicacao(int protocoloComunicacao) : base("ConfigurarProtocoloComunicacao")
        {
            this.protocoloComunicacao = protocoloComunicacao;
        }
        protected override string FunctionParameters()
        {
            return "\"protocoloComunicacao\"" + ":" + this.protocoloComunicacao;
        }
    }
}