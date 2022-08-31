using Xamarin_Forms_Intent_Digital_Hub.IntentServices;
using Xamarin_Forms_Intent_Digital_Hub.IntentServices.Termica;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Xamarin_Forms_Intent_Digital_Hub.Utils;

namespace Xamarin_Forms_Intent_Digital_Hub.Printer.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class PrinterImageView : ContentView
    {
        public static string pathOfLastImageSelected = "";
        Stream selectedImage;

        private readonly IActivityUtils activityUtils = DependencyService.Get<IActivityUtils>();

        // Serviço de execução de comandos via intent
        private readonly IIntentDigitalHubCommandStarter digitalHubCommandStarter = DependencyService.Get<IIntentDigitalHubCommandStarter>();

        public PrinterImageView()
        {
            InitializeComponent();

            pathOfLastImageSelected = "/Android/data/" + AppInfo.PackageName + "/files" + "/ImageToPrint.jpg";

            isCutPaper.IsChecked = false;

            displayImage.Source = "elgin_logo_default_print_image.jpg";

            btnSelectImage.Clicked += ButtonSelectImageFunction;

            btnPrintImage.Clicked += ButtonPrintImageFunction;

            StoreDefaultImage();
        }

        private async void ButtonSelectImageFunction(object v, EventArgs ev)
        {
            Stream imageStream = await activityUtils.GetImageStreamAsync();
            if (imageStream != null)
            {
                selectedImage = new MemoryStream();
                imageStream.CopyTo(selectedImage);

                // RESETAR STREAMS
                selectedImage.Position = 0;
                imageStream.Position = 0;

                displayImage.Source = ImageSource.FromStream(() => imageStream);

                StoreSelectedImage();
            }
        }

        private void ButtonPrintImageFunction(object v, EventArgs ev)
        {
            List<IntentDigitalHubCommand> termicaCommands = new List<IntentDigitalHubCommand>();

            string path = pathOfLastImageSelected;

            ImprimeImagem imprimeImagemCommand = new ImprimeImagem(path);
            termicaCommands.Add(imprimeImagemCommand);

            if (isCutPaper.IsChecked)
            {
                Corte corteCommand = new Corte(0);
                termicaCommands.Add(corteCommand);
            }

            digitalHubCommandStarter.StartHubCommandActivity(termicaCommands, PrinterPage.IMPRIME_IMAGEM_REQUESTCODE);
        }
    
        void StoreDefaultImage()
        {
            activityUtils.StoreDefaultImage();
        }

        void StoreSelectedImage()
        {
            activityUtils.StoreSelectedImage(selectedImage);
        }
    }
}