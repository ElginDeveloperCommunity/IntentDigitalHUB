package com.elgin.intent_digital_hub.Printer.Fragments

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter.startHubCommandActivity
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.*
import com.elgin.intent_digital_hub.R
import androidx.fragment.app.Fragment
import com.elgin.intent_digital_hub.ActivityUtils
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.ImpressaoTexto
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.AvancaPapel
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Corte
import com.elgin.intent_digital_hub.Printer.PrinterActivity
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.ImprimeXMLNFCe
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.ImprimeXMLSAT
import java.util.ArrayList

class PrinterTextFragment : Fragment() {
    private val XML_EXTENSION = ".xml"
    private val XML_NFCE_ARCHIVE_NAME = "xmlnfce"
    private val XML_SAT_ARCHIVE_NAME = "xmlsat"
    private lateinit var PrinterActivityReference: Activity
    private lateinit var buttonPrinter: Button
    private lateinit var buttonPrinterXMLNFCe: Button
    private lateinit var buttonPrinterXMLSAT: Button
    private lateinit var radioGroupAlign: RadioGroup
    private lateinit var buttonRadioCenter: RadioButton
    private lateinit var editTextInputMessage: EditText
    private lateinit var spinnerFontFamily: Spinner
    private lateinit var spinnerselectedFontSize: Spinner
    private var checkBoxIsBold: CheckBox? = null
    private var checkBoxIsUnderLine: CheckBox? = null
    private var checkBoxIsCutPaper: CheckBox? = null
    private var selectedAlignment = Alignment.CENTRO
    private var selectedFontFamily = FontFamily.FONT_A
    private var selectedFontSize = 17
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_printer_text, container, false)
        PrinterActivityReference = requireActivity()
        editTextInputMessage = v.findViewById(R.id.editTextInputMessage)
        editTextInputMessage.setText("ELGIN DEVELOPERS COMMUNITY")
        radioGroupAlign = v.findViewById(R.id.radioGroupAlign)
        buttonRadioCenter = v.findViewById(R.id.radioButtonCenter)
        spinnerFontFamily = v.findViewById(R.id.spinnerFontFamily)
        spinnerselectedFontSize = v.findViewById(R.id.spinnerFontSize)
        checkBoxIsBold = v.findViewById(R.id.checkBoxBold)
        checkBoxIsUnderLine = v.findViewById(R.id.checkBoxUnderline)
        checkBoxIsCutPaper = v.findViewById(R.id.checkBoxCutPaper)
        buttonPrinter = v.findViewById(R.id.buttonPrinterText)
        buttonPrinterXMLNFCe = v.findViewById(R.id.buttonPrinterNFCe)
        buttonPrinterXMLSAT = v.findViewById(R.id.buttonPrinterSAT)

        //Funcionalidade Radio Alinhamento
        buttonRadioCenter.setChecked(true)
        radioGroupAlign = v.findViewById(R.id.radioGroupAlign)
        radioGroupAlign.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonLeft -> selectedAlignment = Alignment.ESQUERDA
                R.id.radioButtonCenter -> selectedAlignment = Alignment.CENTRO
                R.id.radioButtonRight -> selectedAlignment = Alignment.DIREITA
            }
        })

        //Funcionalidade do Spinner de seleção de fonte
        spinnerFontFamily.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>?, v: View, i: Int, lng: Long) {
                selectedFontFamily = if (i == 0) FontFamily.FONT_A else FontFamily.FONT_B
            }
        })
        spinnerselectedFontSize.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>, v: View, i: Int, lng: Long) {
                selectedFontSize = adapter.getItemAtPosition(i).toString().toInt()
            }
        })
        buttonPrinter.setOnClickListener(View.OnClickListener { v: View -> buttonPrinterFunction(v) })
        buttonPrinterXMLNFCe.setOnClickListener(View.OnClickListener { v: View ->
            buttonPrinterXMLNFCeFunction(
                v
            )
        })
        buttonPrinterXMLSAT.setOnClickListener(View.OnClickListener { v: View ->
            buttonPrinterXMlSATFunction(
                v
            )
        })
        return v
    }

    private fun buttonPrinterFunction(v: View) {
        if (editTextInputMessage!!.text.toString().isEmpty()) ActivityUtils.showAlertMessage(
            PrinterActivityReference,
            "Alerta",
            "Campo mensagem vazio!"
        ) else {
            val posicao = selectedAlignment.alignmentValue
            val stilo = stiloValue
            val impressaoTextoCommand = ImpressaoTexto(
                editTextInputMessage!!.text.toString(),
                posicao,
                stilo,
                selectedFontSize
            )
            val avancaPapelCommand = AvancaPapel(10)
            val termicaCommands: MutableList<IntentDigitalHubCommand> = ArrayList()
            termicaCommands.add(impressaoTextoCommand)
            termicaCommands.add(avancaPapelCommand)
            if (checkBoxIsCutPaper!!.isChecked) {
                val corteCommand = Corte(0)
                termicaCommands.add(corteCommand)
            }
            startHubCommandActivity(
                PrinterActivityReference!!,
                termicaCommands,
                PrinterActivity.IMPRESSAO_TEXTO_REQUESTCODE
            )
        }
    }

    /**
     * Calcula o valor do estilo de acordo com a parametrização definida
     */
    private val stiloValue: Int
        private get() {
            var stilo = 0
            if (selectedFontFamily == FontFamily.FONT_B) stilo += 1
            if (checkBoxIsUnderLine!!.isChecked) stilo += 2
            if (checkBoxIsBold!!.isChecked) stilo += 8
            return stilo
        }

    private fun buttonPrinterXMLNFCeFunction(v: View) {
        //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciá-lo
        ActivityUtils.loadXMLFileAndStoreItOnApplicationRootDir(
            PrinterActivityReference,
            XML_NFCE_ARCHIVE_NAME
        )
        val dados = ActivityUtils.getFilePathForIDH(
            PrinterActivityReference,
            XML_NFCE_ARCHIVE_NAME + XML_EXTENSION
        )
        val indexcsc = 1
        val csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES"
        val param = 0
        val imprimeXMLNFCeCommand = ImprimeXMLNFCe(dados, indexcsc, csc, param)
        val avancaPapelCommand = AvancaPapel(10)
        val termicaCommands: MutableList<IntentDigitalHubCommand> = ArrayList()
        termicaCommands.add(imprimeXMLNFCeCommand)
        termicaCommands.add(avancaPapelCommand)
        if (checkBoxIsCutPaper!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommands.add(corteCommand)
        }
        startHubCommandActivity(
            PrinterActivityReference!!,
            termicaCommands,
            PrinterActivity.IMPRIME_XML_NFCE_REQUESTCODE
        )
    }

    private fun buttonPrinterXMlSATFunction(v: View) {
        //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
        ActivityUtils.loadXMLFileAndStoreItOnApplicationRootDir(
            PrinterActivityReference,
            XML_SAT_ARCHIVE_NAME
        )
        val dados = ActivityUtils.getFilePathForIDH(
            PrinterActivityReference,
            XML_SAT_ARCHIVE_NAME + XML_EXTENSION
        )
        val param = 0
        val imprimeXMLSATCommand = ImprimeXMLSAT(dados, param)
        val avancaPapelCommand = AvancaPapel(10)
        val termicaCommands: MutableList<IntentDigitalHubCommand> = ArrayList()
        termicaCommands.add(imprimeXMLSATCommand)
        termicaCommands.add(avancaPapelCommand)
        if (checkBoxIsCutPaper!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommands.add(corteCommand)
        }
        startHubCommandActivity(
            PrinterActivityReference!!,
            termicaCommands,
            PrinterActivity.IMPRESSAO_TEXTO_REQUESTCODE
        )
    }

    /**
     * Tipos alinhamento e seus respectivos valores para o comando
     */
    private enum class Alignment(val alignmentValue: Int) {
        ESQUERDA(0), CENTRO(1), DIREITA(2);

    }

    /**
     * Fontes disponíveis para impressão
     */
    private enum class FontFamily {
        FONT_A, FONT_B
    }
}