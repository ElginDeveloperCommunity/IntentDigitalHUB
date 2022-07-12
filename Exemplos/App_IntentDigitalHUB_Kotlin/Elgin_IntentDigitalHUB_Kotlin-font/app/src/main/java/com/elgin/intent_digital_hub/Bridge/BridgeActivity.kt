package com.elgin.intent_digital_hub.Bridge

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.CheckBox
import android.os.Bundle
import com.elgin.intent_digital_hub.R
import com.elgin.intent_digital_hub.InputMasks.InputMaskMoney
import androidx.appcompat.content.res.AppCompatResources
import android.content.Intent
import android.app.AlertDialog
import org.json.JSONArray
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.IniciaVendaCredito
import com.google.gson.Gson
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.IniciaVendaDebito
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.IniciaCancelamentoVenda
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.IniciaOperacaoAdministrativa
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.ImprimirCupomNfce
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.ImprimirCupomSat
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.ImprimirCupomSatCancelamento
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.ConsultarStatus
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.GetTimeout
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.ConsultarUltimaTransacao
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.SetSenhaServer
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.SetTimeout
import org.json.JSONException
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.BridgeCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.SetServer
import com.elgin.intent_digital_hub.IntentDigitalHubService.BRIDGE.SetSenha
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter
import android.graphics.Typeface
import android.content.DialogInterface
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.elgin.intent_digital_hub.ActivityUtils
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class BridgeActivity : AppCompatActivity() {
    private val XML_EXTENSION = ".xml"
    private val XML_NFCE_ARCHIVE_NAME = "xmlnfce"
    private val XML_SAT_ARCHIVE_NAME = "xmlsat"
    private val XML_SAT_CANCELLATION_ARCHIVE_NAME = "xmlsatcancelamento"

    //Nome do equipamento PDV para operações Bridge
    private val PDV_NAME = "PDV"

    //EditTexts
    protected var editTextIpBridge: EditText? = null
    protected lateinit var editTextValueBridge: EditText
    protected var editTextNumberOfInstallmentsBridge: EditText? = null
    protected var editTextTransactionPort: EditText? = null
    protected var editTextStatusPort: EditText? = null
    protected var editTextPassword: EditText? = null

    //LinearLayout que agem como botão
    var buttonCreditOptionBridge: LinearLayout? = null
    var buttonDebitOptionBridge: LinearLayout? = null
    var buttonStoreOptionBridge: LinearLayout? = null
    var buttonAdmOptionBridge: LinearLayout? = null
    var buttonInCashOptionBridge: LinearLayout? = null

    //Buttons
    var buttonSendTransactionBridge: Button? = null
    var buttonCancelTransactionBridge: Button? = null
    var buttonAdministrativeOperation: Button? = null
    var buttonPrintTestCoupon: Button? = null
    var buttonConsultTerminalStatus: Button? = null
    var buttonConsultConfiguredTimeout: Button? = null
    var buttonConsultLastTransaction: Button? = null
    var buttonSetTerminalPassword: Button? = null
    var buttonSetTransactionTimeout: Button? = null

    //Layout que devem ficar invisiveis para determinadas operações
    var linearLayoutNumberOfInstallments: LinearLayout? = null
    var linearLayoutTypeInstallments: LinearLayout? = null

    //Checkbox enviar senha
    var checkboxSendPassword: CheckBox? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)

        //Atribuição das Views
        viewsAtribuitions()

        //Valores iniciais
        initialValues()

        //Atribuição de funcionalidade ás views
        viewsFunctionalityAtribution()

        //Colorindo borda das formas de pagamento/parcelamento selecionadas
        updatePaymentMethodsBorderColors()
        updateInstallmentMethodBorderColors()
    }

    //Valores iniciais
    private fun initialValues() {
        editTextIpBridge!!.setText("192.168.0.104")
        editTextValueBridge!!.setText("2000")
        formaPagamentoSelecionada = FormaPagamento.CREDITO
        formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_A_VISTA

        //O padrão da aplicação é iniciar com a opção de pagamento por crédito com parcelamento a vista, portanto o número de parcelas deve ser obrigatoriamente 1
        editTextNumberOfInstallmentsBridge!!.setText("1")
        editTextNumberOfInstallmentsBridge!!.isEnabled = false

        //Valores padrões para as portas de transação/status
        editTextTransactionPort!!.setText("3000")
        editTextStatusPort!!.setText("3001")

        //Senha vazia
        editTextPassword!!.setText("")
    }

    //Atribuição das Views
    private fun viewsAtribuitions() {
        //Checkbox
        checkboxSendPassword = findViewById(R.id.checkboxSendPassword)

        //EditTexts
        editTextIpBridge = findViewById(R.id.editTextIpBridge)
        editTextValueBridge = findViewById(R.id.editTextValueBridge)
        editTextNumberOfInstallmentsBridge = findViewById(R.id.editTextNumberOfInstallmentsBridge)
        editTextTransactionPort = findViewById(R.id.editTextTransactionPort)
        editTextStatusPort = findViewById(R.id.editTextStatusPort)
        editTextPassword = findViewById(R.id.editTextPassword)

        //Aplicando Mask ao Valor
        editTextValueBridge.addTextChangedListener(InputMaskMoney(editTextValueBridge))

        //Formas de pagamento
        buttonCreditOptionBridge = findViewById(R.id.buttonCreditOptionBridge)
        buttonDebitOptionBridge = findViewById(R.id.buttonDebitOptionBridge)

        //Tipos de Parcelamento
        buttonStoreOptionBridge = findViewById(R.id.buttonStoreOptionBridge)
        buttonAdmOptionBridge = findViewById(R.id.buttonAdmOptionBridge)
        buttonInCashOptionBridge = findViewById(R.id.buttonInCashOptionBridge)

        //Botões
        buttonSendTransactionBridge = findViewById(R.id.buttonSendTransactionBridge)
        buttonCancelTransactionBridge = findViewById(R.id.buttonCancelTransactionBridge)
        buttonAdministrativeOperation = findViewById(R.id.buttonAdministrativeOperation)
        buttonPrintTestCoupon = findViewById(R.id.buttonPrintTestCoupon)
        buttonConsultTerminalStatus = findViewById(R.id.buttonConsultTerminalStatus)
        buttonConsultConfiguredTimeout = findViewById(R.id.buttonConsultConfiguredTimeout)
        buttonConsultLastTransaction = findViewById(R.id.buttonConsultLastTransaction)
        buttonSetTerminalPassword = findViewById(R.id.buttonSetTerminalPassword)
        buttonSetTransactionTimeout = findViewById(R.id.buttonSetTransactionTimeout)

        //Layout atribuídos para se tornarem invisivesi/visiveis conforme o tipo de pagamento selecionado
        linearLayoutNumberOfInstallments = findViewById(R.id.linearLayoutNumberOfInstallments)
        linearLayoutTypeInstallments = findViewById(R.id.linearLayoutTypeInstallments)
    }

    //Atribuição de funcionalidade ás views
    private fun viewsFunctionalityAtribution() {

        //No click do checkbox de envio de senha o campo de senha deve ser habilitado/desabilitado de acordo com o estado
        checkboxSendPassword!!.setOnClickListener { v: View? ->
            editTextPassword!!.isEnabled = checkboxSendPassword!!.isChecked
        }

        //Na mudança das formas de pagamento além da redecoração das bordas deve ser habilitar/desabilitar as formas de financiamento
        buttonCreditOptionBridge!!.setOnClickListener { v: View? ->
            formaPagamentoSelecionada = FormaPagamento.CREDITO
            updatePaymentMethodsBorderColors()
            fadeInInstallmentsOptionsLayout()
        }
        buttonDebitOptionBridge!!.setOnClickListener { v: View? ->
            formaPagamentoSelecionada = FormaPagamento.DEBITO
            updatePaymentMethodsBorderColors()
            fadeOutInstallmentOptionsLayout()
        }

        //Na mudança das formas de parcelamento além da redecoração das bordas deve ser travado em 1 para a vista, ou destravado caso outra forma de parcelamento seja selecionada
        buttonStoreOptionBridge!!.setOnClickListener { v: View? ->
            formaFinanciamentoSelecionada =
                FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO
            updateInstallmentMethodBorderColors()
            editTextNumberOfInstallmentsBridge!!.isEnabled = true
        }
        buttonAdmOptionBridge!!.setOnClickListener { v: View? ->
            formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR
            updateInstallmentMethodBorderColors()
            editTextNumberOfInstallmentsBridge!!.isEnabled = true
        }
        buttonInCashOptionBridge!!.setOnClickListener { v: View? ->
            formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_A_VISTA
            updateInstallmentMethodBorderColors()
            editTextNumberOfInstallmentsBridge!!.setText("1")
            editTextNumberOfInstallmentsBridge!!.isEnabled = false
        }
        buttonSendTransactionBridge!!.setOnClickListener { v: View ->
            buttonSendTransactionFunction(
                v
            )
        }
        buttonCancelTransactionBridge!!.setOnClickListener { v: View ->
            buttonCancelTransactionFunction(
                v
            )
        }
        buttonAdministrativeOperation!!.setOnClickListener { v: View ->
            buttonAdministrativeOperationFunction(
                v
            )
        }
        buttonPrintTestCoupon!!.setOnClickListener { v: View -> buttonPrintTestCouponFunction(v) }
        buttonConsultTerminalStatus!!.setOnClickListener { v: View ->
            buttonConsultTerminalStatusFunction(
                v
            )
        }
        buttonConsultConfiguredTimeout!!.setOnClickListener { v: View ->
            buttonConsultConfiguredTimeoutFunction(
                v
            )
        }
        buttonConsultLastTransaction!!.setOnClickListener { v: View ->
            buttonConsultLastTransactionFunction(
                v
            )
        }
        buttonSetTerminalPassword!!.setOnClickListener { v: View ->
            buttonSetTerminalPasswordFunction(
                v
            )
        }
        buttonSetTransactionTimeout!!.setOnClickListener { v: View ->
            buttonSetTransactionTimeoutFunction(
                v
            )
        }
    }

    //Atualiza a decoração da borda das opções de pagamento
    private fun updatePaymentMethodsBorderColors() {
        buttonCreditOptionBridge!!.backgroundTintList =
            if (formaPagamentoSelecionada == FormaPagamento.CREDITO) AppCompatResources.getColorStateList(
                this,
                R.color.verde
            ) else AppCompatResources.getColorStateList(this, R.color.black)
        buttonDebitOptionBridge!!.backgroundTintList =
            if (formaPagamentoSelecionada == FormaPagamento.DEBITO) AppCompatResources.getColorStateList(
                this,
                R.color.verde
            ) else AppCompatResources.getColorStateList(this, R.color.black)
    }

    //Atualiza a decoração da borda das opções de parcelamento
    private fun updateInstallmentMethodBorderColors() {
        buttonStoreOptionBridge!!.backgroundTintList =
            if (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO) AppCompatResources.getColorStateList(
                this,
                R.color.verde
            ) else AppCompatResources.getColorStateList(this, R.color.black)
        buttonAdmOptionBridge!!.backgroundTintList =
            if (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR) AppCompatResources.getColorStateList(
                this,
                R.color.verde
            ) else AppCompatResources.getColorStateList(this, R.color.black)
        buttonInCashOptionBridge!!.backgroundTintList =
            if (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_A_VISTA) AppCompatResources.getColorStateList(
                this,
                R.color.verde
            ) else AppCompatResources.getColorStateList(this, R.color.black)
    }

    //Desalibita as opções de parcelamento, caso a opção de débito seja selecionada
    private fun fadeOutInstallmentOptionsLayout() {
        linearLayoutNumberOfInstallments!!.visibility = View.INVISIBLE
        linearLayoutTypeInstallments!!.visibility = View.INVISIBLE
    }

    //Habilita as opções de parcelamento, caso a opção de crédito seja selecionada
    private fun fadeInInstallmentsOptionsLayout() {
        linearLayoutNumberOfInstallments!!.visibility = View.VISIBLE
        linearLayoutTypeInstallments!!.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val retorno = data!!.getStringExtra("retorno")
            Log.d("retorno", retorno!!)
            /**
             * O retorno é sempre um arrayJSON, seguindo o fluxo para todas as operações descrito em startBridgeCommand() o retorno da operação estará após o retorno das funções SetServer e SetSenha, ou seja, na 3° posição do array
             */
            try {
                val jsonArray = JSONArray(retorno)
                val jsonObjectReturn = jsonArray.getJSONObject(2)
                when (requestCode) {
                    SET_SERVER_REQUEST_CODE, SET_SENHA_REQUEST_CODE -> {}
                    INICIA_VENDA_CREDITO_REQUEST_CODE -> {
                        val iniciaVendaCreditoReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            IniciaVendaCredito::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            iniciaVendaCreditoReturn.resultado
                        )
                    }
                    INICIA_VENDA_DEBITO_REQUEST_CODE -> {
                        val iniciaVendaDebitoReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            IniciaVendaDebito::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            iniciaVendaDebitoReturn.resultado
                        )
                    }
                    INICIA_CANCELAMENTO_VENDA_REQUEST_CODE -> {
                        val iniciaCancelamentoVendaReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            IniciaCancelamentoVenda::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            iniciaCancelamentoVendaReturn.resultado
                        )
                    }
                    INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE -> {
                        val iniciaOperacaoAdministrativaReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            IniciaOperacaoAdministrativa::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            iniciaOperacaoAdministrativaReturn.resultado
                        )
                    }
                    IMPRIMIR_CUPOM_NFCE_REQUEST_CODE -> {
                        val imprimirCupomNfceReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            ImprimirCupomNfce::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            imprimirCupomNfceReturn.resultado
                        )
                    }
                    IMPRIMIR_CUPOM_SAT_REQUEST_CODE -> {
                        val imprimirCupomSatReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            ImprimirCupomSat::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            imprimirCupomSatReturn.resultado
                        )
                    }
                    IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE -> {
                        val imprimirCupomSatCancelamento = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            ImprimirCupomSatCancelamento::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            imprimirCupomSatCancelamento.resultado
                        )
                    }
                    CONSULTAR_STATUS_REQUEST_CODE -> {
                        val consultarStatusReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            ConsultarStatus::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            consultarStatusReturn.resultado
                        )
                    }
                    GET_TIMEOUT_REQUEST_CODE -> {
                        val getTimeoutReturn =
                            Gson().fromJson(jsonObjectReturn.toString(), GetTimeout::class.java)
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            getTimeoutReturn.resultado
                        )
                    }
                    CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE -> {
                        val consultarUltimaTransacaoReturn = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            ConsultarUltimaTransacao::class.java
                        )
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            consultarUltimaTransacaoReturn.resultado
                        )
                    }
                    SET_SENHA_SERVER_REQUEST_CODE -> {
                        val setSenhaServerReturn =
                            Gson().fromJson(jsonObjectReturn.toString(), SetSenhaServer::class.java)
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            setSenhaServerReturn.resultado
                        )
                    }
                    SET_TIMEOUT_REQUEST_CODE -> {
                        val setTimeoutReturn =
                            Gson().fromJson(jsonObjectReturn.toString(), SetTimeout::class.java)
                        ActivityUtils.showAlertMessage(
                            this,
                            "Retorno E1 - BRIDGE",
                            setTimeoutReturn.resultado
                        )
                    }
                    else -> ActivityUtils.showAlertMessage(
                        this,
                        "Alerta",
                        "A intent iniciada não foi encontrada!"
                    )
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

    //Retorna o valor inserido no formato necessário para o pagemento elgin pay, o valor deve ser inserido em centavos, a vírgula é removida
    private val editTextValueBridgeFormatted: String

        private get() = editTextValueBridge!!.text.toString().replace(",", "").trim { it <= ' ' }

    //Validações
    //Valida de os campos de conexão (ip, portaTransacao e portaStatus) do bridge inseridos estão nos conformes aceitos, se estiverem, atualize server bridge
    private fun updateBridgeServer(): Boolean {
        return if (isIpValid && isTransactionPortValid && isStatusPortValid) {
            true
        } else false
    }

    //Valida o formato de IP
    private val isIpValid: Boolean
        private get() {
            val IP = editTextIpBridge!!.text.toString()
            val pattern =
                Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
            val matcher = pattern.matcher(IP)
            val isIpValid = matcher.matches()
            if (isIpValid) return true
            ActivityUtils.showAlertMessage(
                this,
                "Alerta",
                "Insira um Ip válido para a conexão Bridge!"
            )
            return false
        }

    //Valida a porta de transacao
    private val isTransactionPortValid: Boolean
        private get() = try {
            val transactionPortInInt = editTextTransactionPort!!.text.toString().toInt()
            if (transactionPortInInt > 65535) {
                ActivityUtils.showAlertMessage(
                    this,
                    "Alerta",
                    "O valor inserido na porta de transação excede o limite esbelecido de 65535!"
                )
                false
            } else true
        } catch (numberFormatException: NumberFormatException) {
            ActivityUtils.showAlertMessage(
                this,
                "Alerta",
                "O valor inserido na porta de transação não pode estar vazio"
            )
            false
        }

    //Valida a porta de status
    private val isStatusPortValid: Boolean
        private get() = try {
            val statusPortInInt = editTextStatusPort!!.text.toString().toInt()
            if (statusPortInInt > 65535) {
                ActivityUtils.showAlertMessage(
                    this,
                    "Alerta",
                    "O valor inserido na porta de status excede o limite esbelecido de 65535!"
                )
                false
            } else true
        } catch (numberFormatException: NumberFormatException) {
            ActivityUtils.showAlertMessage(
                this,
                "Alerta",
                "O valor inserido na porta de status não pode estar vazio!"
            )
            false
        }

    private fun validateInstallmentsField(): Boolean {
        val numberOfInstallments = editTextNumberOfInstallmentsBridge!!.text.toString()
        if (numberOfInstallments == "") {
            ActivityUtils.showAlertMessage(
                this,
                "Alerta",
                "O campo de parcelas não pode estar vazio!"
            )
            return false
        } else if (editTextNumberOfInstallmentsBridge!!.text.toString()
                .toInt() < 2 && formaFinanciamentoSelecionada != FormaFinanciamento.FINANCIAMENTO_A_VISTA
        ) {
            ActivityUtils.showAlertMessage(
                this,
                "Alerta",
                "O número de parcelas não é valido para esta forma de financiamento"
            )
            return false
        }
        return true
    }


    // O valor mínimo para a transação do elgin pay é de R$ 1,00
    private val isValueValidToElginPay: Boolean
        private get() {
            return try {
                val valueInString: String =
                    editTextValueBridge.getText().toString().replace(",", ".").trim()
                val bigDecimalForComparation = BigDecimal(valueInString)
                if (bigDecimalForComparation.compareTo(BigDecimal("1.00")) < 0) {
                    ActivityUtils.showAlertMessage(
                        this,
                        "Alerta",
                        "O valor deve ser maior que 1 real para uma pagamento via elgin pay!"
                    )
                    return false
                }
                true
            } catch (numberFormatException: NumberFormatException) {
                numberFormatException.printStackTrace()
                ActivityUtils.showAlertMessage(
                    this,
                    "Alerta",
                    "O campo de valor não pode estar vazio!"
                )
                false
            }
        }


    /**
     * Função utilizada para iniciar quaisquer operações Bridge com o fluxo : recebe um comando e o concatena com as funções SetServer e SetSenha, evitando a repetição em todas as funcionalidades e assegurando que as ultimas alterações nos campos de entrada sejam efetivadas antes da excução da operação
     *
     * @param bridgeCommand o operação a ser iniciada
     * @param requestCode   código da intent para filtro de retorno em onActivityResult()
     */
    private fun startBridgeCommand(bridgeCommand: BridgeCommand, requestCode: Int) {
        val listOfCommands: MutableList<IntentDigitalHubCommand> = ArrayList()

        //Adiciona o comando de configuração do servidor SetServer
        listOfCommands.add(
            SetServer(
                editTextIpBridge!!.text.toString(),
                editTextTransactionPort!!.text.toString().toInt(),
                editTextStatusPort!!.text.toString().toInt()
            )
        )

        //Adiciciona o comando de configuração da senha SetSenha
        listOfCommands.add(
            SetSenha(
                editTextPassword!!.text.toString(),
                checkboxSendPassword!!.isChecked
            )
        )

        //Adiciona o comando atual
        listOfCommands.add(bridgeCommand)

        //Inicial a atividade através da classe utilitaŕia
        IntentDigitalHubCommandStarter.startHubCommandActivity(this, listOfCommands, requestCode)
    }

    //Funcionalidade dos botões
    private fun buttonSendTransactionFunction(v: View) {
        //Valida os campos de servidor
        if (updateBridgeServer()) {
            //Valida o valor inserido
            if (isValueValidToElginPay) {
                if (formaPagamentoSelecionada == FormaPagamento.CREDITO) {
                    //Valida número de parcelas inserido
                    if (validateInstallmentsField()) {
                        //Numero de parcelas em inteiro
                        val numberOfInstallments =
                            editTextNumberOfInstallmentsBridge!!.text.toString().toInt()
                        val iniciaVendaCreditoCommand = IniciaVendaCredito(
                            generateRandomForBridgeTransactions(),
                            PDV_NAME,
                            editTextValueBridgeFormatted,
                            formaFinanciamentoSelecionada!!.codigoFormaParcelamento,
                            numberOfInstallments
                        )
                        startBridgeCommand(
                            iniciaVendaCreditoCommand,
                            INICIA_VENDA_CREDITO_REQUEST_CODE
                        )
                    }
                } else {
                    val iniciaVendaDebitoCommand = IniciaVendaDebito(
                        generateRandomForBridgeTransactions(),
                        PDV_NAME,
                        editTextValueBridgeFormatted
                    )
                    startBridgeCommand(iniciaVendaDebitoCommand, INICIA_VENDA_DEBITO_REQUEST_CODE)
                }
            }
        }
    }

    private fun buttonCancelTransactionFunction(v: View) {
        //Data do dia atual, usada como um dos parâmetros necessário para o cancelamento de transação no Elgin Pay
        val date = Date()

        //Objeto capaz de formatar a date para o formato aceito pelo Elgin Pay ("dd/mm/aa")
        val dateFormat = SimpleDateFormat("dd/MM/yy")

        //Aplicando formatação
        val todayDate = dateFormat.format(date)
        val builder = AlertDialog.Builder(this)

        //Definindo título do AlertDialog
        builder.setTitle("Código de Referência:")

        // Criando um EditText para pegar o input do usuário na caixa de diálogo
        val input = EditText(this)

        //Configurando o EditText para negrito e configurando o tipo de inserção para apenas número
        input.setTypeface(null, Typeface.BOLD)
        input.inputType = InputType.TYPE_CLASS_NUMBER

        //Tornando o dialógo não-cancelável
        builder.setCancelable(false)
        builder.setView(input)
        builder.setNegativeButton("CANCELAR") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setPositiveButton(
            "OK"
        ) { dialog: DialogInterface?, whichButton: Int ->
            val saleRef = input.text.toString()
            //Setando o foco de para o input do dialógo
            input.requestFocus()
            val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
            if (saleRef == "") {
                ActivityUtils.showAlertMessage(
                    this,
                    "Alerta",
                    "O campo código de referência da transação não pode ser vazio! Digite algum valor."
                )
            } else {
                if (updateBridgeServer()) {
                    val iniciaCancelamentoVendaCommand = IniciaCancelamentoVenda(
                        generateRandomForBridgeTransactions(),
                        PDV_NAME,
                        editTextValueBridgeFormatted,
                        todayDate,
                        saleRef
                    )
                    startBridgeCommand(
                        iniciaCancelamentoVendaCommand,
                        INICIA_CANCELAMENTO_VENDA_REQUEST_CODE
                    )
                }
            }
        }
        builder.show()
    }

    private fun buttonAdministrativeOperationFunction(v: View) {
        val operations = arrayOf(
            "Operação Administrativa",
            "Operação de Instalação",
            "Operação de Configuração",
            "Operação de Manutenção",
            "Teste de Comunicação",
            "Reimpressão de Comprovante"
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ESCOLHA A OPERAÇÃO ADMINISTRATIVA")

        //Tornando o dialógo não-cancelável
        builder.setCancelable(false)
        builder.setNegativeButton("CANCELAR") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setItems(operations) { dialog: DialogInterface?, which: Int ->
            //IniciaOperaçãoAdministrativa de acordo com qual operação foi selecionada.
            if (updateBridgeServer()) {
                //Neste caso o int which que é um parametro fornecido assim que uma opção é selecionada corresponde diretamente aos valores da documentação da função de operação administrativa
                val iniciaOperacaoAdministrativaCommand = IniciaOperacaoAdministrativa(
                    generateRandomForBridgeTransactions(),
                    PDV_NAME,
                    which
                )
                startBridgeCommand(
                    iniciaOperacaoAdministrativaCommand,
                    INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE
                )
            }
        }
        builder.show()
    }

    private fun buttonPrintTestCouponFunction(v: View) {
        val couponTypes =
            arrayOf("Imprimir Cupom NFCe", "Imprimir Cupom Sat", "Imprimir Cupom Sat Cancelamento")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ESCOLHA O TIPO DE CUPOM")
        builder.setNegativeButton("CANCELAR") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setItems(couponTypes) { dialog: DialogInterface?, selected: Int ->
            //IniciaOperaçãoAdministrativa de acordo com qual operação foi selecionada.
            if (updateBridgeServer()) {

                //Variaveis para comparacao do tipo selecionado
                val NFCE_COUPON = 0
                val SAT_COUPON = 1
                val SAT_CANCELLATION_COUPON = 2
                when (selected) {
                    NFCE_COUPON -> {

                        //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                        ActivityUtils.loadXMLFileAndStoreItOnApplicationRootDir(
                            this,
                            XML_NFCE_ARCHIVE_NAME
                        )
                        val xml = ActivityUtils.getFilePathForIDH(
                            this,
                            XML_NFCE_ARCHIVE_NAME + XML_EXTENSION
                        )
                        val indexcsc = 1
                        val csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES"
                        val imprimirCupomNfceCommand = ImprimirCupomNfce(xml, indexcsc, csc)
                        startBridgeCommand(
                            imprimirCupomNfceCommand,
                            IMPRIMIR_CUPOM_NFCE_REQUEST_CODE
                        )
                    }
                    SAT_COUPON -> {

                        //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                        ActivityUtils.loadXMLFileAndStoreItOnApplicationRootDir(
                            this,
                            XML_SAT_ARCHIVE_NAME
                        )
                        val xml = ActivityUtils.getFilePathForIDH(
                            this,
                            XML_SAT_ARCHIVE_NAME + XML_EXTENSION
                        )
                        val imprimirCupomSatCommand = ImprimirCupomSat(xml)
                        startBridgeCommand(imprimirCupomSatCommand, IMPRIMIR_CUPOM_SAT_REQUEST_CODE)
                    }
                    SAT_CANCELLATION_COUPON -> {

                        //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                        ActivityUtils.loadXMLFileAndStoreItOnApplicationRootDir(
                            this,
                            XML_SAT_CANCELLATION_ARCHIVE_NAME
                        )
                        val xml = ActivityUtils.getFilePathForIDH(
                            this,
                            XML_SAT_CANCELLATION_ARCHIVE_NAME + XML_EXTENSION
                        )
                        val assQRCode =
                            "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w=="
                        val imprimirCupomSatCancelamentoCommand =
                            ImprimirCupomSatCancelamento(xml, assQRCode)
                        startBridgeCommand(
                            imprimirCupomSatCancelamentoCommand,
                            IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE
                        )
                    }
                }
            }
        }
        builder.show()
    }

    private fun buttonConsultTerminalStatusFunction(v: View) {
        if (updateBridgeServer()) {
            val consultarStatusCommand = ConsultarStatus()
            startBridgeCommand(consultarStatusCommand, CONSULTAR_STATUS_REQUEST_CODE)
        }
    }

    private fun buttonConsultConfiguredTimeoutFunction(v: View) {
        if (updateBridgeServer()) {
            val getTimeoutCommand = GetTimeout()
            startBridgeCommand(getTimeoutCommand, GET_TIMEOUT_REQUEST_CODE)
        }
    }

    private fun buttonConsultLastTransactionFunction(v: View) {
        if (updateBridgeServer()) {
            val consultarUltimaTransacaoCommand = ConsultarUltimaTransacao(PDV_NAME)
            startBridgeCommand(
                consultarUltimaTransacaoCommand,
                CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE
            )
        }
    }

    private fun buttonSetTerminalPasswordFunction(v: View) {
        val enableOrDisable =
            arrayOf("Habilitar Senha no Terminal", "Desabilitar Senha no Terminal")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ESCOLHA COMO CONFIGURAR A SENHA")
        //Diálogo cancelável somente por botão
        builder.setCancelable(false)
        builder.setNegativeButton("CANCELAR") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setItems(enableOrDisable) { dialog: DialogInterface?, which: Int ->

            //De acordo com a opção escolhida no alert exterior, será definida se operacao irã habilitar ou desabilitar a senha
            val enable = which == 0

            /**
             * Alert com input requerindo a senha a ser definida para o terminal ; caso a opcao escolhida tenha sido "Habilitar Senha no Terminal"
             */

            //Builder interno para alertDialog que sera chamado caso ao opcao de habilitar senha tenha
            val enableOptionSelectedBuilder = AlertDialog.Builder(this)

            //Define o titulo de acordo com a opcao escolhida
            enableOptionSelectedBuilder.setTitle("DIGITE A SENHA A SER HABILITADA:")

            // Criando um EditText para pegar o input do usuário na caixa de diálogo
            val innerInput = EditText(this)

            //Configurando o EditText para negrito e configurando o tipo de inserção para tipo text_password
            innerInput.setTypeface(null, Typeface.BOLD)
            innerInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            enableOptionSelectedBuilder.setCancelable(false)
            enableOptionSelectedBuilder.setView(innerInput)
            enableOptionSelectedBuilder.setNegativeButton("CANCELAR") { dialog1: DialogInterface, which1: Int -> dialog1.dismiss() }
            enableOptionSelectedBuilder.setPositiveButton(
                "OK"
            ) { dialog12: DialogInterface?, whichButton: Int ->
                val passwordEntered = innerInput.text.toString()

                //Setando o foco de para o input do dialógo
                innerInput.requestFocus()
                val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(innerInput, InputMethodManager.SHOW_IMPLICIT)
                if (passwordEntered == "") {
                    ActivityUtils.showAlertMessage(
                        this,
                        "Alerta",
                        "O campo de senha a ser habilitada não pode ser vazio!"
                    )
                } else {
                    val HABILITAR_SENHA_TERMINAL = true
                    if (updateBridgeServer()) {
                        //Se, Senão ; Opcao de habilitar senha
                        val setSenhaServerCommand = SetSenhaServer(
                            passwordEntered,
                            HABILITAR_SENHA_TERMINAL
                        )
                        startBridgeCommand(setSenhaServerCommand, SET_SENHA_SERVER_REQUEST_CODE)
                    }
                }
            }
            /**
             * Se a opcao escolhida for "Habilitar Senha do Terminal", mostre o alert acima, caso contrario tente desabilitar a senha do terminal enviando uma String vazia, pois a funcao SetSenhaServer() com parametro booleano falso apenas
             * desabilitara a requisicao de senha e nao sobrescrevera a senha ja salva no terminal
             */
            if (enable) enableOptionSelectedBuilder.show() else {
                if (!checkboxSendPassword!!.isChecked) ActivityUtils.showAlertMessage(
                    this,
                    "Alerta",
                    "Habilite a opção de envio de senha e envie a senha mais atual para desabilitar a senha do terminal!"
                ) else {
                    if (updateBridgeServer()) {
                        val DESABILITAR_SENHA_TERMINAL = false

                        //Deve ser passado um string vazia para deletar a senha no terminal, pois é mais intuitivo desabilitar a senha atual e deleta-la do que desabilitar e atualizar com uma nova
                        val setSenhaServerCommand = SetSenhaServer(
                            "",
                            DESABILITAR_SENHA_TERMINAL
                        )
                        startBridgeCommand(setSenhaServerCommand, SET_SENHA_REQUEST_CODE)
                    }
                }
            }
        }
        builder.show()
    }

    private fun buttonSetTransactionTimeoutFunction(v: View) {
        val builder = AlertDialog.Builder(this)

        //Definindo título do AlertDialog
        builder.setTitle("DEFINA UM NOVO TIMEOUT PARA TRANSAÇÃO (em segundos):")

        // Criando um EditText para pegar o input do usuário na caixa de diálogo
        val input = EditText(this)

        //Configurando o EditText para negrito e configurando o tipo de inserção para apenas número
        input.setTypeface(null, Typeface.BOLD)
        input.inputType = InputType.TYPE_CLASS_NUMBER

        //Tornando o dialógo não-cancelável
        builder.setCancelable(false)
        builder.setView(input)
        builder.setNegativeButton("CANCELAR") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setPositiveButton(
            "OK"
        ) { dialog: DialogInterface?, whichButton: Int ->
            val newTimeoutInSeconds = input.text.toString().trim { it <= ' ' }

            //Setando o foco de para o input do dialógo
            input.requestFocus()
            val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
            if (newTimeoutInSeconds == "") ActivityUtils.showAlertMessage(
                this,
                "Alerta",
                "O campo que representa a quantidade timeout a ser configurado não pode ser vazio! Digite algum valor."
            ) else {
                if (updateBridgeServer()) {
                    //O valor do editText deve ser convetido para inteiro
                    val setTimeoutCommand = SetTimeout(newTimeoutInSeconds.toInt())
                    startBridgeCommand(setTimeoutCommand, SET_TIMEOUT_REQUEST_CODE)
                }
            }
        }
        builder.show()
    }

    companion object {
        //Valores de requestCode para o filtro das intents em @ActivityResult
        private const val SET_SERVER_REQUEST_CODE = 1
        private const val SET_SENHA_REQUEST_CODE = 2
        private const val INICIA_VENDA_CREDITO_REQUEST_CODE = 3
        private const val INICIA_VENDA_DEBITO_REQUEST_CODE = 4
        private const val INICIA_CANCELAMENTO_VENDA_REQUEST_CODE = 5
        private const val INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE = 6
        private const val IMPRIMIR_CUPOM_NFCE_REQUEST_CODE = 7
        private const val IMPRIMIR_CUPOM_SAT_REQUEST_CODE = 8
        private const val IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE = 9
        private const val CONSULTAR_STATUS_REQUEST_CODE = 10
        private const val GET_TIMEOUT_REQUEST_CODE = 11
        private const val CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE = 12
        private const val SET_SENHA_SERVER_REQUEST_CODE = 13
        private const val SET_TIMEOUT_REQUEST_CODE = 14

        //Opções escolhidas no início da atividade
        private var formaPagamentoSelecionada: FormaPagamento? = null
        private var formaFinanciamentoSelecionada: FormaFinanciamento? = null

        //Gera número aleatório entre 0 e 999999 para as transações bridge
        private fun generateRandomForBridgeTransactions(): Int {
            val random = Random()
            return random.nextInt(1000000)
        }
    }
}