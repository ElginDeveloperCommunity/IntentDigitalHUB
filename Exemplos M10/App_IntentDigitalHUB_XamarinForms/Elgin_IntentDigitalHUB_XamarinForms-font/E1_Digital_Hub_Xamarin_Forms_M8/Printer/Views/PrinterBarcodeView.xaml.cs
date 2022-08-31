using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica;
using System;
using System.Collections.Generic;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Xamarin_Forms_Intent_Digital_Hub.Printer.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class PrinterBarCodeView : ContentView
    {
        private BarcodeType selectedBarcodeType = BarcodeType.EAN_8;
        private Alignment selectedAlignment = Alignment.CENTRO;

        private int widthOfBarCode = 1;
        private int heightOfBarCode = 20;

        // Serviço de execução de comandos via intent
        private readonly IIntentDigitalHubCommandStarter digitalHubCommandStarter = DependencyService.Get<IIntentDigitalHubCommandStarter>();

        public PrinterBarCodeView()
        {
            InitializeComponent();

            string[] barCodeOptions = new string[] { "EAN 8", "EAN 13", "QR CODE", "UPC-A", "CODE 39", "ITF", "CODE BAR", "CODE 93", "CODE 128" };
            barCodeType.ItemsSource = barCodeOptions;

            string[] heigthOptions = new string[] { "20", "60", "120", "200" };
            heigthPicker.ItemsSource = heigthOptions;

            string[] widthOptions = new string[] { "1", "2", "3", "4", "5", "6" };
            widthPicker.ItemsSource = widthOptions;

            codeEntry.Text = "40170725";
            barCodeType.SelectedIndex = 0;
            alignCent.IsChecked = true;
            widthPicker.SelectedIndex = 0;
            heigthPicker.SelectedIndex = 0;
            isCutPaper.IsChecked = false;

            barCodeType.SelectedIndexChanged += SetSelectedBarcodeType;

            //Funcionalidade Radio Alinhamento
            alignEsq.CheckedChanged += (s, eventArgs) => {
                selectedAlignment = eventArgs.Value ? Alignment.ESQUERDA : selectedAlignment;
            };

            alignCent.CheckedChanged += (s, eventArgs) => {
                selectedAlignment = eventArgs.Value ? Alignment.CENTRO : selectedAlignment;
            };

            alignDir.CheckedChanged += (s, eventArgs) => {
                selectedAlignment = eventArgs.Value ? Alignment.DIREITA : selectedAlignment;
            };

            //CONFIGS WIDTH BAR CODE
            widthPicker.SelectedIndexChanged += (s, e) => {
                widthOfBarCode = int.Parse(widthPicker.SelectedItem.ToString());
            };

            //CONFIGS HEIGHT BAR CODE
            heigthPicker.SelectedIndexChanged += (s, e) => {
                heightOfBarCode = int.Parse(heigthPicker.SelectedItem.ToString());
            };

            btnPrinterBarCode.Clicked += ButtonPrinterBarCodeFunction;
        }

        private void SetSelectedBarcodeType(object sender, EventArgs e)
        {
            //Se o tipo de código escolhido não fo QR_CODE, é possível selecionar widht e height separadamente, caso contrário apenas a opção SQUARE deve ser disponibilizada
            bool isQrCodeSelected = barCodeType.Items[barCodeType.SelectedIndex] == "QR CODE";
            lblWidth.Text = isQrCodeSelected ? "SQUARE" : "WIDTH";
            heigthStack.IsVisible = !isQrCodeSelected;

            //O enumerator está na mesma ordem que o índice do spinner, portanto pode-se atribuir diretamente:
            selectedBarcodeType = BarcodeType.options[barCodeType.SelectedIndex];

            //O texto de mensagem a ser transformada em código de barras recebe o padrão para o tipo escolhido
            codeEntry.Text = selectedBarcodeType.DefaultBarcodeMessage;
        }

        private void ButtonPrinterBarCodeFunction(object sender, EventArgs e)
        {
            //A lista de comandos da impressão
            List<IntentDigitalHubCommand> termicaCommandList = new List<IntentDigitalHubCommand>();

            //O comando de alinhamento para os códigos são chamados através de DefinePosicao()
            int posicao = (int)selectedAlignment;

            DefinePosicao definePosicaoCommand = new DefinePosicao(posicao);

            //Adiciona o comando de define posição
            termicaCommandList.Add(definePosicaoCommand);

            string dados = codeEntry.Text;

            //Para a impressão de QR_CODE existe uma função específica
            if (selectedBarcodeType == BarcodeType.QR_CODE)
            {
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

            if (isCutPaper.IsChecked)
            {
                Corte corteCommand = new Corte(0);

                termicaCommandList.Add(corteCommand);
            }

            digitalHubCommandStarter.StartHubCommandActivity(termicaCommandList, PrinterPage.IMPRESSAO_CODIGO_BARRAS_REQUESTCODE);
        }

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