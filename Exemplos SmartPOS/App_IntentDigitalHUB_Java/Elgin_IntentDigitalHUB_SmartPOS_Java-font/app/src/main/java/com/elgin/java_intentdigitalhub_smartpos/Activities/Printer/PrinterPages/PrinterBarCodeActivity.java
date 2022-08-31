package com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterPages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterMenuActivity;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.AvancaPapel;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.Corte;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.DefinePosicao;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImpressaoCodigoBarras;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImpressaoQRCode;
import com.elgin.java_intentdigitalhub_smartpos.R;

import java.util.ArrayList;
import java.util.List;

public class PrinterBarCodeActivity extends AppCompatActivity {

    //Campo do barCode a ser impresso.
    private EditText editTextInputBarCode;

    //Spinner/Dropdowns de seleção de tipos de código de barras, largura da impressão e altura da impressão.
    private Spinner spinnerBarCodeType, spinnerBarCodeWidth, spinnerBarCodeHeight;

    //RadioGroup de alinhamento.
    private RadioGroup radioGroupAlignBarCode;

    //RadioButton de alinhamento no centralizado.
    private RadioButton buttonRadioAlignCenter;

    //Ceckbox de corte de papel. (a opção de corte de papel só é ofericda na impressão por impressora externa, o dispositivo SmartPOS não possuio guilhotina)
    private CheckBox checkBoxIsCutPaperBarCode;

    //Texto rótulos na tela, que apresentam as palavras "largura" e "altura". (caso o tipo de código de barras selecionado seja QRCode, esse rótulo deve ser subsítuido pela palavra "square")
    //uma vez que QRCode é código quadrático.
    private TextView textViewBarCodeWidth, textViewBarCodeHeight;

    //O rótulo, "estilização" é omitido caso o código não seja QRCODE ou CODE 128, para o SmartPOS.
    private TextView textViewEstilizacao;

    //Botão de impressão de código de barras.
    private Button buttonPrinterBarCode;

    //Opções de alinhamento.
    private enum Alignment {
        LEFT(0), CENTER(1), RIGHT(2);

        //O alinhamento é inserido através de um int para os comandos, portanto a cada opção é atribuída um valor.
        final private int alignmentValue;

        Alignment(int alignmentValue) {
            this.alignmentValue = alignmentValue;
        }
    }

    //Valores do código de barra para a impressão de código de barras, de acordo com a documentação
    private enum BarcodeType {
        EAN_8(3, "40170725"),
        EAN_13(2, "0123456789012"),
        //O código QR_CODE possui sua função própia, por isto seu valor-código para as funções não é utilizado.
        QR_CODE(null, "ELGIN DEVELOPERS COMMUNITY"),
        UPC_A(0, "123601057072"),
        CODE_39(4, "CODE39"),
        ITF(5, "05012345678900"),
        CODE_BAR(6, "A3419500A"),
        CODE_93(7, "CODE93"),
        CODE_128(8, "{C1233");

        //Código utilizado para a identificação do tipo de código de barras, de acordo com a documentação
        final private Integer barcodeTypeValue;

        //String utilizada como mensagem-exemplo ao se selecionar um novo tipo de código para a impresão
        final private String defaultBarcodeMessage;

        BarcodeType(Integer barcodeTypeValue, String defaultBarcodeMessage) {
            this.barcodeTypeValue = barcodeTypeValue;
            this.defaultBarcodeMessage = defaultBarcodeMessage;
        }

        public Integer getBarcodeTypeValue() {
            return barcodeTypeValue;
        }

        public String getDefaultBarcodeMessage() {
            return defaultBarcodeMessage;
        }
    }

    //Variáveis de controle.

    //Tipo de código de barras selecionado inicialmente.
    private BarcodeType selectedBarcodeType = BarcodeType.EAN_8;

    //Alinhamento escolhido inicialmente
    private Alignment selectedAlignment = Alignment.CENTER;

    //Largura do código de barras escolhida inicialmente.
    private int selectedWidthOfBarCode = 1;

    ///Altura do código de barras escolhida inicialmente.
    private int selectedHeightOfBarCode = 20;

    //Int usado para inicio da atividade;
    private final int IMPRESSAO_CODIGO_BARRAS_REQUESTCODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_bar_code);

        //Atribui as views ao iniciar da tela.
        viewsAssignment();

        //Estado inicial da tela.
        initialState();

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment();
    }

    //Atribuição das views.
    private void viewsAssignment() {
        editTextInputBarCode = findViewById(R.id.editTextInputBarCode);

        textViewBarCodeWidth = findViewById(R.id.textViewBarCodeWidth);
        textViewBarCodeHeight = findViewById(R.id.textViewBarCodeHeight);
        textViewEstilizacao = findViewById(R.id.textViewEstilizacao);

        spinnerBarCodeType = findViewById(R.id.spinnerBarCodeType);
        spinnerBarCodeWidth = findViewById(R.id.spinnerBarCodeWidth);
        spinnerBarCodeHeight = findViewById(R.id.spinnerBarCodeHeight);

        radioGroupAlignBarCode = findViewById(R.id.radioGroupAlignBarCode);

        buttonRadioAlignCenter = findViewById(R.id.radioButtonBarCodeAlignCenter);

        checkBoxIsCutPaperBarCode = findViewById(R.id.checkBoxCutPaperBarCode);

        buttonPrinterBarCode = findViewById(R.id.buttonPrinterBarCode);
    }

    //Aplica algumas configurações iniciais de tela.
    private void initialState() {
        //O alinhamento escolhido inicialmente é o centralizado.
        buttonRadioAlignCenter.setChecked(true);

        //O corte de papel só esta disponível em impressões por impressora externa, caso a opção escolhida no menu de impressora não tenha sido impressora externa, a checkbox de corte de papel deve sumir.
        if (PrinterMenuActivity.selectedPrinterConnectionType != PrinterMenuActivity.PrinterConnectionMethod.EXTERN)
            checkBoxIsCutPaperBarCode.setVisibility(View.INVISIBLE);

        //Código de barras exemplo inicial ao abrir a tela.
        editTextInputBarCode.setText(BarcodeType.EAN_8.defaultBarcodeMessage);

        //No SmartPOS, apenas nos códigos de barras QRCode e CODE 128 é possível mudar as dimensões da impressão.

        //Como a tela inicia em EAN 8 selecionado, o menu de estilização deverá ser omitido inicialmente.
        textViewEstilizacao.setVisibility(View.INVISIBLE);

        textViewBarCodeHeight.setVisibility(View.INVISIBLE);
        spinnerBarCodeWidth.setVisibility(View.INVISIBLE);

        textViewBarCodeWidth.setVisibility(View.INVISIBLE);
        spinnerBarCodeHeight.setVisibility(View.INVISIBLE);
    }

    //Atribuição das funcionalidades das views.
    private void viewsFunctionalityAssignment() {
        //Funcionalidade do spinner/dropdown de atualização do tipo de código de barras selecionado.
        spinnerBarCodeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSelectedBarCodeType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Funcionalidade do radioGroup de atualização do tipo de alinhamento.
        radioGroupAlignBarCode.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonBarCodeAlignLeft:
                    selectedAlignment = Alignment.LEFT;
                    break;
                case R.id.radioButtonBarCodeAlignCenter:
                    selectedAlignment = Alignment.CENTER;
                    break;
                case R.id.radioButtonBarCodeAlignRight:
                    selectedAlignment = Alignment.RIGHT;
                    break;
            }
        });

        //Funcionalidade do spinner/dropdown de atualização da largura de impressão.
        spinnerBarCodeWidth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWidthOfBarCode = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Funcionalidade do spinner/dropdown de atualização da altura da impressão.
        spinnerBarCodeHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHeightOfBarCode = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Botão de impressão de código de barras.
        buttonPrinterBarCode.setOnClickListener(v -> printBarCode());
    }

    //Aplica as mundaças relacionadas ao tipo de código de barras selecionado.
    private void updateSelectedBarCodeType(int index) {
        //O spinner e o enum de tipos de código de barras estão declarados na mesma sequência, por isso a atribuição a seguir é possível.
        selectedBarcodeType = BarcodeType.values()[index];

        //Apenas para os códigos QRCODE e CODE 128 é possível altera as dimensões.
        boolean shouldLayoutGoVisible = ((selectedBarcodeType == BarcodeType.QR_CODE) || (selectedBarcodeType == BarcodeType.CODE_128));

        textViewEstilizacao.setVisibility(shouldLayoutGoVisible ? View.VISIBLE : View.INVISIBLE);

        textViewBarCodeHeight.setVisibility(shouldLayoutGoVisible ? View.VISIBLE : View.INVISIBLE);
        spinnerBarCodeWidth.setVisibility(shouldLayoutGoVisible ? View.VISIBLE : View.INVISIBLE);

        textViewBarCodeWidth.setVisibility(shouldLayoutGoVisible ? View.VISIBLE : View.INVISIBLE);
        spinnerBarCodeHeight.setVisibility(shouldLayoutGoVisible ? View.VISIBLE : View.INVISIBLE);

        //Para o QRCODE somente, o nome da estilização deve ser "square" pois as dimensões de largura e altura de um QRCode não diferem.
        if (selectedBarcodeType == BarcodeType.QR_CODE) {
            textViewBarCodeWidth.setText("SQUARE");

            //Apenas a largura importa para QRCODE.
            textViewBarCodeHeight.setVisibility(View.INVISIBLE);
            spinnerBarCodeHeight.setVisibility(View.INVISIBLE);
        } else {
            //Caso não seja QRCode, retorne ao label "largura".
            textViewBarCodeWidth.setText("WIDHT");
        }

        //O texto de mensagem a ser transformada em código de barras recebe o padrão para o tipo escolhido
        editTextInputBarCode.setText(selectedBarcodeType.getDefaultBarcodeMessage());
    }

    //Realiza a impressão do código de barras.
    private void printBarCode() {
        //A lista de comandos da impressão
        List<IntentDigitalHubCommand> termicaCommandList = new ArrayList<>();

        //O comando de alinhamento para os códigos são chamados através de DefinePosicao()
        final int posicao = selectedAlignment.alignmentValue;

        DefinePosicao definePosicaoCommand = new DefinePosicao(posicao);

        //Adiciona o comando de define posição
        termicaCommandList.add(definePosicaoCommand);

        //Para a impressão de QR_CODE existe uma função específica
        if (selectedBarcodeType == BarcodeType.QR_CODE) {
            final String dados = editTextInputBarCode.getText().toString();

            final int tamanho = selectedWidthOfBarCode;

            final int nivelCorrecao = 2;

            ImpressaoQRCode impressaoQRCodeCommand = new ImpressaoQRCode(dados,
                    tamanho,
                    nivelCorrecao);

            termicaCommandList.add(impressaoQRCodeCommand);
        } else {
            final int tipo = selectedBarcodeType.getBarcodeTypeValue();

            final String dados = editTextInputBarCode.getText().toString();

            final int altura = selectedHeightOfBarCode;

            final int largura = selectedWidthOfBarCode;

            //Não imprimir valor abaixo do código
            final int HRI = 4;

            ImpressaoCodigoBarras impressaoCodigoBarrasCommand = new ImpressaoCodigoBarras(tipo,
                    dados,
                    altura,
                    largura,
                    HRI);

            termicaCommandList.add(impressaoCodigoBarrasCommand);
        }

        AvancaPapel avancaPapelCommand = new AvancaPapel(10);

        termicaCommandList.add(avancaPapelCommand);

        if (checkBoxIsCutPaperBarCode.isChecked()) {
            Corte corteCommand = new Corte(0);

            termicaCommandList.add(corteCommand);
        }

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, termicaCommandList, IMPRESSAO_CODIGO_BARRAS_REQUESTCODE);
    }
}