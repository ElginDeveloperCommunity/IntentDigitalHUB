using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Org.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices.Balanca.Commands;
using Xamarin_Forms_Intent_Digital_Hub.Utils;

namespace Xamarin_Forms_Intent_Digital_Hub.Balance
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class BalancPage : ContentPage
    {
        //Constantes utilizadas para o filtro do retorno da intent em @OnActivityResult
        private const int CONFIGURAR_BALANCA_REQUESTCODE = 1;
        private const int LER_PESO_REQUESTCODE = 2;

        private BalanceModel selectedBalanceModel = BalanceModel.DP3005;
        private BalanceProtocol selectedBalanceProtocol = BalanceProtocol.PROTOCOL_0;

        string typeProtocol = "PROTOCL 0";
        string selectedNumProtocl = "";
        string typeModel = "DP30CK";
        int protocol;

        private readonly IActivityUtils activityUtils = DependencyService.Get<IActivityUtils>();

        // Serviço de execução de comandos via intent
        private readonly IIntentDigitalHubCommandStarter digitalHubCommandStarter = DependencyService.Get<IIntentDigitalHubCommandStarter>();

        public BalancPage()
        {
            InitializeComponent();

            buttonConfigurarBalanca.Clicked += ButtonConfigurarBalanca;
            buttonLerPeso.Clicked += ButtonLerPeso;

            DP3005Radio.CheckedChanged += BalanceModelsChanged;
            SA110Radio.CheckedChanged += BalanceModelsChanged;
            DPSCRadio.CheckedChanged += BalanceModelsChanged;
            DP3005Radio.CheckedChanged += BalanceModelsChanged;

            //externalPrinterUsbRadio.CheckedChanged += PrinterConectionChanged;

            // Escutar eventos emitidos por resultados de intent
            MessagingCenter.Unsubscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result");
            MessagingCenter.Subscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result", HandleIntentResult);

        }

        private void ButtonLerPeso(object sender, EventArgs e)
        {
            AbrirSerial abrirSerialCommand = new AbrirSerial(2400, 8, 'N', 1);

            LerPeso lerPesoCommand = new LerPeso(1);

            Fechar fecharCommand = new Fechar();

            List<IntentDigitalHubCommand> balancaCommandList = new List<IntentDigitalHubCommand>();

            balancaCommandList.Add(abrirSerialCommand);
            balancaCommandList.Add(lerPesoCommand);
            balancaCommandList.Add(fecharCommand);

            digitalHubCommandStarter.StartHubCommandActivity(balancaCommandList, LER_PESO_REQUESTCODE);
        }

        private void ButtonConfigurarBalanca(object sender, EventArgs args)
        {
            Console.WriteLine(typeProtocol);
            Console.WriteLine(textReturnValueBalanca.Text);

            ConfigurarModeloBalanca configurarModeloBalancaCommand = new ConfigurarModeloBalanca(selectedBalanceModel.getBalanceCode());
            Console.WriteLine("balanceModel", selectedBalanceModel.getBalanceCode().ToString());

            ConfigurarProtocoloComunicacao configurarProtocoloComunicacaoCommand = new ConfigurarProtocoloComunicacao(protocol);
            Console.WriteLine("protocol", protocol.ToString());

            List<IntentDigitalHubCommand> balancaCommandList = new List<IntentDigitalHubCommand>();

            balancaCommandList.Add(configurarModeloBalancaCommand);
            balancaCommandList.Add(configurarProtocoloComunicacaoCommand);

            digitalHubCommandStarter.StartHubCommandActivity(balancaCommandList, CONFIGURAR_BALANCA_REQUESTCODE);
        }


        private void pckProtocol_SelectedIndexChanged(object sender, EventArgs e)
        {
            var itemSelecionado = pckProtocol.Items[pckProtocol.SelectedIndex];
            
            typeProtocol = itemSelecionado;
            Console.WriteLine(typeProtocol);

            switch (itemSelecionado) 
            {
                case "PROTOCOL 0":
                    protocol = 0;
                    break;
                case "PROTOCOL 1":
                    protocol = 1;
                    break;
                case "PROTOCOL 2":
                    protocol = 2;
                    break;
                case "PROTOCOL 3":
                    protocol = 3;
                    break;
                case "PROTOCOL 4":
                    protocol = 4;
                    break;
                case "PROTOCOL 5":
                    protocol = 5;
                    break;
                case "PROTOCOL 6":
                    protocol = 6;
                    break;
                case "PROTOCOL 7":
                    protocol = 7;
                    break;
                default:
                    protocol = 0;
                    break;
            }

            Console.WriteLine(protocol);
        }

        private void BalanceModelsChanged(object sender, CheckedChangedEventArgs e)
        {
            RadioButton rb = sender as RadioButton;

            if (rb == DP3005Radio)
            {
                selectedBalanceModel = BalanceModel.DP3005;
            }else if (rb == SA110Radio)
            {
                selectedBalanceModel = BalanceModel.SA110;
                Console.WriteLine(selectedBalanceModel.ToString());
            }else if (rb == DPSCRadio)
            {
                selectedBalanceModel = BalanceModel.DPSC;
            }
            else if (rb == DP30CKRadio)
            {
                selectedBalanceModel = BalanceModel.DP30CK;
            }
        }

        private void HandleIntentResult(object sender, Tuple<int, string> resposta)
        {
            int requestCode = resposta.Item1;
            JArray jsonArray = JsonConvert.DeserializeObject<JArray>(resposta.Item2);
            string jsonObjectReturn = jsonArray[0].ToString();

            try
            {
                switch (requestCode)
                {
                    case CONFIGURAR_BALANCA_REQUESTCODE:
                        string configurarModeloBalancaJson = jsonArray[0].ToString();
                        string configurarProtocoloComunicacaoJson = jsonArray[1].ToString();

                        ConfigurarModeloBalanca configurarModeloBalancaReturn = JsonConvert.DeserializeObject<ConfigurarModeloBalanca>(configurarModeloBalancaJson.ToString());
                        ConfigurarProtocoloComunicacao configurarProtocoloComunicacaoReturn = JsonConvert.DeserializeObject<ConfigurarProtocoloComunicacao>(configurarProtocoloComunicacaoJson.ToString());

                        DisplayAlert(
                            "", "ConfigurarModeloBalanca: " + configurarModeloBalancaReturn.GetResultado().ToString()+"\n"+ 
                            "ConfigurarProtocoloComunicacao: "+ configurarProtocoloComunicacaoReturn.GetResultado().ToString(),
                            "OK"
                            );
                        break;

                    case LER_PESO_REQUESTCODE:
                        string abrirSerialReturn = jsonArray[0].ToString();
                        string lerSerialReturn = jsonArray[1].ToString();
                        string fecharSerialReturn = jsonArray[2].ToString();

                        AbrirSerial abrirSerial = JsonConvert.DeserializeObject<AbrirSerial>(abrirSerialReturn.ToString());
                        LerPeso lerrSerial = JsonConvert.DeserializeObject<LerPeso>(lerSerialReturn.ToString());
                        Fechar fecharSerial = JsonConvert.DeserializeObject<Fechar>(fecharSerialReturn.ToString());

                        DisplayAlert(
                            "", "AbrirSerial: " + abrirSerial.GetResultado().ToString() + "\n" +
                            "LerPeso: " + lerrSerial.GetResultado().ToString() + "\n" +
                            "LerPeso: " + lerrSerial.GetResultado().ToString(),
                            "OK"
                            );

                        Double weightRead = Convert.ToDouble(lerrSerial.GetResultado());

                        if (weightRead > 0.00)
                        {
                            string result = Convert.ToString(weightRead / 1000);
                            textReturnValueBalanca.Text = result;
                        }
                        break;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
                DisplayAlert("Alerta", "O Comando não foi bem sucedido!", "OK");
            }
            
        }

    }
}