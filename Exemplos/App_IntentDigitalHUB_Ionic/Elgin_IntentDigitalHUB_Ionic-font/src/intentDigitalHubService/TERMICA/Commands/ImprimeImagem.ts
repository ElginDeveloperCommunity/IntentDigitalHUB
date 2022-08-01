import { TermicaCommand } from "../TermicaCommand";

export class ImprimeImagem extends TermicaCommand {
  readonly path: string;

  constructor(path: string) {
    super("ImprimeImagem");
    this.path = path;
  }

  functionParameters(): object {
    return { path: this.path };
  }
}
