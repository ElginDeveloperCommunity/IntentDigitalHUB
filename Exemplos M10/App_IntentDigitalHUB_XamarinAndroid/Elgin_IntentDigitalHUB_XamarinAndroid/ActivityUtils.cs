using Android.App;
using Android.Content;
using System;
using System.IO;
using System.Text;

namespace Xamarin_Android_Intent_Digital_Hub
{
    /**
     * Classe que utilidades que todas as atividades podem utilizar, reduzindo a repetição de código em funcionalidades com processos similares
    */
    sealed public class ActivityUtils
    {
        //Classe utilitária, não deve ser possível instãnciar
        private ActivityUtils()
        {
        }

        /**
         * Função utilitária que inicia uma nova atividade
         *
         * @param sourceActivity       Contexto necessário da atividade que irá invocar a atividade alvo
         * @param activityClassToStart Classe que representa a Ativity alvos
        */
        public static void StartNewActivity(Activity sourceActivity, Type activityClassToStart)
        {
            Intent intent = new Intent(sourceActivity, activityClassToStart);
            sourceActivity.StartActivity(intent);
        }

        /**
         * Função utilitária que cria um alert e os mostra
         *
         * @param activityContext Contexto necessário para a função
         * @param alertTitle      Título do Alert
         * @param alertMessage    Texto corpo do Alert
        */
        public static void ShowAlertMessage(Context activityContext, string alertTitle, string alertMessage)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(activityContext).Create();
            alertDialog.SetTitle(alertTitle);
            alertDialog.SetMessage(alertMessage);
            alertDialog.SetButton((int)DialogButtonType.Neutral, "OK", (c, ev) =>
            {
                ((IDialogInterface)c).Dispose();
            });
            alertDialog.Show();
        }

        /**
         * Cria, caso não exista, o diretório raiz da aplicação que será ultilizado para salvar os XMLs e a imagem no módulo de impressão de imagem, fornece como retorno do path do diretório (Android/data/com.elgin.intent_digital_hub/files/)
         *
         * @param activity Contexto necessário para a função
         * @return String path do diretório da aplicação
        */
        public static string GetRootDirectoryPATH(Activity activity)
        {
            string path = activity.GetExternalFilesDir(null).AbsolutePath;

            //Cria o diretório que a aplicação utilizara para salvar as mídias, caso não exista
            if (!Directory.Exists(activity.GetExternalFilesDir(null).AbsolutePath))
            {
                if (!Directory.CreateDirectory(path).Exists)
                {
                    //Se não foi possível criar o diretório, a exceção será lançada
                    throw new UnauthorizedAccessException("Permissão não garantida para a criação do diretório externo da aplicação!");
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
        public static string GetFilePathForIDH(Activity activityForReference, string filenameWithExtension)
        {
            //Comandos por meio de PATH devem iniciar com "path=" antes do argumento do caminho de arquivo
            return "path=" + "/Android/data/" + activityForReference.ApplicationContext.PackageName + "/files/" + filenameWithExtension;
        }

        /**
         * Função utilizada para ler os XMLs do projeto e então salvá-los dentro da diretório externo raiz da aplicação (Android/data/com.elgin.intent_digital_hub/files/)
         *
         * @param activityForReference Contexto necessário para a função
         * @param xmlFileName          Nome do arquivo .XMl, é o nome do XML encontrado em res/raw do projeto e também será o mesmo nome com o qual o XMl será salvo dentro do diretório da aplicação
         */
        public static void LoadXMLFileAndStoreItOnApplicationRootDir(Activity activityForReference, string xmlFileName)
        {
            //Carrega o conteúdo do XML do projeto em String
            string xmlContent = ReadXmlFileAsString(activityForReference, xmlFileName);

            //Em seguida, cria e salva no diretório da aplicação um arquivo .XML a partir da String com o mesmo do xml procurado
            StoreXmlFile(activityForReference, xmlContent, xmlFileName);
        }

        /**
         * Lẽ os XMLs do projeto, que estão salvos em res/raw, e retorna o seu conteúdo em String
         *
         * @param activityForReference Contexto necessário para a função
         * @param xmlFileName          Nome do arquivo .XML a ser lido de dentro do projeto
         * @return xmlReadInString String contendo o texto do arquivo XMl lido
         */
        public static string ReadXmlFileAsString(Activity activityForReference, string xmlFileName)
        {
            string xmlReadInString;

            //Todos os .XMLs advindos do projeto estão em res/raw
            Stream ins = activityForReference.Resources.OpenRawResource(
                    activityForReference.Resources.GetIdentifier(
                            xmlFileName,
                            "raw",
                            activityForReference.PackageName
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

        /**
         * Cria arquivo .XML no diretório raiz da aplicação
         *
         * @param activity           Contexto necessário para a função
         * @param xmlContentInString Conteúdo do .XML a ser salvo
         * @param xmlFileName        Nome do arquivo xml
         */
        private static void StoreXmlFile(Activity activity, string xmlContentInString, string xmlFileName)
        {
            string newXmlArchive = Path.Combine(GetRootDirectoryPATH(activity), xmlFileName + ".xml");

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
    }
}