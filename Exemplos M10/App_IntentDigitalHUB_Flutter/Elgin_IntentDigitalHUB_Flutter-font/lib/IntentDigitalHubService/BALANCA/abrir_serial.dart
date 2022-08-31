import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/balanca_command.dart';

class AbrirSerial extends BalancaCommand {
  final int _baudrate;
  final int _lenght;
  final String _parity; //parity é um 'char'
  final int _stopbits;

  AbrirSerial(this._baudrate, this._lenght, this._parity, this._stopbits)
      : super('AbrirSerial');

  @override
  String functionParameters() {
    return '"baudrate"'
        ':'
        '$_baudrate'
        ','
        '"lenght"'
        ':'
        '$_lenght'
        ','
        '"parity"'
        ':'
        '"$_parity"'
        ','
        '"stopbits"'
        ':'
        '$_stopbits';
  }
}
