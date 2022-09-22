package com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterPages

import ActivityUtils.readXmlFileFromProjectAsString
import ActivityUtils.showAlertMessage
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterMenuActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.AvancaPapel
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.Corte
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImpressaoTexto
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImprimeXMLNFCe
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImprimeXMLSAT
import com.elgin.kotlin_intent_digital_hub_smartpos.R


class PrinterTextActivity(): AppCompatActivity() {

    //Campo de mensagem a ser impressa.
    private lateinit var editTextInputMessage: EditText

    //Botões de impressão.
    private lateinit var buttonPrintText: Button

    //Botões de impressão.
    private lateinit var buttonPrinterNFCe: Button

    //Botões de impressão.
    private lateinit var buttonPrinterSAT: Button

    //RadioGroup de alinhamento.
    private lateinit var radioGroupAlign: RadioGroup

    //RadioButton de alinhamento no centralizado.
    private lateinit var buttonRadioCenter: RadioButton

    //Spinner/Dropwdown de seleção de fonte e tamanho da fonte.
    private lateinit var spinnerFontFamily: Spinner

    //Spinner/Dropwdown de seleção de fonte e tamanho da fonte.
    private lateinit var spinnerFontSize: Spinner

    //Chebox de opções negrito/sublinhado e corte de papel. (a opção de corte de papel só é ofericda na impressão por impressora externa, o dispositivo SmartPOS não possuio guilhotina)
    private lateinit var checkBoxIsBold: CheckBox

    //Chebox de opções negrito/sublinhado e corte de papel. (a opção de corte de papel só é ofericda na impressão por impressora externa, o dispositivo SmartPOS não possuio guilhotina)
    private lateinit var checkBoxIsUnderLine: CheckBox

    //Chebox de opções negrito/sublinhado e corte de papel. (a opção de corte de papel só é ofericda na impressão por impressora externa, o dispositivo SmartPOS não possuio guilhotina)
    private lateinit var checkBoxIsCutPaper: CheckBox


    //Opções de alinhamento.
    private enum class Alignment(  //O alinhamento é inserido através de um int para os comandos, portanto a cada opção é atribuída um valor.
        val alignmentValue: Int
    ) {
        LEFT(0), CENTER(1), RIGHT(2);

    }

    //Opções de font
    private enum class FontFamily {
        FONT_A, FONT_B
    }

    //Váriaveis de controle.

    //Váriaveis de controle.
    //Alinhamento escolhido inicialmente.
    private var selectedAlignment = Alignment.CENTER

    //Font Family escolhida inicialmente.
    private var selectedFontFamily = FontFamily.FONT_A

    //Tamanho de fonte escolhida inicialmente;
    private var selectedFontSize = 17

    //Códigos utilizados para filtros dos comandos, necessário para o ínicio de um intent e para que seu resultado possa ser capturado em @onActivityResult.
    private object REQUEST_CODE {
        const val IMPRESSAO_TEXTO = 1
        const val IMPRIME_XML_NFCE = 2
        const val IMPRIME_XML_SAT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer_text)

        //Atribui as views ao iniciar da tela.
        viewsAssignment()

        //Estado inicial da tela.
        initialState()

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment()
    }

    //Atribuição das views.
    private fun viewsAssignment() {
        editTextInputMessage = findViewById(R.id.editTextInputMessage)
        radioGroupAlign = findViewById(R.id.radioGroupAlign)
        buttonRadioCenter = findViewById(R.id.radioButtonCenter)
        spinnerFontFamily = findViewById(R.id.spinnerFontFamily)
        spinnerFontSize = findViewById(R.id.spinnerFontSize)
        checkBoxIsBold = findViewById(R.id.checkBoxBold)
        checkBoxIsUnderLine = findViewById(R.id.checkBoxUnderline)
        checkBoxIsCutPaper = findViewById(R.id.checkBoxCutPaper)
        buttonPrintText = findViewById(R.id.buttonPrintText)
        buttonPrinterNFCe = findViewById(R.id.buttonPrinterNFCe)
        buttonPrinterSAT = findViewById(R.id.buttonPrinterSAT)
    }

    //Aplica algumas configurações iniciais de tela.
    private fun initialState() {
        //O alinhamento escolhido inicialmente é o centralizado.
        buttonRadioCenter!!.isChecked = true

        //O corte de papel só esta disponível em impressões por impressora externa, caso a opção escolhida no menu de impressora não tenha sido impressora externa, a checkbox de corte de papel deve sumir.
        if (PrinterMenuActivity.selectedPrinterConnectionType !== PrinterMenuActivity.PrinterConnectionMethod.EXTERN) checkBoxIsCutPaper!!.visibility =
            View.INVISIBLE

        //Texto inicial ao abrir a tela.
        editTextInputMessage!!.setText("ELGIN DEVELOPERS COMMUNITY")
    }

    //Atribuição das funcionalidades das views.
    private fun viewsFunctionalityAssignment() {
        //Funcionalidade do radioGroup de atualização do tipo de alinhamento.
        radioGroupAlign!!.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonLeft -> selectedAlignment = Alignment.LEFT
                R.id.radioButtonCenter -> selectedAlignment = Alignment.CENTER
                R.id.radioButtonRight -> selectedAlignment = Alignment.RIGHT
            }
        }

        //Funcionalidade do spinner de atualização do tipo de fonte.
        spinnerFontFamily!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>?, v: View, i: Int, lng: Long) {
                selectedFontFamily = if (i == 0) FontFamily.FONT_A else FontFamily.FONT_B

                //A opção FONT_B e BOLD ao mesmo tempo não estão disponíveis no SmartPOS.
                if (selectedFontFamily == FontFamily.FONT_B) {
                    checkBoxIsBold!!.isChecked = false
                    checkBoxIsBold!!.visibility = View.INVISIBLE
                } else checkBoxIsBold!!.visibility = View.VISIBLE
            }
        }

        //Funcionalidade do spinner de atualização do tamanho da fonte.
        spinnerFontSize!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>, v: View, i: Int, lng: Long) {
                selectedFontSize = adapter.getItemAtPosition(i).toString().toInt()
            }
        }

        //Botões de impressão.
        buttonPrintText!!.setOnClickListener { v: View? -> printText() }
        buttonPrinterNFCe!!.setOnClickListener { v: View? -> printXmlNfce() }
        buttonPrinterSAT!!.setOnClickListener { v: View? -> printXmlSat() }
    }

    private fun printText() {
        //Valida se o campo de mensagem não está vazio.
        if (editTextInputMessage!!.text.toString().isEmpty()) {
            showAlertMessage(this, "Alerta", "Campo mensagem vazio!")
        } else {
            val posicao = selectedAlignment.alignmentValue
            val stilo = getStiloValue()
            val impressaoTextoCommand = ImpressaoTexto(
                editTextInputMessage!!.text.toString(),
                posicao,
                stilo,
                selectedFontSize
            )
            val avancaPapelCommand = AvancaPapel(10)
            val termicaCommands: MutableList<IntentDigitalHubCommand> =
                ArrayList<IntentDigitalHubCommand>()
            termicaCommands.add(impressaoTextoCommand)
            termicaCommands.add(avancaPapelCommand)
            if (checkBoxIsCutPaper!!.isChecked) {
                val corteCommand = Corte(0)
                termicaCommands.add(corteCommand)
            }
            IntentDigitalHubCommandStarter.startIDHCommandForResult(
                this,
                termicaCommands,
                REQUEST_CODE.IMPRESSAO_TEXTO
            )
        }
    }

    //Calcula o valor do estilo de acordo com a parametrização definida.
    private fun getStiloValue(): Int {
        var stilo = 0
        if (selectedFontFamily == FontFamily.FONT_B) stilo += 1
        if (checkBoxIsUnderLine!!.isChecked) stilo += 2
        if (checkBoxIsBold!!.isChecked) stilo += 8
        return stilo
    }

    private fun printXmlNfce() {
        //Realiza a leitura do xml no projeto em string.
        val dados: String = readXmlFileFromProjectAsString(this, ActivityUtils.ProjectXml.XML_NFCE)
        val indexcsc = 1
        val csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES"
        val param = 0
        val imprimeXMLNFCeCommand = ImprimeXMLNFCe(dados, indexcsc, csc, param)
        val avancaPapelCommand = AvancaPapel(10)
        val termicaCommands: MutableList<IntentDigitalHubCommand> =
            ArrayList<IntentDigitalHubCommand>()
        termicaCommands.add(imprimeXMLNFCeCommand)
        termicaCommands.add(avancaPapelCommand)
        if (checkBoxIsCutPaper!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommands.add(corteCommand)
        }
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            termicaCommands,
            REQUEST_CODE.IMPRIME_XML_NFCE
        )
    }

    private fun printXmlSat() {
        //Realiza a leitura do xml no projeto em string.
        val dados: String = readXmlFileFromProjectAsString(this, ActivityUtils.ProjectXml.XML_SAT)
        val param = 0
        val imprimeXMLSATCommand = ImprimeXMLSAT(dados, param)
        val avancaPapelCommand = AvancaPapel(10)
        val termicaCommands: MutableList<IntentDigitalHubCommand> =
            ArrayList<IntentDigitalHubCommand>()
        termicaCommands.add(imprimeXMLSATCommand)
        termicaCommands.add(avancaPapelCommand)
        if (checkBoxIsCutPaper!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommands.add(corteCommand)
        }
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            termicaCommands,
            REQUEST_CODE.IMPRIME_XML_SAT
        )
    }
}