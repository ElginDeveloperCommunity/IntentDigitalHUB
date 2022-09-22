declare var Capacitor: any;
const { IntentDigitalHubPlugin } = Capacitor.Plugins;

/**
 * Classe que define os métodos implementados no lado nativo da aplicação (pasta android) e os expõe como uma interface TS, facilitando o uso removendo a necessidade de se importar o plugin em várias páginas.
 */
interface NativeServiceInterface {
  /**
   * Função utilizada para iniciar um comando do Intent Digital Hub.
   * @param args Json contendo o comando e seus parâmetros e o modulo ao qual o comando se refere.
   * @return Json contendo retorna do comando bridge com a chave "intentDigitalHubResponse".
   */
  startDigitalHubIntent(args: {
    commandJson: string;
    idhModuleFilter: string;
  }): Promise<{ intentDigitalHubResponse: string }>;

  /**
   * Função utilizada para pedir a permissão de escrita no diretório externo da aplicação, necessária para salvar os Xmls dentro do diretório da aplicação.
   * É invocada ao iniciar da aplicação, em home.ts, pedindo a permissão e verificando o resultado do request, caso seja negado a aplicação é impedida de continuar uma vez que os arquivos são necessário para o funcionamento de diversos exemplos desta aplicação.
   * @return Json contendo o retorno do request de permissão com a chave "permissionRequestResponse".
   */
  askWriteExternalStoragePermission(): Promise<{
    permissionRequestResponse: boolean;
  }>;
}

export default IntentDigitalHubPlugin as NativeServiceInterface;
