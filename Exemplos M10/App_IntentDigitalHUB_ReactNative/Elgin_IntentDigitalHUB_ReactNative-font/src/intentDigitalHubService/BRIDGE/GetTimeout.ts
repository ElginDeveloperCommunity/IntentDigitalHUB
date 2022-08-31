import {BridgeCommand} from './BridgeCommand';

export class GetTimeout extends BridgeCommand {
  constructor() {
    super('GetTimeout');
  }

  functionParameters(): object {
    return '';
  }
}
