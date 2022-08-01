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
   * Função utilizada para salvar um arquivo XML dentro do diretório externo da aplicação.
   * É invocada em XmlStorageService(chamado após a permissão de escrita ser concedida) onde é feito o salvamento de todos XMLs que a aplicação usará nos exemplos.
   * O IntentDigitalHub permite o envio de 'path' para o comando, podendo então direcionar um XML dentro do dispositivos, como é utilizado e demonstrado neste exemplo.
   * @param args Json contendo o conteúdo que será escrito no arquivo xml bem como nome com o qual o arquivo será salvo.
   */
  storeXmlFile(args: {
    xmlContentInString: string;
    xmlFileName: string;
  }): Promise<void>;

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
