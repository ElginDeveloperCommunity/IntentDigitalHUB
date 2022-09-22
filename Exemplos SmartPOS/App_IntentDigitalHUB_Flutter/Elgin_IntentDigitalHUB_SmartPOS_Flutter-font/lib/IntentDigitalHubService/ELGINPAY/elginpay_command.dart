import 'package:flutter_smartpos/IntentDigitalHubService/intent_digital_hub_module.dart';
import '../intent_digital_hub_command_starter.dart';

abstract class ElginPayCommand extends IntentDigitalHubCommand {
  ElginPayCommand(String functionName)
      : super(functionName, IntentDigitalHubModule.ELGINPAY);
}
