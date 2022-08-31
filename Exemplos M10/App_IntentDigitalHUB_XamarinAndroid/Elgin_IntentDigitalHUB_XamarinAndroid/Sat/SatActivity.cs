using Android.App;
using Android.Content;
using Android.OS;
using Android.Support.V7.App;
using Android.Util;
using Android.Widget;
using Xamarin_Android_Intent_Digital_Hub.IntentServices;
using Xamarin_Android_Intent_Digital_Hub.IntentServices.Sat;
using Newtonsoft.Json;
using Org.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using Environment = System.Environment;

namespace Xamarin_Android_Intent_Digital_Hub.Sat
{
    [Activity(Label = "SatActivity")]
    public class SatActivity : AppCompatActivity
    {
        private const int ATIVAR_SAT_REQUESTCODE = 1;
        private const int ASSOCIAR_ASSINATURA_REQUESTCODE = 2;
        private const int CONSULTAR_SAT_REQUESTCODE = 3;
        private const int CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE = 4;
        private const int ENVIAR_DADOS_VENDA_REQUESTCODE = 5;
        private const int CANCELAR_ULTIMA_VENDA_REQUESTCODE = 6;
        private const int EXTRAIR_LOGS_REQUESTCODE = 7;

        //Nome do arquivo utilizado para fazer o cancelamento de venda, no drietório res/raw/
        private const string XML_CANCELLATION_ARCHIVE_NAME = "sat_cancelamento";
        private const string XML_EXTENSION = ".xml";

        //Views
        private TextView textRetorno;
        private EditText editTextInputCodeAtivacao;
        private RadioGroup radioGroupModelsSAT;
        private RadioButton radioButtonSMARTSAT;
        private Button buttonConsultarSAT;
        private Button buttonConsultarStatusOperacionalSAT;
        private Button buttonRealizarVendaSAT;
        private Button buttonCancelamentoSAT;
        private Button buttonAtivarSAT;
        private Button buttonAssociarSAT;
        private Button buttonExtrairLogSat;

        //Váriavel utilizada para fazer a substituição da tag CFE, necessária para a montagem do xml de cancelamento
        private string cfeCancelamento = "";

        //Modelo de SAT selecionado
        private SatModel selectedSatModel = SatModel.SMART_SAT;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_sat);

            // Create your application here

            textRetorno = FindViewById<TextView>(Resource.Id.textRetorno);

            radioGroupModelsSAT = FindViewById<RadioGroup>(Resource.Id.radioGroupModelsSAT);
            radioButtonSMARTSAT = FindViewById<RadioButton>(Resource.Id.radioButtonSMARTSAT);

            editTextInputCodeAtivacao = FindViewById<EditText>(Resource.Id.editTextInputCodeAtivacao);
            editTextInputCodeAtivacao.Text = "123456789";

            buttonConsultarSAT = FindViewById<Button>(Resource.Id.buttonConsultarSAT);
            buttonConsultarStatusOperacionalSAT = FindViewById<Button>(Resource.Id.buttonConsultarStatusOperacionalSAT);
            buttonRealizarVendaSAT = FindViewById<Button>(Resource.Id.buttonRealizarVendaSAT);
            buttonCancelamentoSAT = FindViewById<Button>(Resource.Id.buttonCancelamentoSAT);
            buttonAtivarSAT = FindViewById<Button>(Resource.Id.buttonAtivarSAT);
            buttonAssociarSAT = FindViewById<Button>(Resource.Id.buttonAssociarSAT);
            buttonExtrairLogSat = FindViewById<Button>(Resource.Id.buttonExtrairLogSat);

            //Modelo do SAT escolhido inicialmente e funcionalidade do radioButton de seleção
            radioButtonSMARTSAT.Checked = true;

            radioGroupModelsSAT.CheckedChange += (group, args) => {
                selectedSatModel = (args.CheckedId == Resource.Id.radioButtonSMARTSAT) ? SatModel.SMART_SAT : SatModel.SAT_GO;
            };

            buttonAtivarSAT.Click += ButtonAtivarSATFunction;

            buttonAssociarSAT.Click += ButtonAssociarSatFunction;

            buttonConsultarSAT.Click += ButtonConsultarSATFunction;

            buttonConsultarStatusOperacionalSAT.Click += ButtonConsultarStatusOperacionalSATFunction;

            buttonRealizarVendaSAT.Click += ButtonRealizarVendaSATFunction;

            buttonCancelamentoSAT.Click += ButtonCancelamentoSATFunction;

            buttonExtrairLogSat.Click += ButtonExtrairLogSatFunction;
        }

        private void ButtonAtivarSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            int subComando = 2;
            string codAtivacao = editTextInputCodeAtivacao.Text;
            string cnpj = "14200166000166";
            int cUF = 15;

            AtivarSAT ativarSatCommand = new AtivarSAT(numSessao,
                    subComando,
                    codAtivacao,
                    cnpj,
                    cUF);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, ativarSatCommand, ATIVAR_SAT_REQUESTCODE);
        }

        private void ButtonAssociarSatFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = editTextInputCodeAtivacao.Text;
            string cnpjSh = "16716114000172";
            string assinaturaAC = "SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT";

            AssociarAssinatura associarAssinaturaCommand = new AssociarAssinatura(numSessao,
                    codAtivacao,
                    cnpjSh,
                    assinaturaAC);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, associarAssinaturaCommand, ASSOCIAR_ASSINATURA_REQUESTCODE);
        }

        private void ButtonConsultarSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();

            ConsultarSAT consultarSATCommand = new ConsultarSAT(numSessao);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, consultarSATCommand, CONSULTAR_SAT_REQUESTCODE);
        }

        private void ButtonConsultarStatusOperacionalSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = editTextInputCodeAtivacao.Text;

            ConsultarStatusOperacional consultarStatusOperacionalCommand = new ConsultarStatusOperacional(numSessao, codAtivacao);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, consultarStatusOperacionalCommand, CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE);
        }

        private void ButtonRealizarVendaSATFunction(object sender, EventArgs args)
        {
            //Como uma nova venda será realizada, o cfeCancelamento utilizado para cancelamento deve ser sobrescrito
            cfeCancelamento = "";

            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = editTextInputCodeAtivacao.Text;

            //O envio de venda SAT será realizo por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
            ActivityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(this, selectedSatModel.SALE_XML_ARCHIVE_NAME);
            string dadosVenda = ActivityUtils.GetFilePathForIDH(this, selectedSatModel.SALE_XML_ARCHIVE_NAME + XML_EXTENSION);

            EnviarDadosVenda enviarDadosVendaCommand = new EnviarDadosVenda(numSessao, codAtivacao, dadosVenda);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, enviarDadosVendaCommand, ENVIAR_DADOS_VENDA_REQUESTCODE);
        }

        private void ButtonCancelamentoSATFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = editTextInputCodeAtivacao.Text;
            string numeroCFe = cfeCancelamento;

            if (cfeCancelamento.Length == 0)
            {
                ActivityUtils.ShowAlertMessage(this, "Alerta", "Não foi feita uma venda para cancelar!");
                return;
            }

            string dadosCancelamento = GenerateXmlForSatCancellation();
            CancelarUltimaVenda cancelarUltimaVendaCommand = new CancelarUltimaVenda(numSessao, codAtivacao, numeroCFe, dadosCancelamento);
            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, cancelarUltimaVendaCommand, CANCELAR_ULTIMA_VENDA_REQUESTCODE);
        }

        /**
         * Utiliza o XML em res/raw/sat_cancelamento como base para gerar um XML de cancelamento de venda SAT
         *
         * @return String já formatada para envio no JSON de comando
        */
        private String GenerateXmlForSatCancellation()
        {
            //Lẽ o XMl base usado para cancelamento de venda SAT
            String baseXmlForCacellation = ActivityUtils.ReadXmlFileAsString(this, XML_CANCELLATION_ARCHIVE_NAME);
            //Troca o valor do cfe do XMl base pelo valor do cfeCancelamento mais atual e formata a String com os escapes necessários para o funcionamento
            return baseXmlForCacellation.Replace("novoCFe", cfeCancelamento).Replace("\"", "\\\"");
        }

        private void ButtonExtrairLogSatFunction(object sender, EventArgs args)
        {
            int numSessao = GenerateNumberForSatSession();
            string codAtivacao = editTextInputCodeAtivacao.Text;

            ExtrairLogs extrairLogsCommand = new ExtrairLogs(numSessao, codAtivacao);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, extrairLogsCommand, EXTRAIR_LOGS_REQUESTCODE);
        }

        //Gera número aleatório para diferenciar as sessões com o dispositivo
        private int GenerateNumberForSatSession()
        {
            return new Random().Next(1_000_000);
        }

        protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);

            if (resultCode == Result.Ok)
            {
                string retorno = data.GetStringExtra("retorno");
                Log.Debug("retorno", retorno);
                /**
                 * No módulo SAT apenas um comando é executa por vez, portanto o retorno do comando mais recente está sempre na primeira posição do arrayJSON de retorno
                */
                try
                {
                    JSONArray jsonArray = new JSONArray(retorno);
                    JSONObject jsonObjectReturn = jsonArray.GetJSONObject(0);

                    switch (requestCode)
                    {
                        case ATIVAR_SAT_REQUESTCODE:
                            AtivarSAT ativarSATCommand = JsonConvert.DeserializeObject<AtivarSAT>(jsonObjectReturn.ToString());
                            textRetorno.Text = ativarSATCommand.GetResultado();
                            break;
                        case ASSOCIAR_ASSINATURA_REQUESTCODE:
                            AssociarAssinatura associarAssinaturaCommand = JsonConvert.DeserializeObject<AssociarAssinatura>(jsonObjectReturn.ToString());
                            textRetorno.Text = associarAssinaturaCommand.GetResultado();
                            break;
                        case CONSULTAR_SAT_REQUESTCODE:
                            ConsultarSAT consultarSATCommand = JsonConvert.DeserializeObject<ConsultarSAT>(jsonObjectReturn.ToString());
                            textRetorno.Text = consultarSATCommand.GetResultado();
                            break;
                        case CONSULTAR_STATUS_OPERACIONAL_REQUESTCODE:
                            ConsultarStatusOperacional consultarStatusOperacionalCommand = JsonConvert.DeserializeObject<ConsultarStatusOperacional>(jsonObjectReturn.ToString());
                            textRetorno.Text = consultarStatusOperacionalCommand.GetResultado();
                            break;
                        case ENVIAR_DADOS_VENDA_REQUESTCODE:
                            EnviarDadosVenda enviarDadosVendaCommand = JsonConvert.DeserializeObject<EnviarDadosVenda>(jsonObjectReturn.ToString());
                            textRetorno.Text = enviarDadosVendaCommand.GetResultado();

                            //Se a venda ocorreu com sucesso, atualizar o cfe de cancelamento
                            List<string> saleReturn = new List<string>(enviarDadosVendaCommand.GetResultado().Split("|"));

                            if (saleReturn.Count > 8) {
                                cfeCancelamento = saleReturn[8];
                            }

                            break;
                        case CANCELAR_ULTIMA_VENDA_REQUESTCODE:
                            CancelarUltimaVenda cancelarUltimaVendaCommand = JsonConvert.DeserializeObject<CancelarUltimaVenda>(jsonObjectReturn.ToString());
                            textRetorno.Text = (cancelarUltimaVendaCommand.GetResultado());
                            break;
                        case EXTRAIR_LOGS_REQUESTCODE:
                            ExtrairLogs extrairLogsCommand = JsonConvert.DeserializeObject<ExtrairLogs>(jsonObjectReturn.ToString());

                            /*
                             Se o dispositivo não tiver sido encontrado, simplesmente exiba o retorno 'DeviceNotFound' na tela
                             caso contrário, indique que o log foi salvo no caminho
                            */

                            if (extrairLogsCommand.GetResultado().Equals("DeviceNotFound"))
                            textRetorno.Text = (extrairLogsCommand.GetResultado());
                            else {
                                textRetorno.Text = "Log SAT salvo em " + extrairLogsCommand.GetResultado();
                            }
                            break;
                        default:
                            ActivityUtils.ShowAlertMessage(this, "Alerta", "Código de comando não encontrado");
                            break;
                    }
                } catch (JSONException e)
                {
                    e.PrintStackTrace();
                    ActivityUtils.ShowAlertMessage(this, "Alerta", "O retorno não está no formato esperado!");
                }
            }
        }
    }
}