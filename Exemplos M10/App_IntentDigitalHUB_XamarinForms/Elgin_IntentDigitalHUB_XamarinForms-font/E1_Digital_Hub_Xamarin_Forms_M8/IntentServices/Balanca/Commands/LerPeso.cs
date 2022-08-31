using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Balanca.Commands
{
    class LerPeso : BalanceCommand
    {
        readonly private int qtdLeituras;

        public LerPeso(int qtdLeituras) : base("LerPeso")
        {
            this.qtdLeituras = qtdLeituras;
        }

        protected override string FunctionParameters()
        {
            return "\"qtdLeituras\"" + ":" + this.qtdLeituras;
        }
    }
}
