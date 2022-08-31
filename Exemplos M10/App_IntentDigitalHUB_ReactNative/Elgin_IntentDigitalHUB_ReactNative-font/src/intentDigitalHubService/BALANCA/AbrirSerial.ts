import {BalancaCommand} from './BalancaCommand';

export class AbrirSerial extends BalancaCommand {
  readonly baudrate: number;
  readonly lenght: number;
  readonly parity: string;
  readonly stopbits: number;

  constructor(
    baudrate: number,
    lenght: number,
    parity: string,
    stopbits: number,
  ) {
    super('AbrirSerial');
    this.baudrate = baudrate;
    this.lenght = lenght;
    this.parity = parity;
    this.stopbits = stopbits;
  }

  functionParameters(): object {
    return {
      baudrate: this.baudrate,
      lenght: this.lenght,
      parity: this.parity,
      stopbits: this.stopbits,
    };
  }
}
