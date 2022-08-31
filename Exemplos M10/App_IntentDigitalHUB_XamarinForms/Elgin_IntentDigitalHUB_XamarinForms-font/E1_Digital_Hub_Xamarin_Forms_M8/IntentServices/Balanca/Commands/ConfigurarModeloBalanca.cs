using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Balanca.Commands
{
    class ConfigurarModeloBalanca : BalanceCommand
    {
        readonly int modeloBalanca;

        public ConfigurarModeloBalanca(int modeloBalanca) : base("ConfigurarModeloBalanca")
        {
            this.modeloBalanca = modeloBalanca;
        }

        protected override string FunctionParameters()
        {
            return "\"modeloBalanca\"" + ":" + this.modeloBalanca;
        }
    }
}
