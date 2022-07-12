using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices.Sat;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Xamarin_Forms_Intent_Digital_Hub.Utils;
using Newtonsoft.Json.Linq;

namespace Xamarin_Forms_Intent_Digital_Hub.Sat
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class SatPage : ContentPage
    {
        //Código da intent do pedido de permissão para escrever arquivos no diretório externo
        private const int ATIVAR_SAT_REQUESTCODE = 91;
        private const int ASSOCIAR_ASSINATURA_REQUESTCODE = 92;
        private const int CONSULTAR_SAT_REQUESTCODE = 93;
        private const int CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE = 94;
        private const int ENVIAR_DADOS_VENDA_REQUESTCODE = 95;
        private const int CANCELAR_ULTIMA_VENDA_REQUESTCODE = 96;
        private const int EXTRAIR_LOGS_REQUESTCODE = 97;

        //Nome do arquivo utilizado para fazer o cancelamento de venda, no drietório res/raw/
        private const string XML_CANCELLATION_ARCHIVE_NAME = "sat_cancelamento";
        private const string XML_EXTENSION = ".xml";

        //Váriavel utilizada para fazer a substituição da tag CFE, necessária para a montagem do xml de cancelamento
        private string cfeCancelamento = "";

        //Modelo de SAT selecionado
        private SatModel selectedSatModel = SatModel.SMART_SAT;

        private readonly IActivityUtils activityUtils = DependencyService.Get<IActivityUtils>();

        // Serviço de execução de comandos via intent
        private readonly IIntentDigitalHubCommandStarter digitalHubCommandStarter = DependencyService.Get<IIntentDigitalHubCommandStarter>();

        public SatPage()
        {
            InitializeComponent();

            entryCodigoAtivacao.Text = "123456789";

            //Modelo do SAT escolhido inicialmente e funcionalidade do radioButton de seleção
            radioSmartSAT.IsChecked = true;

            radioSmartSAT.CheckedChanged += (group, args) =>
            {
                selectedSatModel = args.Value ? SatModel.SMART_SAT : SatModel.SAT_GO;
            };

            // Escutar eventos emitidos por resultados de intent
            MessagingCenter.Unsubscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result");
            MessagingCenter.Subscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result", HandleIntentResult);

            buttonAtivarSAT.Clicked += ButtonAtivarSATFunction;

            buttonAssociarSAT.Clicked += ButtonAssociarSatFunction;

            buttonConsultarSAT.Clicked += ButtonConsultarSATFunction;

            buttonConsultarStatusOperacionalSAT.Clicked += ButtonConsultarStatusOperacionalSATFunction;

            buttonRealizarVendaSAT.Clicked += ButtonRealizarVendaSATFunction;

            buttonCancelamentoSAT.Clicked += ButtonCancelamentoSATFunction;

            buttonExtrairLogSat.Clicked += ButtonExtrairLogSatFunction;
        }

        private void HandleIntentResult(object sender, Tuple<int, string> resposta)
        {
            int requestCode = resposta.Item1;
            JArray jsonArray = JsonConvert.DeserializeObject<JArray>(resposta.Item2);
            string jsonObjectReturn = jsonArray[0].ToString();
            /**
             * No módulo SAT apenas um comando é executa por vez, portanto o retorno do comando mais recente está sempre na primeira posição do arrayJSON de retorno
             */
            try
            {
                switch (requestCode)
                {
                    case ATIVAR_SAT_REQUESTCODE:
                        AtivarSAT ativarSATCommand = JsonConvert.DeserializeObject<AtivarSAT>(jsonObjectReturn.ToString());
                        txtRetornoSat.Text = ativarSATCommand.GetResultado();
                        break;
                    case ASSOCIAR_ASSINATURA_REQUESTCODE:
                        AssociarAssinatura associarAssinaturaCommand = JsonConvert.DeserializeObject<AssociarAssinatura>(jsonObjectReturn.ToString());
                        txtRetornoSat.Text = associarAssinaturaCommand.GetResultado();
                        break;
                    case CONSULTAR_SAT_REQUESTCODE:
                        ConsultarSAT consultarSATCommand = JsonConvert.DeserializeObject<ConsultarSAT>(jsonObjectReturn.ToString());
                        txtRetornoSat.Text = consultarSATCommand.GetResultado();
                        break;
                    case CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE:
                        ConsultarStatusOperacional consultarStatusOperacionalCommand = JsonConvert.DeserializeObject<ConsultarStatusOperacional>(jsonObjectReturn.ToString());
                        txtRetornoSat.Text = consultarStatusOperacionalCommand.GetResultado();
                        break;
                    case ENVIAR_DADOS_VENDA_REQUESTCODE:
                        EnviarDadosVenda enviarDadosVendaCommand = JsonConvert.DeserializeObject<EnviarDadosVenda>(jsonObjectReturn.ToString());
                        txtRetornoSat.Text = enviarDadosVendaCommand.GetResultado();

                        //Se a venda ocorreu com sucesso, atualizar o cfe de cancelamento
                        List<string> saleReturn = new List<string>(enviarDadosVendaCommand.GetResultado().Split('|'));

                        if (saleReturn.Count > 8)
                        {
                            cfeCancelamento = saleReturn[8];
                        }

                        break;
                    case CANCELAR_ULTIMA_VENDA_REQUESTCODE:
                        CancelarUltimaVenda cancelarUltimaVendaCommand = JsonConvert.DeserializeObject<CancelarUltimaVenda>(jsonObjectReturn.ToString());
                        txtRetornoSat.Text = cancelarUltimaVendaCommand.GetResultado();
                        break;
                    case EXTRAIR_LOGS_REQUESTCODE:
                        ExtrairLogs extrairLogsCommand = JsonConvert.DeserializeObject<ExtrairLogs>(jsonObjectReturn.ToString());

                        /*
                        Se o dispositivo não tiver sido encontrado, simplesmente exiba o retorno 'DeviceNotFound' na tela
                        caso contrário, indique que o log foi salvo no caminho
                        */

                        if (extrairLogsCommand.GetResultado().Equals("DeviceNotFound"))
                            txtRetornoSat.Text = extrairLogsCommand.GetResultado();
                        else
                        {
                            //Apresenta na box a mensagem indicando o diretório onde foi salvo
                            txtRetornoSat.Text = "Log Sat salvo em " + extrairLogsCommand.GetResultado();
                        }
                        break;
                    default:
                        DisplayAlert("Alerta", "Código de comando não encontrado", "OK");
                        break;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
                DisplayAlert("Alerta", "O retorno não está no formato esperado!", "OK");
            }
        }

        private void ButtonAtivarSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            int subComando = 2;
            string codAtivacao = entryCodigoAtivacao.Text;
            string cnpj = "14200166000166";
            int cUF = 15;

            AtivarSAT ativarSatCommand = new AtivarSAT(numSessao,
                    subComando,
                    codAtivacao,
                    cnpj,
                    cUF);

            digitalHubCommandStarter.StartHubCommandActivity(ativarSatCommand, ATIVAR_SAT_REQUESTCODE);
        }

        private void ButtonAssociarSatFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = entryCodigoAtivacao.Text;
            string cnpjSh = "16716114000172";
            string assinaturaAC = "SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT";

            AssociarAssinatura associarAssinaturaCommand = new AssociarAssinatura(numSessao,
                    codAtivacao,
                    cnpjSh,
                    assinaturaAC);

            digitalHubCommandStarter.StartHubCommandActivity(associarAssinaturaCommand, ASSOCIAR_ASSINATURA_REQUESTCODE);
        }

        private void ButtonConsultarSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();

            ConsultarSAT consultarSATCommand = new ConsultarSAT(numSessao);

            digitalHubCommandStarter.StartHubCommandActivity(consultarSATCommand, CONSULTAR_SAT_REQUESTCODE);
        }

        private void ButtonConsultarStatusOperacionalSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = entryCodigoAtivacao.Text;

            ConsultarStatusOperacional consultarStatusOperacionalCommand = new ConsultarStatusOperacional(numSessao, codAtivacao);

            digitalHubCommandStarter.StartHubCommandActivity(consultarStatusOperacionalCommand, CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE);
        }

        private void ButtonRealizarVendaSATFunction(object sender, EventArgs args)
        {
            //Como uma nova venda será realizada, o cfeCancelamento utilizado para cancelamento deve ser subescrito
            cfeCancelamento = "";

            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = entryCodigoAtivacao.Text;

            //O envio de venda SAT será realizo por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
            activityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(selectedSatModel.SALE_XML_ARCHIVE_NAME);
            string dadosVenda = activityUtils.GetFilePathForIDH(selectedSatModel.SALE_XML_ARCHIVE_NAME + XML_EXTENSION);

            EnviarDadosVenda enviarDadosVendaCommand = new EnviarDadosVenda(numSessao, codAtivacao, dadosVenda);

            digitalHubCommandStarter.StartHubCommandActivity(enviarDadosVendaCommand, ENVIAR_DADOS_VENDA_REQUESTCODE);
        }

        private void ButtonCancelamentoSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = entryCodigoAtivacao.Text;
            string numeroCFe = cfeCancelamento;

            if (cfeCancelamento.Length == 0)
            {
                DisplayAlert("Alerta", "Não foi feita uma venda para cancelar!", "OK");
                return;
            }

            string dadosCancelamento = GenerateXmlForSatCancellation();
            CancelarUltimaVenda cancelarUltimaVendaCommand = new CancelarUltimaVenda(numSessao, codAtivacao, numeroCFe, dadosCancelamento);
            digitalHubCommandStarter.StartHubCommandActivity(cancelarUltimaVendaCommand, CANCELAR_ULTIMA_VENDA_REQUESTCODE);
        }

        /**
         * Utiliza o XML em res/raw/sat_cancelamento como base para gerar um XML de cancelamento de venda SAT
         *
         * @return String já formatada para envio no JSON de comando
        */
        private string GenerateXmlForSatCancellation()
        {
            //Lẽ o XMl base usado para cancelamento de venda SAT
            String baseXmlForCacellation = activityUtils.ReadXmlFileAsString(XML_CANCELLATION_ARCHIVE_NAME);
            //Troca o valor do cfe do XMl base pelo valor do cfeCancelamento mais atual e formata a String com os escapes necessários para o funcionamento
            return baseXmlForCacellation.Replace("novoCFe", cfeCancelamento).Replace("\"", "\\\"");
        }

        private void ButtonExtrairLogSatFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = entryCodigoAtivacao.Text;

            ExtrairLogs extrairLogsCommand = new ExtrairLogs(numSessao, codAtivacao);

            digitalHubCommandStarter.StartHubCommandActivity(extrairLogsCommand, EXTRAIR_LOGS_REQUESTCODE);
        }

        //Gera número aleatório para diferenciar as sessões com o dispositivo
        private int GenerateNumberForSatSession()
        {
            return new Random().Next(1_000_000);
        }
    }
}