using Android.App;
using Android.Content;
using Android.Graphics;
using Android.OS;
using Android.Text;
using Android.Util;
using Android.Views;
using Android.Views.InputMethods;
using Android.Widget;
using AndroidX.AppCompat.Content.Res;
using Xamarin_Android_Intent_Digital_Hub.InputMasks;
using Xamarin_Android_Intent_Digital_Hub.IntentServices;
using Xamarin_Android_Intent_Digital_Hub.IntentServices.Bridge;
using Java.Lang;
using Java.Math;
using Java.Text;
using Java.Util;
using Java.Util.Regex;
using Newtonsoft.Json;
using Org.Json;
using System;
using System.Collections.Generic;
using Pattern = Java.Util.Regex.Pattern;
using Random = Java.Util.Random;

namespace Xamarin_Android_Intent_Digital_Hub.Bridge
{
    [Activity(Label = "BridgeActivity")]
    class BridgeActivity : Activity
    {
        //Valores de requestCode para o filtro das intents em @ActivityResult
        private const int SET_SERVER_REQUEST_CODE = 1;
        private const int SET_SENHA_REQUEST_CODE = 2;
        private const int INICIA_VENDA_CREDITO_REQUEST_CODE = 3;
        private const int INICIA_VENDA_DEBITO_REQUEST_CODE = 4;
        private const int INICIA_CANCELAMENTO_VENDA_REQUEST_CODE = 5;
        private const int INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE = 6;
        private const int IMPRIMIR_CUPOM_NFCE_REQUEST_CODE = 7;
        private const int IMPRIMIR_CUPOM_SAT_REQUEST_CODE = 8;
        private const int IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE = 9;
        private const int CONSULTAR_STATUS_REQUEST_CODE = 10;
        private const int GET_TIMEOUT_REQUEST_CODE = 11;
        private const int CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE = 12;
        private const int SET_SENHA_SERVER_REQUEST_CODE = 13;
        private const int SET_TIMEOUT_REQUEST_CODE = 14;

        //Opções escolhidas no início da atividade
        private static FormaPagamento formaPagamentoSelecionada;
        private static FormaFinanciamento formaFinanciamentoSelecionada; 

        private const string XML_EXTENSION = ".xml";
        private const string XML_NFCE_ARCHIVE_NAME = "xmlnfce";
        private const string XML_SAT_ARCHIVE_NAME = "xmlsat";
        private const string XML_SAT_CANCELLATION_ARCHIVE_NAME = "xmlsatcancelamento";

        //Nome do equipamento PDV para operações Bridge
        private readonly string PDV_NAME = "PDV";
        //EditTexts
        protected EditText editTextIpBridge, editTextValueBridge, editTextNumberOfInstallmentsBridge, editTextTransactionPort, editTextStatusPort, editTextPassword;
        //LinearLayout que agem como botão
        LinearLayout buttonCreditOptionBridge, buttonDebitOptionBridge, buttonStoreOptionBridge, buttonAdmOptionBridge, buttonInCashOptionBridge;
        //Buttons
        Button buttonSendTransactionBridge, buttonCancelTransactionBridge, buttonAdministrativeOperation, buttonPrintTestCoupon, buttonConsultTerminalStatus, buttonConsultConfiguredTimeout, buttonConsultLastTransaction, buttonSetTerminalPassword, buttonSetTransactionTimeout;
        //Layout que devem ficar invisiveis para determinadas operações
        LinearLayout linearLayoutNumberOfInstallments, linearLayoutTypeInstallments;
        //Checkbox enviar senha
        CheckBox checkboxSendPassword;

        //Gera número aleatório entre 0 e 999999 para as transações bridge
        private static int GenerateRandomForBridgeTransactions()
        {
            Random random = new Random();
            return random.NextInt(1000000);
        }

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.activity_bridge);

            //Atribuição das Views
            ViewsAtribuitions();

            //Valores iniciais
            InitialValues();

            //Atribuição de funcionalidade ás views
            ViewsFunctionalityAtribution();

            //Colorindo borda das formas de pagamento/parcelamento selecionadas
            UpdatePaymentMethodsBorderColors();
            UpdateInstallmentMethodBorderColors();
        }

        //Valores iniciais
        private void InitialValues()
        {
            editTextIpBridge.Text = "192.168.0.104";
            editTextValueBridge.Text = "2000";

            formaPagamentoSelecionada = FormaPagamento.CREDITO;
            formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_A_VISTA;

            //O padrão da aplicação é iniciar com a opção de pagamento por crédito com parcelamento a vista, portanto o número de parcelas deve ser obrigatoriamente 1
            editTextNumberOfInstallmentsBridge.Text = "1";
            editTextNumberOfInstallmentsBridge.Enabled = false;

            //Valores padrões para as portas de transação/status
            editTextTransactionPort.Text = "3000";
            editTextStatusPort.Text = "3001";

            //Senha vazia
            editTextPassword.Text = "";
        }

        private void ViewsAtribuitions()
        {
            //Checkbox
            checkboxSendPassword = FindViewById<CheckBox>(Resource.Id.checkboxSendPassword);

            //EditTexts
            editTextIpBridge = FindViewById<EditText>(Resource.Id.editTextIpBridge);
            editTextValueBridge = FindViewById<EditText>(Resource.Id.editTextValueBridge);
            editTextNumberOfInstallmentsBridge = FindViewById<EditText>(Resource.Id.editTextNumberOfInstallmentsBridge);
            editTextTransactionPort = FindViewById<EditText>(Resource.Id.editTextTransactionPort);
            editTextStatusPort = FindViewById<EditText>(Resource.Id.editTextStatusPort);
            editTextPassword = FindViewById<EditText>(Resource.Id.editTextPassword);

            //Aplicando Mask ao Valor
            editTextValueBridge.AddTextChangedListener(new InputMaskMoney(editTextValueBridge));

            //Formas de pagamento
            buttonCreditOptionBridge = FindViewById<LinearLayout>(Resource.Id.buttonCreditOptionBridge);
            buttonDebitOptionBridge = FindViewById<LinearLayout>(Resource.Id.buttonDebitOptionBridge);

            //Tipos de Parcelamento
            buttonStoreOptionBridge = FindViewById<LinearLayout>(Resource.Id.buttonStoreOptionBridge);
            buttonAdmOptionBridge = FindViewById<LinearLayout>(Resource.Id.buttonAdmOptionBridge);
            buttonInCashOptionBridge = FindViewById<LinearLayout>(Resource.Id.buttonInCashOptionBridge);

            //Botões
            buttonSendTransactionBridge = FindViewById<Button>(Resource.Id.buttonSendTransactionBridge);
            buttonCancelTransactionBridge = FindViewById<Button>(Resource.Id.buttonCancelTransactionBridge);
            buttonAdministrativeOperation = FindViewById<Button>(Resource.Id.buttonAdministrativeOperation);
            buttonPrintTestCoupon = FindViewById<Button>(Resource.Id.buttonPrintTestCoupon);

            buttonConsultTerminalStatus = FindViewById<Button>(Resource.Id.buttonConsultTerminalStatus);
            buttonConsultConfiguredTimeout = FindViewById<Button>(Resource.Id.buttonConsultConfiguredTimeout);
            buttonConsultLastTransaction = FindViewById<Button>(Resource.Id.buttonConsultLastTransaction);
            buttonSetTerminalPassword = FindViewById<Button>(Resource.Id.buttonSetTerminalPassword);
            buttonSetTransactionTimeout = FindViewById<Button>(Resource.Id.buttonSetTransactionTimeout);

            //Layout atribuídos para se tornarem invisivesi/visiveis conforme o tipo de pagamento selecionado
            linearLayoutNumberOfInstallments = FindViewById<LinearLayout>(Resource.Id.linearLayoutNumberOfInstallments);
            linearLayoutTypeInstallments = FindViewById<LinearLayout>(Resource.Id.linearLayoutTypeInstallments);
        }

        //Atribuição de funcionalidade ás views
        private void ViewsFunctionalityAtribution()
        {
            //No click do checkbox de envio de senha o campo de senha deve ser habilitado/desabilitado de acordo com o estado
            checkboxSendPassword.Click += delegate {
                editTextPassword.Enabled = checkboxSendPassword.Checked;
            };

            //Na mudança das formas de pagamento além da redecoração das bordas deve ser habilitar/desabilitar as formas de financiamento

            buttonCreditOptionBridge.Click += delegate {
                formaPagamentoSelecionada = FormaPagamento.CREDITO;
                UpdatePaymentMethodsBorderColors();
                FadeInInstallmentsOptionsLayout();
            };

            buttonDebitOptionBridge.Click += delegate {
                formaPagamentoSelecionada = FormaPagamento.DEBITO;
                UpdatePaymentMethodsBorderColors();
                FadeOutInstallmentOptionsLayout();
            };

            //Na mudança das formas de parcelamento além da redecoração das bordas deve ser travado em 1 para a vista, ou destravado caso outra forma de parcelamento seja selecionada

            buttonStoreOptionBridge.Click += delegate {
                formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO;
                UpdateInstallmentMethodBorderColors();
                editTextNumberOfInstallmentsBridge.Enabled = true;
            };

            buttonAdmOptionBridge.Click += delegate {
                formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR;
                UpdateInstallmentMethodBorderColors();
                editTextNumberOfInstallmentsBridge.Enabled = true;
            };

            buttonInCashOptionBridge.Click += delegate {
                formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_A_VISTA;
                UpdateInstallmentMethodBorderColors();
                editTextNumberOfInstallmentsBridge.Text = "1";
                editTextNumberOfInstallmentsBridge.Enabled = false;
            };

            buttonSendTransactionBridge.Click += ButtonSendTransactionFunction;

            buttonCancelTransactionBridge.Click += ButtonCancelTransactionFunction;

            buttonAdministrativeOperation.Click += ButtonAdministrativeOperationFunction;

            buttonPrintTestCoupon.Click += ButtonPrintTestCouponFunction;

            buttonConsultTerminalStatus.Click += ButtonConsultTerminalStatusFunction;

            buttonConsultConfiguredTimeout.Click += ButtonConsultConfiguredTimeoutFunction;

            buttonConsultLastTransaction.Click += ButtonConsultLastTransactionFunction;

            buttonSetTerminalPassword.Click += ButtonSetTerminalPasswordFunction;

            buttonSetTransactionTimeout.Click += ButtonSetTransactionTimeoutFunction;
        }

        //Atualiza a decoração da borda das opções de pagamento
        private void UpdatePaymentMethodsBorderColors()
        {
            buttonCreditOptionBridge.BackgroundTintList =
                    (formaPagamentoSelecionada == FormaPagamento.CREDITO) ?
                            AppCompatResources.GetColorStateList(this, Resource.Color.verde) :
                            AppCompatResources.GetColorStateList(this, Resource.Color.black);

            buttonDebitOptionBridge.BackgroundTintList =
                    (formaPagamentoSelecionada == FormaPagamento.DEBITO) ?
                            AppCompatResources.GetColorStateList(this, Resource.Color.verde) :
                            AppCompatResources.GetColorStateList(this, Resource.Color.black);
        }

        //Atualiza a decoração da borda das opções de parcelamento
        private void UpdateInstallmentMethodBorderColors()
        {
            buttonStoreOptionBridge.BackgroundTintList =
                    (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO) ?
                            AppCompatResources.GetColorStateList(this, Resource.Color.verde) :
                            AppCompatResources.GetColorStateList(this, Resource.Color.black);

            buttonAdmOptionBridge.BackgroundTintList =
                    (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR) ?
                            AppCompatResources.GetColorStateList(this, Resource.Color.verde) :
                            AppCompatResources.GetColorStateList(this, Resource.Color.black);

            buttonInCashOptionBridge.BackgroundTintList =
                    (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_A_VISTA) ?
                            AppCompatResources.GetColorStateList(this, Resource.Color.verde) :
                            AppCompatResources.GetColorStateList(this, Resource.Color.black);
        }

        //Desalibita as opções de parcelamento, caso a opção de débito seja selecionada
        private void FadeOutInstallmentOptionsLayout()
        {
            linearLayoutNumberOfInstallments.Visibility = ViewStates.Invisible;
            linearLayoutTypeInstallments.Visibility = ViewStates.Invisible;
        }

        protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);

            //Se o resultado for OK
            Log.Debug("resultCode", resultCode.ToString());

            if (resultCode == Result.Ok)
            {
                string retorno = data.GetStringExtra("retorno");
                Log.Debug("retorno", retorno);
                /**
                * O retorno é sempre um arrayJSON, seguindo o fluxo para todas as operações descrito em startBridgeCommand() o retorno da operação estará após o retorno das funções SetServer e SetSenha, ou seja, na 3° posição do array
                */
                try
                {
                    JSONArray jsonArray = new JSONArray(retorno);
                    JSONObject jsonObjectReturn = jsonArray.GetJSONObject(2);

                    switch (requestCode)
                    {
                        case SET_SERVER_REQUEST_CODE:
                        case SET_SENHA_REQUEST_CODE:
                            break;
                        case INICIA_VENDA_CREDITO_REQUEST_CODE:
                            IniciaVendaCredito iniciaVendaCreditoReturn = JsonConvert.DeserializeObject<IniciaVendaCredito>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", iniciaVendaCreditoReturn.GetResultado());
                            break;
                        case INICIA_VENDA_DEBITO_REQUEST_CODE:
                            IniciaVendaDebito iniciaVendaDebitoReturn = JsonConvert.DeserializeObject<IniciaVendaDebito>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", iniciaVendaDebitoReturn.GetResultado());
                            break;
                        case INICIA_CANCELAMENTO_VENDA_REQUEST_CODE:
                            IniciaCancelamentoVenda iniciaCancelamentoVendaReturn = JsonConvert.DeserializeObject<IniciaCancelamentoVenda>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", iniciaCancelamentoVendaReturn.GetResultado());
                            break;
                        case INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE:
                            IniciaOperacaoAdministrativa iniciaOperacaoAdministrativaReturn = JsonConvert.DeserializeObject<IniciaOperacaoAdministrativa>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", iniciaOperacaoAdministrativaReturn.GetResultado());
                            break;
                        case IMPRIMIR_CUPOM_NFCE_REQUEST_CODE:
                            ImprimirCupomNfce imprimirCupomNfceReturn = JsonConvert.DeserializeObject<ImprimirCupomNfce>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", imprimirCupomNfceReturn.GetResultado());
                            break;
                        case IMPRIMIR_CUPOM_SAT_REQUEST_CODE:
                            ImprimirCupomSat imprimirCupomSatReturn = JsonConvert.DeserializeObject<ImprimirCupomSat>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", imprimirCupomSatReturn.GetResultado());
                            break;
                        case IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE:
                            ImprimirCupomSatCancelamento imprimirCupomSatCancelamento = JsonConvert.DeserializeObject<ImprimirCupomSatCancelamento>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", imprimirCupomSatCancelamento.GetResultado());
                            break;
                        case CONSULTAR_STATUS_REQUEST_CODE:
                            ConsultarStatus consultarStatusReturn = JsonConvert.DeserializeObject<ConsultarStatus>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", consultarStatusReturn.GetResultado());
                            break;
                        case GET_TIMEOUT_REQUEST_CODE:
                            GetTimeout getTimeoutReturn = JsonConvert.DeserializeObject<GetTimeout>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", getTimeoutReturn.GetResultado());
                            break;
                        case CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE:
                            ConsultarUltimaTransacao consultarUltimaTransacaoReturn = JsonConvert.DeserializeObject<ConsultarUltimaTransacao>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", consultarUltimaTransacaoReturn.GetResultado());
                            break;
                        case SET_SENHA_SERVER_REQUEST_CODE:
                            SetSenhaServer setSenhaServerReturn = JsonConvert.DeserializeObject<SetSenhaServer>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", setSenhaServerReturn.GetResultado());
                            break;
                        case SET_TIMEOUT_REQUEST_CODE:
                            SetTimeout setTimeoutReturn = JsonConvert.DeserializeObject<SetTimeout>(jsonObjectReturn.ToString());
                            ActivityUtils.ShowAlertMessage(this, "Retorno E1 - BRIDGE", setTimeoutReturn.GetResultado());
                            break;
                        default:
                            ActivityUtils.ShowAlertMessage(this, "Alerta", "O comando " + requestCode + " não foi encontrado!");
                            break;
                    }
                }
                catch (JSONException e) {
                    e.PrintStackTrace();
                    ActivityUtils.ShowAlertMessage(this, "Alerta", "O retorno não está no formato esperado!");
                }
            } else {
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O comando não foi bem sucedido!");
            }
        }

        //Habilita as opções de parcelamento, caso a opção de crédito seja selecionada
        private void FadeInInstallmentsOptionsLayout()
        {
            linearLayoutNumberOfInstallments.Visibility = ViewStates.Visible;
            linearLayoutTypeInstallments.Visibility = ViewStates.Visible;
        }

        //Retorna o valor inserido no formato necessário para o pagemento elgin pay, o valor deve ser inserido em centavos, a vírgula é removida
        private string GetEditTextValueBridgeFormatted()
        {
            return editTextValueBridge.Text.Replace(",", "").Trim();
        }

        //Validações

        //Valida de os campos de conexão (ip, portaTransacao e portaStatus) do bridge inseridos estão nos conformes aceitos, se estiverem, atualize server bridge
        private bool UpdateBridgeServer()
        {
            return IsIpValid() && IsTransactionPortValid() && IsStatusPortValid();
        }

        //Valida o formato de IP
        private bool IsIpValid()
        {
            string IP = editTextIpBridge.Text;

            Pattern pattern = Pattern.Compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
            Matcher matcher = pattern.Matcher(IP);

            bool isIpValid = matcher.Matches();

            if (isIpValid) return true;

            ActivityUtils.ShowAlertMessage(this, "Alerta", "Insira um Ip válido para a conexão Bridge!");
            return false;
        }

        //Valida a porta de transacao
        private bool IsTransactionPortValid()
        {
            try {
                int transactionPortInInt = int.Parse(editTextTransactionPort.Text);

                if (transactionPortInInt > 65535) {
                    ActivityUtils.ShowAlertMessage(this, "Alerta", "O valor inserido na porta de transação excede o limite esbelecido de 65535!");
                    return false;
                } else
                    return true;
            } catch (NumberFormatException numberFormatException) {
                numberFormatException.PrintStackTrace();
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O valor inserido na porta de transação não pode estar vazio");
                return false;
            }
        }

        //Valida a porta de status
        private bool IsStatusPortValid() {
            try {
                int statusPortInInt = int.Parse(editTextStatusPort.Text);

                if (statusPortInInt > 65535) {
                    ActivityUtils.ShowAlertMessage(this, "Alerta", "O valor inserido na porta de status excede o limite esbelecido de 65535!");
                    return false;
                } else
                    return true;
            } catch (NumberFormatException numberFormatException) {
                numberFormatException.PrintStackTrace();
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O valor inserido na porta de status não pode estar vazio!");
                return false;
            }
        }

        private bool ValidateInstallmentsField() {
            string numberOfInstallments = editTextNumberOfInstallmentsBridge.Text;

            if (numberOfInstallments.Equals("")) {
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O campo de parcelas não pode estar vazio!");
                return false;
            } else if ((int.Parse(editTextNumberOfInstallmentsBridge.Text) < 2) && formaFinanciamentoSelecionada != FormaFinanciamento.FINANCIAMENTO_A_VISTA) {
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O número de parcelas não é valido para esta forma de financiamento");
                return false;
            }

            return true;
        }

        // O valor mínimo para a transação do elgin pay é de R$ 1,00
        private bool IsValueValidToElginPay()
        {
            try
            {
                string valueInString = editTextValueBridge.Text.Replace(",", ".").Trim();

                BigDecimal bigDecimalForComparation = new BigDecimal(valueInString);

                if (bigDecimalForComparation.CompareTo(new BigDecimal("1.00")) < 0)
                {
                    ActivityUtils.ShowAlertMessage(this, "Alerta", "O valor deve ser maior que 1 real para uma pagamento via elgin pay!");
                    return false;
                }
                return true;
            }
            catch (NumberFormatException numberFormatException)
            {
                numberFormatException.PrintStackTrace();
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O campo de valor não pode estar vazio!");
                return false;
            }
        }

        /**
        * Função utilizada para iniciar quaisquer operações Bridge com o fluxo : recebe um comando e o concatena com as funções SetServer e SetSenha, evitando a repetição em todas as funcionalidades e assegurando que as ultimas alterações nos campos de entrada sejam efetivadas antes da excução da operação
        *
        * @param bridgeCommand o operação a ser iniciada
        * @param requestCode   código da intent para filtro de retorno em onActivityResult()
        */
        private void StartBridgeCommand(BridgeCommand bridgeCommand, int requestCode)
        {
            List<IntentDigitalHubCommand> listOfCommands = new List<IntentDigitalHubCommand>
            {
                //Adiciona o comando de configuração do servidor SetServer
                new SetServer(editTextIpBridge.Text, int.Parse(editTextTransactionPort.Text), int.Parse(editTextStatusPort.Text)),

                //Adiciciona o comando de configuração da senha SetSenha
                new SetSenha(editTextPassword.Text, checkboxSendPassword.Checked),

                //Adiciona o comando atual
                bridgeCommand
            };

            //Inicial a atividade através da classe utilitaŕia
            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, listOfCommands, requestCode);
        }

        private void ButtonSendTransactionFunction(object v, EventArgs ev)
        {
            //Valida os campos de servidor
            if (UpdateBridgeServer())
            {
                //Valida o valor inserido
                if (IsValueValidToElginPay())
                {
                    if (formaPagamentoSelecionada == FormaPagamento.CREDITO)
                    {
                        //Valida número de parcelas inserido
                        if (ValidateInstallmentsField())
                        {
                            //Numero de parcelas em inteiro
                            int numberOfInstallments = int.Parse(editTextNumberOfInstallmentsBridge.Text);

                            IniciaVendaCredito iniciaVendaCreditoCommand = new IniciaVendaCredito(GenerateRandomForBridgeTransactions(),
                                    PDV_NAME,
                                    GetEditTextValueBridgeFormatted(),
                                    (int) formaFinanciamentoSelecionada,
                                    numberOfInstallments);

                            StartBridgeCommand(iniciaVendaCreditoCommand, INICIA_VENDA_CREDITO_REQUEST_CODE);
                        }
                    }
                    else
                    {
                        IniciaVendaDebito iniciaVendaDebitoCommand = new IniciaVendaDebito(GenerateRandomForBridgeTransactions(),
                                PDV_NAME,
                                GetEditTextValueBridgeFormatted());

                        StartBridgeCommand(iniciaVendaDebitoCommand, INICIA_VENDA_DEBITO_REQUEST_CODE);
                    }
                }
            }
        }

        private void ButtonCancelTransactionFunction(object v, EventArgs ev)
        {
            //Data do dia atual, usada como um dos parâmetros necessário para o cancelamento de transação no Elgin Pay
            Date date = new Date();

            //Objeto capaz de formatar a date para o formato aceito pelo Elgin Pay ("dd/mm/aa")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

            //Aplicando formatação
            string todayDate = dateFormat.Format(date);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Definindo título do AlertDialog
            builder.SetTitle("Código de Referência:");

            // Criando um EditText para pegar o input do usuário na caixa de diálogo
            EditText input = new EditText(this);

            //Configurando o EditText para negrito e configurando o tipo de inserção para apenas número
            input.SetTypeface(null, TypefaceStyle.Bold);
            input.InputType = InputTypes.ClassNumber;

            //Tornando o dialógo não-cancelável
            builder.SetCancelable(false);

            builder.SetView(input);

            builder.SetNegativeButton("CANCELAR", (c, ev) =>
            {
                ((IDialogInterface)c).Dismiss();
            });

            builder.SetPositiveButton("OK", (c, ev) => {
                string saleRef = input.Text;
                //Setando o foco de para o input do dialógo
                input.RequestFocus();
                InputMethodManager imm = (InputMethodManager)GetSystemService(InputMethodService);
                imm.ShowSoftInput(input, ShowFlags.Implicit);

                if (saleRef.Equals(""))
                {
                    ActivityUtils.ShowAlertMessage(this, "Alerta", "O campo código de referência da transação não pode ser vazio! Digite algum valor.");
                }
                else
                {
                    if (UpdateBridgeServer())
                    {
                        IniciaCancelamentoVenda iniciaCancelamentoVendaCommand = new IniciaCancelamentoVenda(GenerateRandomForBridgeTransactions(),
                                PDV_NAME,
                                GetEditTextValueBridgeFormatted(),
                                todayDate,
                                saleRef);

                        StartBridgeCommand(iniciaCancelamentoVendaCommand, INICIA_CANCELAMENTO_VENDA_REQUEST_CODE);
                    }
                }
            });

            builder.Show();
        }

        private void ButtonAdministrativeOperationFunction(object v, EventArgs ev)
        {
            string[] operations = { "Operação Administrativa", "Operação de Instalação", "Operação de Configuração", "Operação de Manutenção", "Teste de Comunicação", "Reimpressão de Comprovante"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.SetTitle("ESCOLHA A OPERAÇÃO ADMINISTRATIVA");

            //Tornando o dialógo não-cancelável
            builder.SetCancelable(false);

            builder.SetNegativeButton("CANCELAR", (c, ev) =>
            {
                ((IDialogInterface)c).Dismiss();
            });

            builder.SetItems(operations, (c, ev) => {
                //IniciaOperaçãoAdministrativa de acordo com qual operação foi selecionada.
                if (UpdateBridgeServer())
                {
                    //Neste caso o int which que é um parametro fornecido assim que uma opção é selecionada corresponde diretamente aos valores da documentação da função de operação administrativa
                    IniciaOperacaoAdministrativa iniciaOperacaoAdministrativaCommand = new IniciaOperacaoAdministrativa(GenerateRandomForBridgeTransactions(),
                            PDV_NAME,
                            ev.Which);

                    StartBridgeCommand(iniciaOperacaoAdministrativaCommand, INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE);
                }
            });
            builder.Show();
        }

        private void ButtonPrintTestCouponFunction(object v, EventArgs ev)
        {
            string[] couponTypes = { "Imprimir Cupom NFCe", "Imprimir Cupom Sat", "Imprimir Cupom Sat Cancelamento" };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.SetTitle("ESCOLHA O TIPO DE CUPOM");

            builder.SetNegativeButton("CANCELAR", (c, ev) =>
            {
                ((IDialogInterface)c).Dismiss();
            });

            builder.SetItems(couponTypes, (c, ev) => {
                //IniciaOperaçãoAdministrativa de acordo com qual operação foi selecionada.
                if (UpdateBridgeServer())
                {
                    //Variaveis para comparacao do tipo selecionado
                    const int NFCE_COUPON = 0;
                    const int SAT_COUPON = 1;
                    const int SAT_CANCELLATION_COUPON = 2;

                    int selected = ev.Which;

                    switch (selected)
                    {
                        case NFCE_COUPON:
                            {
                                //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                                ActivityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(this, XML_NFCE_ARCHIVE_NAME);

                                string xml = ActivityUtils.GetFilePathForIDH(this, XML_NFCE_ARCHIVE_NAME + XML_EXTENSION);
                                int indexcsc = 1;
                                string csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";

                                //   longLog(XML_EXEMPLO_CUPOM_NFCE);
                                ImprimirCupomNfce imprimirCupomNfceCommand = new ImprimirCupomNfce(xml, indexcsc, csc);
                                StartBridgeCommand(imprimirCupomNfceCommand, IMPRIMIR_CUPOM_NFCE_REQUEST_CODE);
                                break;
                            }
                        case SAT_COUPON:
                            {
                                //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                                ActivityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(this, XML_SAT_ARCHIVE_NAME);

                                string xml = ActivityUtils.GetFilePathForIDH(this, XML_SAT_ARCHIVE_NAME + XML_EXTENSION);

                                ImprimirCupomSat imprimirCupomSatCommand = new ImprimirCupomSat(xml);

                                StartBridgeCommand(imprimirCupomSatCommand, IMPRIMIR_CUPOM_SAT_REQUEST_CODE);
                                break;
                            }
                        case SAT_CANCELLATION_COUPON:
                            {
                                //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                                ActivityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(this, XML_SAT_CANCELLATION_ARCHIVE_NAME);

                                string xml = ActivityUtils.GetFilePathForIDH(this, XML_SAT_CANCELLATION_ARCHIVE_NAME + XML_EXTENSION);
                                string assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

                                ImprimirCupomSatCancelamento imprimirCupomSatCancelamentoCommand = new ImprimirCupomSatCancelamento(xml, assQRCode);

                                StartBridgeCommand(imprimirCupomSatCancelamentoCommand, IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE);
                                break;
                            }
                    }
                }
            });
            builder.Show();
        }

        private void ButtonConsultTerminalStatusFunction(object v, EventArgs ev)
        {
            if (UpdateBridgeServer())
            {
                ConsultarStatus consultarStatusCommand = new ConsultarStatus();
                StartBridgeCommand(consultarStatusCommand, CONSULTAR_STATUS_REQUEST_CODE);
            }
        }

        private void ButtonConsultConfiguredTimeoutFunction(object v, EventArgs ev)
        {
            if (UpdateBridgeServer())
            {
                GetTimeout getTimeoutCommand = new GetTimeout();
                StartBridgeCommand(getTimeoutCommand, GET_TIMEOUT_REQUEST_CODE);
            }
        }

        private void ButtonConsultLastTransactionFunction(object v, EventArgs ev)
        {
            if (UpdateBridgeServer())
            {
                ConsultarUltimaTransacao consultarUltimaTransacaoCommand = new ConsultarUltimaTransacao(PDV_NAME);
                StartBridgeCommand(consultarUltimaTransacaoCommand, CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE);
            }
        }

        private void ButtonSetTerminalPasswordFunction(object v, EventArgs ev)
        {
            string[] enableOrDisable = { "Habilitar Senha no Terminal", "Desabilitar Senha no Terminal" };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.SetTitle("ESCOLHA COMO CONFIGURAR A SENHA");
            //Diálogo cancelável somente por botão
            builder.SetCancelable(false);

            builder.SetNegativeButton("CANCELAR", (c, ev) =>
            {
                ((IDialogInterface)c).Dismiss();
            });

            builder.SetItems(enableOrDisable, (c, ev) => {

                //De acordo com a opção escolhida no alert exterior, será definida se operacao irã habilitar ou desabilitar a senha
                bool enable = ev.Which == 0;

                /**
                 * Alert com input requerindo a senha a ser definida para o terminal ; caso a opcao escolhida tenha sido "Habilitar Senha no Terminal"
                */

                //Builder interno para alertDialog que sera chamado caso ao opcao de habilitar senha tenha
                AlertDialog.Builder enableOptionSelectedBuilder = new AlertDialog.Builder(this);

                //Define o titulo de acordo com a opcao escolhida
                enableOptionSelectedBuilder.SetTitle("DIGITE A SENHA A SER HABILITADA:");

                // Criando um EditText para pegar o input do usuário na caixa de diálogo
                EditText innerInput = new EditText(this);

                //Configurando o EditText para negrito e configurando o tipo de inserção para tipo text_password
                innerInput.SetTypeface(null, TypefaceStyle.Bold);
                innerInput.InputType = InputTypes.TextVariationPassword;

                enableOptionSelectedBuilder.SetCancelable(false);

                enableOptionSelectedBuilder.SetView(innerInput);

                enableOptionSelectedBuilder.SetNegativeButton("CANCELAR", (c, ev) =>
                {
                    ((IDialogInterface)c).Dismiss();
                });


                enableOptionSelectedBuilder.SetPositiveButton("OK", (c, ev) => {
                    string passwordEntered = innerInput.Text;

                    //Setando o foco de para o input do dialógo
                    innerInput.RequestFocus();
                    InputMethodManager imm = (InputMethodManager)this.GetSystemService(InputMethodService);
                    imm.ShowSoftInput(innerInput, ShowFlags.Implicit);

                    if (passwordEntered.Equals(""))
                    {
                        ActivityUtils.ShowAlertMessage(this, "Alerta", "O campo de senha a ser habilitada não pode ser vazio!");
                    }
                    else
                    {
                        bool HABILITAR_SENHA_TERMINAL = true;

                        if (UpdateBridgeServer())
                        {
                            //Se, Senão ; Opcao de habilitar senha
                            SetSenhaServer setSenhaServerCommand = new SetSenhaServer(passwordEntered,
                                    HABILITAR_SENHA_TERMINAL);

                            StartBridgeCommand(setSenhaServerCommand, SET_SENHA_SERVER_REQUEST_CODE);
                        }
                    }
                });

                /**
                 * Se a opcao escolhida for "Habilitar Senha do Terminal", mostre o alert acima, caso contrario tente desabilitar a senha do terminal enviando uma String vazia, pois a funcao SetSenhaServer() com parametro booleano falso apenas
                 * desabilitara a requisicao de senha e nao sobrescrevera a senha ja salva no terminal
                 *
                 */

                if (enable)
                    enableOptionSelectedBuilder.Show();
                else
                {
                    if (!checkboxSendPassword.Checked)
                        ActivityUtils.ShowAlertMessage(this, "Alerta", "Habilite a opção de envio de senha e envie a senha mais atual para desabilitar a senha do terminal!");
                    else
                    {
                        if (UpdateBridgeServer())
                        {
                            bool DESABILITAR_SENHA_TERMINAL = false;

                            //Deve ser passado um string vazia para deletar a senha no terminal, pois é mais intuitivo desabilitar a senha atual e deleta-la do que desabilitar e atualizar com uma nova
                            SetSenhaServer setSenhaServerCommand = new SetSenhaServer("",
                                    DESABILITAR_SENHA_TERMINAL);

                            StartBridgeCommand(setSenhaServerCommand, SET_SENHA_SERVER_REQUEST_CODE);
                        }
                    }
                }
            });
            builder.Show();
        }

        private void ButtonSetTransactionTimeoutFunction(object v, EventArgs ev)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Definindo título do AlertDialog
            builder.SetTitle("DEFINA UM NOVO TIMEOUT PARA TRANSAÇÃO (em segundos):");

            // Criando um EditText para pegar o input do usuário na caixa de diálogo
            EditText input = new EditText(this);

            //Configurando o EditText para negrito e configurando o tipo de inserção para apenas número
            input.SetTypeface(null, TypefaceStyle.Bold);
            input.InputType = InputTypes.ClassNumber;

            //Tornando o dialógo não-cancelável
            builder.SetCancelable(false);

            builder.SetView(input);
            builder.SetNegativeButton("CANCELAR", (c, ev) =>
            {
                ((IDialogInterface)c).Dismiss();
            });
            builder.SetPositiveButton("OK", (c, ev) =>
            {
                string newTimeoutInSeconds = input.Text.Trim();

                //Setando o foco de para o input do dialógo
                input.RequestFocus();
                InputMethodManager imm = (InputMethodManager)GetSystemService(InputMethodService);
                imm.ShowSoftInput(input, ShowFlags.Implicit);

                if (newTimeoutInSeconds.Equals(""))
                {
                    ActivityUtils.ShowAlertMessage(this, "Alerta", "O campo que representa a quantidade timeout a ser configurado não pode ser vazio! Digite algum valor.");
                    return;
                }
                else
                {
                    if (UpdateBridgeServer())
                    {
                        //O valor do editText deve ser convetido para inteiro
                        SetTimeout setTimeoutCommand = new SetTimeout(int.Parse(newTimeoutInSeconds));
                        StartBridgeCommand(setTimeoutCommand, SET_TIMEOUT_REQUEST_CODE);
                    }
                }
            });

            builder.Show();
        }
    }
}