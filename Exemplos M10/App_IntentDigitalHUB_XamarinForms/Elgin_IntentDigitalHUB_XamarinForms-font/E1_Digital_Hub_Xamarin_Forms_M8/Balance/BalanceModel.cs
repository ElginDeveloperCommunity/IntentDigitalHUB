using System;
using System.Collections.Generic;
using System.Text;

namespace Xamarin_Forms_Intent_Digital_Hub.Balance
{
    class BalanceModel
    {
        private BalanceModel(int balanceCode) { this.balanceCode = balanceCode; }

        private int balanceCode;
        public int getBalanceCode() { return balanceCode; }

        public static BalanceModel DP3005 = new BalanceModel(0);
        public static BalanceModel SA110 = new BalanceModel(1);
        public static BalanceModel DPSC = new BalanceModel(2);
        public static BalanceModel DP30CK = new BalanceModel(3);
    }
}
