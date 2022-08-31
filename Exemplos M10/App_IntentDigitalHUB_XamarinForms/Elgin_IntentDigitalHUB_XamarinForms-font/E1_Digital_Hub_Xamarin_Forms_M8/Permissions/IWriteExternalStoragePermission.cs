using System.Threading.Tasks;
using Xamarin.Essentials;

namespace Xamarin_Forms_Intent_Digital_Hub.Permissions
{
    public interface IWriteExternalStoragePermission
    {
        Task<PermissionStatus> CheckStatusAsync();

        Task<PermissionStatus> RequestAsync();
    }
}
