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