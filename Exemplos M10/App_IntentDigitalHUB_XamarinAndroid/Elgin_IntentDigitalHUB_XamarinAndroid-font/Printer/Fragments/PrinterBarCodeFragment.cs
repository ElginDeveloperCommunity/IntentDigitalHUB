using Android.OS;
using Android.Support.V4.App;
using Android.Views;
using Android.Widget;
using Xamarin_Android_Intent_Digital_Hub.IntentServices;
using Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica;
using System;
using System.Collections.Generic;

namespace Xamarin_Android_Intent_Digital_Hub.Printer.Fragments
{
    public class PrinterBarCodeFragment : Fragment
    {
        private Android.App.Activity PrinterActivityReference;

        private EditText editTextInputBarCode;

        private Spinner spinnerBarCodeType;
        private Spinner spinnerBarCodeWidth;
        private Spinner spinnerBarCodeHeight;

        private RadioGroup radioGroupAlignBarCode;
        private RadioButton buttonRadioAlignCenter;

        private CheckBox checkBoxIsCutPaperBarCode;

        private TextView textViewWidth, textViewHeight;

        private Button buttonPrinterBarCode;

        private BarcodeType selectedBarcodeType = BarcodeType.EAN_8;
        private Alignment selectedAlignment = Alignment.CENTRO;

        private int widthOfBarCode = 1;
        private int heightOfBarCode = 20;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Create your fragment here
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v = inflater.Inflate(Resource.Layout.fragment_printer_bar_code, container, false);

            PrinterActivityReference = Activity;

            editTextInputBarCode = v.FindViewById<EditText>(Resource.Id.editTextInputBarCode);
            editTextInputBarCode.Text = "40170725";

            textViewWidth = v.FindViewById<TextView>(Resource.Id.textViewWidth);
            textViewHeight = v.FindViewById<TextView>(Resource.Id.textViewHeight);

            spinnerBarCodeType = v.FindViewById<Spinner>(Resource.Id.spinnerBarCodeType);
            buttonRadioAlignCenter = v.FindViewById<RadioButton>(Resource.Id.radioButtonBarCodeAlignCenter);
            spinnerBarCodeWidth = v.FindViewById<Spinner>(Resource.Id.spinnerBarCodeWidth);
            spinnerBarCodeHeight = v.FindViewById<Spinner>(Resource.Id.spinnerBarCodeHeight);
            checkBoxIsCutPaperBarCode = v.FindViewById<CheckBox>(Resource.Id.checkBoxCutPaper);
            buttonPrinterBarCode = v.FindViewById<Button>(Resource.Id.buttonPrintBarCode);

            spinnerBarCodeType.ItemSelected += (s, e) => {
                SetSelectedBarcodeType(e.Position);
            };

            //Funcionalidade Radio Alinhamento
            buttonRadioAlignCenter.Checked = true;
            radioGroupAlignBarCode = v.FindViewById<RadioGroup>(Resource.Id.radioGroupAlignBarCode);
            radioGroupAlignBarCode.CheckedChange += (s, eventArgs) => {
                switch (eventArgs.CheckedId)
                {
                    case Resource.Id.radioButtonBarCodeAlignLeft:
                        selectedAlignment = Alignment.ESQUERDA;
                        break;
                    case Resource.Id.radioButtonBarCodeAlignCenter:
                        selectedAlignment = Alignment.CENTRO;
                        break;
                    case Resource.Id.radioButtonBarCodeAlignRight:
                        selectedAlignment = Alignment.DIREITA;
                        break;
                }
            };

            //CONFIGS WIDTH BAR CODE
            spinnerBarCodeWidth.ItemSelected += (s, e) => {
                widthOfBarCode = int.Parse(spinnerBarCodeWidth.SelectedItem.ToString());
            };

            //CONFIGS HEIGHT BAR CODE
            spinnerBarCodeHeight.ItemSelected += (s, e) => {
                heightOfBarCode = int.Parse(spinnerBarCodeHeight.SelectedItem.ToString());
            };

            buttonPrinterBarCode.Click += ButtonPrinterBarCodeFunction;

            return v;
        }

        //Aplica o tipo de código de barras selecionado
        private void SetSelectedBarcodeType(int selectedIndex)
        {
            //Se o tipo de código escolhido não fo QR_CODE, é possível selecionar widht e height separadamente, caso contrário apenas a opção SQUARE deve ser disponibilizada
            textViewWidth.Text = "WIDTH";
            textViewHeight.Visibility = ViewStates.Visible;
            spinnerBarCodeHeight.Visibility = ViewStates.Visible;

            if (selectedIndex == 2)
            {
                textViewWidth.Text = "SQUARE";
                textViewHeight.Visibility = ViewStates.Invisible;
                spinnerBarCodeHeight.Visibility = ViewStates.Invisible;
            }

            //O enumerator está na mesma ordem que o índice do spinner, portanto pode-se atribuir diretamente:
            selectedBarcodeType = BarcodeType.options[selectedIndex];

            //O texto de mensagem a ser transformada em código de barras recebe o padrão para o tipo escolhido
            editTextInputBarCode.Text = selectedBarcodeType.DefaultBarcodeMessage;
        }

        private void ButtonPrinterBarCodeFunction(object v, EventArgs ev)
        {
            //A lista de comandos da impressão
            List<IntentDigitalHubCommand> termicaCommandList = new List<IntentDigitalHubCommand>();

            //O comando de alinhamento para os códigos são chamados através de DefinePosicao()
            int posicao = (int) selectedAlignment;

            DefinePosicao definePosicaoCommand = new DefinePosicao(posicao);

            //Adiciona o comando de define posição
            termicaCommandList.Add(definePosicaoCommand);

            //Para a impressão de QR_CODE existe uma função específica
            if (selectedBarcodeType == BarcodeType.QR_CODE)
            {
                string dados = editTextInputBarCode.Text;

                int tamanho = widthOfBarCode;

                int nivelCorrecao = 2;

                ImpressaoQRCode impressaoQRCodeCommand = new ImpressaoQRCode(dados,
                        tamanho,
                        nivelCorrecao);

                termicaCommandList.Add(impressaoQRCodeCommand);
            }
            else
            {
                int tipo = selectedBarcodeType.BarcodeTypeValue;

                string dados = editTextInputBarCode.Text;

                int altura = heightOfBarCode;

                int largura = widthOfBarCode;

                //Não imprimir valor abaixo do código
                int HRI = 4;

                ImpressaoCodigoBarras impressaoCodigoBarrasCommand = new ImpressaoCodigoBarras(tipo,
                        dados,
                        altura,
                        largura,
                        HRI);

                termicaCommandList.Add(impressaoCodigoBarrasCommand);
            }

            AvancaPapel avancaPapelCommand = new AvancaPapel(10);

            termicaCommandList.Add(avancaPapelCommand);

            if (checkBoxIsCutPaperBarCode.Checked)
            {
                Corte corteCommand = new Corte(0);

                termicaCommandList.Add(corteCommand);
            }

            IntentDigitalHubCommandStarter.StartHubCommandActivity(PrinterActivityReference, termicaCommandList, PrinterActivity.IMPRESSAO_CODIGO_BARRAS_REQUESTCODE);
        }

        /**
         * Valores do código de barra para a impressão de código de barras, de acordo com a documentação
        */
        internal class BarcodeType
        {
            private BarcodeType(int? barcodeTypeValue, string defaultBarcodeMessage)
            {
                if (barcodeTypeValue.HasValue)
                {
                    BarcodeTypeValue = (int)barcodeTypeValue;
                }
                DefaultBarcodeMessage = defaultBarcodeMessage;
            }

            public int BarcodeTypeValue { get; private set; }

            //String utilizada como mensagem-exemplo ao se selecionar um novo tipo de código para a impresão
            public string DefaultBarcodeMessage { get; private set; }

            public static BarcodeType EAN_8 = new BarcodeType(3, "40170725");
            public static BarcodeType EAN_13 = new BarcodeType(2, "0123456789012");
            //O código QR_CODE possui sua função própia, por isto seu valor-código para as funções não é utilizado
            public static BarcodeType QR_CODE = new BarcodeType(null, "ELGIN DEVELOPERS COMMUNITY"); 
            public static BarcodeType UPC_A = new BarcodeType(0, "123601057072"); 
            public static BarcodeType CODE_39 = new BarcodeType(4, "CODE39"); 
            public static BarcodeType ITF = new BarcodeType(5, "05012345678900"); 
            public static BarcodeType CODE_BAR = new BarcodeType(6, "A3419500A"); 
            public static BarcodeType CODE_93 = new BarcodeType(7, "CODE93");
            public static BarcodeType CODE_128 = new BarcodeType(8, "{C1233");

            public static BarcodeType[] options = new BarcodeType[] { EAN_8, EAN_13, QR_CODE, UPC_A, CODE_39, ITF, CODE_BAR, CODE_93, CODE_128 };
        }

        internal enum Alignment : ushort
        {
            ESQUERDA = 0, CENTRO = 1, DIREITA = 2
        }
    }
}