// ignore_for_file: constant_identifier_names

part of 'balance_page.dart';

enum BalanceProtocol {
  PROTOCOL_0,
  PROTOCOL_1,
  PROTOCOL_2,
  PROTOCOL_3,
  PROTOCOL_4,
  PROTOCOL_5,
  PROTOCOL_6,
  PROTOCOL_7,
}

extension BalanceProtocolExtension on BalanceProtocol {
  //Define o @name de cada protocol afim de reusar na apresentação da tela
  String get name {
    switch (this) {
      case BalanceProtocol.PROTOCOL_0:
        return 'PROTOCOL 0';
      case BalanceProtocol.PROTOCOL_1:
        return 'PROTOCOL 1';
      case BalanceProtocol.PROTOCOL_2:
        return 'PROTOCOL 2';
      case BalanceProtocol.PROTOCOL_3:
        return 'PROTOCOL 3';
      case BalanceProtocol.PROTOCOL_4:
        return 'PROTOCOL 4';
      case BalanceProtocol.PROTOCOL_5:
        return 'PROTOCOL 5';
      case BalanceProtocol.PROTOCOL_6:
        return 'PROTOCOL 6';
      case BalanceProtocol.PROTOCOL_7:
        return 'PROTOCOL 7';
    }
  }

  //Retorna o código de protocolo correspondente de cada protocolo
  int get protocolCode {
    switch (this) {
      case BalanceProtocol.PROTOCOL_0:
        return 0;
      case BalanceProtocol.PROTOCOL_1:
        return 1;
      case BalanceProtocol.PROTOCOL_2:
        return 2;
      case BalanceProtocol.PROTOCOL_3:
        return 3;
      case BalanceProtocol.PROTOCOL_4:
        return 4;
      case BalanceProtocol.PROTOCOL_5:
        return 5;
      case BalanceProtocol.PROTOCOL_6:
        return 6;
      case BalanceProtocol.PROTOCOL_7:
        return 7;
    }
  }
}
