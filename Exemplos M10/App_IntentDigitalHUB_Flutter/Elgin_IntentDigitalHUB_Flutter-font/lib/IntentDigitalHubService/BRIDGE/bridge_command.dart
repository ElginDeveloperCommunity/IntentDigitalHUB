import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_module.dart';

abstract class BridgeCommand extends IntentDigitalHubCommand {
  BridgeCommand(String functionName)
      : super(functionName, IntentDigitalHubModule.BRIDGE);
}
