package com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer

import ActivityUtils.showAlertMessage
import ActivityUtils.startNewActivity
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterPages.PrinterBarCodeActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterPages.PrinterImageActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterPages.PrinterTextActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.AbreConexaoImpressora
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.FechaConexaoImpressora
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.StatusImpressora
import com.elgin.kotlin_intent_digital_hub_smartpos.R
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.util.regex.Pattern


class PrinterMenuActivity(): AppCompatActivity(){

    //RadioGroup de seleção de conexão de impressora interna OU externa.
    private lateinit var radioGroupPrinterConnection: RadioGroup

    //RadioButton referente à conexão de impressora interna.
    private lateinit var radioButtonConnectPrinterIntern: RadioButton

    //Campo de IP.
    private lateinit var editTextInputIP: EditText

    //Botões.
    private lateinit var buttonPrinterText: Button
    private lateinit var buttonPrinterBarCode: Button
    private lateinit var buttonPrinterImage: Button
    private lateinit var buttonStatusPrinter: Button

    //Método de conexão de impressora, externa e interna.
    enum class PrinterConnectionMethod {
        INTERN, EXTERN
    }

    //Modelos de impressora externa disponíveis
    private enum class ExternalPrinterModel {
        i8, i9
    }

    //Método de conexão com impressora selecionado inicialmente.
     companion object{
        var selectedPrinterConnectionType = PrinterConnectionMethod.INTERN
     }

    //Códigos utilizados para filtros dos comandos, necessário para o ínicio de um intent e para que seu resultado possa ser capturado em @onActivityResult.
    private object REQUEST_CODE {
        const val ABRE_CONEXAO_IMPRESSORA = 1
        const val FECHA_CONEXAO_IMPRESSORA = 2
        const val STATUS_IMPRESSORA = 3
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer_menu)

        //Atribui as views ao iniciar da tela.
        viewsAssignment()

        //Ao iniciar da tela, a conexão com a impressora interna deve ser estabelecida.
        connectPrinterIntern()

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment()

        //Um exemplo de IP já estará presente no campo de IP.
        editTextInputIP!!.setText("192.168.0.100:9100")
    }

    //Ao sair da página, a impressora deve ser corretamente desligada.
    protected override fun onDestroy() {
        super.onDestroy()
        val fechaConexaoImpressoraCommand = FechaConexaoImpressora()
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            fechaConexaoImpressoraCommand,
            REQUEST_CODE.FECHA_CONEXAO_IMPRESSORA
        )
    }

    //Atribuição das views.
    private fun viewsAssignment() {
        radioGroupPrinterConnection = findViewById<RadioGroup>(R.id.radioGroupPrinterConnection)
        radioButtonConnectPrinterIntern = findViewById<RadioButton>(R.id.radioButtonConnectPrinterIntern)
        editTextInputIP = findViewById<EditText>(R.id.editTextInputIP)
        buttonPrinterText = findViewById<Button>(R.id.buttonPrinterText)
        buttonPrinterBarCode = findViewById<Button>(R.id.buttonPrinterBarCode)
        buttonPrinterImage = findViewById<Button>(R.id.buttonPrinterImage)
        buttonStatusPrinter = findViewById<Button>(R.id.buttonStatusPrinter)
    }

    //Atribuição das funcionalidades das views.
    private fun viewsFunctionalityAssignment() {
        radioGroupPrinterConnection!!.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonConnectPrinterIntern ->                     //Se a impressora selecionada já for a interna, não é necessário conectar com a impressora interna novamente, isso evita recursão.
                    if (selectedPrinterConnectionType != PrinterConnectionMethod.INTERN) connectPrinterIntern()
                R.id.radioButtonConnectPrinterExtern ->                     //Caso o botão de impressora externa seja selecionado, é necessário validar o ip.
                    if (isIpValid(editTextInputIP!!.text.toString())) {
                        //Invoca o dialog que permitirá a escolha do modelo de impressora externa a ser conectado, e posteirormente tentará a conexão.
                        invokeDialogForPrinterModelSelection()
                    } else {
                        showAlertMessage(
                            this,
                            "Alerta",
                            "O IP inserido não é valido!"
                        )
                        connectPrinterIntern()
                    }
            }
        }
        buttonPrinterText!!.setOnClickListener { v: View? ->
            startNewActivity(
                this,
                PrinterTextActivity::class.java
            )
        }
        buttonPrinterBarCode!!.setOnClickListener { v: View? ->
            startNewActivity(
                this,
                PrinterBarCodeActivity::class.java
            )
        }
        buttonPrinterImage!!.setOnClickListener { v: View? ->
            startNewActivity(
                this,
                PrinterImageActivity::class.java
            )
        }
        buttonStatusPrinter!!.setOnClickListener { v: View? -> checkPrinterStatus() }
    }

    //Inicia a conexão com a impressora interna.
    private fun connectPrinterIntern() {
        //Atualiza a váriavel de controle.
        selectedPrinterConnectionType = PrinterConnectionMethod.INTERN
        //Atualiza o radioButton de seleção.
        radioButtonConnectPrinterIntern!!.isChecked = true
        val abreConexaImpressoraCommand = AbreConexaoImpressora(5, "SMARTPOS", "", 0)
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            abreConexaImpressoraCommand,
            REQUEST_CODE.ABRE_CONEXAO_IMPRESSORA
        )
    }

    //Invoca um dialog que permite a opção de escolha entre os modelos de impressora, assim que um modelo for escolhido a conexão com aquele modelo tentará ser estabelecida
    //caso não obtenha sucesso, a conexão com a impressora interna será retorna e uma alerta será joga na tela.
    private fun invokeDialogForPrinterModelSelection() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecione o modelo de impressora a ser conectado")

        //Tornando o dialógo não-cancelável
        builder.setCancelable(false)
        builder.setNegativeButton("CANCELAR") { dialog: DialogInterface, which: Int ->
            //Se a opção de cancelamento tiver sido escolhida, retorne sempre à opção de impressão por impressora interna
            connectPrinterIntern()
            dialog.dismiss()
        }

        //Cria o vetor de modelos de impressora disponíveis.
        val externalPrinterModels =
            arrayOf(ExternalPrinterModel.i8.toString(), ExternalPrinterModel.i9.toString())
        builder.setItems(
            externalPrinterModels
        ) { dialog: DialogInterface?, which: Int ->
            //A opção 1 no array se refere a impressora i8, consequentemente a opção 2 se refere a impressora i9.
            if (which == 1) connectPrinterExtern(ExternalPrinterModel.i8) else connectPrinterExtern(
                ExternalPrinterModel.i9
            )
        }
        builder.show()
    }

    //Tenta iniciar conexão com impressora externa, é necessário prover um modelo.
    private fun connectPrinterExtern(externalPrinterModelSelected: ExternalPrinterModel) {
        //Atualiza variável de controle.
        selectedPrinterConnectionType = PrinterConnectionMethod.EXTERN

        //O ip deve ser enviado separadamente com a porta.
        val ipAndPort = editTextInputIP!!.text.toString().split(":").toTypedArray()

        //O ip deve ser passado como string.
        val ip = ipAndPort[0]
        //A porta deve ser passada como inteiro.
        val port = ipAndPort[1].toInt()
        val abreConexaImpressoraCommand = AbreConexaoImpressora(3, externalPrinterModelSelected.toString(), ip, port)
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            abreConexaImpressoraCommand,
            REQUEST_CODE.ABRE_CONEXAO_IMPRESSORA
        )
    }

    //Inicia o comando que verifica o estado da impressora e seu papel, em @onActivityResult, onde é manejado o retorno do comando, será jogado na tela o resultado.
    private fun checkPrinterStatus() {
        //A mesma função é utilizada para diversas checagem de status, para o papel da impressora o param a ser enviado é 3;
        val param = 3
        val statusImpressoraCommand = StatusImpressora(param)
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            statusImpressoraCommand,
            REQUEST_CODE.STATUS_IMPRESSORA
        )
    }

    //Capturado o resultado dos comandos.

    //Capturado o resultado dos comandos.
    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //O retorno dos comandos no IDH, está sempre sob a chave "retorno", no extra da intent de retorno.
            val retorno = data!!.getStringExtra("retorno")
            try {
                //Os retornos dos comando do Intent Digital Hub, estão sempre em um Array de Json, neste módulo, apenas alguns retornos terão seus resultados manipulados
                //e estes, se referem apenas a comandos únicos enviados, portanto, o retorno será capturado do único json que o array de retorno terá.
                val jsonArray = JSONArray(retorno)
                val jsonObjectReturn = jsonArray.getJSONObject(0)
                when (requestCode) {
                    REQUEST_CODE.ABRE_CONEXAO_IMPRESSORA -> {
                        Log.d("LOG", jsonObjectReturn.toString())
                        //Se o comando tentou iniciar a conexão com impressora externa, e não obteve sucesso (resultado != 0).
                        if (selectedPrinterConnectionType == PrinterConnectionMethod.EXTERN) {
                            val abreConexaImpressoraReturn: AbreConexaoImpressora = Gson().fromJson(
                                jsonObjectReturn.toString(),
                                AbreConexaoImpressora::class.java
                            )
                            if (abreConexaImpressoraReturn.resultado !== 0) {
                                showAlertMessage(
                                    this,
                                    "Alerta",
                                    "Não foi possível conectar a impressora externa!"
                                )
                                //Volta a conexão com a impressora interna.
                                connectPrinterIntern()
                            }
                        }
                    }
                    REQUEST_CODE.STATUS_IMPRESSORA -> {
                        //Lida com o status retornado pelo comando.
                        val statusImpressoraReturn: StatusImpressora = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            StatusImpressora::class.java
                        )
                        var statusMessage = ""
                        statusMessage = when (statusImpressoraReturn.resultado) {
                            5 -> "Papel está presente e não está próximo do fim!"
                            6 -> "Papel está próximo do fim!"
                            7 -> "Papel ausente!"
                            else -> "Status desconhecido!"
                        }
                        showAlertMessage(this, "Alerta", statusMessage)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    //Validações

    //Validações
    //Validação de IP
    private fun isIpValid(ip: String): Boolean {
        val pattern =
            Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$")
        val matcher = pattern.matcher(ip)
        return matcher.matches()
    }
}