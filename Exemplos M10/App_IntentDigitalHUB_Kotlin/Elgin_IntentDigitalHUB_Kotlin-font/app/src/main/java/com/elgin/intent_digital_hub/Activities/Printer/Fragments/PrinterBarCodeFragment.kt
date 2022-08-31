package com.elgin.intent_digital_hub.Activities.Printer.Fragments

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter.startHubCommandActivity
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.*
import com.elgin.intent_digital_hub.R
import androidx.fragment.app.Fragment
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands.DefinePosicao
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands.ImpressaoQRCode
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands.ImpressaoCodigoBarras
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands.AvancaPapel
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Commands.Corte
import com.elgin.intent_digital_hub.Activities.Printer.PrinterActivity
import java.util.ArrayList

class PrinterBarCodeFragment : Fragment() {
    private lateinit var PrinterActivityReference: Activity
    private lateinit var editTextInputBarCode: EditText
    private lateinit var spinnerBarCodeType: Spinner
    private lateinit var spinnerBarCodeWidth: Spinner
    private lateinit var spinnerBarCodeHeight: Spinner
    private lateinit var radioGroupAlignBarCode: RadioGroup
    private lateinit var buttonRadioAlignCenter: RadioButton
    private lateinit var checkBoxIsCutPaperBarCode: CheckBox
    private lateinit var textViewWidth: TextView
    private lateinit var textViewHeight: TextView
    private lateinit var buttonPrinterBarCode: Button
    private var selectedBarcodeType = BarcodeType.EAN_8
    private var selectedAlignment = Alignment.CENTRO
    private var widthOfBarCode = 1
    private var heightOfBarCode = 20
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_printer_bar_code, container, false)
        PrinterActivityReference = requireActivity()
        editTextInputBarCode = v.findViewById(R.id.editTextInputBarCode)
        editTextInputBarCode.setText("40170725")
        textViewWidth = v.findViewById(R.id.textViewWidth)
        textViewHeight = v.findViewById(R.id.textViewHeight)
        spinnerBarCodeType = v.findViewById(R.id.spinnerBarCodeType)
        buttonRadioAlignCenter = v.findViewById(R.id.radioButtonBarCodeAlignCenter)
        spinnerBarCodeWidth = v.findViewById(R.id.spinnerBarCodeWidth)
        spinnerBarCodeHeight = v.findViewById(R.id.spinnerBarCodeHeight)
        checkBoxIsCutPaperBarCode = v.findViewById(R.id.checkBoxCutPaper)
        buttonPrinterBarCode = v.findViewById(R.id.buttonPrintBarCode)
        spinnerBarCodeType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>?, v: View, i: Int, lng: Long) {
                setSelectedBarcodeType(i)
            }
        })

        //Funcionalidade Radio Alinhamento
        buttonRadioAlignCenter.setChecked(true)
        radioGroupAlignBarCode = v.findViewById(R.id.radioGroupAlignBarCode)
        radioGroupAlignBarCode.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonBarCodeAlignLeft -> selectedAlignment = Alignment.ESQUERDA
                R.id.radioButtonBarCodeAlignCenter -> selectedAlignment = Alignment.CENTRO
                R.id.radioButtonBarCodeAlignRight -> selectedAlignment = Alignment.DIREITA
            }
        })
        spinnerBarCodeWidth.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>, v: View, i: Int, lng: Long) {
                widthOfBarCode = adapter.getItemAtPosition(i).toString().toInt()
            }
        })
        spinnerBarCodeHeight.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>, v: View, i: Int, lng: Long) {
                heightOfBarCode = adapter.getItemAtPosition(i).toString().toInt()
            }
        })
        buttonPrinterBarCode.setOnClickListener(View.OnClickListener { v: View ->
            buttonPrinterBarCodeFunction(
                v
            )
        })
        return v
    }

    //Aplica o tipo de código de barras selecionado
    private fun setSelectedBarcodeType(selectedIndex: Int) {
        //Se o tipo de código escolhido não fo QR_CODE, é possível selecionar widht e height separadamente, caso contrário apenas a opção SQUARE deve ser disponibilizada
        textViewWidth!!.text = "WIDTH"
        textViewHeight!!.visibility = View.VISIBLE
        spinnerBarCodeHeight!!.visibility = View.VISIBLE
        if (selectedIndex == 2) {
            textViewWidth!!.text = "SQUARE"
            textViewHeight!!.visibility = View.INVISIBLE
            spinnerBarCodeHeight!!.visibility = View.INVISIBLE
        }

        //O enumerator está na mesma ordem que o índice do spinner, portanto pode-se atribuir diretamente:
        selectedBarcodeType = BarcodeType.values()[selectedIndex]

        //O texto de mensagem a ser transformada em código de barras recebe o padrão para o tipo escolhido
        editTextInputBarCode!!.setText(selectedBarcodeType.defaultBarcodeMessage)
    }

    private fun buttonPrinterBarCodeFunction(v: View) {
        //A lista de comandos da impressão
        val termicaCommandList: MutableList<IntentDigitalHubCommand> = ArrayList()

        //O comando de alinhamento para os códigos são chamados através de DefinePosicao()
        val posicao = selectedAlignment.alignmentValue
        val definePosicaoCommand = DefinePosicao(posicao)

        //Adiciona o comando de define posição
        termicaCommandList.add(definePosicaoCommand)

        //Para a impressão de QR_CODE existe uma função específica
        if (selectedBarcodeType == BarcodeType.QR_CODE) {
            val dados = editTextInputBarCode!!.text.toString()
            val tamanho = widthOfBarCode
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
            val altura = heightOfBarCode
            val largura = widthOfBarCode

            //Não imprimir valor abaixo do código
            val HRI = 4
            val impressaoCodigoBarrasCommand = tipo?.let {
                ImpressaoCodigoBarras(
                    it,
                    dados,
                    altura,
                    largura,
                    HRI
                )
            }
            if (impressaoCodigoBarrasCommand != null) {
                termicaCommandList.add(impressaoCodigoBarrasCommand)
            }
        }
        val avancaPapelCommand = AvancaPapel(10)
        termicaCommandList.add(avancaPapelCommand)
        if (checkBoxIsCutPaperBarCode!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommandList.add(corteCommand)
        }
        startHubCommandActivity(
            PrinterActivityReference!!,
            termicaCommandList,
            PrinterActivity.IMPRESSAO_CODIGO_BARRAS_REQUESTCODE
        )
    }

    /**
     * Valores do código de barra para a impressão de código de barras, de acordo com a documentação
     */
    private enum class BarcodeType(//Código utilizado para a identificação do tipo de código de barras, de acordo com a documentação
        val barcodeTypeValue: Int?, //String utilizada como mensagem-exemplo ao se selecionar um novo tipo de código para a impresão
        val defaultBarcodeMessage: String
    ) {
        EAN_8(3, "40170725"), EAN_13(
            2,
            "0123456789012"
        ),  //O código QR_CODE possui sua função própia, por isto seu valor-código para as funções não é utilizado
        QR_CODE(null, "ELGIN DEVELOPERS COMMUNITY"), UPC_A(0, "123601057072"), CODE_39(
            4,
            "CODE39"
        ),
        ITF(5, "05012345678900"), CODE_BAR(6, "A3419500A"), CODE_93(7, "CODE93"), CODE_128(
            8,
            "{C1233"
        );

    }

    //Valores de alinhamento para a impressão de código de barras
    private enum class Alignment(val alignmentValue: Int) {
        ESQUERDA(0), CENTRO(1), DIREITA(2);

    }
}