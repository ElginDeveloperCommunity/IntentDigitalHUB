using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica;
using System;
using System.Collections.Generic;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Xamarin_Forms_Intent_Digital_Hub.Utils;

namespace Xamarin_Forms_Intent_Digital_Hub.Printer.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class PrinterTextView : ContentView
    {
        private const string XML_EXTENSION = ".xml";
        private const string XML_NFCE_ARCHIVE_NAME = "xmlnfce";
        private const string XML_SAT_ARCHIVE_NAME = "xmlsat";

        private Alignment selectedAlignment = Alignment.CENTRO;
        private FontFamily selectedFontFamily = FontFamily.FONT_A;
        private int selectedFontSize = 17;

        private readonly IActivityUtils activityUtils = DependencyService.Get<IActivityUtils>();

        // Serviço de execução de comandos via intent
        private readonly IIntentDigitalHubCommandStarter digitalHubCommandStarter = DependencyService.Get<IIntentDigitalHubCommandStarter>();

        public PrinterTextView()
        {
            InitializeComponent();

            textEntry.Text = "ELGIN DEVELOPERS COMMUNITY";

            //Funcionalidade Radio Alinhamento
            alignCent.IsChecked = true;
            alignEsq.CheckedChanged += (s, eventArgs) => {
                selectedAlignment = eventArgs.Value ? Alignment.ESQUERDA : selectedAlignment;
            };

            alignCent.CheckedChanged += (s, eventArgs) => {
                selectedAlignment = eventArgs.Value ? Alignment.CENTRO : selectedAlignment;
            };

            alignDir.CheckedChanged += (s, eventArgs) => {
                selectedAlignment = eventArgs.Value ? Alignment.DIREITA : selectedAlignment;
            };

            //Funcionalidade do Spinner de seleção de fonte
            string[] fontFamilyOptions = new string[] { "FONT A", "FONT B" };
            fontFamilyPicker.ItemsSource = fontFamilyOptions;
            string[] fontSizeOptions = new string[] { "17", "34", "51", "68" };
            fontSizePicker.ItemsSource = fontSizeOptions;
            fontFamilyPicker.SelectedIndex = 0;
            fontSizePicker.SelectedIndex = 0;

            fontFamilyPicker.SelectedIndexChanged += (s, e) => {
                selectedFontFamily = (fontFamilyPicker.SelectedIndex == 0) ? FontFamily.FONT_A : FontFamily.FONT_B;
            };

            fontSizePicker.SelectedIndexChanged += (s, e) => {
                selectedFontSize = int.Parse(fontSizePicker.SelectedItem.ToString());
            };

            btnPrinter.Clicked += ButtonPrinterFunction;

            btnPrinterXMLNFCe.Clicked += ButtonPrinterXMLNFCeFunction;

            btnPrinterXMLSAT.Clicked += ButtonPrinterXMlSATFunction;
        }

        private void ButtonPrinterFunction(object v, EventArgs ev)
        {
            if (textEntry.Text.Length == 0)
                Application.Current.MainPage.DisplayAlert("Alerta", "Campo mensagem vazio!", "OK");
            else
            {
                int posicao = (int)selectedAlignment;

                int stilo = GetStiloValue();

                ImpressaoTexto impressaoTextoCommand = new ImpressaoTexto(textEntry.Text,
                        posicao,
                        stilo,
                        selectedFontSize);

                AvancaPapel avancaPapelCommand = new AvancaPapel(10);

                List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

                termicaCommands.Add(impressaoTextoCommand);
                termicaCommands.Add(avancaPapelCommand);

                if (isCutPaper.IsChecked)
                {
                    Corte corteCommand = new Corte(0);

                    termicaCommands.Add(corteCommand);
                }

                digitalHubCommandStarter.StartHubCommandActivity(termicaCommands, PrinterPage.IMPRESSAO_TEXTO_REQUESTCODE);
            }
        }

        //Calcula o valor do estilo de acordo com a parametrização definida
        private int GetStiloValue()
        {
            int stilo = 0;

            if (selectedFontFamily == FontFamily.FONT_B)
                stilo += 1;
            if (isSub.IsChecked)
                stilo += 2;
            if (isBold.IsChecked)
                stilo += 8;

            return stilo;
        }

        private void ButtonPrinterXMLNFCeFunction(object v, EventArgs ev)
        {
            //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciá-lo
            activityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(XML_NFCE_ARCHIVE_NAME);
            string dados = activityUtils.GetFilePathForIDH(XML_NFCE_ARCHIVE_NAME + XML_EXTENSION);

            int indexcsc = 1;
            string csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";
            int param = 0;

            ImprimeXMLNFCe imprimeXMLNFCeCommand = new ImprimeXMLNFCe(dados, indexcsc, csc, param);
            AvancaPapel avancaPapelCommand = new AvancaPapel(10);

            List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

            termicaCommands.Add(imprimeXMLNFCeCommand);
            termicaCommands.Add(avancaPapelCommand);

            if (isCutPaper.IsChecked)
            {
                Corte corteCommand = new Corte(0);
                termicaCommands.Add(corteCommand);
            }

            digitalHubCommandStarter.StartHubCommandActivity(termicaCommands, PrinterPage.IMPRIME_XML_NFCE_REQUESTCODE);
        }

        private void ButtonPrinterXMlSATFunction(object v, EventArgs ev)
        {
            //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
            activityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(XML_SAT_ARCHIVE_NAME);
            string dados = activityUtils.GetFilePathForIDH(XML_SAT_ARCHIVE_NAME + XML_EXTENSION);
            int param = 0;

            ImprimeXMLSAT imprimeXMLSATCommand = new ImprimeXMLSAT(dados, param);
            AvancaPapel avancaPapelCommand = new AvancaPapel(10);

            List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

            termicaCommands.Add(imprimeXMLSATCommand);
            termicaCommands.Add(avancaPapelCommand);

            if (isCutPaper.IsChecked)
            {
                Corte corteCommand = new Corte(0);
                termicaCommands.Add(corteCommand);
            }

            digitalHubCommandStarter.StartHubCommandActivity(termicaCommands, PrinterPage.IMPRESSAO_TEXTO_REQUESTCODE);
        }

        //Valores de alinhamento para a impressão de texto
        internal enum Alignment : ushort
        {
            ESQUERDA = 0, CENTRO = 1, DIREITA = 2
        }

        internal enum FontFamily
        {
            FONT_A, FONT_B
        }
    }
}