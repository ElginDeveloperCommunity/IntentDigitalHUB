import 'sat_command.dart';

class ConsultarSAT extends SatCommand {
  final int _numSessao;

  ConsultarSAT(this._numSessao) : super('ConsultarSat');

  @override
  functionParameters() {
    return '"numSessao"' ':' '$_numSessao';
  }
}
