// ignore_for_file: constant_identifier_names

part of 'balance_page.dart';

enum BalanceModel { DP3005, SA110, DPSC, DP30CK }

extension BalanceModelExtension on BalanceModel {
  //Retorna o código de modelo correspondente de cada balança
  int get balanceCode {
    switch (this) {
      case BalanceModel.DP3005:
        return 0;
      case BalanceModel.SA110:
        return 1;
      case BalanceModel.DPSC:
        return 2;
      case BalanceModel.DP30CK:
        return 3;
    }
  }
}
