import { BridgeCommand } from "../BridgeCommand";

export class IniciaCancelamentoVenda extends BridgeCommand {
  readonly idTransacao: number;
  readonly pdv: string;
  readonly valorTotal: string;
  readonly dataHora: string;
  readonly nsu: string;

  constructor(
    idTransacao: number,
    pdv: string,
    valorTotal: string,
    dataHora: string,
    nsu: string
  ) {
    super("IniciaCancelamentoVenda");
    this.idTransacao = idTransacao;
    this.pdv = pdv;
    this.valorTotal = valorTotal;
    this.dataHora = dataHora;
    this.nsu = nsu;
  }

  functionParameters(): object {
    return {
      idTransacao: this.idTransacao,
      pdv: this.pdv,
      valorTotal: this.valorTotal,
      dataHora: this.dataHora,
      nsu: this.nsu,
    };
  }
}
