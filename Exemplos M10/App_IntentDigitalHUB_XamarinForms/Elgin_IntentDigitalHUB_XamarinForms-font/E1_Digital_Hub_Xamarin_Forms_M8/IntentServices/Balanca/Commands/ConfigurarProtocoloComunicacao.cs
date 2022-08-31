using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Balanca.Commands
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
