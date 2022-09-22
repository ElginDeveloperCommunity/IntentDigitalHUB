import {ElginPayCommand} from '../ElginPayCommand';

export class SetPersonalizacao extends ElginPayCommand {
  readonly iconeToolbar: string;
  readonly fonte: string;
  readonly corFonte: string;
  readonly corFonteTeclado: string;
  readonly corFundoToolbar: string;
  readonly corFundoTela: string;
  readonly corTeclaLiberadaTeclado: string;
  readonly corFundoTeclado: string;
  readonly corTextoCaixaEdicao: string;
  readonly corSeparadorMenu: string;

  constructor(
    iconeToolbar: string,
    fonte: string,
    corFonte: string,
    corFonteTeclado: string,
    corFundoToolbar: string,
    corFundoTela: string,
    corTeclaLiberadaTeclado: string,
    corFundoTeclado: string,
    corTextoCaixaEdicao: string,
    corSeparadorMenu: string,
  ) {
    super('setPersonalizacao');
    this.iconeToolbar = iconeToolbar;
    this.fonte = fonte;
    this.corFonte = corFonte;
    this.corFonteTeclado = corFonteTeclado;
    this.corFundoToolbar = corFundoToolbar;
    this.corFundoTela = corFundoTela;
    this.corTeclaLiberadaTeclado = corTeclaLiberadaTeclado;
    this.corFundoTeclado = corFundoTeclado;
    this.corTextoCaixaEdicao = corTextoCaixaEdicao;
    this.corSeparadorMenu = corSeparadorMenu;
  }

  functionParameters(): object {
    return {
      iconeToolbar: this.iconeToolbar,
      fonte: this.fonte,
      corFonte: this.corFonte,
      corFonteTeclado: this.corFonteTeclado,
      corFundoToolbar: this.corFundoToolbar,
      corFundoTela: this.corFundoTela,
      corTeclaLiberadaTeclado: this.corTeclaLiberadaTeclado,
      corFundoTeclado: this.corFundoTeclado,
      corTextoCaixaEdicao: this.corTextoCaixaEdicao,
      corSeparadorMenu: this.corSeparadorMenu,
    };
  }
}
