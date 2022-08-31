package com.elgin.java_intentdigitalhub_smartpos.Activities.ElginPay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.elgin.java_intentdigitalhub_smartpos.Activities.ActivityUtils;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaCancelamentoVenda;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaOperacaoAdministrativa;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaVendaCredito;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.IniciaVendaDebito;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.Commands.SetPersonalizacao;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.ELGINPAY.ElginPayCommand;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter;
import com.elgin.java_intentdigitalhub_smartpos.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ElginPayActivity extends AppCompatActivity {

    //Campos de valor e parcelas.
    private EditText editTextValue, editTextNumberOfInstallments;

    //Botões de tipos de pagamento.
    private Button buttonCreditOption, buttonDebitOption;

    //Botões de tipo de financiamento;
    private Button buttonStoreOption, buttonAdmOption, buttonInCashOption;

    //Checkbox de customização de layout.
    private CheckBox checkBoxCustomLayout;

    //Botões de ação.
    private Button buttonSendTransaction, buttonCancelTransaction, buttonInitializeAdmOperation;

    //Váriaveis de controle das opções selecionadas, inicializadas com os valores iniciais ao abrir a tela.

    //Forma de pagamento selecionada.
    private FormaPagamento selectedPaymentMethod = FormaPagamento.CREDITO;

    //Forma de financiamento selecionada.
    private FormaFinanciamento selectedInstallmentMethod = FormaFinanciamento.FINANCIAMENTO_A_VISTA;

    //Caputa o layout referente ao campo de "número de parcelas", para aplicar a loǵica de sumir este campo caso o pagamento por débito seja selecionado.
    private LinearLayout linearLayoutNumberOfInstallments;

    //Catura o layout referente aos botoões de financiamento, para aplicar a lógica de sumir estas opções caso o pagamento por débito seja selecionado.
    private LinearLayout linearLayoutInstallmentsMethods;

    //Códigos utilizados para filtros dos comandos, necessário para o ínicio de um intent e para que seu resultado possa ser capturado em @onActivityResult.
    private static class REQUEST_CODE {
        final static int SET_PERSONALIZACAO = 1;
        final static int INICIA_VENDA_CREDITO = 2;
        final static int INICIA_VENDA_DEBITO = 3;
        final static int INICIA_CANCELAMENTO_VENDA = 4;
        final static int INICIA_OPERACAO_ADMINISTRATIVA = 5;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elgin_pay);

        //Atribui as views.
        viewsAssignment();

        //Decoração para variáveis iniciais
        initalBusinessRule();

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment();
    }

    //Atribuição das views.
    private void viewsAssignment() {
        editTextValue = findViewById(R.id.editTextInputValue);
        //Aplica a máscara de moeda ao campo de valor, para melhor formatação do valor entrado.
        editTextValue.addTextChangedListener(new InputMaskMoney(editTextValue));
        //Valor inicial.
        editTextValue.setText("2000");

        editTextNumberOfInstallments = findViewById(R.id.editTextInputNumberOfInstallments);
        //Número de parcelas inicial.
        editTextNumberOfInstallments.setText("2");

        buttonCreditOption = findViewById(R.id.buttonCreditOption);
        buttonDebitOption = findViewById(R.id.buttonDebitOption);

        buttonStoreOption = findViewById(R.id.buttonStoreOption);
        buttonAdmOption = findViewById(R.id.buttonAdmOption);
        buttonInCashOption = findViewById(R.id.buttonAvistaOption);

        checkBoxCustomLayout = findViewById(R.id.checkBoxCustomLayout);

        buttonSendTransaction = findViewById(R.id.buttonSendTransaction);
        buttonCancelTransaction = findViewById(R.id.buttonCancelTransaction);
        buttonInitializeAdmOperation = findViewById(R.id.buttonInitializeAdmOperation);

        linearLayoutNumberOfInstallments = findViewById(R.id.linearLayoutNumberOfInstallments);
        linearLayoutInstallmentsMethods = findViewById(R.id.linearLayoutInstallmentsMethods);
    }

    //Decoração inicial das bordas, de acordo com os valores iniciais escolhidos. (pagamento via crédito e parcelamento via loja)
    private void initalBusinessRule() {
        //Borda verde.
        final ColorStateList GREEN_BORDER = AppCompatResources.getColorStateList(this, R.color.verde);

        buttonCreditOption.setBackgroundTintList(GREEN_BORDER);
        buttonStoreOption.setBackgroundTintList(GREEN_BORDER);
    }

    //Atribuição das funcionalidades a cada view.
    private void viewsFunctionalityAssignment() {
        buttonCreditOption.setOnClickListener(v -> updatePaymentMethodBusinessRule(FormaPagamento.CREDITO));
        buttonDebitOption.setOnClickListener(v -> updatePaymentMethodBusinessRule(FormaPagamento.DEBITO));

        buttonStoreOption.setOnClickListener(v -> updateInstallmentMethodBusinessRule(FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO));
        buttonAdmOption.setOnClickListener(v -> updateInstallmentMethodBusinessRule(FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR));
        buttonInCashOption.setOnClickListener(v -> updateInstallmentMethodBusinessRule(FormaFinanciamento.FINANCIAMENTO_A_VISTA));

        checkBoxCustomLayout.setOnClickListener(v -> setCustomLayoutOnOrOff(checkBoxCustomLayout.isChecked()));

        buttonSendTransaction.setOnClickListener(v -> {
            if (selectedPaymentMethod == FormaPagamento.CREDITO) sendCreditTransaction();
            else sendDebitTransaction();
        });

        buttonCancelTransaction.setOnClickListener(v -> cancelTransaction());

        buttonInitializeAdmOperation.setOnClickListener(v -> initializeAdmOperation());
    }

    //Atualiza as regras e decoração de tela, de acordo com a forma de pagamento selecionada.
    private void updatePaymentMethodBusinessRule(FormaPagamento selectedPaymentMethod) {
        //Atualiza a váriavel de controle.
        this.selectedPaymentMethod = selectedPaymentMethod;

        //1. Muda a coloração da borda dos botões de crédito e débito, conforme a opção selecionda.

        //Borda verde.
        final ColorStateList GREEN_BORDER = AppCompatResources.getColorStateList(this, R.color.verde);
        //Borda preta.
        final ColorStateList BLACK_BORDER = AppCompatResources.getColorStateList(this, R.color.black);

        buttonCreditOption.setBackgroundTintList(selectedPaymentMethod == FormaPagamento.CREDITO ? GREEN_BORDER : BLACK_BORDER);
        buttonDebitOption.setBackgroundTintList(selectedPaymentMethod == FormaPagamento.DEBITO ? GREEN_BORDER : BLACK_BORDER);

        //2. Caso a opção de débito seja seleciona, o campo "número de parcelas" devem sumir, caso a opção selecionada seja a de crédito, o campo deve reaparecer.
        linearLayoutNumberOfInstallments.setVisibility(selectedPaymentMethod == FormaPagamento.DEBITO ? View.INVISIBLE : View.VISIBLE);

        //3. Caso a opção de débito seja selecionada, os botões "tipos de parcelamento" devem sumir, caso a opção de crédito seja selecionada, devem reaparecer.
        linearLayoutInstallmentsMethods.setVisibility(selectedPaymentMethod == FormaPagamento.DEBITO ? View.INVISIBLE : View.VISIBLE);
    }

    //Atualiza as regras e decoração de tela, de acordo com a forma de parcelamento selecionada.
    private void updateInstallmentMethodBusinessRule(FormaFinanciamento selectedInstallmentMethod) {
        //Atualiza a variável de controle.
        this.selectedInstallmentMethod = selectedInstallmentMethod;

        //1. Muda a coloração da borda dos botões de formas de parcelamento, conforme o método seleciondo.

        //Borda verde.
        final ColorStateList GREEN_BORDER = AppCompatResources.getColorStateList(this, R.color.verde);
        //Borda preta.
        final ColorStateList BLACK_BORDER = AppCompatResources.getColorStateList(this, R.color.black);

        buttonStoreOption.setBackgroundTintList(selectedInstallmentMethod == FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO ? GREEN_BORDER : BLACK_BORDER);
        buttonAdmOption.setBackgroundTintList(selectedInstallmentMethod == FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR ? GREEN_BORDER : BLACK_BORDER);
        buttonInCashOption.setBackgroundTintList(selectedInstallmentMethod == FormaFinanciamento.FINANCIAMENTO_A_VISTA ? GREEN_BORDER : BLACK_BORDER);

        //2. Caso a forma de parcelamento selecionada seja a vista, o campo "número de parcelas" deve ser "travado" em "1", caso contrário o campo deve ser destravado e inserido "2", pois é o minimo de parcelas para as outras modalidades.
        editTextNumberOfInstallments.setEnabled(selectedInstallmentMethod != FormaFinanciamento.FINANCIAMENTO_A_VISTA);
        editTextNumberOfInstallments.setText(selectedInstallmentMethod == FormaFinanciamento.FINANCIAMENTO_A_VISTA ? "1" : "2");
    }

    //Habilita ou desabilita o layout personalizado do elgin pay de acordo com a ação na checkbox.
    private void setCustomLayoutOnOrOff(boolean onOrOff) {
        //Caso a checkbox tenha sido marcada, altere o layout para um customizado.
        if (onOrOff) {
            final String YELLOW = "#FED20B";
            final String BLACK = "#050609";

            SetPersonalizacao setPersonalizacaoCommand = new SetPersonalizacao("", "", YELLOW, BLACK, YELLOW, BLACK, YELLOW, BLACK, YELLOW, YELLOW);

            IntentDigitalHubCommandStarter.startIDHCommandForResult(this, setPersonalizacaoCommand, REQUEST_CODE.SET_PERSONALIZACAO);
        }
        //Caso a checkbox tenha sido desmarcada, altere para o layout padrão do elgin pay.
        else {
            String ELGINPAY_BLUE = "#0864a4";
            String WHITE = "#FFFFFF";

            SetPersonalizacao setPersonalizacaoCommand = new SetPersonalizacao("", "", ELGINPAY_BLUE, WHITE, ELGINPAY_BLUE, WHITE, ELGINPAY_BLUE, WHITE, ELGINPAY_BLUE, ELGINPAY_BLUE);

            IntentDigitalHubCommandStarter.startIDHCommandForResult(this, setPersonalizacaoCommand, REQUEST_CODE.SET_PERSONALIZACAO);
        }
    }

    private void sendCreditTransaction() {
        //Validações
        if (isValueValidForElginPayTransaction() && isNumberOfInstallmentsValidForCreditTransaction()) {
            final String valorTotal = getValueTreated();
            final int tipoFinanciamento = selectedInstallmentMethod.getCodigoFormaParcelamento();
            final int numeroParcelas = Integer.parseInt(editTextNumberOfInstallments.getText().toString());

            IniciaVendaCredito iniciaVendaCreditoCommand = new IniciaVendaCredito(valorTotal, tipoFinanciamento, numeroParcelas);

            IntentDigitalHubCommandStarter.startIDHCommandForResult(this, iniciaVendaCreditoCommand, REQUEST_CODE.INICIA_VENDA_CREDITO);
        }
    }

    private void sendDebitTransaction() {
        //Validações
        if (isValueValidForElginPayTransaction()) {
            final String valorTotal = getValueTreated();

            IniciaVendaDebito iniciaVendaDebitoCommand = new IniciaVendaDebito(valorTotal);

            IntentDigitalHubCommandStarter.startIDHCommandForResult(this, iniciaVendaDebitoCommand, REQUEST_CODE.INICIA_VENDA_DEBITO);
        }
    }

    private void cancelTransaction() {
        //Para capturar a referência da venda a partir do input do usuário, é feito um dialog com input.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Definindo título do AlertDialog
        builder.setTitle("Código de Referência:");

        // Criando um EditText para pegar o input do usuário na caixa de diálogo
        final EditText input = new EditText(this);

        //Configurando o EditText para negrito e configurando o tipo de inserção para apenas número
        input.setTypeface(null, Typeface.BOLD);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        //Tornando o dialógo não-cancelável
        builder.setCancelable(false);

        builder.setView(input);

        builder.setPositiveButton("OK",
                (dialog, whichButton) -> {
                    String saleRef = input.getText().toString();

                    //Setando o foco de para o input do dialógo
                    input.requestFocus();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

                    if (saleRef.equals("")) {
                        ActivityUtils.showAlertMessage(this, "Alert", "O campo código de referência da transação não pode ser vazio! Digite algum valor.");
                        return;
                    } else {
                        final String valorTotal = getValueTreated();

                        final String ref = saleRef;

                        //Dia de hoje no formato, "dd/mm/aa"
                        final String data = new SimpleDateFormat("dd/MM/yy").format(new Date());

                        IniciaCancelamentoVenda iniciaCancelamentoVendaCommand = new IniciaCancelamentoVenda(valorTotal, ref, data);

                        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, iniciaCancelamentoVendaCommand, REQUEST_CODE.INICIA_CANCELAMENTO_VENDA);
                    }
                });

        builder.show();
    }

    private void initializeAdmOperation() {
        IniciaOperacaoAdministrativa iniciaOperacaoAdministrativaCommand = new IniciaOperacaoAdministrativa();

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, iniciaOperacaoAdministrativaCommand, REQUEST_CODE.INICIA_OPERACAO_ADMINISTRATIVA);
    }

    //Função utilitária para retorna o valor do campo elgin pay da maneira que as funções devem receber.
    private String getValueTreated() {
        //As funções esperam os valores em centavos. Exemplo: para 20,00 deve ser passado 2000.

        //Remove todos os "." e ",".
        return editTextValue.getText().toString().replaceAll(",", "").replaceAll("\\.", "");
    }

    //Capturando o resultado dos comandos onde o resultado é utilizado em tela.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //O retorno dos comandos no IDH, está sempre sob a chave "retorno", no extra da intent de retorno.
            final String retorno = data.getStringExtra("retorno");
            try {
                //O retorno dos comandos do Intent Digital Hub estão sempre em um Array de Json, apesar de, neste módulo, são executados apenas um comando por vez, portanto o Array de retorno possuí somente um Json.
                JSONArray jsonArray = new JSONArray(retorno);
                JSONObject jsonObjectReturn = jsonArray.getJSONObject(0);

                //Somente algumas operações terão retorno em tela.
                switch (requestCode) {
                    case REQUEST_CODE.INICIA_VENDA_CREDITO:
                        IniciaVendaCredito iniciaVendaCreditoReturn = new Gson().fromJson(jsonObjectReturn.toString(), IniciaVendaCredito.class);

                        ActivityUtils.showAlertMessage(this, "Retorno ElginPay", iniciaVendaCreditoReturn.getResultado());

                        break;
                    case REQUEST_CODE.INICIA_VENDA_DEBITO:
                        IniciaVendaDebito iniciaVendaDebitoReturn = new Gson().fromJson(jsonObjectReturn.toString(), IniciaVendaDebito.class);

                        ActivityUtils.showAlertMessage(this, "Retorno ElginPay", iniciaVendaDebitoReturn.getResultado());
                        break;
                    case REQUEST_CODE.INICIA_CANCELAMENTO_VENDA:
                        IniciaCancelamentoVenda iniciaCancelamentoVendaReturn = new Gson().fromJson(jsonObjectReturn.toString(), IniciaCancelamentoVenda.class);

                        ActivityUtils.showAlertMessage(this, "Retorno ElginPay", iniciaCancelamentoVendaReturn.getResultado());
                        break;
                    case REQUEST_CODE.INICIA_OPERACAO_ADMINISTRATIVA:
                        IniciaOperacaoAdministrativa iniciaOperacaoAdministrativaReturn = new Gson().fromJson(jsonObjectReturn.toString(), IniciaOperacaoAdministrativa.class);

                        ActivityUtils.showAlertMessage(this, "Retorno ElginPay", iniciaOperacaoAdministrativaReturn.getResultado());
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Validações

    //O valor mínimo para uma transação via elgin pay é de R$ 1,00. (um real)
    private boolean isValueValidForElginPayTransaction() {
        //Formata o valor para BigDecimal, para que seja possível comparar com o valor de um real.

        //Remove, primeiramente, os "." referente as casas de mil. Exemplo: 2.222,00 -> 2222,00
        String treatedValue = editTextValue.getText().toString().replaceAll("\\.", "");
        //Substitui a virgula das casas decimais por um ".". Exemplo 2222.00
        treatedValue = treatedValue.replaceAll(",", ".");

        //Cria um BigDecimal de acordo com o valor inserido.
        final BigDecimal valueAsBigDecimal = new BigDecimal(treatedValue);

        //BigDecimal equivalente a um real, para comparação.
        final BigDecimal realAsBigDecimal = new BigDecimal("1.00");

        if (valueAsBigDecimal.compareTo(realAsBigDecimal) < 0) {
            ActivityUtils.showAlertMessage(this, "Aleta", "O valor mínimo para a transação é de R$1.00!");
            return false;
        }
        return true;
    }

    //Valida se o campo de parcelas é valido para o pagamento por crédito.
    private boolean isNumberOfInstallmentsValidForCreditTransaction() {
        try {
            int numberOfInstallments = Integer.parseInt(editTextNumberOfInstallments.getText().toString());

            //Para o pagamento a vista não será necessário tratar, pois o valor é sempre travado em 1, para as demais formas de parcelamento, é necessário um mínimo de 2 parcelas.
            if (selectedInstallmentMethod != FormaFinanciamento.FINANCIAMENTO_A_VISTA) {
                if (numberOfInstallments < 2) {
                    ActivityUtils.showAlertMessage(this, "Alerta", "O número mínimo de parcelas para esse tipo de parcelamento é 2!");
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            ActivityUtils.showAlertMessage(this, "Alerta", "O campo de número de parcelas não pode estar vazio!");
            return false;
        }
    }

}