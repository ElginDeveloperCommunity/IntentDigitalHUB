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