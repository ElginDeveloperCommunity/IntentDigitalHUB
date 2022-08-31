using Android.Content;
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
    public class PrinterImageFragment : Fragment
    {
        public static ImageView imageView;
        private Button buttonSelectImage;
        private Button buttonPrintImage;
        private CheckBox checkBoxCutPaperImage;

        private string pathOfLastImageSelected = "";

        private Android.App.Activity PrinterActivityReference;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Create your fragment here
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v = inflater.Inflate(Resource.Layout.fragment_printer_image, container, false);

            //Atribui o path da ultima imagem selecionada, sempre em Android/data/com.elgin.intent_digital_hub/files/ImageToPrint.jpg
            PrinterActivityReference = Activity;

            //Captura a referência da atividade onde o fragment é utilizado
            pathOfLastImageSelected = "/Android/data/" + PrinterActivityReference.ApplicationContext.PackageName + "/files" + "/ImageToPrint.jpg";

            imageView = v.FindViewById<ImageView>(Resource.Id.previewImgDefault);

            buttonSelectImage = v.FindViewById<Button>(Resource.Id.buttonSelectImage);
            buttonPrintImage = v.FindViewById<Button>(Resource.Id.buttonPrintImage);

            checkBoxCutPaperImage = v.FindViewById<CheckBox>(Resource.Id.checkBoxCutPaperPrintImage);

            buttonSelectImage.Click += ButtonSelectImageFunction;

            buttonPrintImage.Click += ButtonPrintImageFunction;

            return v;
        }

        private void ButtonSelectImageFunction(object v, EventArgs ev)
        {
            Intent cameraIntent = new Intent(Intent.ActionPick);

            cameraIntent.SetType("image/*");

            PrinterActivityReference.StartActivityForResult(cameraIntent, PrinterActivity.OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE);
        }

        private void ButtonPrintImageFunction(object v, EventArgs ev)
        {
            List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

            string path = pathOfLastImageSelected;

            ImprimeImagem imprimeImagemCommand = new ImprimeImagem(path);
            termicaCommands.Add(imprimeImagemCommand); 
            
            AvancaPapel avancaPapelCommand = new AvancaPapel(10);
            termicaCommands.Add(avancaPapelCommand);

            if (checkBoxCutPaperImage.Checked)
            {
                Corte corteCommand = new Corte(0);
                termicaCommands.Add(corteCommand);
            }

            IntentDigitalHubCommandStarter.StartHubCommandActivity(PrinterActivityReference, termicaCommands, PrinterActivity.IMPRIME_IMAGEM_REQUESTCODE);
        }

    }
}