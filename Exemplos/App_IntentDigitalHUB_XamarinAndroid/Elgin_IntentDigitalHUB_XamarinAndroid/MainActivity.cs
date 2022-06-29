using Android;
using Android.App;
using Android.Content.PM;
using Android.OS;
using Android.Runtime;
using Android.Support.V4.App;
using Android.Support.V7.App;
using Android.Widget;
using Xamarin_Android_Intent_Digital_Hub.Bridge;
using Xamarin_Android_Intent_Digital_Hub.Printer;
using Xamarin_Android_Intent_Digital_Hub.Sat;

namespace Xamarin_Android_Intent_Digital_Hub
{
    [Activity(Label = "@string/app_name", Theme = "@style/Theme.IntentDigitalHub", MainLauncher = true)]
    public class MainActivity : AppCompatActivity
    {
        private const int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;

        LinearLayout buttonBridge;
        LinearLayout buttonPrinter;
        LinearLayout buttonSat;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.activity_main);

            buttonBridge = FindViewById<LinearLayout>(Resource.Id.buttonBridge);
            buttonPrinter = FindViewById<LinearLayout>(Resource.Id.buttonPrinter);
            buttonSat = FindViewById<LinearLayout>(Resource.Id.buttonSat);

            buttonBridge.Click += delegate { ActivityUtils.StartNewActivity(this, typeof(BridgeActivity)); };
            buttonPrinter.Click += delegate { ActivityUtils.StartNewActivity(this, typeof(PrinterActivity)); };
            buttonSat.Click += delegate { ActivityUtils.StartNewActivity(this, typeof(SatActivity)); };

            AskWriteExternalStoragePermission();
        }

        private void AskWriteExternalStoragePermission()
        {
            ActivityCompat.RequestPermissions(this, new string[] { Manifest.Permission.WriteExternalStorage }, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }

        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Android.Content.PM.Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            //Impede que a aplicação continue caso a permissão seja negada, uma vez que vários módulos dependem da permissão de acesso ao armazenamento
            if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.Length > 0 && grantResults[0] == Permission.Denied)
            {
                Toast.MakeText(this, "É necessário conceder a permissão para várias funcionalidades da aplicação!", ToastLength.Long).Show();
                CloseApplication();
            }
        }

        //Força o fechamento da aplicação
        private void CloseApplication()
        {
            if (Build.VERSION.SdkInt >= BuildVersionCodes.JellyBean)
                FinishAffinity();
            else
                Finish();
        }
    }
}