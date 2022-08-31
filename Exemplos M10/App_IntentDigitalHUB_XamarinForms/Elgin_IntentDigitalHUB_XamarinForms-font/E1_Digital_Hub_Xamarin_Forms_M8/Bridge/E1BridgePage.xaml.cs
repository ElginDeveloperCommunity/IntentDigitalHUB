using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices.Bridge;
using Newtonsoft.Json;
using System;
using System.Text.RegularExpressions;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using System.Collections.Generic;
using Xamarin_Forms_Intent_Digital_Hub.Utils;
using Newtonsoft.Json.Linq;

namespace Xamarin_Forms_Intent_Digital_Hub.Bridge
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class E1BridgePage : ContentPage
    {
        //Valores de requestCode para o filtro das intents em @ActivityResult
        public const int SET_SERVER_REQUEST_CODE = 1;
        public const int SET_SENHA_REQUEST_CODE = 2;
        public const int INICIA_VENDA_CREDITO_REQUEST_CODE = 3;
        public const int INICIA_VENDA_DEBITO_REQUEST_CODE = 4;
        public const int INICIA_CANCELAMENTO_VENDA_REQUEST_CODE = 5;
        public const int INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE = 6;
        public const int IMPRIMIR_CUPOM_NFCE_REQUEST_CODE = 7;
        public const int IMPRIMIR_CUPOM_SAT_REQUEST_CODE = 8;
        public const int IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE = 9;
        public const int CONSULTAR_STATUS_REQUEST_CODE = 10;
        public const int GET_TIMEOUT_REQUEST_CODE = 11;
        public const int CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE = 12;
        public const int SET_SENHA_SERVER_REQUEST_CODE = 13;
        public const int SET_TIMEOUT_REQUEST_CODE = 14;

        //Opções escolhidas no início da atividade
        private static FormaPagamento formaPagamentoSelecionada;
        private static FormaFinanciamento formaFinanciamentoSelecionada;

        private const string XML_EXTENSION = ".xml";
        private const string XML_NFCE_ARCHIVE_NAME = "xmlnfce";
        private const string XML_SAT_ARCHIVE_NAME = "xmlsat";
        private const string XML_SAT_CANCELLATION_ARCHIVE_NAME = "xmlsatcancelamento";

        //Nome do equipamento PDV para operações Bridge
        private readonly string PDV_NAME = "PDV";

        private readonly IActivityUtils activityUtils = DependencyService.Get<IActivityUtils>();

        // Serviço de execução de comandos via intent
        private readonly IIntentDigitalHubCommandStarter digitalHubCommandStarter = DependencyService.Get<IIntentDigitalHubCommandStarter>();

        public E1BridgePage()
        {
            InitializeComponent();

            //Valores iniciais
            InitialValues();

            //Atribuição de funcionalidade ás views
            ViewsAtribuitions();

            //Colorindo borda das formas de pagamento/parcelamento selecionadas
            UpdatePaymentMethodsBorderColors();
            UpdateInstallmentMethodBorderColors();

            // Escutar eventos emitidos por resultados de intent
            MessagingCenter.Unsubscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result");
            MessagingCenter.Subscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result", HandleIntentResult);
        }
        
        private void HandleIntentResult(object sender, Tuple<int, string> resposta)
        {
            int requestCode = resposta.Item1;
            JArray jsonArray = JsonConvert.DeserializeObject<JArray>(resposta.Item2);
            string jsonObjectReturn = jsonArray[2].ToString();

            try
            {
                switch (requestCode)
                {
                    case SET_SERVER_REQUEST_CODE:
                    case SET_SENHA_REQUEST_CODE:
                        break;
                    case INICIA_VENDA_CREDITO_REQUEST_CODE:
                        IniciaVendaCredito iniciaVendaCreditoReturn = JsonConvert.DeserializeObject<IniciaVendaCredito>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", iniciaVendaCreditoReturn.GetResultado(), "OK");
                        break;
                    case INICIA_VENDA_DEBITO_REQUEST_CODE:
                        IniciaVendaDebito iniciaVendaDebitoReturn = JsonConvert.DeserializeObject<IniciaVendaDebito>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", iniciaVendaDebitoReturn.GetResultado(), "OK");
                        break;
                    case INICIA_CANCELAMENTO_VENDA_REQUEST_CODE:
                        IniciaCancelamentoVenda iniciaCancelamentoVendaReturn = JsonConvert.DeserializeObject<IniciaCancelamentoVenda>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", iniciaCancelamentoVendaReturn.GetResultado(), "OK");
                        break;
                    case INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE:
                        IniciaOperacaoAdministrativa iniciaOperacaoAdministrativaReturn = JsonConvert.DeserializeObject<IniciaOperacaoAdministrativa>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", iniciaOperacaoAdministrativaReturn.GetResultado(), "OK");
                        break;
                    case IMPRIMIR_CUPOM_NFCE_REQUEST_CODE:
                        ImprimirCupomNfce imprimirCupomNfceReturn = JsonConvert.DeserializeObject<ImprimirCupomNfce>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", imprimirCupomNfceReturn.GetResultado(), "OK");
                        break;
                    case IMPRIMIR_CUPOM_SAT_REQUEST_CODE:
                        ImprimirCupomSat imprimirCupomSatReturn = JsonConvert.DeserializeObject<ImprimirCupomSat>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", imprimirCupomSatReturn.GetResultado(), "OK");
                        break;
                    case IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE:
                        ImprimirCupomSatCancelamento imprimirCupomSatCancelamento = JsonConvert.DeserializeObject<ImprimirCupomSatCancelamento>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", imprimirCupomSatCancelamento.GetResultado(), "OK");
                        break;
                    case CONSULTAR_STATUS_REQUEST_CODE:
                        ConsultarStatus consultarStatusReturn = JsonConvert.DeserializeObject<ConsultarStatus>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", consultarStatusReturn.GetResultado(), "OK");
                        break;
                    case GET_TIMEOUT_REQUEST_CODE:
                        GetTimeout getTimeoutReturn = JsonConvert.DeserializeObject<GetTimeout>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", getTimeoutReturn.GetResultado(), "OK");
                        break;
                    case CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE:
                        ConsultarUltimaTransacao consultarUltimaTransacaoReturn = JsonConvert.DeserializeObject<ConsultarUltimaTransacao>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", consultarUltimaTransacaoReturn.GetResultado(), "OK");
                        break;
                    case SET_SENHA_SERVER_REQUEST_CODE:
                        SetSenhaServer setSenhaServerReturn = JsonConvert.DeserializeObject<SetSenhaServer>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", setSenhaServerReturn.GetResultado(), "OK");
                        break;
                    case SET_TIMEOUT_REQUEST_CODE:
                        SetTimeout setTimeoutReturn = JsonConvert.DeserializeObject<SetTimeout>(jsonObjectReturn.ToString());
                        DisplayAlert("Retorno E1 - BRIDGE", setTimeoutReturn.GetResultado(), "OK");
                        break;
                    default:
                        DisplayAlert("Alerta", "O comando " + requestCode + " não foi encontrado!", "OK");
                        break;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
                DisplayAlert("Alerta", "O retorno não está no formato esperado!", "OK");
            }
        }

        private void InitialValues()
        {
            //Valores iniciais
            ipEntry.Text = "192.168.0.104";
            valueEntry.Text = "2000";

            formaPagamentoSelecionada = FormaPagamento.CREDITO;
            formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_A_VISTA;

            //O padrão da aplicação é iniciar com a opção de pagamento por crédito com parcelamento a vista, portanto o número de parcelas deve ser obrigatoriamente 1
            numberOfInstallmentsEntry.Text = "1";
            numberOfInstallmentsEntry.IsEnabled = false;

            //Valores padrões para as portas
            transactionPortEntry.Text = "3000";
            statusPortEntry.Text = "3001";

            //Senha vazia
            passwordEntry.Text = "";
            passwordEntry.IsEnabled = false;
        }

        private void ViewsAtribuitions()
        {
            //No click do checkbox de envio de senha o campo de senha deve ser habilitado/desabilitado de acordo com o estado
            checkboxSendPassword.CheckedChanged += delegate(object sender, CheckedChangedEventArgs e)
            {
                passwordEntry.IsEnabled = e.Value;
            };

            //Na mudança das formas de pagamento além da redecoração das bordas deve ser habilitar/desabilitar as formas de financiamento
            btnCreditOptionBridge.Clicked += delegate {
                formaPagamentoSelecionada = FormaPagamento.CREDITO;
                UpdatePaymentMethodsBorderColors();
                FadeInInstallmentsOptionsLayout();
            };

            btnDebitOptionBridge.Clicked += delegate {
                formaPagamentoSelecionada = FormaPagamento.DEBITO;
                UpdatePaymentMethodsBorderColors();
                FadeOutInstallmentOptionsLayout();
            };

            //Na mudança das formas de parcelamento além da redecoração das bordas deve ser travado em 1 para a vista, ou destravado caso outra forma de parcelamento seja selecionada
            btnStoreOptionBridge.Clicked += delegate {
                formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO;
                UpdateInstallmentMethodBorderColors();
                numberOfInstallmentsEntry.IsEnabled = true;
            };

            btnAdmOptionBridge.Clicked += delegate
            {
                formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR;
                UpdateInstallmentMethodBorderColors();
                numberOfInstallmentsEntry.IsEnabled = true;
            };

            btnInCashOptionBridge.Clicked += delegate 
            {
                formaFinanciamentoSelecionada = FormaFinanciamento.FINANCIAMENTO_A_VISTA;
                UpdateInstallmentMethodBorderColors();
                numberOfInstallmentsEntry.Text = "1";
                numberOfInstallmentsEntry.IsEnabled = false;
            };

            btnSendTransaction.Clicked += delegate 
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
                                int numberOfInstallments = int.Parse(numberOfInstallmentsEntry.Text);

                                IniciaVendaCredito iniciaVendaCreditoCommand = new IniciaVendaCredito(GenerateRandomForBridgeTransactions(),
                                        PDV_NAME,
                                        GetEditTextValueBridgeFormatted(),
                                        (int)formaFinanciamentoSelecionada,
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
            };

            // A operacao de cancelameno de vende requer uma data, para fins de simplicação, usaremos apenas a data do mesmo dia
            // uma vez que as transações são fantasias
            btnCancelTransaction.Clicked += async delegate {
                //Data do dia atual, usada como um dos parâmetros necessário para o cancelamento de transação no Elgin Pay
                string todayDate = DateTime.Now.ToString("dd/MM/yyyy");

                // Alert com Entry para pegar o input do usuário na caixa de diálogo
                string saleRef = await DisplayPromptAsync("Alerta", "Código de Referência:", "OK", "CANCELAR", keyboard: Keyboard.Numeric);
                if (saleRef == null) return;

                if (saleRef.Equals(""))
                {
                    AlertMessageStatus("Alerta", "O campo código de referência da transação não pode ser vazio! Digite algum valor.");
                    return;
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
            };

            btnAdministrativeOperation.Clicked += async delegate {
                string[] operations = { "Operação Administrativa", "Operação de Instalação", "Operação de Configuração", "Operação de Manutenção", "Teste de Comunicação", "Reimpressão de Comprovante" };
                string option = await DisplayActionSheet("ESCOLHA A OPERAÇÃO ADMINISTRATIVA", "CANCELAR", null, operations);
                if (option == null) return;

                if (UpdateBridgeServer())
                {
                    //Neste caso o int index que é um parametro fornecido assim que uma opção é selecionada corresponde diretamente aos valores da documentação.
                    int index = Array.IndexOf(operations, option);

                    IniciaOperacaoAdministrativa iniciaOperacaoAdministrativaCommand = new IniciaOperacaoAdministrativa(GenerateRandomForBridgeTransactions(),
                            PDV_NAME,
                            index);

                    StartBridgeCommand(iniciaOperacaoAdministrativaCommand, INICIA_OPERACAO_ADMINISTRATIVA_REQUEST_CODE);
                }
            };

            btnPrintTestCoupon.Clicked += async delegate {
                //IniciaOperaçãoAdministrativa de acordo com qual operação foi selecionada.

                string[] couponTypes = { "Imprimir Cupom NFCe", "Imprimir Cupom Sat", "Imprimir Cupom Sat Cancelamento" };
                string option = await DisplayActionSheet("ESCOLHA O TIPO DE CUPOM", "CANCELAR", null, couponTypes);
                if (option == null) return;

                if (UpdateBridgeServer())
                {
                    //Variaveis para comparacao do tipo selecionado
                    const int NFCE_COUPON = 0;
                    const int SAT_COUPON = 1;
                    const int SAT_CANCELLATION_COUPON = 2;

                    int selected = Array.IndexOf(couponTypes, option);
                    switch (selected)
                    {
                        case NFCE_COUPON: {
                            //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                            activityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(XML_NFCE_ARCHIVE_NAME);

                            string xml = activityUtils.GetFilePathForIDH(XML_NFCE_ARCHIVE_NAME + XML_EXTENSION); 
                            int indexcsc = 1;
                            const string csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";

                            ImprimirCupomNfce imprimirCupomNfceCommand = new ImprimirCupomNfce(xml,
                                    indexcsc,
                                    csc);

                            StartBridgeCommand(imprimirCupomNfceCommand, IMPRIMIR_CUPOM_NFCE_REQUEST_CODE);
                            break;
                        }
                        case SAT_COUPON: {
                            //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                            activityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(XML_SAT_ARCHIVE_NAME);

                            string xml = activityUtils.GetFilePathForIDH(XML_SAT_ARCHIVE_NAME + XML_EXTENSION);

                            ImprimirCupomSat imprimirCupomSatCommand = new ImprimirCupomSat(xml);

                            StartBridgeCommand(imprimirCupomSatCommand, IMPRIMIR_CUPOM_SAT_REQUEST_CODE);
                            break;
                        }
                        case SAT_CANCELLATION_COUPON: {
                            //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
                            activityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(XML_SAT_CANCELLATION_ARCHIVE_NAME);

                            string xml = activityUtils.GetFilePathForIDH(XML_SAT_CANCELLATION_ARCHIVE_NAME + XML_EXTENSION);
                            string assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

                            ImprimirCupomSatCancelamento imprimirCupomSatCancelamentoCommand = new ImprimirCupomSatCancelamento(xml, assQRCode);

                            StartBridgeCommand(imprimirCupomSatCancelamentoCommand, IMPRIMIR_CUPOM_SAT_CANCELAMENTO_REQUEST_CODE);
                            break;
                        }
                    }
                }
            };

            btnGetTerminalStatus.Clicked += delegate {
                if (UpdateBridgeServer())
                {
                    ConsultarStatus consultarStatusCommand = new ConsultarStatus();

                    StartBridgeCommand(consultarStatusCommand, CONSULTAR_STATUS_REQUEST_CODE);
                }
            };

            btnGetConfiguredTimeout.Clicked += delegate {
                if (UpdateBridgeServer())
                {
                    GetTimeout getTimeoutCommand = new GetTimeout();

                    StartBridgeCommand(getTimeoutCommand, GET_TIMEOUT_REQUEST_CODE);
                }
            };

            btnGetLastTransaction.Clicked += delegate {
                if (UpdateBridgeServer())
                {
                    ConsultarUltimaTransacao consultarUltimaTransacaoCommand = new ConsultarUltimaTransacao(PDV_NAME);

                    StartBridgeCommand(consultarUltimaTransacaoCommand, CONSULTAR_ULTIMA_TRANSACAO_REQUEST_CODE);
                }
            };

            btnSetTransactionTimeout.Clicked += async delegate {
                string newTimeoutInSeconds = await DisplayPromptAsync("Alerta", "DEFINA UM NOVO TIMEOUT PARA TRANSAÇÃO (em segundos):", "OK", "CANCELAR", keyboard: Keyboard.Numeric);
                if (newTimeoutInSeconds == null) return;

                newTimeoutInSeconds = newTimeoutInSeconds.Trim();

                if (newTimeoutInSeconds.Equals(""))
                {
                    AlertMessageStatus("Alerta", "O campo que representa a quantidade timeout a ser configurado não pode ser vazio! Digite algum valor.");
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
            };

            btnSetTerminalPassword.Clicked += async delegate 
            {
                string[] enableOrDisable = { "Habilitar Senha no Terminal", "Desabilitar Senha no Terminal" };
                string option = await DisplayActionSheet("ESCOLHA COMO CONFIGURAR A SENHA", "CANCELAR", null, enableOrDisable);
                if (option == null) return;

                //De acordo com a opção escolhida no alert exterior, será definida se operacao irã habilitar ou desabilitar a senha
                bool enable = Array.IndexOf(enableOrDisable, option) == 0;

                if (enable)
                {
                    string passwordEntered = await DisplayPromptAsync("Alerta", "DIGITE A SENHA A SER HABILITADA:", "OK", "CANCELAR", keyboard: Keyboard.Text);
                    if (passwordEntered == null) return;
                    if (passwordEntered.Equals(""))
                    {
                        AlertMessageStatus("Alerta", "O campo de senha a ser habilitada não pode ser vazio!");
                    }
                    else
                    {
                        bool HABILITAR_SENHA_TERMINAL = true;

                        if (UpdateBridgeServer())
                        {
                            //Deve ser passado um string vazia para deletar a senha no terminal, pois é mais intuitivo desabilitar a senha atual e deleta-la do que desabilitar e atualizar com uma nova
                            SetSenhaServer setSenhaServerCommand = new SetSenhaServer(passwordEntered,
                                    HABILITAR_SENHA_TERMINAL);

                            StartBridgeCommand(setSenhaServerCommand, SET_SENHA_SERVER_REQUEST_CODE);
                        }
                    }
                }
                else if (!checkboxSendPassword.IsChecked)
                {
                    AlertMessageStatus("Alerta", "Habilite a opção de envio de senha e envie a senha mais atual para desabilitar a senha do terminal!");
                }
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
            };
        }

        //Atualiza a decoração da borda das opções de pagamento
        private void UpdatePaymentMethodsBorderColors()
        {
            btnCreditOptionBridge.BorderColor =
                    (formaPagamentoSelecionada == FormaPagamento.CREDITO) ?
                            Color.FromHex("23F600") :
                            Color.Black;

            btnDebitOptionBridge.BorderColor =
                    (formaPagamentoSelecionada == FormaPagamento.DEBITO) ?
                            Color.FromHex("23F600") :
                            Color.Black;
        }

        //Atualiza a decoração da borda das opções de parcelamento
        private void UpdateInstallmentMethodBorderColors()
        {
            btnStoreOptionBridge.BorderColor =
                    (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO) ?
                            Color.FromHex("23F600") :
                            Color.Black;

            btnAdmOptionBridge.BorderColor =
                    (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_PARCELADO_EMISSOR) ?
                            Color.FromHex("23F600") :
                            Color.Black;

            btnInCashOptionBridge.BorderColor =
                    (formaFinanciamentoSelecionada == FormaFinanciamento.FINANCIAMENTO_A_VISTA) ?
                            Color.FromHex("23F600") :
                            Color.Black;
        }

        //Desalibita as opções de parcelamento, caso a opção de débito seja selecionada
        private void FadeOutInstallmentOptionsLayout()
        {
            stackParcelas.IsVisible = false;
            stackTypeInstallments.IsVisible = false;
        }

        //Habilita as opções de parcelamento, caso a opção de crédito seja selecionada
        private void FadeInInstallmentsOptionsLayout()
        {
            stackParcelas.IsVisible = true;
            stackTypeInstallments.IsVisible = true;
        }

        //Retorna o valor inserido no formato necessário para o pagemento elgin pay, o valor deve ser inserido em centavos, a vírgula é removida
        private string GetEditTextValueBridgeFormatted()
        {
            return valueEntry.Text.Replace(",", "").Trim();
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
            string IP = ipEntry.Text;

            Regex regexIP = new Regex(@"^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

            bool isIpValid = regexIP.Match(IP).Success;

            if (isIpValid) return true;

            AlertMessageStatus("Alerta", "Insira um IP válido para a conexão Bridge!");
            return false;
        }

        //Valida a porta de transacao
        private bool IsTransactionPortValid()
        {
            try
            {
                int transactionPortInInt = int.Parse(transactionPortEntry.Text);

                if (transactionPortInInt > 65535)
                {
                    AlertMessageStatus("Alerta", "O valor inserido na porta de transação excede o limite esbelecido de 65535!");
                    return false;
                }
                else return true;
            }
            catch (FormatException)
            {
                AlertMessageStatus("Alerta", "O valor inserido na porta de transação não pode estar vazio");
                return false;
            }
        }

        //Valida a porta de status
        private bool IsStatusPortValid()
        {
            try
            {
                int statusPortInInt = int.Parse(statusPortEntry.Text);

                if (statusPortInInt > 65535)
                {
                    AlertMessageStatus("Alerta", "O valor inserido na porta de status excede o limite esbelecido de 65535!");
                    return false;
                }
                else return true;
            }
            catch (FormatException)
            {
                AlertMessageStatus("Alerta", "O valor inserido na porta de status não pode estar vazio!");
                return false;
            }
        }

        private bool ValidateInstallmentsField()
        {
            string numberOfInstallments = numberOfInstallmentsEntry.Text;

            if (numberOfInstallments.Equals(""))
            {
                AlertMessageStatus("Alerta", "O campo de parcelas não pode estar vazio!");
                return false;
            }
            else if ((int.Parse(numberOfInstallmentsEntry.Text) < 2) && formaFinanciamentoSelecionada != FormaFinanciamento.FINANCIAMENTO_A_VISTA)
            {
                AlertMessageStatus("Alerta", "O número de parcelas não é valido para esta forma de financiamento");
                return false;
            }

            return true;
        }

        // O valor mínimo para a transação do elgin pay é de R$ 1,00
        private bool IsValueValidToElginPay()
        {
            string s = valueEntry.Text.Replace(",", ".").Trim();
            double value = double.Parse(s);
            bool isValid = value >= 1.00;
            if (!isValid)
            {
                AlertMessageStatus("Alerta", "O valor mínimo para a transação é de R$1.00!");
            }
            return isValid;
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
                new SetServer(ipEntry.Text, int.Parse(transactionPortEntry.Text), int.Parse(statusPortEntry.Text)),

                //Adiciciona o comando de configuração da senha SetSenha
                new SetSenha(passwordEntry.Text, checkboxSendPassword.IsChecked),

                //Adiciona o comando atual
                bridgeCommand
            };

            //Inicial a atividade através da classe utilitaŕia
            digitalHubCommandStarter.StartHubCommandActivity(listOfCommands, requestCode);
        }

        //Gera número aleatório entre 0 e 999999 para as transações bridge
        private static int GenerateRandomForBridgeTransactions()
        {
            Random random = new Random();
            return random.Next(1000000);
        }

        private async void AlertMessageStatus(string v, string resultadoOperacao)
        {
            await DisplayAlert(v, resultadoOperacao, "OK");
        }
    }
}