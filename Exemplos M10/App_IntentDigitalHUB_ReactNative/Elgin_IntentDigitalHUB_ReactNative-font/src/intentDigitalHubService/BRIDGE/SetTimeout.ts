import {BridgeCommand} from './BridgeCommand';

export class SetTimeout extends BridgeCommand {
  readonly timeout: number;

  constructor(timeout: number) {
    super('SetTimeout');
    this.timeout = timeout;
  }

  functionParameters(): object {
    return {timeout: this.timeout};
  }
}
