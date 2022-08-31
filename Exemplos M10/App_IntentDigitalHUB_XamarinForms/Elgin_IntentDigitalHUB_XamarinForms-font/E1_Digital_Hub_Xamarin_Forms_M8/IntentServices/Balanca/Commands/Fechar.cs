using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Balanca.Commands
{
    class Fechar : BalanceCommand
    {
        public Fechar() : base("Fechar") { }

        protected override string FunctionParameters()
        {
            return "";
        }

    }
}
