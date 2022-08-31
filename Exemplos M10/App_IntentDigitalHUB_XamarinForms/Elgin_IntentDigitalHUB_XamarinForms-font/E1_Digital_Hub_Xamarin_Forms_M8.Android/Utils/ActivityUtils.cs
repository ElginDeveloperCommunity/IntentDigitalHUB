using Android.App;
using Android.Content;
using System.Threading.Tasks;
using System.IO;
using Path = System.IO.Path;
using Bitmap = Android.Graphics.Bitmap;
using Android.Graphics;
using System.Text;
using Xamarin.Forms;
using Xamarin.Essentials;
using Xamarin_Forms_Intent_Digital_Hub.Utils;
using Android.Util;
using Android.Widget;
using System;

[assembly: Dependency(typeof(Xamarin_Forms_Intent_Digital_Hub.Droid.ActivityUtils))]
namespace Xamarin_Forms_Intent_Digital_Hub.Droid
{
    class ActivityUtils : IActivityUtils
    {
        public void ShowLongToast(string text)
        {
            Toast.MakeText(Platform.CurrentActivity, text, ToastLength.Long).Show();
        }

        /**
         * Função utilizada para ler os XMLs do projeto e então salvá-los dentro da diretório externo raiz da aplicação (Android/data/com.elgin.intent_digital_hub/files/)
         *
         * @param activityForReference Contexto necessário para a função
         * @param xmlFileName          Nome do arquivo .XMl, é o nome do XML encontrado em res/raw do projeto e também será o mesmo nome com o qual o XMl será salvo dentro do diretório da aplicação
         */
        public void LoadXMLFileAndStoreItOnApplicationRootDir(string xmlFileName)
        {
            //Carrega o conteúdo do XML do projeto em String
            string xmlContent = ReadXmlFileAsString(xmlFileName);

            //Em seguida, cria e salva no diretório da aplicação um arquivo .XML a partir da String com o mesmo do xml procurado
            StoreXmlFile(xmlContent, xmlFileName);
        }

        /**
         * Cria arquivo .XML no diretório raiz da aplicação
         *
         * @param activity           Contexto necessário para a função
         * @param xmlContentInString Conteúdo do .XML a ser salvo
         * @param xmlFileName        Nome do arquivo xml
         */
        private void StoreXmlFile(string xmlContentInString, string xmlFileName)
        {
            string newXmlArchive = Path.Combine(GetRootDirectoryPATH(), xmlFileName + ".xml");

            //Não é necessário criar novamente o arquivo, caso o mesmo já exista
            if (!File.Exists(newXmlArchive))
            {
                try
                {
                    File.WriteAllText(newXmlArchive, xmlContentInString);
                }
                catch (IOException e)
                {
                    Console.WriteLine(e.StackTrace);
                }
            }
        }


        /**
         * Lẽ os XMLs do projeto, que estão salvos em res/raw, e retorna o seu conteúdo em String
         *
         * @param xmlFileName          Nome do arquivo .XML a ser lido de dentro do projeto
         * @return xmlReadInString String contendo o texto do arquivo XMl lido
         */
        public string ReadXmlFileAsString(string xmlName)
        {
            string xmlReadInString;

            Stream ins = Platform.CurrentActivity.Resources.OpenRawResource(
                    Platform.CurrentActivity.Resources.GetIdentifier(
                            xmlName,
                            "raw",
                            Platform.CurrentActivity.PackageName
                    )
            );

            StreamReader br = new StreamReader(ins);
            StringBuilder sb = new StringBuilder();

            try
            {
                sb.Append(br.ReadToEnd());
            }
            catch (IOException e)
            {
                Console.WriteLine(e.StackTrace);
            }

            xmlReadInString = sb.ToString();

            return xmlReadInString;
        }

        //Cria e fornece o PATH do diretório da aplicação, utilizado para salvar imagens para impressão e log sat
        public string GetRootDirectoryPATH()
        {
            string path = Platform.CurrentActivity.GetExternalFilesDir(null).AbsolutePath;
            //Cria o diretório que a aplicação utilizara para salvar as mídias, caso não exista
            if (!Directory.Exists(path))
            {
                if (!Directory.CreateDirectory(path).Exists)
                {
                    //Se não foi possível criar o diretório, a exceção será lançada
                    throw new Java.Lang.SecurityException("Permissão não garantida para a criação do diretório externo da aplicação!");
                }
            }

            return path;
        }

        /**
         * Função facilitadora utilizada para retornar o PATH dos arquivos para o Intent Digital Hub para as funções que utilizaram o caminho dos arquivos como método de entrada.
         * Para o Intent Digital Hub não é necessário enviar o PATH absoluto dos arquivos, o caminho inicial já parte do diretório externo do dispositivo, ou seja, para apontarmos para os arquivos salvos e usados pela aplicação basta passar o caminho a partir do diretório externo
         *
         * @param activityForReference  Contexto necessário para a função
         * @param filenameWithExtension Nome do arquivo buscado, com a sua extensão
        */
        public string GetFilePathForIDH(string filenameWithExtension)
        {
            //Comandos por meio de PATH devem iniciar com "path=" antes do argumento do caminho de arquivo
            return "path=" + "/Android/data/" + Platform.CurrentActivity.ApplicationContext.PackageName + "/files/" + filenameWithExtension;
        }

        public Task<Stream> GetImageStreamAsync()
        {
            var activity = (MainActivity)Platform.CurrentActivity;
            PhotoPickerListener listener = new PhotoPickerListener(activity);

            const int RequestBarCode = 1000;
            Intent cameraIntent = new Intent(Intent.ActionPick);

            cameraIntent.SetType("image/*");
            activity.StartActivityForResult(cameraIntent, RequestBarCode);

            return listener.Task;
        }

        public void StoreDefaultImage()
        {
            //Cria a logo que será impressa dentro do diretório da aplicação
            Bitmap elgin_logo_default_print_image = BitmapFactory.DecodeResource(Platform.CurrentActivity.Resources, Resource.Drawable.elgin_logo_default_print_image);
            StoreImage(elgin_logo_default_print_image);
        }

        public void StoreSelectedImage(Stream selectedImage)
        {
            selectedImage.Position = 0;
            Bitmap bitmapImage = BitmapFactory.DecodeStream(selectedImage);
            StoreImage(bitmapImage);
        }

        //Salva uma copia da imagem enviada como bitmap por parametro dentro do diretorio do dispostivo, para a impressao via comando ImprimeImagem
        private void StoreImage(Bitmap image)
        {
            Java.IO.File pictureFile = GetCreatedImage();

            //Salva a imagem
            try
            {
                Stream fos = Platform.CurrentActivity.ContentResolver.OpenOutputStream(Android.Net.Uri.FromFile(pictureFile));
                image.Compress(Bitmap.CompressFormat.Png, 100, fos);
                fos.Close();
            }
            catch (Java.IO.FileNotFoundException e)
            {
                Log.Error("Error", "Arquivo não encontrado: " + e.Message);
                e.PrintStackTrace();
            }
            catch (Java.IO.IOException e)
            {
                Log.Error("Error", "Erro ao acessar o arquivo: " + e.Message);
                e.PrintStackTrace();
            }
        }

        //Cria a imagem que será salva no diretório de mídias da aplicação
        private Java.IO.File GetCreatedImage()
        {
            string rootDirectoryPATH = GetRootDirectoryPATH();

            // A imagem a ser impressa sempre tera o mesmo nome para que a impressão ache o ultimo arquivo salvo
            Java.IO.File mediaFile;
            string mImageName = "ImageToPrint.jpg";
            mediaFile = new Java.IO.File(rootDirectoryPATH + Path.DirectorySeparatorChar + mImageName);
            return mediaFile;
        }

        private class PhotoPickerListener
        {
            private TaskCompletionSource<Stream> Complete = new TaskCompletionSource<Stream>();
            public Task<Stream> Task { get { return Complete.Task; } }

            public static readonly int PickImageId = 1000;

            public PhotoPickerListener(MainActivity activity)
            {
                // subscribe to activity results
                activity.ActivityResult += OnActivityResult;
            }

            private void OnActivityResult(int requestCode, Result resultCode, Intent data)
            {
                // unsubscribe from activity results
                MainActivity activity = (MainActivity)Platform.CurrentActivity;
                activity.ActivityResult -= OnActivityResult;

                // process result
                if (requestCode != PickImageId || (resultCode.ToString() != "2" && resultCode != Result.Ok) || data == null)
                    Complete.TrySetResult(null);
                else
                {
                    Android.Net.Uri uri = data.Data;
                    Stream stream = ((MainActivity)Platform.CurrentActivity).ContentResolver.OpenInputStream(uri);
                    Complete.SetResult(stream);
                }
            }
        }
    }
}