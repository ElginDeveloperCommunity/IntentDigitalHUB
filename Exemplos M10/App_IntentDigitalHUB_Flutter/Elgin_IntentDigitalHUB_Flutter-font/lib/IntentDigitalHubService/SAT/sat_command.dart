import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_command_starter.dart';
import 'package:intent_digital_hub/IntentDigitalHubService/intent_digital_hub_module.dart';

abstract class SatCommand extends IntentDigitalHubCommand {
  SatCommand(String functionName)
      : super(functionName, IntentDigitalHubModule.SAT);
}
