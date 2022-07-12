package com.elgin.intent_digital_hub.SAT

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter.startHubCommandActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elgin.intent_digital_hub.R
import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.AtivarSAT
import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.AssociarAssinatura
import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.ConsultarSAT
import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.ConsultarStatusOperacional
import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.EnviarDadosVenda
import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.CancelarUltimaVenda
import com.elgin.intent_digital_hub.IntentDigitalHubService.SAT.ExtrairLogs
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.widget.*
import com.elgin.intent_digital_hub.ActivityUtils
import org.json.JSONArray
import com.google.gson.Gson
import org.json.JSONException
import java.io.Console
import java.util.*

class SATActivity : AppCompatActivity() {
    //Nome do arquivo utilizado para fazer o cancelamento de venda, no drietório res/raw/
    private val XML_CANCELLATION_ARCHIVE_NAME = "sat_cancelamento"
    private val XML_EXTENSION = ".xml"

    //Views
    private lateinit var textRetorno: TextView
    private lateinit var editTextInputCodeAtivacao: EditText
    private lateinit var radioGroupModelsSAT: RadioGroup
    private lateinit var radioButtonSMARTSAT: RadioButton
    private lateinit var buttonConsultarSAT: Button
    private lateinit var buttonConsultarStatusOperacionalSAT: Button
    private lateinit var buttonRealizarVendaSAT: Button
    private lateinit var buttonCancelamentoSAT: Button
    private lateinit var buttonAtivarSAT: Button
    private lateinit var buttonAssociarSAT: Button
    private lateinit var buttonExtrairLogSat: Button

    //Váriavel utilizada para fazer a substituição da tag CFE, necessária para a montagem do xml de cancelamento
    private var cfeCancelamento = ""

    //Modelo de SAT selecionado
    private var selectedSatModel = SatModel.SMART_SAT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_satactivity)
        textRetorno = findViewById(R.id.textRetorno)
        radioGroupModelsSAT = findViewById(R.id.radioGroupModelsSAT)
        radioButtonSMARTSAT = findViewById(R.id.radioButtonSMARTSAT)
        editTextInputCodeAtivacao = findViewById(R.id.editTextInputCodeAtivacao)
        editTextInputCodeAtivacao.setText("123456789")
        buttonConsultarSAT = findViewById(R.id.buttonConsultarSAT)
        buttonConsultarStatusOperacionalSAT = findViewById(R.id.buttonConsultarStatusOperacionalSAT)
        buttonRealizarVendaSAT = findViewById(R.id.buttonRealizarVendaSAT)
        buttonCancelamentoSAT = findViewById(R.id.buttonCancelamentoSAT)
        buttonAtivarSAT = findViewById(R.id.buttonAtivarSAT)
        buttonAssociarSAT = findViewById(R.id.buttonAssociarSAT)
        buttonExtrairLogSat = findViewById(R.id.buttonExtrairLogSat)

        //Modelo do SAT escolhido inicialmente e funcionalidade do radioButton de seleção
        radioButtonSMARTSAT.setChecked(true)
        radioGroupModelsSAT.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            selectedSatModel =
                if (checkedId == R.id.radioButtonSMARTSAT) SatModel.SMART_SAT else SatModel.SAT_GO
        })
        buttonAtivarSAT.setOnClickListener(View.OnClickListener { v: View ->
            buttonAtivarSATFunction(
                v
            )
        })
        buttonAssociarSAT.setOnClickListener(View.OnClickListener { v: View ->
            buttonAssociarSatFunction(
                v
            )
        })
        buttonConsultarSAT.setOnClickListener(View.OnClickListener { v: View ->
            buttonConsultarSATFunction(
                v
            )
        })
        buttonConsultarStatusOperacionalSAT.setOnClickListener(View.OnClickListener { v: View ->
            buttonConsultarStatusOperacionalSATFunction(
                v
            )
        })
        buttonRealizarVendaSAT.setOnClickListener(View.OnClickListener { v: View ->
            buttonRealizarVendaSATFunction(
                v
            )
        })
        buttonCancelamentoSAT.setOnClickListener(View.OnClickListener { v: View ->
            buttonCancelamentoSATFunction(
                v
            )
        })
        buttonExtrairLogSat.setOnClickListener(View.OnClickListener { v: View ->
            buttonExtrairLogSatFunction(
                v
            )
        })
    }

    private fun buttonAtivarSATFunction(v: View) {
        val numSessao = generateNumberForSatSession()
        val subComando = 2
        val codAtivacao = editTextInputCodeAtivacao!!.text.toString()
        val cnpj = "14200166000166"
        val cUF = 15
        val ativarSatCommand = AtivarSAT(
            numSessao,
            subComando,
            codAtivacao,
            cnpj,
            cUF
        )
        startHubCommandActivity(this, ativarSatCommand, ATIVAR_SAT_REQUESTCODE)
    }

    private fun buttonAssociarSatFunction(v: View) {
        val numSessao = generateNumberForSatSession()
        val codAtivacao = editTextInputCodeAtivacao!!.text.toString()
        val cnpjSh = "16716114000172"
        val assinaturaAC = "SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT"
        val associarAssinaturaCommand = AssociarAssinatura(
            numSessao,
            codAtivacao,
            cnpjSh,
            assinaturaAC
        )
        startHubCommandActivity(this, associarAssinaturaCommand, ASSOCIAR_ASSINATURA_REQUESTCODE)
    }

    private fun buttonConsultarSATFunction(v: View) {
        val numSessao = generateNumberForSatSession()
        val consultarSATCommand = ConsultarSAT(numSessao)
        startHubCommandActivity(this, consultarSATCommand, CONSULTAR_SAT_REQUESTCODE)
    }

    private fun buttonConsultarStatusOperacionalSATFunction(v: View) {
        val numSessao = generateNumberForSatSession()
        val codAtivacao = editTextInputCodeAtivacao!!.text.toString()
        val consultarStatusOperacionalCommand = ConsultarStatusOperacional(numSessao, codAtivacao)
        startHubCommandActivity(
            this,
            consultarStatusOperacionalCommand,
            CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE
        )
    }

    private fun buttonRealizarVendaSATFunction(v: View) {
        //Como uma nova venda será realizada, o cfeCancelamento utilizado para cancelamento deve ser sobrescrito
        cfeCancelamento = ""
        val numSessao = generateNumberForSatSession()
        val codAtivacao = editTextInputCodeAtivacao!!.text.toString()

        //O envio de venda SAT será realizo por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
        selectedSatModel.SALE_XML_ARCHIVE_NAME()?.let {
            ActivityUtils.loadXMLFileAndStoreItOnApplicationRootDir(
                this,
                it
            )
        }
        val dadosVenda = ActivityUtils.getFilePathForIDH(
            this,
            selectedSatModel.SALE_XML_ARCHIVE_NAME() + XML_EXTENSION
        )
        val enviarDadosVendaCommand = EnviarDadosVenda(numSessao, codAtivacao, dadosVenda)
        startHubCommandActivity(this, enviarDadosVendaCommand, ENVIAR_DADOS_VENDA_REQUESTCODE)
    }

    private fun buttonCancelamentoSATFunction(v: View) {
        val numSessao = generateNumberForSatSession()
        val codAtivacao = editTextInputCodeAtivacao!!.text.toString()
        val numeroCFe = cfeCancelamento

       Log.d("TESTE", numeroCFe)

        if (cfeCancelamento.isEmpty()) {
            ActivityUtils.showAlertMessage(this, "Alerta", "Não foi feita uma venda para cancelar!")
            return
        }
        val dadosCancelamento = generateXmlForSatCancellation()
        val cancelarUltimaVendaCommand =
            CancelarUltimaVenda(numSessao, codAtivacao, numeroCFe, dadosCancelamento)
        startHubCommandActivity(this, cancelarUltimaVendaCommand, CANCELAR_ULTIMA_VENDA_REQUESTCODE)
    }

    private fun buttonExtrairLogSatFunction(v: View) {
        val numSessao = generateNumberForSatSession()
        val codAtivacao = editTextInputCodeAtivacao!!.text.toString()
        val extrairLogsCommand = ExtrairLogs(numSessao, codAtivacao)
        startHubCommandActivity(this, extrairLogsCommand, EXTRAIR_LOGS_REQUESTCODE)
    }

    //Gera número aleatório para diferenciar as sessões com o dispositivo
    private fun generateNumberForSatSession(): Int {
        return Random().nextInt(1000000)
    }

    /**
     * Utiliza o XML em res/raw/sat_cancelamento como base para gerar um XML de cancelamento de venda SAT
     *
     * @return String já formatada para envio no JSON de comando
     */
    private fun generateXmlForSatCancellation(): String {
        //Lẽ o XMl base usado para cancelamento de venda SAT
        val baseXmlForCacellation =
            ActivityUtils.readXmlFileAsString(this, XML_CANCELLATION_ARCHIVE_NAME)
        //Troca o valor do cfe do XMl base pelo valor do cfeCancelamento mais atual e formata a String com os escapes necessários para o funcionamento
        return baseXmlForCacellation.replace("novoCFe", cfeCancelamento)
            .replace("\"".toRegex(), "\\\\\"")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val retorno = data!!.getStringExtra("retorno")
            Log.d("retorno", retorno!!)
            /**
             * No módulo SAT apenas um comando é executa por vez, portanto o retorno do comando mais recente está sempre na primeira posição do arrayJSON de retorno
             */
            try {
                val jsonArray = JSONArray(retorno)
                val jsonObjectReturn = jsonArray.getJSONObject(0)
                when (requestCode) {
                    ATIVAR_SAT_REQUESTCODE -> {
                        val ativarSATCommand =
                            Gson().fromJson(jsonObjectReturn.toString(), AtivarSAT::class.java)
                        textRetorno!!.text = ativarSATCommand.resultado
                    }
                    ASSOCIAR_ASSINATURA_REQUESTCODE -> {
                        val associarAssinaturaCommand = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            AssociarAssinatura::class.java
                        )
                        textRetorno!!.text = associarAssinaturaCommand.resultado
                    }
                    CONSULTAR_SAT_REQUESTCODE -> {
                        val consultarSATCommand =
                            Gson().fromJson(jsonObjectReturn.toString(), ConsultarSAT::class.java)
                        textRetorno!!.text = consultarSATCommand.resultado
                    }
                    CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE -> {
                        val consultarStatusOperacionalCommand = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            ConsultarStatusOperacional::class.java
                        )
                        textRetorno!!.text = consultarStatusOperacionalCommand.resultado
                    }
                    ENVIAR_DADOS_VENDA_REQUESTCODE -> {
                        val enviarDadosVendaCommand = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            EnviarDadosVenda::class.java
                        )
                        textRetorno!!.text = enviarDadosVendaCommand.resultado

                        //Se a venda ocorreu com sucesso, atualizar o cfe de cancelamento
                        val saleReturn = (enviarDadosVendaCommand.resultado!!.split("|")
                        )
                        if (saleReturn.size > 8) {
                            cfeCancelamento = saleReturn[8]
                        }
                    }
                    CANCELAR_ULTIMA_VENDA_REQUESTCODE -> {
                        val cancelarUltimaVendaCommand = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            CancelarUltimaVenda::class.java
                        )
                        textRetorno!!.text = cancelarUltimaVendaCommand.resultado
                    }
                    EXTRAIR_LOGS_REQUESTCODE -> {
                        val extrairLogsCommand =
                            Gson().fromJson(jsonObjectReturn.toString(), ExtrairLogs::class.java)

                        /*
                        Se o dispositivo não tiver sido encontrado, simplesmente exiba o retorno 'DeviceNotFound' na tela
                        caso contrário, indique que o log foi salvo no caminho
                        */if (extrairLogsCommand.resultado == "DeviceNotFound") textRetorno!!.text =
                            extrairLogsCommand.resultado else {
                            textRetorno!!.text = "Log SAT salvo em " + extrairLogsCommand.resultado
                        }
                    }
                    else -> ActivityUtils.showAlertMessage(
                        this,
                        "Alerta",
                        "Código de comando não encontrado"
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                ActivityUtils.showAlertMessage(
                    this,
                    "Alerta",
                    "O retorno não está no formato esperado!"
                )
            }
        }
    }

    companion object {
        private const val ATIVAR_SAT_REQUESTCODE = 1
        private const val ASSOCIAR_ASSINATURA_REQUESTCODE = 2
        private const val CONSULTAR_SAT_REQUESTCODE = 3
        private const val CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE = 4
        private const val ENVIAR_DADOS_VENDA_REQUESTCODE = 5
        private const val CANCELAR_ULTIMA_VENDA_REQUESTCODE = 6
        private const val EXTRAIR_LOGS_REQUESTCODE = 7
    }
}