using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.IntentServices.Balanca.Commands
{
    class AbrirSerial : BalanceCommand
    {
        readonly int baudrate;
        readonly int lenght;
        readonly char parity;
        readonly int stopbits;

        public AbrirSerial(int baudrate, int lenght, char parity, int stopbits) : base("AbrirSerial")
        {
            this.baudrate = baudrate;
            this.lenght = lenght;
            this.parity = parity;
            this.stopbits = stopbits;
        }

        protected override string FunctionParameters()
        {
            return "\"baudrate\"" + ":" + this.baudrate + "," +
                "\"lenght\"" + ":" + this.lenght + "," +
                "\"parity\"" + ":" + "\"" + this.parity + "\"" + "," +
                "\"stopbits\"" + ":" + this.stopbits;
        }

    }
}
