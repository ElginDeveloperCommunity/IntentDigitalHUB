using Android;
using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.Graphics;
using Android.Net;
using Android.OS;
using Android.Provider;
using Android.Runtime;
using Android.Support.V4.App;
using Android.Support.V7.App;
using Android.Util;
using Android.Widget;

using AndroidX.AppCompat.Content.Res;

using Xamarin_Android_Intent_Digital_Hub.IntentServices;
using Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica;
using Xamarin_Android_Intent_Digital_Hub.Printer.Fragments;

using Java.IO;
using Java.Util.Regex;
using Newtonsoft.Json;
using Org.Json;
using static Android.Widget.RadioGroup;
using AlertDialog = Android.App.AlertDialog;
using FragmentTransaction = Android.Support.V4.App.FragmentTransaction;
using Pattern = Java.Util.Regex.Pattern;

namespace Xamarin_Android_Intent_Digital_Hub.Printer
{
    [Android.App.Activity(Label = "PrinterActivity")]
    public class PrinterActivity : AppCompatActivity
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

        private Button buttonPrinterTextSelected;
        private Button buttonPrinterBarCodeSelected;
        private Button buttonPrinterImageSelected;
        private Button buttonStatusPrinter;
        private Button buttonStatusGaveta;
        private Button buttonAbrirGaveta;
        private RadioGroup radioGroupConnectPrinterIE;
        private RadioButton radioButtonConnectPrinterIntern;
        private EditText editTextInputIP;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.activity_printer);

            //Inicia a impressora interna ao abrir da tela
            ConnectInternPrinter();

            //Atualiza Fragment
            SwitchToPrinterTextFragment();

            editTextInputIP = FindViewById<EditText>(Resource.Id.editTextInputIP);
            buttonPrinterTextSelected = FindViewById<Button>(Resource.Id.buttonPrinterTextSelect);
            buttonPrinterImageSelected = FindViewById<Button>(Resource.Id.buttonPrinterImageSelect);
            buttonPrinterBarCodeSelected = FindViewById<Button>(Resource.Id.buttonPrinterBarCodeSelect);
            buttonStatusPrinter = FindViewById<Button>(Resource.Id.buttonStatus);
            buttonStatusGaveta = FindViewById<Button>(Resource.Id.buttonStatusGaveta);
            buttonAbrirGaveta = FindViewById<Button>(Resource.Id.buttonAbrirGaveta);
            radioButtonConnectPrinterIntern = FindViewById<RadioButton>(Resource.Id.radioButtonConnectPrinterIntern);
            radioGroupConnectPrinterIE = FindViewById<RadioGroup>(Resource.Id.radioGroupConnectPrinterIE);

            //Atualiza a borda selecionada inicialmente
            UpdateSelectedScreenButtonBorder("Text");

            radioButtonConnectPrinterIntern.Checked = true;
            editTextInputIP.Text = "192.168.0.103:9100";

            buttonPrinterTextSelected.Click += delegate {
                UpdateSelectedScreenButtonBorder("Text");
                SwitchToPrinterTextFragment();
            };
            buttonPrinterBarCodeSelected.Click += delegate {
                UpdateSelectedScreenButtonBorder("Barcode");
                SwitchToPrinterBarCodeFragment();
            };
            buttonPrinterImageSelected.Click += delegate {
                UpdateSelectedScreenButtonBorder("Image");
                SwitchToPrinterImageFragment();
            };

            radioGroupConnectPrinterIE.CheckedChange += OnRadioConnectPrinterIEChanged;

            buttonStatusPrinter.Click += delegate { StatusPrinter(); };

            buttonStatusGaveta.Click += delegate { StatusDrawer(); };

            buttonAbrirGaveta.Click += delegate { OpenDrawer(); };
        }

        private void UpdateSelectedScreenButtonBorder(string screenSelected)
        {
            buttonPrinterTextSelected.BackgroundTintList = AppCompatResources.GetColorStateList(this,
                    screenSelected.Equals("Text") ? Resource.Color.azul : Resource.Color.black);
            buttonPrinterBarCodeSelected.BackgroundTintList = AppCompatResources.GetColorStateList(this,
                    screenSelected.Equals("Barcode") ? Resource.Color.azul : Resource.Color.black);
            buttonPrinterImageSelected.BackgroundTintList = AppCompatResources.GetColorStateList(this,
                    screenSelected.Equals("Image") ? Resource.Color.azul : Resource.Color.black);
        }

        private void SwitchToPrinterTextFragment()
        {
            PrinterTextFragment printerTextFragment = new PrinterTextFragment();
            FragmentTransaction transaction = SupportFragmentManager.BeginTransaction();
            transaction.Replace(Resource.Id.containerFragments, printerTextFragment);
            transaction.Commit();
        }

        private void SwitchToPrinterBarCodeFragment()
        {
            PrinterBarCodeFragment printerBarCodeFragment = new PrinterBarCodeFragment();
            FragmentTransaction transaction = SupportFragmentManager.BeginTransaction();
            transaction.Replace(Resource.Id.containerFragments, printerBarCodeFragment);
            transaction.Commit();
        }

        //A função de impressão de imagem requer a permissão para escrever no diretório externo da aplicação
        private void SwitchToPrinterImageFragment()
        {
            //Cria a logo que será impressa dentro do diretório da aplicação
            Bitmap elgin_logo_default_print_image = BitmapFactory.DecodeResource(Resources, Resource.Drawable.elgin_logo_default_print_image);
            StoreImage(elgin_logo_default_print_image);

            PrinterImageFragment printerImageFragment = new PrinterImageFragment();
            FragmentTransaction transaction = SupportFragmentManager.BeginTransaction();
            transaction.Replace(Resource.Id.containerFragments, printerImageFragment);
            transaction.Commit();
        }

        //Validação de IP
        private bool IsIpValid(string ip)
        {
            Pattern pattern = Pattern.Compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");

            Matcher matcher = pattern.Matcher(ip);

            return matcher.Matches();
        }

        private void OnRadioConnectPrinterIEChanged(object v, CheckedChangeEventArgs eventArgs)
        {
            switch (eventArgs.CheckedId)
            {
                case Resource.Id.radioButtonConnectPrinterIntern:
                    ConnectInternPrinter();
                    break;

                case Resource.Id.radioButtonConnectPrinterExternByIP:
                    if (IsIpValid(editTextInputIP.Text))
                    {
                        //Invoca o alertDialog que permite a escolha do modelo de impressora antes da tentativa de iniciar a conexão por IP
                        AlertDialogSetSelectedPrinterModelThenConnect(EXTERNAL_CONNECTION_METHOD_IP);
                    }
                    else
                    {
                        //Se não foi possível validar o ip antes da chamada da função, retorne para a conexão com impressora interna
                        radioButtonConnectPrinterIntern.Checked = true;
                        ConnectInternPrinter();
                    }
                    break;

                case Resource.Id.radioButtonConnectPrinterExternByUSB:
                    //Invoca o alertDialog que permite a escolha do modelo de impressora antes da tentativa de iniciar a conexão por IP
                    AlertDialogSetSelectedPrinterModelThenConnect(EXTERNAL_CONNECTION_METHOD_USB);
                    break;

            }
        }

        //Dialogo usado para escolher definir o modelo de impressora externa que sera estabelecida a conexao
        public void AlertDialogSetSelectedPrinterModelThenConnect(string externalConnectionMethod)
        {
            string[] operations = { EXTERNAL_PRINTER_MODEL_I9, EXTERNAL_PRINTER_MODEL_I8 };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.SetTitle("Selecione o modelo de impressora a ser conectado");

            //Tornando o dialógo não-cancelável
            builder.SetCancelable(false);

            builder.SetNegativeButton("CANCELAR", (c, ev) => {
                //Se a opção de cancelamento tiver sido escolhida, retorne sempre à opção de impressão por impressora interna
                radioButtonConnectPrinterIntern.Checked = true;

                AbreConexaoImpressora abreConexaoImpressoraCommand = new AbreConexaoImpressora(6, "M8", "", 0);

                IntentDigitalHubCommandStarter.StartHubCommandActivity(this, abreConexaoImpressoraCommand, ABRE_CONEXAO_IMPRESSORA_REQUESTCODE);
                ((IDialogInterface)c).Dismiss();
            });

            builder.SetItems(operations, (c, ev) => {
                //Envia o parâmetro escolhido para a função que atualiza o modelo de impressora selecionado
                SetSelectedPrinterModel(ev.Which);

                //inicializa depois da seleção do modelo a conexão de impressora, levando em conta o parâmetro que define se a conexão deve ser via IP ou USB
                if (externalConnectionMethod.Equals("USB"))
                    ConnectExternPrinterByUSB();
                else
                    ConnectExternPrinterByIP();
            });
            builder.Show();
        }

        private void SetSelectedPrinterModel(int whichSelected)
        {
            selectedPrinterModel = whichSelected == 0 ? EXTERNAL_PRINTER_MODEL_I9 : EXTERNAL_PRINTER_MODEL_I8;
        }

        private void ConnectInternPrinter()
        {
            AbreConexaoImpressora abreConexaoImpressoraCommand = new AbreConexaoImpressora(6, "M8", "", 0);
            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, abreConexaoImpressoraCommand, ABRE_CONEXAO_IMPRESSORA_REQUESTCODE);
        }

        private void ConnectExternPrinterByIP()
        {
            string ip = editTextInputIP.Text;
            string[] ipAndPort = ip.Split(":");

            AbreConexaoImpressora abreConexaoImpressoraCommand = new AbreConexaoImpressora(3, selectedPrinterModel, ipAndPort[0], int.Parse(ipAndPort[1]));

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, abreConexaoImpressoraCommand, ABRE_CONEXAO_IMPRESSORA_IP_REQUESTCODE);
        }

        private void ConnectExternPrinterByUSB()
        {
            AbreConexaoImpressora abreConexaoImpressoraCommand = new AbreConexaoImpressora(1, selectedPrinterModel, "USB", 0);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, abreConexaoImpressoraCommand, ABRE_CONEXAO_IMPRESSORA_USB_REQUESTCODE);
        }

        private void StatusPrinter()
        {
            StatusImpressora statusImpressoraCommand = new StatusImpressora(3);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, statusImpressoraCommand, STATUS_IMPRESSORA_REQUESTCODE);
        }

        private void StatusDrawer()
        {
            StatusImpressora statusImpressoraCommand = new StatusImpressora(1);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, statusImpressoraCommand, STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE);
        }

        private void OpenDrawer()
        {
            AbreGavetaElgin abreGavetaElginCommand = new AbreGavetaElgin();

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, abreGavetaElginCommand, ABRE_GAVETA_ELGIN_REQUESTCODE);
        }

        protected override void OnActivityResult(int requestCode, [GeneratedEnum] Android.App.Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);

            //Se o resultado for OK
            Log.Debug("resultCode", resultCode.ToString());

            if (resultCode == Result.Ok)
            {
                //Resultado da intent de seleção de imagem
                if (requestCode == OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE)
                {
                    /**
                     * Cria um bitmap através do URI da imagem selecionada da galeria, e através dele cria e salva uma imagem em Android/data/applicationPackage/files/ImageToPrint.jpg, que será utilizada na impressão de imagem por PATH
                    */
                    Uri returnUri = data.Data;
                    Bitmap bitmapImage = null;

                    try
                    {
                        bitmapImage = MediaStore.Images.Media.GetBitmap(ContentResolver, returnUri);
                    }
                    catch (IOException e)
                    {
                        e.PrintStackTrace();
                    }

                    //Atualiza a view pela imagem selecionada na galeria
                    PrinterImageFragment.imageView.SetImageBitmap(bitmapImage);

                    //Salva a imagem dentro do diretório da aplicação
                    StoreImage(bitmapImage);
                }

                //Código de retorno digital hub
                else
                {
                    string retorno = data.GetStringExtra("retorno");
                    Log.Debug("retorno", retorno);
                    //O retorno é sempre um JSONArray, no App_Experience apenas um comando é dado por vez, portanto o JSONArray de retorno sempre terá somente um JSON.
                    try
                    {
                        JSONArray jsonArray = new JSONArray(retorno);
                        JSONObject jsonObjectReturn = jsonArray.GetJSONObject(0);

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
                                    ActivityUtils.ShowAlertMessage(this, "Alerta", "A tentativa de conexão por USB não foi bem sucedida");
                                    radioButtonConnectPrinterIntern.Checked = true;
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

                                string statusPrinter = "";

                                statusPrinter = statusImpressoraReturn.GetResultado() switch
                                {
                                    5 => "Papel está presente e não está próximo do fim!",
                                    6 => "Papel próximo do fim!",
                                    7 => "Papel ausente!",
                                    _ => "Status Desconhecido!",
                                };
                                ActivityUtils.ShowAlertMessage(this, "Alert", statusPrinter);
                                break;

                            case STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE:
                                statusImpressoraReturn = JsonConvert.DeserializeObject<StatusImpressora>(jsonObjectReturn.ToString());

                                string statusGaveta = "";

                                statusGaveta = statusImpressoraReturn.GetResultado() switch
                                {
                                    1 => "Gaveta aberta!",
                                    2 => "Gaveta fechada",
                                    _ => "Status Desconhecido!",
                                };
                                ActivityUtils.ShowAlertMessage(this, "Alert", statusGaveta);
                                break;
                            case ABRE_GAVETA_ELGIN_REQUESTCODE:
                                break;
                            default:
                                ActivityUtils.ShowAlertMessage(this, "Alerta", "O comando " + requestCode + " não foi encontrado!");
                                break;
                        }
                    }
                    catch (JSONException e)
                    {
                        e.PrintStackTrace();
                        ActivityUtils.ShowAlertMessage(this, "Alerta", "O retorno não está no formato esperado!");
                    }
                }
            }
            else
            {
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O comando não foi bem sucedido!");
            }
        }

        //Desliga a impressora após sair da página
        protected override void OnDestroy()
        {
            base.OnDestroy();

            FechaConexaoImpressora fechaConexaoImpressoraCommand = new FechaConexaoImpressora();

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, fechaConexaoImpressoraCommand, FECHA_CONEXAO_IMPRESSORA_REQUESTCDOE);
        }

        /**
         * Salva uma copia da imagem enviada como bitmap por parametro dentro do diretorio do dispostivo, para a impressao via comando ImprimeImagem
        */
        private void StoreImage(Bitmap image)
        {
            File pictureFile = GetCreatedImage();

            //Salva a imagem
            try
            {
                System.IO.Stream fos = ContentResolver.OpenOutputStream(Uri.FromFile(pictureFile));
                image.Compress(Bitmap.CompressFormat.Png, 100, fos);
                fos.Close();
            }
            catch (FileNotFoundException e)
            {
                Log.Error("Error", "Arquivo não encontrado: " + e.Message);
                e.PrintStackTrace();
            }
            catch (IOException e)
            {
                Log.Error("Error", "Erro ao acessar o arquivo: " + e.Message);
                e.PrintStackTrace();
            }
        }

        /**
         * Cria a imagem que será salva no diretório da aplicação
        */
        private File GetCreatedImage()
        {
            string rootDirectoryPATH = ActivityUtils.GetRootDirectoryPATH(this);

            // A imagem a ser impressa sempre tera o mesmo nome para que a impressão ache o ultimo arquivo salvo
            File mediaFile;
            string mImageName = "ImageToPrint.jpg";
            mediaFile = new File(rootDirectoryPATH + File.Separator + mImageName);
            return mediaFile;
        }
    }
}