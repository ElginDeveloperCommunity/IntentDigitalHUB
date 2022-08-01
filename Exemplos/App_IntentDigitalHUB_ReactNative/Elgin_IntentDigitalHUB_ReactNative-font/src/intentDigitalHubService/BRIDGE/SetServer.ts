import {BridgeCommand} from './BridgeCommand';

export class SetServer extends BridgeCommand {
  readonly ipTerminal: string;
  readonly portaTransacao: number;
  readonly portaStatus: number;

  constructor(ipTerminal: string, portaTransacao: number, portaStatus: number) {
    super('SetServer');
    this.ipTerminal = ipTerminal;
    this.portaTransacao = portaTransacao;
    this.portaStatus = portaStatus;
  }

  functionParameters(): object {
    return {
      ipTerminal: this.ipTerminal,
      portaTransacao: this.portaTransacao,
      portaStatus: this.portaStatus,
    };
  }
}
