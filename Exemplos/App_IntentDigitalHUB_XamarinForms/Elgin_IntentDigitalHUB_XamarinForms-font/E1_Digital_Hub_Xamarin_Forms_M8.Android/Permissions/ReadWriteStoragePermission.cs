using System.Collections.Generic;
using Xamarin.Forms;

[assembly: Dependency(typeof(Xamarin_Forms_Intent_Digital_Hub.Droid.Permissions.ReadWriteStoragePermission))]
namespace Xamarin_Forms_Intent_Digital_Hub.Droid.Permissions
{
    public class ReadWriteStoragePermission : Xamarin.Essentials.Permissions.BasePlatformPermission, Xamarin_Forms_Intent_Digital_Hub.Permissions.IWriteExternalStoragePermission
    {
        public override (string androidPermission, bool isRuntime)[] RequiredPermissions => new List<(string androidPermission, bool isRuntime)>
        {
        (Android.Manifest.Permission.WriteExternalStorage, true)
        }.ToArray();
    }
}