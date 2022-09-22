package com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterPages

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterMenuActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.AvancaPapel
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.Corte
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.DefinePosicao
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImpressaoCodigoBarras
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImpressaoQRCode
import com.elgin.kotlin_intent_digital_hub_smartpos.R

class PrinterBarCodeActivity(): AppCompatActivity() {
    //Campo do barCode a ser impresso.
    private lateinit var editTextInputBarCode: EditText

    //Spinner/Dropdowns de seleção de tipos de código de barras, largura da impressão e altura da impressão.
    private lateinit var spinnerBarCodeType: Spinner

    //Spinner/Dropdowns de seleção de tipos de código de barras, largura da impressão e altura da impressão.
    private lateinit var spinnerBarCodeWidth: Spinner

    //Spinner/Dropdowns de seleção de tipos de código de barras, largura da impressão e altura da impressão.
    private lateinit var spinnerBarCodeHeight: Spinner

    //RadioGroup de alinhamento.
    private lateinit var radioGroupAlignBarCode: RadioGroup

    //RadioButton de alinhamento no centralizado.
    private lateinit var buttonRadioAlignCenter: RadioButton

    //Ceckbox de corte de papel. (a opção de corte de papel só é ofericda na impressão por impressora externa, o dispositivo SmartPOS não possuio guilhotina)
    private lateinit var checkBoxIsCutPaperBarCode: CheckBox

    //Texto rótulos na tela, que apresentam as palavras "largura" e "altura". (caso o tipo de código de barras selecionado seja QRCode, esse rótulo deve ser subsítuido pela palavra "square")
    //uma vez que QRCode é código quadrático.
    private lateinit var textViewBarCodeWidth: TextView
    //Texto rótulos na tela, que apresentam as palavras "largura" e "altura". (caso o tipo de código de barras selecionado seja QRCode, esse rótulo deve ser subsítuido pela palavra "square")

    //uma vez que QRCode é código quadrático.
    private lateinit var textViewBarCodeHeight: TextView

    //O rótulo, "estilização" é omitido caso o código não seja QRCODE ou CODE 128, para o SmartPOS.
    private lateinit var textViewEstilizacao: TextView

    //Botão de impressão de código de barras.
    private lateinit var buttonPrinterBarCode: Button

    //Opções de alinhamento.
    private enum class Alignment(  //O alinhamento é inserido através de um int para os comandos, portanto a cada opção é atribuída um valor.
        val alignmentValue: Int
    ) {
        LEFT(0), CENTER(1), RIGHT(2);

    }

    //Valores do código de barra para a impressão de código de barras, de acordo com a documentação
    private enum class BarcodeType(//Código utilizado para a identificação do tipo de código de barras, de acordo com a documentação
        val barcodeTypeValue: Int?, //String utilizada como mensagem-exemplo ao se selecionar um novo tipo de código para a impresão
        var defaultBarcodeMessage: String
    ) {
        EAN_8(3, "40170725"), EAN_13(
            2,
            "0123456789012"
        ),  //O código QR_CODE possui sua função própia, por isto seu valor-código para as funções não é utilizado.
        QR_CODE(null, "ELGIN DEVELOPERS COMMUNITY"), UPC_A(0, "123601057072"), CODE_39(
            4,
            "CODE39"
        ),
        ITF(5, "05012345678900"), CODE_BAR(6, "A3419500A"), CODE_93(7, "CODE93"), CODE_128(
            8,
            "{C1233"
        );

        init {
            defaultBarcodeMessage = defaultBarcodeMessage
        }
    }

    //Variáveis de controle.

    //Variáveis de controle.
    //Tipo de código de barras selecionado inicialmente.
    private var selectedBarcodeType = BarcodeType.EAN_8

    //Alinhamento escolhido inicialmente
    private var selectedAlignment = Alignment.CENTER

    //Largura do código de barras escolhida inicialmente.
    private var selectedWidthOfBarCode = 1

    ///Altura do código de barras escolhida inicialmente.
    private var selectedHeightOfBarCode = 20

    //Int usado para inicio da atividade;
    private val IMPRESSAO_CODIGO_BARRAS_REQUESTCODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer_bar_code)

        //Atribui as views ao iniciar da tela.
        viewsAssignment()

        //Estado inicial da tela.
        initialState()

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment()
    }

    //Atribuição das views.
    private fun viewsAssignment() {
        editTextInputBarCode = findViewById(R.id.editTextInputBarCode)
        textViewBarCodeWidth = findViewById(R.id.textViewBarCodeWidth)
        textViewBarCodeHeight = findViewById(R.id.textViewBarCodeHeight)
        textViewEstilizacao = findViewById(R.id.textViewEstilizacao)
        spinnerBarCodeType = findViewById(R.id.spinnerBarCodeType)
        spinnerBarCodeWidth = findViewById(R.id.spinnerBarCodeWidth)
        spinnerBarCodeHeight = findViewById(R.id.spinnerBarCodeHeight)
        radioGroupAlignBarCode = findViewById(R.id.radioGroupAlignBarCode)
        buttonRadioAlignCenter = findViewById(R.id.radioButtonBarCodeAlignCenter)
        checkBoxIsCutPaperBarCode = findViewById(R.id.checkBoxCutPaperBarCode)
        buttonPrinterBarCode = findViewById(R.id.buttonPrinterBarCode)
    }

    //Aplica algumas configurações iniciais de tela.
    private fun initialState() {
        //O alinhamento escolhido inicialmente é o centralizado.
        buttonRadioAlignCenter!!.isChecked = true

        //O corte de papel só esta disponível em impressões por impressora externa, caso a opção escolhida no menu de impressora não tenha sido impressora externa, a checkbox de corte de papel deve sumir.
        if (PrinterMenuActivity.selectedPrinterConnectionType !== PrinterMenuActivity.PrinterConnectionMethod.EXTERN) checkBoxIsCutPaperBarCode!!.visibility =
            View.INVISIBLE

        //Código de barras exemplo inicial ao abrir a tela.
        editTextInputBarCode!!.setText(BarcodeType.EAN_8.defaultBarcodeMessage)

        //No SmartPOS, apenas nos códigos de barras QRCode e CODE 128 é possível mudar as dimensões da impressão.

        //Como a tela inicia em EAN 8 selecionado, o menu de estilização deverá ser omitido inicialmente.
        textViewEstilizacao!!.visibility = View.INVISIBLE
        textViewBarCodeHeight!!.visibility = View.INVISIBLE
        spinnerBarCodeWidth!!.visibility = View.INVISIBLE
        textViewBarCodeWidth!!.visibility = View.INVISIBLE
        spinnerBarCodeHeight!!.visibility = View.INVISIBLE
    }

    //Atribuição das funcionalidades das views.
    private fun viewsFunctionalityAssignment() {
        //Funcionalidade do spinner/dropdown de atualização do tipo de código de barras selecionado.
        spinnerBarCodeType!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                updateSelectedBarCodeType(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Funcionalidade do radioGroup de atualização do tipo de alinhamento.
        radioGroupAlignBarCode!!.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonBarCodeAlignLeft -> selectedAlignment = Alignment.LEFT
                R.id.radioButtonBarCodeAlignCenter -> selectedAlignment = Alignment.CENTER
                R.id.radioButtonBarCodeAlignRight -> selectedAlignment = Alignment.RIGHT
            }
        }

        //Funcionalidade do spinner/dropdown de atualização da largura de impressão.
        spinnerBarCodeWidth!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedWidthOfBarCode = parent.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Funcionalidade do spinner/dropdown de atualização da altura da impressão.
        spinnerBarCodeHeight!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    selectedHeightOfBarCode = parent.getItemAtPosition(position).toString().toInt()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        //Botão de impressão de código de barras.
        buttonPrinterBarCode!!.setOnClickListener { v: View? -> printBarCode() }
    }

    //Aplica as mundaças relacionadas ao tipo de código de barras selecionado.
    private fun updateSelectedBarCodeType(index: Int) {
        //O spinner e o enum de tipos de código de barras estão declarados na mesma sequência, por isso a atribuição a seguir é possível.
        selectedBarcodeType =
            com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterPages.PrinterBarCodeActivity.BarcodeType.values()
                .get(index)

        //Apenas para os códigos QRCODE e CODE 128 é possível altera as dimensões.
        val shouldLayoutGoVisible =
            selectedBarcodeType == BarcodeType.QR_CODE || selectedBarcodeType == BarcodeType.CODE_128
        textViewEstilizacao!!.visibility =
            if (shouldLayoutGoVisible) View.VISIBLE else View.INVISIBLE
        textViewBarCodeHeight!!.visibility =
            if (shouldLayoutGoVisible) View.VISIBLE else View.INVISIBLE
        spinnerBarCodeWidth!!.visibility =
            if (shouldLayoutGoVisible) View.VISIBLE else View.INVISIBLE
        textViewBarCodeWidth!!.visibility =
            if (shouldLayoutGoVisible) View.VISIBLE else View.INVISIBLE
        spinnerBarCodeHeight!!.visibility =
            if (shouldLayoutGoVisible) View.VISIBLE else View.INVISIBLE

        //Para o QRCODE somente, o nome da estilização deve ser "square" pois as dimensões de largura e altura de um QRCode não diferem.
        if (selectedBarcodeType == BarcodeType.QR_CODE) {
            textViewBarCodeWidth!!.text = "SQUARE"

            //Apenas a largura importa para QRCODE.
            textViewBarCodeHeight!!.visibility = View.INVISIBLE
            spinnerBarCodeHeight!!.visibility = View.INVISIBLE
        } else {
            //Caso não seja QRCode, retorne ao label "largura".
            textViewBarCodeWidth!!.text = "WIDHT"
        }

        //O texto de mensagem a ser transformada em código de barras recebe o padrão para o tipo escolhido
        editTextInputBarCode!!.setText(selectedBarcodeType.defaultBarcodeMessage)
    }

    //Realiza a impressão do código de barras.
    private fun printBarCode() {
        //A lista de comandos da impressão
        val termicaCommandList: MutableList<IntentDigitalHubCommand> =
            ArrayList<IntentDigitalHubCommand>()

        //O comando de alinhamento para os códigos são chamados através de DefinePosicao()
        val posicao = selectedAlignment.alignmentValue
        val definePosicaoCommand = DefinePosicao(posicao)

        //Adiciona o comando de define posição
        termicaCommandList.add(definePosicaoCommand)

        //Para a impressão de QR_CODE existe uma função específica
        if (selectedBarcodeType == BarcodeType.QR_CODE) {
            val dados = editTextInputBarCode!!.text.toString()
            val tamanho = selectedWidthOfBarCode
            val nivelCorrecao = 2
            val impressaoQRCodeCommand = ImpressaoQRCode(
                dados,
                tamanho,
                nivelCorrecao
            )
            termicaCommandList.add(impressaoQRCodeCommand)
        } else {
            val tipo = selectedBarcodeType.barcodeTypeValue
            val dados = editTextInputBarCode!!.text.toString()
            val altura = selectedHeightOfBarCode
            val largura = selectedWidthOfBarCode

            //Não imprimir valor abaixo do código
            val HRI = 4
            val impressaoCodigoBarrasCommand = ImpressaoCodigoBarras(
                tipo!!,
                dados,
                altura,
                largura,
                HRI
            )
            termicaCommandList.add(impressaoCodigoBarrasCommand)
        }
        val avancaPapelCommand = AvancaPapel(10)
        termicaCommandList.add(avancaPapelCommand)
        if (checkBoxIsCutPaperBarCode!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommandList.add(corteCommand)
        }
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            termicaCommandList,
            IMPRESSAO_CODIGO_BARRAS_REQUESTCODE
        )
    }
}