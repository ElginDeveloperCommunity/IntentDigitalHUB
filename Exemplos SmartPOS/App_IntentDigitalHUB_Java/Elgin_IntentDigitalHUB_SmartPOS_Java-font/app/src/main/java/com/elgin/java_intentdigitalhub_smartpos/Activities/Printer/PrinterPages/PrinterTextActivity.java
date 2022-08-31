package com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterPages;

import static com.elgin.java_intentdigitalhub_smartpos.Activities.ActivityUtils.ProjectXml.XML_NFCE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.elgin.java_intentdigitalhub_smartpos.Activities.ActivityUtils;
import com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterMenuActivity;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.AvancaPapel;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.Corte;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImpressaoTexto;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImprimeXMLNFCe;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImprimeXMLSAT;
import com.elgin.java_intentdigitalhub_smartpos.R;

import java.util.ArrayList;
import java.util.List;

public class PrinterTextActivity extends AppCompatActivity {

    //Campo de mensagem a ser impressa.
    private EditText editTextInputMessage;

    //Botões de impressão.
    private Button buttonPrintText, buttonPrinterNFCe, buttonPrinterSAT;

    //RadioGroup de alinhamento.
    private RadioGroup radioGroupAlign;

    //RadioButton de alinhamento no centralizado.
    private RadioButton buttonRadioCenter;

    //Spinner/Dropwdown de seleção de fonte e tamanho da fonte.
    private Spinner spinnerFontFamily, spinnerFontSize;

    //Chebox de opções negrito/sublinhado e corte de papel. (a opção de corte de papel só é ofericda na impressão por impressora externa, o dispositivo SmartPOS não possuio guilhotina)
    private CheckBox checkBoxIsBold, checkBoxIsUnderLine, checkBoxIsCutPaper;

    //Opções de alinhamento.
    private enum Alignment {
        LEFT(0), CENTER(1), RIGHT(2);

        //O alinhamento é inserido através de um int para os comandos, portanto a cada opção é atribuída um valor.
        final private int alignmentValue;

        Alignment(int alignmentValue) {
            this.alignmentValue = alignmentValue;
        }
    }

    //Opções de font
    private enum FontFamily {
        FONT_A, FONT_B
    }

    //Váriaveis de controle.

    //Alinhamento escolhido inicialmente.
    private Alignment selectedAlignment = Alignment.CENTER;

    //Font Family escolhida inicialmente.
    private FontFamily selectedFontFamily = FontFamily.FONT_A;

    //Tamanho de fonte escolhida inicialmente;
    private int selectedFontSize = 17;

    //Códigos utilizados para filtros dos comandos, necessário para o ínicio de um intent e para que seu resultado possa ser capturado em @onActivityResult.
    private static class REQUEST_CODE {
        final static int IMPRESSAO_TEXTO = 1;
        final static int IMPRIME_XML_NFCE = 2;
        final static int IMPRIME_XML_SAT = 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_text);

        //Atribui as views ao iniciar da tela.
        viewsAssignment();

        //Estado inicial da tela.
        initialState();

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment();
    }

    //Atribuição das views.
    private void viewsAssignment() {
        editTextInputMessage = findViewById(R.id.editTextInputMessage);

        radioGroupAlign = findViewById(R.id.radioGroupAlign);
        buttonRadioCenter = findViewById(R.id.radioButtonCenter);

        spinnerFontFamily = findViewById(R.id.spinnerFontFamily);
        spinnerFontSize = findViewById(R.id.spinnerFontSize);

        checkBoxIsBold = findViewById(R.id.checkBoxBold);
        checkBoxIsUnderLine = findViewById(R.id.checkBoxUnderline);
        checkBoxIsCutPaper = findViewById(R.id.checkBoxCutPaper);

        buttonPrintText = findViewById(R.id.buttonPrintText);
        buttonPrinterNFCe = findViewById(R.id.buttonPrinterNFCe);
        buttonPrinterSAT = findViewById(R.id.buttonPrinterSAT);
    }

    //Aplica algumas configurações iniciais de tela.
    private void initialState() {
        //O alinhamento escolhido inicialmente é o centralizado.
        buttonRadioCenter.setChecked(true);

        //O corte de papel só esta disponível em impressões por impressora externa, caso a opção escolhida no menu de impressora não tenha sido impressora externa, a checkbox de corte de papel deve sumir.
        if (PrinterMenuActivity.selectedPrinterConnectionType != PrinterMenuActivity.PrinterConnectionMethod.EXTERN)
            checkBoxIsCutPaper.setVisibility(View.INVISIBLE);

        //Texto inicial ao abrir a tela.
        editTextInputMessage.setText("ELGIN DEVELOPERS COMMUNITY");
    }

    //Atribuição das funcionalidades das views.
    private void viewsFunctionalityAssignment() {
        //Funcionalidade do radioGroup de atualização do tipo de alinhamento.
        radioGroupAlign.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonLeft:
                    selectedAlignment = Alignment.LEFT;
                    break;
                case R.id.radioButtonCenter:
                    selectedAlignment = Alignment.CENTER;
                    break;
                case R.id.radioButtonRight:
                    selectedAlignment = Alignment.RIGHT;
                    break;
            }
        });

        //Funcionalidade do spinner de atualização do tipo de fonte.
        spinnerFontFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                selectedFontFamily = (i == 0) ? FontFamily.FONT_A : FontFamily.FONT_B;

                //A opção FONT_B e BOLD ao mesmo tempo não estão disponíveis no SmartPOS.
                if (selectedFontFamily == FontFamily.FONT_B) {
                    checkBoxIsBold.setChecked(false);
                    checkBoxIsBold.setVisibility(View.INVISIBLE);
                } else checkBoxIsBold.setVisibility(View.VISIBLE);
            }
        });

        //Funcionalidade do spinner de atualização do tamanho da fonte.
        spinnerFontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                selectedFontSize = Integer.parseInt(adapter.getItemAtPosition(i).toString());
            }
        });

        //Botões de impressão.

        buttonPrintText.setOnClickListener(v -> printText());
        buttonPrinterNFCe.setOnClickListener(v -> printXmlNfce());
        buttonPrinterSAT.setOnClickListener(v -> printXmlSat());
    }

    private void printText() {
        //Valida se o campo de mensagem não está vazio.
        if (editTextInputMessage.getText().toString().isEmpty()) {
            ActivityUtils.showAlertMessage(this, "Alerta", "Campo mensagem vazio!");
        } else {
            final int posicao = selectedAlignment.alignmentValue;

            final int stilo = getStiloValue();

            ImpressaoTexto impressaoTextoCommand = new ImpressaoTexto(editTextInputMessage.getText().toString(),
                    posicao,
                    stilo,
                    selectedFontSize);

            AvancaPapel avancaPapelCommand = new AvancaPapel(10);

            List<IntentDigitalHubCommand> termicaCommands = new ArrayList<>();

            termicaCommands.add(impressaoTextoCommand);
            termicaCommands.add(avancaPapelCommand);

            if (checkBoxIsCutPaper.isChecked()) {
                Corte corteCommand = new Corte(0);

                termicaCommands.add(corteCommand);
            }

            IntentDigitalHubCommandStarter.startIDHCommandForResult(this, termicaCommands, REQUEST_CODE.IMPRESSAO_TEXTO);
        }
    }

    //Calcula o valor do estilo de acordo com a parametrização definida.
    private int getStiloValue() {
        int stilo = 0;

        if (selectedFontFamily == FontFamily.FONT_B)
            stilo += 1;
        if (checkBoxIsUnderLine.isChecked())
            stilo += 2;
        if (checkBoxIsBold.isChecked())
            stilo += 8;

        return stilo;
    }

    private void printXmlNfce() {
        //Realiza a leitura do xml no projeto em string.
        final String dados = ActivityUtils.readXmlFileFromProjectAsString(this, XML_NFCE);

        final int indexcsc = 1;

        final String csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";

        final int param = 0;

        ImprimeXMLNFCe imprimeXMLNFCeCommand = new ImprimeXMLNFCe(dados, indexcsc, csc, param);

        AvancaPapel avancaPapelCommand = new AvancaPapel(10);

        List<IntentDigitalHubCommand> termicaCommands = new ArrayList<>();

        termicaCommands.add(imprimeXMLNFCeCommand);
        termicaCommands.add(avancaPapelCommand);

        if (checkBoxIsCutPaper.isChecked()) {
            Corte corteCommand = new Corte(0);

            termicaCommands.add(corteCommand);
        }

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, termicaCommands, REQUEST_CODE.IMPRIME_XML_NFCE);
    }

    private void printXmlSat() {
        //Realiza a leitura do xml no projeto em string.
        final String dados = ActivityUtils.readXmlFileFromProjectAsString(this, XML_NFCE);

        final int param = 0;

        ImprimeXMLSAT imprimeXMLSATCommand = new ImprimeXMLSAT(dados, param);

        AvancaPapel avancaPapelCommand = new AvancaPapel(10);

        List<IntentDigitalHubCommand> termicaCommands = new ArrayList<>();

        termicaCommands.add(imprimeXMLSATCommand);
        termicaCommands.add(avancaPapelCommand);

        if (checkBoxIsCutPaper.isChecked()) {
            Corte corteCommand = new Corte(0);

            termicaCommands.add(corteCommand);
        }

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, termicaCommands, REQUEST_CODE.IMPRIME_XML_SAT);
    }
}