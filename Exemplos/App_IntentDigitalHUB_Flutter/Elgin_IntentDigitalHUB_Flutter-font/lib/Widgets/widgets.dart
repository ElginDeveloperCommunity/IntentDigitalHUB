import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class GeneralWidgets extends StatelessWidget {
  const GeneralWidgets({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container();
  }

  static Future<void> showAlertDialog(
      {required dynamic mainWidgetContext,
      required String dialogTitle,
      required dialogText}) async {
    return showDialog<void>(
      context: mainWidgetContext,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(dialogTitle),
          content: SingleChildScrollView(
            child: ListBody(
              children: <Widget>[Text(dialogText)],
            ),
          ),
          actions: <Widget>[
            TextButton(
              child: const Text('OK'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  static Future<Widget?> showAlertDialogWithSelectableOptions(
      {required dynamic mainWidgetContext,
      required String alertTitle,
      required List<String> listOfOptions,
      required Future<void> Function(int index) onTap,
      //Váriavel que determina se a seleção de uma opção é forçada
      bool isDismissible = false}) async {
    return showDialog<Widget>(
        context: mainWidgetContext,
        barrierDismissible: isDismissible,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text(alertTitle),
            actions: <Widget>[
              TextButton(
                child: const Text('CANCELAR'),
                onPressed: () {
                  Navigator.of(context).pop();
                },
              ),
            ],
            content: SizedBox(
              width: 300,
              child: Scrollbar(
                child: ListView.builder(
                  shrinkWrap: true,
                  itemCount: (listOfOptions.length),
                  itemBuilder: (BuildContext context, int index) {
                    return ListTile(
                      onTap: () => onTap(index),
                      title: Text(listOfOptions[index]),
                    );
                  },
                ),
              ),
            ),
          );
        });
  }

  static Future<Widget?> showAlertDialogWithInputField({
    required dynamic mainWidgetContext,
    required String dialogTitle,
    required void Function()? Function(String textEntered) onTextInput,
    required Future<void> Function(int action) onPressedAction,
    TextInputType textInputType = TextInputType.text,
    FilteringTextInputFormatter? filteringTextInputFormatter,
  }) async {
    return showDialog(
        context: mainWidgetContext,
        barrierDismissible: false,
        builder: (context) {
          return AlertDialog(
            title: Text(dialogTitle),
            content: TextField(
              autofocus: true,
              enableSuggestions: false,
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
              onChanged: (value) => {onTextInput(value)},
              keyboardType: textInputType,
              inputFormatters: const [],
            ),
            actions: <Widget>[
              TextButton(
                child: const Text('CANCELAR'),
                onPressed: () => onPressedAction(0),
              ),
              TextButton(
                  child: const Text('OK'), onPressed: () => onPressedAction(1)),
            ],
          );
        });
  }

  static Widget headerScreen(String textHeader) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 40),
          child: Text(
            textHeader,
            style: const TextStyle(
              fontSize: 35,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(right: 40),
          child: Image.asset(
            "assets/images/elgin_logo.png",
            width: 200,
          ),
        ),
      ],
    );
  }

  static Widget baseboard() {
    return const Align(
      alignment: Alignment.bottomRight,
      child: Padding(
        padding: EdgeInsets.only(right: 40),
        child: Text(
          "Intent Digital Hub - Flutter 1.0.0",
          style: TextStyle(
              color: Colors.grey, fontWeight: FontWeight.bold, fontSize: 18),
        ),
      ),
    );
  }

  static Widget inputField(
    TextEditingController textFormField,
    String label, {
    double textSize = 15,
    double iWidht = 250,
    TextInputType? textInputType,
    bool isEnable = true,
  }) {
    return SizedBox(
      width: iWidht,
      child: TextFormField(
        enabled: isEnable,
        keyboardType: textInputType,
        textAlign: TextAlign.center,
        decoration: InputDecoration(
          prefixIconConstraints:
              const BoxConstraints(minWidth: 0, minHeight: 0),
          isDense: true,
          prefixIcon: Text(
            label,
            style: TextStyle(fontSize: textSize, fontWeight: FontWeight.bold),
          ),
        ),
        controller: textFormField,
        style: TextStyle(
          fontSize: textSize,
        ),
      ),
    );
  }

  static Widget inputFieldWithFormatter(
    TextEditingController textFormField,
    String label,
    TextInputFormatter textInputFormatter, {
    double textSize = 15,
    double iWidht = 250,
    TextInputType? textInputType,
    bool isEnable = true,
  }) {
    return SizedBox(
      width: iWidht,
      child: TextFormField(
        textAlign: TextAlign.center,
        enabled: isEnable,
        keyboardType: textInputType,
        inputFormatters: [textInputFormatter],
        decoration: InputDecoration(
          prefixIconConstraints:
              const BoxConstraints(minWidth: 0, minHeight: 0),
          isDense: true,
          prefixIcon: Text(
            label,
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: textSize, fontWeight: FontWeight.bold),
          ),
        ),
        controller: textFormField,
        style: TextStyle(
          fontSize: textSize,
        ),
      ),
    );
  }

  static Widget personButton(
      {String textButton = "",
      double width = 170,
      double height = 40,
      VoidCallback? callback,
      bool isButtonEnabled = true}) {
    return SizedBox(
      width: width,
      height: height,
      child: ElevatedButton(
        onPressed: isButtonEnabled ? callback : null,
        child: Text(
          textButton,
          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
        ),
        style: ElevatedButton.styleFrom(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10.0),
          ),
        ),
      ),
    );
  }

  static Widget radioBtn(
      dynamic value, String groupSelected, Function onChanged) {
    return SizedBox(
      height: 50,
      width: 190,
      child: ListTile(
        contentPadding: const EdgeInsets.all(0),
        title: Text(
          value,
          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
        ),
        leading: Radio<String>(
          value: value,
          groupValue: groupSelected,
          onChanged: (dynamic value) => onChanged(value),
        ),
      ),
    );
  }

  static Widget checkBox(String text, bool value, Function onChanged) {
    return SizedBox(
      height: 50,
      width: 200,
      child: ListTile(
        title: Text(
          text,
          style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
        ),
        leading: Checkbox(
          value: value,
          onChanged: (bool? value) => onChanged(value),
        ),
      ),
    );
  }

  static Widget dropDown(
      String label, String text, List<String> elements, Function onChange) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label,
            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
        DropdownButton<String>(
          items: elements.map((String value) {
            return DropdownMenuItem<String>(
              value: value,
              child: Text(value),
            );
          }).toList(),
          hint: Text(text, style: const TextStyle(fontSize: 16)),
          onChanged: (String? value) => onChange(value),
        ),
      ],
    );
  }

  // Constroe o Button personalizado, que pode ou não ser selecionado
  static Widget personSelectedButton({
    String nameButton = "",
    String assetImage = "",
    @required VoidCallback? onSelected,
    double mHeight = 55,
    double iconSize = 25,
    double mWidth = 55,
    double fontLabelSize = 15,
    bool isSelectedBtn = false,
    Color color = Colors.green,
  }) {
    return TextButton(
      onPressed: onSelected,
      style: TextButton.styleFrom(
        padding: EdgeInsets.zero,
      ),
      child: Container(
        margin: const EdgeInsets.all(0),
        height: mHeight,
        width: mWidth,
        decoration: BoxDecoration(
          border: Border.all(
            color: isSelectedBtn ? color : Colors.black,
            width: 3,
          ),
          borderRadius: const BorderRadius.all(Radius.circular(15)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (assetImage != "") Image.asset(assetImage, height: iconSize),
            Text(
              nameButton,
              textAlign: TextAlign.center,
              style: TextStyle(
                  fontSize: fontLabelSize,
                  color: Colors.black,
                  fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }

  static Widget personSelectedButtonStatus({
    String nameButton = "",
    String assetImage = "",
    @required VoidCallback? onSelected,
    double mHeight = 55,
    double iconSize = 25,
    double mWidth = 55,
    double fontLabelSize = 10,
    bool isSelectedBtn = false,
    Color color = Colors.green,
  }) {
    return TextButton(
      onPressed: onSelected,
      style: TextButton.styleFrom(
        padding: EdgeInsets.zero,
      ),
      child: Container(
        height: mHeight,
        width: mWidth,
        decoration: BoxDecoration(
          border: Border.all(
            color: isSelectedBtn ? color : Colors.black,
            width: 3,
          ),
          borderRadius: const BorderRadius.all(Radius.circular(15)),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            const Icon(Icons.info, color: Colors.blue),
            Text(
              nameButton,
              textAlign: TextAlign.center,
              style: TextStyle(
                  fontSize: fontLabelSize,
                  color: Colors.black,
                  fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }

  static Widget formFieldPerson(
    TextEditingController editingController, {
    String label = "",
    double width = 100,
    double height = 50,
    bool isEnable = true,
  }) {
    return SizedBox(
      width: width,
      height: height,
      child: Center(
        child: TextFormField(
          textAlign: TextAlign.center,
          controller: editingController,
          keyboardType: TextInputType.text,
          enabled: isEnable,
          decoration: InputDecoration(
            prefixText: label,
            isDense: true,
            hintStyle: const TextStyle(fontSize: 16),
            border: const OutlineInputBorder(
              borderRadius: BorderRadius.all(
                Radius.circular(10.0),
              ),
            ),
            filled: false,
            contentPadding: const EdgeInsets.all(10),
          ),
        ),
      ),
    );
  }
}
