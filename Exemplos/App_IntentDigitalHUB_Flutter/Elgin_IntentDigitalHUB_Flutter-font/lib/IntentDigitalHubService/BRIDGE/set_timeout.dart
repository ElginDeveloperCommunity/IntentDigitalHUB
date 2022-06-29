import 'bridge_command.dart';

class SetTimeout extends BridgeCommand {
  final int _timeout;

  SetTimeout(this._timeout) : super('SetTimeout');

  @override
  functionParameters() => '"timeout"' ':' '$_timeout';
}
