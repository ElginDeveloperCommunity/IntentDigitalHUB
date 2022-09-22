import 'package:flutter_smartpos/IntentDigitalHubService/intent_digital_hub_module.dart';
import '../intent_digital_hub_command_starter.dart';

abstract class TermicaCommand extends IntentDigitalHubCommand {
  TermicaCommand(String functionName)
      : super(functionName, IntentDigitalHubModule.TERMICA);
}
