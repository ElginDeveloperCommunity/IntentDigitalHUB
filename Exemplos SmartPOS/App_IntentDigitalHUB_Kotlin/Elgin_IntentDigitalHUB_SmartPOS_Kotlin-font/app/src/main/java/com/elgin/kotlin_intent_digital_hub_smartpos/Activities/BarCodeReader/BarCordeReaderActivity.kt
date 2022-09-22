package com.elgin.kotlin_intent_digital_hub_smartpos.Activities.BarCodeReader

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubModule
import com.elgin.kotlin_intent_digital_hub_smartpos.R
import org.json.JSONArray
import org.json.JSONException


class BarCodeReaderActivity (): AppCompatActivity() {

    private lateinit var editTextBarCode: EditText
    private lateinit var editTextBarCodeType: EditText
    private lateinit var buttonInitializeReading: Button
    private lateinit var buttonClearField: Button

    //Campos onde o resultado da leitura será exposto.

    private val SCANNER_REQUEST_CODE = 1234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_reader)
        editTextBarCode = findViewById(R.id.editTextCodeBar)
        editTextBarCodeType = findViewById(R.id.editTextCodeBarType)

        //Os campos não devem receber input por parte do usuário.
        editTextBarCode.setFocusable(false)
        editTextBarCodeType.setFocusable(false)
        buttonInitializeReading = findViewById(R.id.buttonInitializeReading)
        buttonClearField = findViewById(R.id.buttonClearField)
        buttonInitializeReading.setOnClickListener { v: View? -> startScannerReading() }
        buttonClearField.setOnClickListener { v: View? ->
            editTextBarCode.setText("")
            editTextBarCodeType.setText("")
        }
    }

    private fun startScannerReading() {
        //Cria o Intent para o scanner com o módulo filtro do SCANNER.
        val scannerIntent = Intent(IntentDigitalHubModule.SCANNER.intentPath)

        //Inicia o scanner.
        this.startActivityForResult(scannerIntent, 1234)
    }

    //Captura o resultado do scanner.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCANNER_REQUEST_CODE) {
            if (resultCode == 0) {
                //O resultado, assim como todos os comandos do IDH, é um array de json (como string) sob a chave "retorno" no extra da intent, que neste caso possuí um único elemento.
                //Exemplo : Exemplo de retorno: [{"funcao":"getScanner","mensagem":"Método executado","resultado":["1","7909189189069","7909189189069","EAN13"]}]
                val result = data!!.getStringExtra("retorno")
                try {
                    //Capturando o resultado dentro de objeto JSONArray, para capturar o seu único elemento, que possuí o resultado da leitura.
                    val jsonArrayReturn = JSONArray(result)

                    //Capturando o json de retorno.
                    val jsonObjectReturn = jsonArrayReturn.getJSONObject(0)

                    //O resultado do código de barras e o tipo de código de barras estão presente em um Array de String.
                    val barCodeReturns = jsonObjectReturn.getJSONArray("resultado")

                    //Atualiza na tela o código de barras capturado.
                    editTextBarCode!!.setText(barCodeReturns.getString(1))

                    //Atualiza na tela o tipo de código de barras capturado.
                    editTextBarCodeType!!.setText(barCodeReturns.getString(3))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
}