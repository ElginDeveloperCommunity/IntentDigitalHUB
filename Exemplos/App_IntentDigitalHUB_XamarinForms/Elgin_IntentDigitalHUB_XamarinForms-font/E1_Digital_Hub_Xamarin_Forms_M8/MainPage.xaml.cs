using Xamarin_Forms_Intent_Digital_Hub.Bridge;
using Xamarin_Forms_Intent_Digital_Hub.Printer;
using Xamarin_Forms_Intent_Digital_Hub.Sat;
using System;
using Xamarin.Forms;
using Xamarin.Essentials;
using Xamarin_Forms_Intent_Digital_Hub.Permissions;
using Xamarin_Forms_Intent_Digital_Hub.Utils;

namespace Xamarin_Forms_Intent_Digital_Hub
{
    public partial class MainPage : ContentPage
    {
        private readonly IActivityUtils activityUtils = DependencyService.Get<IActivityUtils>();

        public MainPage()
        {
            InitializeComponent();

            AskWriteExternalStoragePermission();
        }

        private async void AskWriteExternalStoragePermission()
        {
            var writeExternalStoragePermission = DependencyService.Get<IWriteExternalStoragePermission>();
            PermissionStatus status = await writeExternalStoragePermission.RequestAsync();
            if (status != PermissionStatus.Granted)
            {
                activityUtils.ShowLongToast("É necessário conceder a permissão para várias funcionalidades da aplicação!");
                CloseApplication();
            }
        }

        //Força o fechamento da aplicação
        private void CloseApplication()
        {
            Application.Current.Quit();
        }

        async void OpenE1BridgePage(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new E1BridgePage());
        }

        async void OpenPrinterPage(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new PrinterPage());
        }

        async void OpenSatPage(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new SatPage());
        }
    }
}
