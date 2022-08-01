import {TermicaCommand} from './TermicaCommand';

export class ImprimeImagemMemoria extends TermicaCommand {
  readonly key: string;
  readonly scala: number;

  constructor(key: string, scala: number) {
    super('ImprimeImagemMemoria');
    this.key = key;
    this.scala = scala;
  }

  functionParameters(): object {
    return {key: this.key, scala: this.scala};
  }
}
