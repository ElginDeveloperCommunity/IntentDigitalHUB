using System.IO;
using System.Threading.Tasks;

namespace Xamarin_Forms_Intent_Digital_Hub.Utils
{
    public interface IActivityUtils
    {
        void ShowLongToast(string text);

        void LoadXMLFileAndStoreItOnApplicationRootDir(string xmlFileName);

        string ReadXmlFileAsString(string xmlName);

        string GetRootDirectoryPATH();

        string GetFilePathForIDH(string filenameWithExtension);

        Task<Stream> GetImageStreamAsync();

        void StoreDefaultImage();

        void StoreSelectedImage(Stream selectedImage);
    }
}
