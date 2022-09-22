package com.elgin.kotlin_intent_digital_hub_smartpos.Activities.ElginPay


import ActivityUtils.showAlertMessage
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaCancelamentoVenda
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaOperacaoAdministrativa
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaVendaCredito
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaVendaDebito
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.SetPersonalizacao
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter
import com.elgin.kotlin_intent_digital_hub_smartpos.R
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date


class ElginPayActivity(
) : AppCompatActivity() {

    //Campos de valor e parcelas.
    lateinit var editTextValue: EditText;

    //Campos de valor e parcelas.
    lateinit var editTextNumberOfInstallments: EditText

    //Botões de tipos de pagamento.
    lateinit var buttonCreditOption: Button

    //Botões de tipos de pagamento.
    lateinit var buttonDebitOption: Button

    //Botões de tipo de financiamento;
    lateinit var buttonStoreOption: Button

    //Botões de tipo de financiamento;
    lateinit var buttonAdmOption: Button

    //Botões de tipo de financiamento;
    lateinit var buttonInCashOption: Button

    //Checkbox de customização de layout.
    lateinit var checkBoxCustomLayout: CheckBox

    //Botões de ação.
    lateinit var buttonSendTransaction: Button

    //Botões de ação.
    lateinit var buttonCancelTransaction: Button

    //Botões de ação.
    lateinit var buttonInitializeAdmOperation: Button

    //Váriaveis de controle das opções selecionadas, inicializadas com os valores iniciais ao abrir a tela.

    //Forma de pagamento selecionada.
    var selectedPaymentMethod: FormaPagamento = FormaPagamento.CREDITO

    //Forma de financiamento selecionada.
    var selectedInstallmentMethod: FormaFinanciamento = FormaFinanciamento.FINANCIAMENTO_A_VISTA

    //Caputa o layout referente ao campo de "número de parcelas", para aplicar a loǵica de sumir este campo caso o pagamento por débito seja selecionado.
    lateinit var linearLayoutNumberOfInstallments: LinearLayout

    //Catura o layout referente aos botoões de financiamento, para aplicar a lógica de sumir estas opções caso o pagamento por débito seja selecionado.
    lateinit var linearLayoutInstallmentsMethods: LinearLayout

    //Códigos utilizados para filtros dos comandos, necessário para o ínicio de um intent e para que seu resultado possa ser capturado em @onActivityResult.

    private object REQUEST_CODE {
        const val SET_PERSONALIZACAO = 1
        const val INICIA_VENDA_CREDITO = 2
        const val INICIA_VENDA_DEBITO = 3
        const val INICIA_CANCELAMENTO_VENDA = 4
        const val INICIA_OPERACAO_ADMINISTRATIVA = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elgin_pay)

        //Atribui as views.
        viewsAssignment()

        //Decoração para variáveis iniciais
        initalBusinessRule()

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment()
    }

    //Atribuição das views.
    private fun viewsAssignment() {
        editTextValue = findViewById<EditText>(R.id.editTextInputValue)
        //Aplica a máscara de moeda ao campo de valor, para melhor formatação do valor entrado.
        editTextValue.addTextChangedListener(InputMaskMoney(editTextValue))
        //Valor inicial.
        editTextValue.setText("2000")
        editTextNumberOfInstallments =
            findViewById<EditText>(R.id.editTextInputNumberOfInstallments)
        //Número de parcelas inicial.
        editTextNumberOfInstallments!!.setText("2")
        buttonCreditOption = findViewById<Button>(R.id.buttonCreditOption)
        buttonDebitOption = findViewById<Button>(R.id.buttonDebitOption)
        buttonStoreOption = findViewById<Button>(R.id.buttonStoreOption)
        buttonAdmOption = findViewById<Button>(R.id.buttonAdmOption)
        buttonInCashOption = findViewById<Button>(R.id.buttonAvistaOption)
        checkBoxCustomLayout = findViewById<CheckBox>(R.id.checkBoxCustomLayout)
        buttonSendTransaction = findViewById<Button>(R.id.buttonSendTransaction)
        buttonCancelTransaction = findViewById<Button>(R.id.buttonCancelTransaction)
        buttonInitializeAdmOperation = findViewById<Button>(R.id.buttonInitializeAdmOperation)
        linearLayoutNumberOfInstallments =
            findViewById<LinearLayout>(R.id.linearLayoutNumberOfInstallments)
        linearLayoutInstallmentsMethods =
            findViewById<LinearLayout>(R.id.linearLayoutInstallmentsMethods)
    }

    //Decoração inicial das bordas, de acordo com os valores iniciais escolhidos. (pagamento via crédito e parcelamento via loja)
    private fun initalBusinessRule() {
        //Borda verde.
        val GREEN_BORDER = AppCompatResources.getColorStateList(this, R.color.verde)
        buttonCreditOption!!.backgroundTintList = GREEN_BORDER
        buttonStoreOption!!.backgroundTintList = GREEN_BORDER
    }

    //Atribuição das funcionalidades a cada view.
    private fun viewsFunctionalityAssignment() {
        buttonCreditOption!!.setOnClickListener { v: View? ->
            updatePaymentMethodBusinessRule(
                FormaPagamento.CREDITO
            )
        }
        buttonDebitOption!!.setOnClickListener { v: View? ->
            updatePaymentMethodBusinessRule(
                FormaPagamento.DEBITO
            )
        }

        buttonStoreOption!!.setOnClickListener { v: View? ->
            updateInstallmentMethodBusinessRule(
                FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO
            )
        }
        buttonAdmOption!!.setOnClickListener { v: View? ->
            updateInstallmentMethodBusinessRule(
                FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR
            )
        }
        buttonInCashOption!!.setOnClickListener { v: View? ->
            updateInstallmentMethodBusinessRule(
                FormaFinanciamento.FINANCIAMENTO_A_VISTA
            )
        }

        checkBoxCustomLayout!!.setOnClickListener { v: View? ->
            setCustomLayoutOnOrOff(
                checkBoxCustomLayout!!.isChecked
            )
        }

        buttonSendTransaction!!.setOnClickListener { v: View? ->
            if (selectedPaymentMethod === FormaPagamento.CREDITO) sendCreditTransaction()
            else sendDebitTransaction()
        }
        buttonCancelTransaction!!.setOnClickListener { v: View? -> cancelTransaction() }

        buttonInitializeAdmOperation!!.setOnClickListener { v: View? -> initializeAdmOperation() }
    }

    //Atualiza as regras e decoração de tela, de acordo com a forma de pagamento selecionada.
    private fun updatePaymentMethodBusinessRule(selectedPaymentMethod: FormaPagamento) {
        //Atualiza a váriavel de controle.
        this.selectedPaymentMethod = selectedPaymentMethod

        //1. Muda a coloração da borda dos botões de crédito e débito, conforme a opção selecionda.

        //Borda verde.
        val GREEN_BORDER = AppCompatResources.getColorStateList(this, R.color.verde)
        //Borda preta.
        val BLACK_BORDER = AppCompatResources.getColorStateList(this, R.color.black)
        buttonCreditOption!!.backgroundTintList =
            if (selectedPaymentMethod === FormaPagamento.CREDITO) GREEN_BORDER else BLACK_BORDER
        buttonDebitOption!!.backgroundTintList =
            if (selectedPaymentMethod === FormaPagamento.DEBITO) GREEN_BORDER else BLACK_BORDER

        //2. Caso a opção de débito seja seleciona, o campo "número de parcelas" devem sumir, caso a opção selecionada seja a de crédito, o campo deve reaparecer.
        linearLayoutNumberOfInstallments!!.visibility =
            if (selectedPaymentMethod === FormaPagamento.DEBITO) View.INVISIBLE else View.VISIBLE

        //3. Caso a opção de débito seja selecionada, os botões "tipos de parcelamento" devem sumir, caso a opção de crédito seja selecionada, devem reaparecer.
        linearLayoutInstallmentsMethods!!.visibility =
            if (selectedPaymentMethod === FormaPagamento.DEBITO) View.INVISIBLE else View.VISIBLE
    }

    //Atualiza as regras e decoração de tela, de acordo com a forma de parcelamento selecionada.
    private fun updateInstallmentMethodBusinessRule(selectedInstallmentMethod: FormaFinanciamento) {
        //Atualiza a variável de controle.
        this.selectedInstallmentMethod = selectedInstallmentMethod

        //1. Muda a coloração da borda dos botões de formas de parcelamento, conforme o método seleciondo.

        //Borda verde.
        val GREEN_BORDER = AppCompatResources.getColorStateList(this, R.color.verde)
        //Borda preta.
        val BLACK_BORDER = AppCompatResources.getColorStateList(this, R.color.black)
        buttonStoreOption!!.backgroundTintList =
            if (selectedInstallmentMethod === FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO) GREEN_BORDER else BLACK_BORDER
        buttonAdmOption!!.backgroundTintList =
            if (selectedInstallmentMethod === FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR) GREEN_BORDER else BLACK_BORDER
        buttonInCashOption!!.backgroundTintList =
            if (selectedInstallmentMethod === FormaFinanciamento.FINANCIAMENTO_A_VISTA) GREEN_BORDER else BLACK_BORDER

        //2. Caso a forma de parcelamento selecionada seja a vista, o campo "número de parcelas" deve ser "travado" em "1", caso contrário o campo deve ser destravado e inserido "2", pois é o minimo de parcelas para as outras modalidades.
        editTextNumberOfInstallments!!.isEnabled =
            selectedInstallmentMethod !== FormaFinanciamento.FINANCIAMENTO_A_VISTA
        editTextNumberOfInstallments!!.setText(if (selectedInstallmentMethod === FormaFinanciamento.FINANCIAMENTO_A_VISTA) "1" else "2")
    }

    //Habilita ou desabilita o layout personalizado do elgin pay de acordo com a ação na checkbox.
    private fun setCustomLayoutOnOrOff(onOrOff: Boolean) {
        //Caso a checkbox tenha sido marcada, altere o layout para um customizado.
        if (onOrOff) {
            val YELLOW = "#FED20B"
            val BLACK = "#050609"
            val setPersonalizacaoCommand = SetPersonalizacao(
                "",
                "",
                YELLOW,
                BLACK,
                YELLOW,
                BLACK,
                YELLOW,
                BLACK,
                YELLOW,
                YELLOW
            );

            IntentDigitalHubCommandStarter.startIDHCommandForResult(
                this,
                setPersonalizacaoCommand,
                REQUEST_CODE.SET_PERSONALIZACAO
            )
        } else {
            val ELGINPAY_BLUE = "#0864a4"
            val WHITE = "#FFFFFF"
            val setPersonalizacaoCommand = SetPersonalizacao(
                "",
                "",
                ELGINPAY_BLUE,
                WHITE,
                ELGINPAY_BLUE,
                WHITE,
                ELGINPAY_BLUE,
                WHITE,
                ELGINPAY_BLUE,
                ELGINPAY_BLUE
            );

            IntentDigitalHubCommandStarter.startIDHCommandForResult(
                this,
                setPersonalizacaoCommand,
                REQUEST_CODE.SET_PERSONALIZACAO
            )
        }
    }

    private fun sendCreditTransaction() {
        //Validações
        if (isValueValidForElginPayTransaction() && isNumberOfInstallmentsValidForCreditTransaction()) {
            val valorTotal: String = getValueTreated()
            val tipoFinanciamento: Int = selectedInstallmentMethod.codigoFormaParcelamento
            val numeroParcelas = editTextNumberOfInstallments!!.text.toString().toInt()
            val iniciaVendaCreditoCommand =
                IniciaVendaCredito(valorTotal, tipoFinanciamento, numeroParcelas)
            IntentDigitalHubCommandStarter.startIDHCommandForResult(
                this,
                iniciaVendaCreditoCommand,
                REQUEST_CODE.INICIA_VENDA_CREDITO
            )
        }
    }

    private fun sendDebitTransaction() {
        //Validações
        if (isValueValidForElginPayTransaction()) {
            val valorTotal: String = getValueTreated()
            val iniciaVendaDebitoCommand = IniciaVendaDebito(valorTotal)
            IntentDigitalHubCommandStarter.startIDHCommandForResult(
                this,
                iniciaVendaDebitoCommand,
                REQUEST_CODE.INICIA_VENDA_DEBITO
            )
        }
    }

    private fun cancelTransaction() {
        //Para capturar a referência da venda a partir do input do usuário, é feito um dialog com input.
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
        builder.setPositiveButton(
            "OK"
        ) { dialog: DialogInterface?, whichButton: Int ->
            val saleRef = input.text.toString()

            //Setando o foco de para o input do dialógo
            input.requestFocus()
            val imm =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
            if (saleRef == "") {
                showAlertMessage(
                    this,
                    "Alert",
                    "O campo código de referência da transação não pode ser vazio! Digite algum valor."
                )
                return@setPositiveButton
            } else {
                val valorTotal: String = getValueTreated()

                //Dia de hoje no formato, "dd/mm/aa"
                val data =
                    SimpleDateFormat("dd/MM/yy").format(Date())
                val iniciaCancelamentoVendaCommand =
                    IniciaCancelamentoVenda(valorTotal, saleRef, data)
                IntentDigitalHubCommandStarter.startIDHCommandForResult(
                    this,
                    iniciaCancelamentoVendaCommand,
                    REQUEST_CODE.INICIA_CANCELAMENTO_VENDA
                )
            }
        }
        builder.show()
    }

    private fun initializeAdmOperation() {
        val iniciaOperacaoAdministrativaCommand = IniciaOperacaoAdministrativa()
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            iniciaOperacaoAdministrativaCommand,
            REQUEST_CODE.INICIA_OPERACAO_ADMINISTRATIVA
        )
    }

    //Função utilitária para retorna o valor do campo elgin pay da maneira que as funções devem receber.
    private fun getValueTreated(): String {
        //As funções esperam os valores em centavos. Exemplo: para 20,00 deve ser passado 2000.

        //Remove todos os "." e ",".
        return editTextValue!!.text.toString().replace(",".toRegex(), "")
            .replace("\\.".toRegex(), "")
    }

    //Capturando o resultado dos comandos onde o resultado é utilizado em tela.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //O retorno dos comandos no IDH, está sempre sob a chave "retorno", no extra da intent de retorno.
            val retorno = data!!.getStringExtra("retorno")
            try {
                //O retorno dos comandos do Intent Digital Hub estão sempre em um Array de Json, apesar de, neste módulo, são executados apenas um comando por vez, portanto o Array de retorno possuí somente um Json.
                val jsonArray = JSONArray(retorno)
                val jsonObjectReturn = jsonArray.getJSONObject(0)
                when (requestCode) {
                    REQUEST_CODE.INICIA_VENDA_CREDITO -> {
                        val iniciaVendaCreditoReturn: IniciaVendaCredito = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            IniciaVendaCredito::class.java
                        )
                        showAlertMessage(
                            this,
                            "Retorno ElginPay",
                            iniciaVendaCreditoReturn.resultado
                        )
                    }
                    REQUEST_CODE.INICIA_VENDA_DEBITO -> {
                        val iniciaVendaDebitoReturn: IniciaVendaDebito = Gson().fromJson(
                            jsonObjectReturn.toString(),
                            IniciaVendaDebito::class.java
                        )
                        showAlertMessage(
                            this,
                            "Retorno ElginPay",
                            iniciaVendaDebitoReturn.resultado
                        )
                    }
                    REQUEST_CODE.INICIA_CANCELAMENTO_VENDA -> {
                        val iniciaCancelamentoVendaReturn: IniciaCancelamentoVenda =
                            Gson().fromJson(
                                jsonObjectReturn.toString(),
                                IniciaCancelamentoVenda::class.java
                            )
                        showAlertMessage(
                            this,
                            "Retorno ElginPay",
                            iniciaCancelamentoVendaReturn.resultado
                        )
                    }
                    REQUEST_CODE.INICIA_OPERACAO_ADMINISTRATIVA -> {
                        val iniciaOperacaoAdministrativaReturn: IniciaOperacaoAdministrativa =
                            Gson().fromJson(
                                jsonObjectReturn.toString(),
                                IniciaOperacaoAdministrativa::class.java
                            )
                        showAlertMessage(
                            this,
                            "Retorno ElginPay",
                            iniciaOperacaoAdministrativaReturn.resultado
                        )
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    //Validações

    //Validações
    //O valor mínimo para uma transação via elgin pay é de R$ 1,00. (um real)
    private fun isValueValidForElginPayTransaction(): Boolean {
        //Formata o valor para BigDecimal, para que seja possível comparar com o valor de um real.

        //Remove, primeiramente, os "." referente as casas de mil. Exemplo: 2.222,00 -> 2222,00
        var treatedValue = editTextValue!!.text.toString().replace("\\.".toRegex(), "")
        //Substitui a virgula das casas decimais por um ".". Exemplo 2222.00
        treatedValue = treatedValue.replace(",".toRegex(), ".")

        //Cria um BigDecimal de acordo com o valor inserido.
        val valueAsBigDecimal = BigDecimal(treatedValue)

        //BigDecimal equivalente a um real, para comparação.
        val realAsBigDecimal = BigDecimal("1.00")
        if (valueAsBigDecimal.compareTo(realAsBigDecimal) < 0) {
            showAlertMessage(
                this,
                "Aleta",
                "O valor mínimo para a transação é de R$1.00!"
            )
            return false
        }
        return true
    }

    //Valida se o campo de parcelas é valido para o pagamento por crédito.
    private fun isNumberOfInstallmentsValidForCreditTransaction(): Boolean {
        return try {
            val numberOfInstallments = editTextNumberOfInstallments!!.text.toString().toInt()

            //Para o pagamento a vista não será necessário tratar, pois o valor é sempre travado em 1, para as demais formas de parcelamento, é necessário um mínimo de 2 parcelas.
            if (selectedInstallmentMethod !== FormaFinanciamento.FINANCIAMENTO_A_VISTA) {
                if (numberOfInstallments < 2) {
                    showAlertMessage(
                        this,
                        "Alerta",
                        "O número mínimo de parcelas para esse tipo de parcelamento é 2!"
                    )
                    return false
                }
            }
            true
        } catch (e: NumberFormatException) {
            showAlertMessage(
                this,
                "Alerta",
                "O campo de número de parcelas não pode estar vazio!"
            )
            false
        }
    }
}