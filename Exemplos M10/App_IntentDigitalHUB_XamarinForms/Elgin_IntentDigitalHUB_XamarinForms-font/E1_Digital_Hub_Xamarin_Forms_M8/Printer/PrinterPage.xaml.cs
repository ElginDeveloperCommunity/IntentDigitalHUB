using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica;
using Xamarin_Forms_Intent_Digital_Hub.Printer.Views;
using Newtonsoft.Json;
using System;
using System.Text.RegularExpressions;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Newtonsoft.Json.Linq;

namespace Xamarin_Forms_Intent_Digital_Hub.Printer
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class PrinterPage : ContentPage
    {
        public const int ABRE_CONEXAO_IMPRESSORA_REQUESTCODE = 15;
        public const int ABRE_CONEXAO_IMPRESSORA_USB_REQUESTCODE = 16;
        public const int ABRE_CONEXAO_IMPRESSORA_IP_REQUESTCODE = 17;
        public const int FECHA_CONEXAO_IMPRESSORA_REQUESTCDOE = 18;
        public const int STATUS_IMPRESSORA_REQUESTCODE = 19;
        public const int STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE = 20;
        public const int ABRE_GAVETA_ELGIN_REQUESTCODE = 21;
        public const int IMPRESSAO_TEXTO_REQUESTCODE = 22;
        public const int IMPRIME_XML_NFCE_REQUESTCODE = 23;
        public const int IMPRIME_XML_SAT_REQUESTCODE = 24;
        public const int IMPRESSAO_CODIGO_BARRAS_REQUESTCODE = 25;
        public const int OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE = 26;
        public const int IMPRIME_IMAGEM_REQUESTCODE = 27;
        private readonly static string EXTERNAL_CONNECTION_METHOD_USB = "USB";
        private readonly static string EXTERNAL_CONNECTION_METHOD_IP = "IP";

        private readonly string EXTERNAL_PRINTER_MODEL_I9 = "i9";
        private readonly string EXTERNAL_PRINTER_MODEL_I8 = "i8";
        private string selectedPrinterModel;

        // Serviço de execução de comandos via intent
        private readonly IIntentDigitalHubCommandStarter digitalHubCommandStarter = DependencyService.Get<IIntentDigitalHubCommandStarter>();

        public PrinterPage()
        {
            InitializeComponent();

            //Inicia a impressora interna ao abrir da tela
            ConnectInternPrinter();

            //Atualiza a borda selecionada inicialmente
            UpdateSelectedScreenButtonBorder("Text");

            //Atualiza Fragment
            FragmentContainer.Content = new PrinterTextView();

            internalPrinterRadio.IsChecked = true;
            ipEntry.Text = "192.168.0.103:9100"; 
            
            internalPrinterRadio.CheckedChanged += PrinterConectionChanged;
            externalPrinterIpRadio.CheckedChanged += PrinterConectionChanged;
            externalPrinterUsbRadio.CheckedChanged += PrinterConectionChanged;

            // Escutar eventos emitidos por resultados de intent
            MessagingCenter.Unsubscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result");
            MessagingCenter.Subscribe<Application, Tuple<int, string>>(this, "digital_hub_intent_result", HandleIntentResult);
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
                    case ABRE_CONEXAO_IMPRESSORA_REQUESTCODE:
                        AbreConexaoImpressora abreConexaoImpressoraReturn = JsonConvert.DeserializeObject<AbreConexaoImpressora>(jsonObjectReturn.ToString());
                        break;
                    //Caso o comando seja conexão por impressora externa (interna ou usb)
                    case ABRE_CONEXAO_IMPRESSORA_USB_REQUESTCODE:
                    case ABRE_CONEXAO_IMPRESSORA_IP_REQUESTCODE:
                        abreConexaoImpressoraReturn = JsonConvert.DeserializeObject<AbreConexaoImpressora>(jsonObjectReturn.ToString());

                        //Se a conexão não obtiver sucesso, retorne a impressora interna
                        if (abreConexaoImpressoraReturn.GetResultado() != 0)
                        {
                            DisplayAlert("Alerta", "A tentativa de conexão por USB não foi bem sucedida", "OK");
                            internalPrinterRadio.IsChecked = true;
                            ConnectInternPrinter();
                        }
                        break;
                    //Comandos advindos dos fragments ; não possuem retorno em tela
                    case FECHA_CONEXAO_IMPRESSORA_REQUESTCDOE:
                    case IMPRESSAO_TEXTO_REQUESTCODE:
                    case IMPRIME_XML_NFCE_REQUESTCODE:
                    case IMPRIME_XML_SAT_REQUESTCODE:
                    case IMPRESSAO_CODIGO_BARRAS_REQUESTCODE:
                    case IMPRIME_IMAGEM_REQUESTCODE:
                        break;
                    case STATUS_IMPRESSORA_REQUESTCODE:
                        StatusImpressora statusImpressoraReturn = JsonConvert.DeserializeObject<StatusImpressora>(jsonObjectReturn.ToString());

                        string statusPrinter = statusImpressoraReturn.GetResultado() switch
                        {
                            5 => "Papel está presente e não está próximo do fim!",
                            6 => "Papel próximo do fim!",
                            7 => "Papel ausente!",
                            _ => "Status Desconhecido!",
                        };
                        DisplayAlert("Alert", statusPrinter, "OK");
                        break;

                    case STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE:
                        statusImpressoraReturn = JsonConvert.DeserializeObject<StatusImpressora>(jsonObjectReturn.ToString());

                        string statusGaveta = statusGaveta = statusImpressoraReturn.GetResultado() switch
                        {
                            1 => "Gaveta aberta!",
                            2 => "Gaveta fechada",
                            _ => "Status Desconhecido!",
                        };
                        DisplayAlert("Alert", statusGaveta, "OK");
                        break;
                    case ABRE_GAVETA_ELGIN_REQUESTCODE:
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

        private void OpenPrinterOption(object sender, EventArgs e)
        {
            Button btn = (Button)sender;

            switch (btn.Text)
            {
                case "IMPRESSÃO DE TEXTO":
                    FragmentContainer.Content = new PrinterTextView();
                    UpdateSelectedScreenButtonBorder("Text");
                    break;
                case "IMPRESSÃO DE CÓDIGO DE BARRAS":
                    FragmentContainer.Content = new PrinterBarCodeView();
                    UpdateSelectedScreenButtonBorder("Barcode");
                    break;
                case "IMPRESSÃO DE IMAGEM":
                    FragmentContainer.Content = new PrinterImageView();
                    UpdateSelectedScreenButtonBorder("Image");
                    break;
                default:
                    break;
            }
        }

        private void UpdateSelectedScreenButtonBorder(string screenSelected)
        {
            buttonPrinterTextSelected.BorderColor = screenSelected.Equals("Text") ?
                            Color.FromHex("23F600") :
                            Color.Black;
            buttonPrinterBarCodeSelected.BorderColor = screenSelected.Equals("Barcode") ?
                            Color.FromHex("23F600") :
                            Color.Black;
            buttonPrinterImageSelected.BorderColor = screenSelected.Equals("Image") ?
                            Color.FromHex("23F600") :
                            Color.Black;
        }
        
        //Valida o formato de IP
        private bool IsIpValid()
        {
            string IP = ipEntry.Text;

            Regex regexIP = new Regex(@"^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):[0-9]+$");

            bool isIpValid = regexIP.Match(IP).Success;

            if (isIpValid) return true;

            DisplayAlert("Alerta", "Insira um IP válido para a conexão Bridge!", "OK");
            return false;
        }

        private void PrinterConectionChanged(object sender, CheckedChangedEventArgs e)
        {
            RadioButton rb = sender as RadioButton;
            if (!rb.IsChecked) return;

            if (rb == internalPrinterRadio)
            {
                //printerService.PrinterInternalImpStart();
            }
            else if (rb == externalPrinterUsbRadio)
            {
                //Invoca o alertDialog que permite a escolha do modelo de impressora antes da tentativa de iniciar a conexão por IP
                AlertDialogSetSelectedPrinterModelThenConnect(EXTERNAL_CONNECTION_METHOD_USB);
            }
            else if (rb == externalPrinterIpRadio)
            {
                if (IsIpValid())
                {
                    //Invoca o alertDialog que permite a escolha do modelo de impressora antes da tentativa de iniciar a conexão por IP
                    AlertDialogSetSelectedPrinterModelThenConnect(EXTERNAL_CONNECTION_METHOD_IP);
                }
                else
                {
                    //Se não foi possível validar o ip antes da chamada da função, retorne para a conexão com impressora interna
                    internalPrinterRadio.IsChecked = true;
                    ConnectInternPrinter();
                }
            }
        }

        public async void AlertDialogSetSelectedPrinterModelThenConnect(string externalConnectionMethod)
        {
            string[] models = { EXTERNAL_PRINTER_MODEL_I9, EXTERNAL_PRINTER_MODEL_I8 };
            string option = await DisplayActionSheet("Selecione o modelo de impressora a ser conectado", "CANCELAR", null, models);

            if (option == null)
            {
                internalPrinterRadio.IsChecked = true;
                ConnectInternPrinter();
                return;
            }

            // Atualiza o modelo de impressora selecionado
            selectedPrinterModel = option;

            //inicializa depois da seleção do modelo a conexão de impressora, levando em contra o parâmetro que define se a conexão deve ser via IP ou USB
            if (externalConnectionMethod.Equals("USB"))
            {
                ConnectExternPrinterByUSB();
            }
            else ConnectExternPrinterByIP();
        }

        private void ConnectInternPrinter()
        {
            AbreConexaoImpressora abreConexaoImpressoraCommand = new AbreConexaoImpressora(6, "M8", "", 0);
            digitalHubCommandStarter.StartHubCommandActivity(abreConexaoImpressoraCommand, ABRE_CONEXAO_IMPRESSORA_REQUESTCODE);
        }

        private void ConnectExternPrinterByIP()
        {
            string ip = ipEntry.Text;
            string[] ipAndPort = ip.Split(':');

            AbreConexaoImpressora abreConexaoImpressoraCommand = new AbreConexaoImpressora(3, selectedPrinterModel, ipAndPort[0], int.Parse(ipAndPort[1]));

            digitalHubCommandStarter.StartHubCommandActivity(abreConexaoImpressoraCommand, ABRE_CONEXAO_IMPRESSORA_IP_REQUESTCODE);
        }

        private void ConnectExternPrinterByUSB()
        {
            AbreConexaoImpressora abreConexaoImpressoraCommand = new AbreConexaoImpressora(1, selectedPrinterModel, "USB", 0);

            digitalHubCommandStarter.StartHubCommandActivity(abreConexaoImpressoraCommand, ABRE_CONEXAO_IMPRESSORA_USB_REQUESTCODE);
        }

        private void StatusPrinter(object sender, EventArgs e)
        {
            StatusImpressora statusImpressoraCommand = new StatusImpressora(3);

            digitalHubCommandStarter.StartHubCommandActivity(statusImpressoraCommand, STATUS_IMPRESSORA_REQUESTCODE);
        }
        
        private void StatusDrawer(object sender, EventArgs e)
        {
            StatusImpressora statusImpressoraCommand = new StatusImpressora(1);

            digitalHubCommandStarter.StartHubCommandActivity(statusImpressoraCommand, STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE);
        }

        private void OpenDrawer(object sender, EventArgs e)
        {
            AbreGavetaElgin abreGavetaElginCommand = new AbreGavetaElgin();

            digitalHubCommandStarter.StartHubCommandActivity(abreGavetaElginCommand, ABRE_GAVETA_ELGIN_REQUESTCODE);
        }
    }
}