import '../elginpay_command.dart';

class SetPersonalizacao extends ElginPayCommand {
  final String iconeToolbar;
  final String fonte;
  final String corFonte;
  final String corFonteTeclado;
  final String corFundoToolbar;
  final String corFundoTela;
  final String corTeclaLiberadaTeclado;
  final String corFundoTeclado;
  final String corTextoCaixaEdicao;
  final String corSeparadorMenu;

  SetPersonalizacao(
      this.iconeToolbar,
      this.fonte,
      this.corFonte,
      this.corFonteTeclado,
      this.corFundoToolbar,
      this.corFundoTela,
      this.corTeclaLiberadaTeclado,
      this.corFundoTeclado,
      this.corTextoCaixaEdicao,
      this.corSeparadorMenu)
      : super('setPersonalizacao');

  @override
  get functionParametersJson => {
        'iconeToolbar': iconeToolbar,
        'fonte': fonte,
        'corFonte': corFonte,
        'corFonteTeclado': corFonteTeclado,
        'corFundoToolbar': corFundoToolbar,
        'corFundoTela': corFundoTela,
        'corTeclaLiberadaTeclado': corTeclaLiberadaTeclado,
        'corFundoTeclado': corFundoTeclado,
        'corTextoCaixaEdicao': corTextoCaixaEdicao,
        'corSeparadorMenu': corSeparadorMenu,
      };
}
