using System;
using Xamarin.Forms;

using Android.App;
using Android.Content.PM;
using Android.Runtime;
using Android.OS;
using Android.Content;

namespace Xamarin_Forms_Intent_Digital_Hub.Droid
{
    [Activity(Label = "Xamarin_Forms_Intent_Digital_Hub",RoundIcon = "@mipmap/ic_launcher_round", Icon = "@mipmap/ic_launcher", Theme = "@style/MainTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize )]
    public class MainActivity : Xamarin.Forms.Platform.Android.FormsAppCompatActivity
    {
        public event Action<int, Result, Intent> ActivityResult;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            Forms.Init(this, savedInstanceState);
            LoadApplication(new App());
        }
        
        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);
            if (ActivityResult != null)
            {
                ActivityResult(requestCode, resultCode, data);
                return;
            }

            if (resultCode == Result.Ok)
            {
                string retorno = data.GetStringExtra("retorno");

                Tuple<int, string> intentData = new Tuple<int, string>(requestCode, retorno);
                MessagingCenter.Send(Xamarin.Forms.Application.Current, "digital_hub_intent_result", intentData);
            }
        }
    }
}