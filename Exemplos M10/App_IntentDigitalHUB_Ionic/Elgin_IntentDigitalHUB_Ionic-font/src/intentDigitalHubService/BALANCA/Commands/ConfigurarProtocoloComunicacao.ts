import {BalancaCommand} from '../BalancaCommand';

export class ConfigurarProtocoloComunicacao extends BalancaCommand {
  readonly protocoloComunicacao: number;

  constructor(protocoloComunicacao: number) {
    super('ConfigurarProtocoloComunicacao');
    this.protocoloComunicacao = protocoloComunicacao;
  }

  functionParameters(): object {
    return {
      protocoloComunicacao: this.protocoloComunicacao,
    };
  }
}
