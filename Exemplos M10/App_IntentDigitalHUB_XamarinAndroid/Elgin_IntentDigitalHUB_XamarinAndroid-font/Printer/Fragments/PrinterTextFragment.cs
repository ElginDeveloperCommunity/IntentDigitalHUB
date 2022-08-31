using Android.OS;
using Android.Support.V4.App;
using Android.Views;
using Android.Widget;
using Xamarin_Android_Intent_Digital_Hub.IntentServices;
using Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica;
using System;
using System.Collections.Generic;
using System.IO;
using static Android.Widget.RadioGroup;
using IOException = Java.IO.IOException;
using System.Text;

namespace Xamarin_Android_Intent_Digital_Hub.Printer.Fragments
{
    public class PrinterTextFragment : Fragment
    {
        private const string XML_EXTENSION = ".xml";
        private const string XML_NFCE_ARCHIVE_NAME = "xmlnfce";
        private const string XML_SAT_ARCHIVE_NAME = "xmlsat";

        private Android.App.Activity PrinterActivityReference;
        private Button buttonPrinter;
        private Button buttonPrinterXMLNFCe;
        private Button buttonPrinterXMLSAT;
        private RadioGroup radioGroupAlign;
        private RadioButton buttonRadioCenter;
        private EditText editTextInputMessage;
        private Spinner spinnerFontFamily;
        private Spinner spinnerselectedFontSize;
        private CheckBox checkBoxIsBold;
        private CheckBox checkBoxIsUnderLine;
        private CheckBox checkBoxIsCutPaper;
        private Alignment selectedAlignment = Alignment.CENTRO;
        private FontFamily selectedFontFamily = FontFamily.FONT_A;
        private int selectedFontSize = 17;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Create your fragment here
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v = inflater.Inflate(Resource.Layout.fragment_printer_text, container, false);

            PrinterActivityReference = Activity;

            editTextInputMessage = v.FindViewById<EditText>(Resource.Id.editTextInputMessage);
            editTextInputMessage.Text = "ELGIN DEVELOPERS COMMUNITY";

            radioGroupAlign = v.FindViewById<RadioGroup>(Resource.Id.radioGroupAlign);
            buttonRadioCenter = v.FindViewById<RadioButton>(Resource.Id.radioButtonCenter);
            spinnerFontFamily = v.FindViewById<Spinner>(Resource.Id.spinnerFontFamily);
            spinnerselectedFontSize = v.FindViewById<Spinner>(Resource.Id.spinnerFontSize);
            checkBoxIsBold = v.FindViewById<CheckBox>(Resource.Id.checkBoxBold);
            checkBoxIsUnderLine = v.FindViewById<CheckBox>(Resource.Id.checkBoxUnderline);
            checkBoxIsCutPaper = v.FindViewById<CheckBox>(Resource.Id.checkBoxCutPaper);
            buttonPrinter = v.FindViewById<Button>(Resource.Id.buttonPrinterText);
            buttonPrinterXMLNFCe = v.FindViewById<Button>(Resource.Id.buttonPrinterNFCe);
            buttonPrinterXMLSAT = v.FindViewById<Button>(Resource.Id.buttonPrinterSAT);

            //Funcionalidade Radio Alinhamento
            buttonRadioCenter.Checked = true;
            radioGroupAlign = v.FindViewById<RadioGroup>(Resource.Id.radioGroupAlign);
            radioGroupAlign.CheckedChange += delegate (object v, CheckedChangeEventArgs eventArgs) {
                switch (eventArgs.CheckedId)
                {
                    case Resource.Id.radioButtonLeft:
                        selectedAlignment = Alignment.ESQUERDA;
                        break;
                    case Resource.Id.radioButtonCenter:
                        selectedAlignment = Alignment.CENTRO;
                        break;
                    case Resource.Id.radioButtonRight:
                        selectedAlignment = Alignment.DIREITA;
                        break;
                }
            };

            //Funcionalidade do Spinner de seleção de fonte
            spinnerFontFamily.ItemSelected += (s, e) => {
                selectedFontFamily = (e.Position == 0) ? FontFamily.FONT_A : FontFamily.FONT_B;
            };

            spinnerselectedFontSize.ItemSelected += (s, e) => {
                selectedFontSize = int.Parse(spinnerselectedFontSize.SelectedItem.ToString());
            };

            buttonPrinter.Click += ButtonPrinterFunction;

            buttonPrinterXMLNFCe.Click += ButtonPrinterXMLNFCeFunction;

            buttonPrinterXMLSAT.Click += ButtonPrinterXMlSATFunction;

            return v;
        }
        
        private void ButtonPrinterFunction(object v, EventArgs ev)
        {
            if (editTextInputMessage.Text.Length == 0)
            {
                ActivityUtils.ShowAlertMessage(PrinterActivityReference, "Alerta", "Campo mensagem vazio!");
                return;
            }
            int posicao = (int)selectedAlignment;

            int stilo = GetStiloValue();

            ImpressaoTexto impressaoTextoCommand = new ImpressaoTexto(editTextInputMessage.Text,
                    posicao,
                    stilo,
                    selectedFontSize);

            AvancaPapel avancaPapelCommand = new AvancaPapel(10);

            List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

            termicaCommands.Add(impressaoTextoCommand);
            termicaCommands.Add(avancaPapelCommand);

            if (checkBoxIsCutPaper.Checked)
            {
                Corte corteCommand = new Corte(0);
                termicaCommands.Add(corteCommand);
            }

            IntentDigitalHubCommandStarter.StartHubCommandActivity(PrinterActivityReference, termicaCommands, PrinterActivity.IMPRESSAO_TEXTO_REQUESTCODE);
        }

        /**
         * Calcula o valor do estilo de acordo com a parametrização definida
        */
        private int GetStiloValue()
        {
            int stilo = 0;

            if (selectedFontFamily == FontFamily.FONT_B)
                stilo += 1;
            if (checkBoxIsUnderLine.Checked)
                stilo += 2;
            if (checkBoxIsBold.Checked)
                stilo += 8;

            return stilo;
        }

        private void ButtonPrinterXMLNFCeFunction(object v, EventArgs ev)
        {
            //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciá-lo
            ActivityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(PrinterActivityReference, XML_NFCE_ARCHIVE_NAME);
            string dados = ActivityUtils.GetFilePathForIDH(PrinterActivityReference, XML_NFCE_ARCHIVE_NAME + XML_EXTENSION);
            
            int indexcsc = 1;
            string csc = "CODIGO-CSC-CONTRIBUINTE-36-CARACTERES";
            int param = 0;

            ImprimeXMLNFCe imprimeXMLNFCeCommand = new ImprimeXMLNFCe(dados, indexcsc, csc, param);
            AvancaPapel avancaPapelCommand = new AvancaPapel(10);

            List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

            termicaCommands.Add(imprimeXMLNFCeCommand);
            termicaCommands.Add(avancaPapelCommand);

            if (checkBoxIsCutPaper.Checked)
            {
                Corte corteCommand = new Corte(0);
                termicaCommands.Add(corteCommand);
            }

            IntentDigitalHubCommandStarter.StartHubCommandActivity(PrinterActivityReference, termicaCommands, PrinterActivity.IMPRIME_XML_NFCE_REQUESTCODE);
        }

        private void ButtonPrinterXMlSATFunction(object v, EventArgs ev)
        {
            //O impressão dos XMLs será feita por PATH, por isso é necessário salvar o XMl do projeto dentro do diretório da aplicação, para depois referenciar seu caminho
            ActivityUtils.LoadXMLFileAndStoreItOnApplicationRootDir(PrinterActivityReference, XML_SAT_ARCHIVE_NAME);
            string dados = ActivityUtils.GetFilePathForIDH(PrinterActivityReference, XML_SAT_ARCHIVE_NAME + XML_EXTENSION);
            int param = 0;

            ImprimeXMLSAT imprimeXMLSATCommand = new ImprimeXMLSAT(dados, param);
            AvancaPapel avancaPapelCommand = new AvancaPapel(10);

            List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

            termicaCommands.Add(imprimeXMLSATCommand);
            termicaCommands.Add(avancaPapelCommand);

            if (checkBoxIsCutPaper.Checked)
            {
                Corte corteCommand = new Corte(0);

                termicaCommands.Add(corteCommand);
            }

            IntentDigitalHubCommandStarter.StartHubCommandActivity(PrinterActivityReference, termicaCommands, PrinterActivity.IMPRESSAO_TEXTO_REQUESTCODE);
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