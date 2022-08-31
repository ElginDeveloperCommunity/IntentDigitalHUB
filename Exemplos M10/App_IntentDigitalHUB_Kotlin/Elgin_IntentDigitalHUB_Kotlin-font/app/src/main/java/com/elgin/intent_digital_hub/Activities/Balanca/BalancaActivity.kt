package com.elgin.intent_digital_hub.Activities.Balanca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.elgin.intent_digital_hub.Activities.ActivityUtils
import com.elgin.intent_digital_hub.IntentDigitalHubService.BALANCA.Commands.*
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter.startHubCommandActivity
import com.elgin.intent_digital_hub.R
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException

class BalancaActivity : AppCompatActivity() {

    private lateinit var buttonConfigurarBalanca: Button;
    private lateinit var buttonLerPeso: Button;

    private lateinit var textReturnValueBalanca: TextView;

    private val CONFIGURAR_BALANCA_REQUESTCODE = 1;
    private val LER_PESO_REQUESTCODE = 2;

    //Group de seleção de modelos de balança
    private lateinit var radioGroupBalanceModels: RadioGroup

    //Spinner de dropdown de seleção dos protocolos de comunicação com a balança
    private lateinit var spinnerProtocols: Spinner

    //Modelo e protocolo selecionas, iniciados com a configuração DP3005 e protocolo 0
    private var selectedBalanceModel = BalanceModel.DP3005
    private var selectedBalanceProtocol = BalanceProtocol.PROTOCOL_0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balanca)

        //Captura as views pelo id
        textReturnValueBalanca = findViewById(R.id.textReturnValueBalanca)
        buttonConfigurarBalanca = findViewById(R.id.buttonConfigurarBalanca)
        buttonLerPeso = findViewById(R.id.buttonLerPeso)
        radioGroupBalanceModels = findViewById<RadioGroup>(R.id.radioGroupBalanceModels)
        spinnerProtocols = findViewById(R.id.spinnerProtocols)

        //Funcionalidade das views
        buttonConfigurarBalanca.setOnClickListener {
            buttonConfigurarBalancaFunction()
        }

        buttonLerPeso.setOnClickListener {
            buttonLerPesoFunction()
        }

        radioGroupBalanceModels.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.radioButtonDP3005 -> selectedBalanceModel = BalanceModel.DP3005
                R.id.radioButtonSA110 -> selectedBalanceModel = BalanceModel.SA110
                R.id.radioButtonDPSC -> selectedBalanceModel = BalanceModel.DPSC
                R.id.radioButtonDP30CK -> selectedBalanceModel = BalanceModel.DP30CK
            }
        }

        //O spinner é montado a partir do Enum de protocolos disponíveis,
        spinnerProtocols.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            BalanceProtocol.values()
        )
        spinnerProtocols.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedBalanceProtocol = BalanceProtocol.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    //Configura o modelo e protocolo selecionada para a balança, deve ser utilizado antes da leitura de peso
    fun buttonConfigurarBalancaFunction(): Unit {
        val configurarModeloBalancaCommand =
            ConfigurarModeloBalanca(selectedBalanceModel.balanceCode)

        val configurarProtocoloComunicacaoCommand =
            ConfigurarProtocoloComunicacao(selectedBalanceProtocol.ordinal)

        val balancaCommandList: MutableList<IntentDigitalHubCommand> = ArrayList()

        balancaCommandList.add(configurarModeloBalancaCommand)
        balancaCommandList.add(configurarProtocoloComunicacaoCommand)

        startHubCommandActivity(
            this,
            balancaCommandList,
            CONFIGURAR_BALANCA_REQUESTCODE
        )

        Log.d(
            "MADARA",
            selectedBalanceModel.balanceCode.toString() + " " + selectedBalanceProtocol.ordinal.toString()
        )
    }

    //Função que realiza uma leitura, é necessário abrir o serial e depois fechar a conexão
    fun buttonLerPesoFunction(): Unit {
        val abrirSerialCommand = AbrirSerial(2400, 8, 'N', 1)

        val lerPesoCommand = LerPeso(1)

        val fecharCommand = Fechar()

        val balancaCommandList: MutableList<IntentDigitalHubCommand> = ArrayList()

        balancaCommandList.add(abrirSerialCommand)
        balancaCommandList.add(lerPesoCommand)
        balancaCommandList.add(fecharCommand)

        startHubCommandActivity(this, balancaCommandList, LER_PESO_REQUESTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val retorno = data!!.getStringExtra("retorno")
            Log.d("retorno", retorno!!)

            /**
             * O retorno das operações Intent Digital Hub será sempre um ArrayJSON; mo módulo balança é mostrado num toast o retorno de todos os comandos realizados em cada botão
             */

            try {
                val jsonArray = JSONArray(retorno)

                when (requestCode) {
                    CONFIGURAR_BALANCA_REQUESTCODE -> {
                        //O retorno do comando de configuração do modelo da balaça é o primeiro índice do ArrayJSON de retorno
                        val configurarModeloBalancaJson = jsonArray.getJSONObject(0)

                        //O retorno do comando de configuração de protocolo é o próximo json
                        val configurarProtocoloComunicacaoJson = jsonArray.getJSONObject(1)


                        //Capturando os resultados através da desserialização do objetos

                        val configurarModeloBalancaReturn = Gson().fromJson(
                            configurarModeloBalancaJson.toString(),
                            ConfigurarModeloBalanca::class.java
                        )

                        var configurarProtocoloComunicacaoReturn = Gson().fromJson(
                            configurarProtocoloComunicacaoJson.toString(),
                            ConfigurarProtocoloComunicacao::class.java
                        )

                        Toast.makeText(
                            this,
                            String.format(
                                "ConfigurarModeloBalanca: %d\nConfigurarProtocoloComunicacao: %d",
                                configurarModeloBalancaReturn.resultado,
                                configurarProtocoloComunicacaoReturn.resultado
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    LER_PESO_REQUESTCODE -> {
                        //O retorno do comando de AbrirSerial é o primeiro índice do ArrayJSON de retorno
                        val abrirSerialJson = jsonArray.getJSONObject(0)

                        //O retorno do comando LerPeso é o próximo índice
                        val lerPesoJson = jsonArray.getJSONObject(1)

                        //O retorno do comando Fechar é o próximo índice
                        val fecharJson = jsonArray.getJSONObject(2)

                        val abrirSerialReturn = Gson().fromJson(
                            abrirSerialJson.toString(),
                            AbrirSerial::class.java
                        )

                        val lerPesoReturn = Gson().fromJson(
                            lerPesoJson.toString(),
                            LerPeso::class.java
                        )

                        val fecharReturn = Gson().fromJson(
                            fecharJson.toString(),
                            Fechar::class.java
                        )

                        Toast.makeText(
                            this,
                            String.format(
                                "AbrirSerial: %d\nLerPeso: %s\nFechar: %d",
                                abrirSerialReturn.resultado,
                                lerPesoReturn.resultado,
                                fecharReturn.resultado
                            ),
                            Toast.LENGTH_SHORT
                        ).show()

                        //Se a função de leitura ocorreu com sucesso, será atualizado o campo VALOR BALANÇA
                        val weightRead = lerPesoReturn.resultado!!.toDouble()

                        if (weightRead > 0.00) {
                            textReturnValueBalanca.text = (weightRead / 1000).toString()
                        }
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
                ActivityUtils.showAlertMessage(
                    this,
                    "Alerta",
                    "O comando $requestCode não foi encontrado!"
                )
            }
        } else {
            ActivityUtils.showAlertMessage(this, "Alerta", "O comando não foi bem sucedido!")
        }
    }

}