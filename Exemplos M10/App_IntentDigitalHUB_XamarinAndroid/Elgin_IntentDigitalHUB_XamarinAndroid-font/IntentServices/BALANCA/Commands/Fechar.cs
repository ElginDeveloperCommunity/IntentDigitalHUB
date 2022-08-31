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
    class Fechar : BalanceCommand
    {
        public Fechar() : base("Fechar") { }

        protected override string FunctionParameters()
        {
            return "";
        }

    }
}