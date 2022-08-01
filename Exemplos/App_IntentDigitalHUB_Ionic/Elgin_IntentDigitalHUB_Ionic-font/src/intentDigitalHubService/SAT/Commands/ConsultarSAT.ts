import { SatCommand } from "../SatCommand";

export class ConsultarSAT extends SatCommand {
  readonly numSessao: number;

  constructor(numSessao: number) {
    super("ConsultarSat");
    this.numSessao = numSessao;
  }

  functionParameters(): object {
    return { numSessao: this.numSessao };
  }
}
