unit Bridge;

interface

uses
  Androidapi.JNI.GraphicsContentViewText,
  Androidapi.Helpers,
  Androidapi.JNI.JavaTypes,
  System.Classes,
  System.IOUtils,
  System.SysUtils,
  System.Messaging,
  Androidapi.Jni.App,
  FMX.Types,
  AndroidAPI.JNIBridge,
  System.StrUtils,
  FMX.Dialogs;

type
  TCallBack = reference to procedure(msg: string = '');

  TBridge = class
    RetornoCallBack: TCallBack;

    FMessageSubscriptionID : integer;
    private
      { private declarations }
      function FileToString(arquivo : string): string;
      function getHead(ip, portaTransacao, portaStatus, senha: string): string;
      procedure longlong(str: string);
    function createFileFromInputStream(input: JInputStream; xml:string): JFile;
    protected
      { protected declarations }
    public

      procedure HandleActivityMessage(const Sender: TObject;const msg: TMessage);
      procedure respostaBridge(dados: JIntent);

      procedure ConsultarStatus(ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure ConsultarTimeout(ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure ConsultarUltimaTransacao(pdv, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure AtualizarTimeout(seconds, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure IniciaVendaCredito(idTransacao, tipoFinanciamento, numeroParcelas,
        pdv, valorTotal,ip, portaTransacao, portaStatus, senha: string;
        Retorno: TCallBack);
      procedure IniciaVendaDebito(idTransacao, pdv, valorTotal, ip, portaTransacao,
        portaStatus, senha: string;
         Retorno: TCallBack);
      procedure IniciaCancelamentoVenda(idTransacao, pdv, valorTotal,
        dataHora, nsu, ip, portaTransacao, portaStatus, senha: string;
        Retorno: TCallBack);
      procedure IniciaOperacaoAdministrativa(idTransacao, op, pdv, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure ImprimirCupomSat(ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure ImprimirCupomSatCancelamento(assQrCode, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure ImprimirCupomNfce(csc, indexcsc, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
      procedure SetSenhaServer(senha, habilitada, ip, portaTransacao,
                                    portaStatus: string;
                                    Retorno: TCallBack);
      end;

var
  bridge_instance: TBridge;

implementation


function TBridge.FileToString(arquivo: string): string;
var
  TextFile: TStringList;
begin

  arquivo := System.IOUtils.TPath.GetDocumentsPath + PathDelim + arquivo;
  TextFile := TStringList.Create;
  try
    try
      TextFile.LoadFromFile(arquivo);

      Result := TextFile.Text;

    finally
      FreeAndNil(TextFile);
    end
  except
    on E:Exception do ShowMessage('N�o foi poss�vel abrir o arquivo XML!');
  end;
end;

function TBridge.getHead(ip, portaTransacao, portaStatus, senha: string):string;
begin
  Result := '[{"funcao":"SetServer","parametros":{'+
                '"ipTerminal":"'+ ip +'",'+
                '"portaTransacao":'+ portaTransacao +','+
                '"portaStatus":'+ portaStatus +'}}';

  if senha <> '' then
    Result:= Result + ',{"funcao":"SetSenha", "parametros":{'+
                          '"senha": "'+ senha +'", '+
                          '"habilitada": True}}';

end;

procedure TBridge.HandleActivityMessage(const Sender: TObject;
  const msg: TMessage);
begin

  try
    if msg is TMessageResultNotification then
    begin
      TMessageManager.DefaultManager.Unsubscribe(TMessageResultNotification, FMessageSubscriptionID);
      FMessageSubscriptionID := 0;
      if TMessageResultNotification(msg).RequestCode = 4321 then
      begin
         if TMessageResultNotification(msg).ResultCode = TJActivity.JavaClass.RESULT_OK then
          respostaBridge(TMessageResultNotification(msg).Value)
         else if TMessageResultNotification(msg).ResultCode =  TJActivity.JavaClass.RESULT_CANCELED then
              respostaBridge(TMessageResultNotification(msg).Value)
         else
              ShowMessage('Erro resposta Bridge');

      end;
    end;

  except
      on e: exception do
        ShowMessage('Erro resposta Bridge: ' + e.Message);
  end;

end;


procedure TBridge.IniciaVendaCredito(idTransacao, tipoFinanciamento, numeroParcelas,
                                    pdv, valorTotal, ip, portaTransacao, portaStatus, senha: string;
                                    Retorno: TCallBack);
var
IntentBridge: JIntent;
comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"IniciaVendaCredito",'+
                  '"parametros":{"idTransacao":'+ idTransacao +
                  ',"pdv":"'+ pdv +'",'+
                  '"valorTotal":"'+ valorTotal +'",'+
                  '"tipoFinanciamento ":'+ tipoFinanciamento +','+
                  '"numParcelas":'+ numeroParcelas +'}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.IniciaVendaDebito(idTransacao, pdv, valorTotal, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
IntentBridge: JIntent;
comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"IniciaVendaDebito",'+
                  '"parametros":{"idTransacao":'+ idTransacao +
                  ',"pdv":"'+ pdv +'",'+
                  '"valorTotal":"'+ valorTotal +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.longlong(str: string);
begin
  if str.Length > 4000 then
  begin
    log.d(str.Substring(0,4000));
    longlong(str.Substring(4000));
  end
  else
    log.d(str);

end;

procedure TBridge.respostaBridge(dados: JIntent);
var
msg : string;
begin
  if Assigned(dados) then
      msg :=  JStringToString(dados.getStringExtra(StringToJString('retorno')));

   RetornoCallBack(msg);
end;


procedure TBridge.SetSenhaServer(senha, habilitada, ip, portaTransacao,
                                    portaStatus: string;
                                    Retorno: TCallBack);
var
IntentBridge: JIntent;
comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"SetSenhaServer",'+
                  '"parametros":{"senha":"'+ senha +
                  '","habilitada":'+ habilitada +'}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.ImprimirCupomNfce(csc, indexcsc, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando, dir, xml: string;
  file_output:JFile;
  input: JInputStream;
begin
   xml:= 'cupomNFCe.xml';
   dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + xml;
   dir := ReplaceStr(dir,'/storage/emulated/0','');

   file_output:=  TJFile.JavaClass.init(StringToJString(System.IOUtils.TPath.GetDocumentsPath + PathDelim + xml));
   input := TJFileInputStream.JavaClass.init(file_output);
   file_output:= createFileFromInputStream(input, xml);

   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"ImprimirCupomNfce",'+
                  '"parametros":{"xml":"path='+ dir +'",'+
                  '"indexcsc":'+ indexcsc +','+
                  '"csc":"'+ csc +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.ImprimirCupomSat(ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
  xml, dir: string;
  IntentBridge: JIntent;
  comando: string;
  file_output:JFile;
  input: JInputStream;
begin
   xml:= 'cupomSat.xml';
   dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + xml;
   dir := ReplaceStr(dir,'/storage/emulated/0','');

   file_output:=  TJFile.JavaClass.init(StringToJString(System.IOUtils.TPath.GetDocumentsPath + PathDelim + xml));
   input := TJFileInputStream.JavaClass.init(file_output);
   file_output:= createFileFromInputStream(input, xml);

   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"ImprimirCupomSat",'+
                  '"parametros":{"xml":"path='+ dir +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.ImprimirCupomSatCancelamento(assQrCode, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
  xml, dir: string;
  IntentBridge: JIntent;
  comando: string;
  file_output:JFile;
  input: JInputStream;
begin
   xml:= 'cupomSatCancelamento.xml';
   dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + xml;
   dir := ReplaceStr(dir,'/storage/emulated/0','');

   file_output:=  TJFile.JavaClass.init(StringToJString(System.IOUtils.TPath.GetDocumentsPath + PathDelim + xml));
   input := TJFileInputStream.JavaClass.init(file_output);
   file_output:= createFileFromInputStream(input, xml);
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"ImprimirCupomSatCancelamento",'+
                  '"parametros":{"xml":"path='+ dir +'",'+
                  '"assQRCode ":"'+ assQrCode +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.IniciaCancelamentoVenda(idTransacao, pdv,valorTotal,
                                          dataHora, nsu, ip, portaTransacao,
                                          portaStatus, senha: string;
                                          Retorno: TCallBack);
var
IntentBridge: JIntent;
comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"IniciaCancelamentoVenda",'+
                  '"parametros":{"idTransacao":'+ idTransacao +
                  ',"pdv":"'+ pdv +'",'+
                  '"valorTotal":"'+ valorTotal +'",'+
                  '"dataHora":"'+ dataHora +'",'+
                  '"nsu":"'+ nsu +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));
   log.d(comando);

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.IniciaOperacaoAdministrativa(idTransacao, op, pdv, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
IntentBridge: JIntent;
comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"IniciaOperacaoAdministrativa",'+
                  '"parametros":{"idTransacao":'+ idTransacao +
                  ',"pdv":"'+ pdv +'",'+
                  '"operacao ":'+ op +'}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.ConsultarStatus(ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
   IntentBridge: JIntent;
   comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"ConsultarStatus",'+
                  '"parametros":{}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.ConsultarTimeout(ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
   IntentBridge: JIntent;
   comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"GetTimeout",'+
                  '"parametros":{}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.AtualizarTimeout(seconds, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
   IntentBridge: JIntent;
   comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"SetTimeout",'+
                  '"parametros":{"timeout":'+ seconds +'}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TBridge.ConsultarUltimaTransacao(pdv, ip, portaTransacao,
                                    portaStatus, senha: string;
                                    Retorno: TCallBack);
var
   IntentBridge: JIntent;
   comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BRIDGE'));

   comando:= getHead(ip, portaTransacao, portaStatus, senha);
   comando:= comando + ',{"funcao":"ConsultarUltimaTransacao",'+
                  '"parametros":{"pdv":"'+ pdv +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

function TBridge.createFileFromInputStream(input: JInputStream; xml: string): JFile;
var
  new_file: JFile;
  output: JOutputStream;
  buffer: TJavaArray<Byte>;
  length: integer;
  dir :string;
begin
  try
    try
      dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + xml;
      new_file:= TJfile.JavaClass.init(StringToJString(dir));
      output:= TJFileOutputStream.JavaClass.init(new_file);

      buffer:= TJavaArray<Byte>.Create(1024);
      length:= input.read(buffer);
      while (length > 0) do
      begin
        output.write(buffer, 0, length);
        length:= input.read(buffer);
      end;
    finally
      output.close;
      input.close;

      Result:= new_file;
    end;
  except
    on E:Exception do ShowMessage('N�o foi poss�vel trasnferir o buffer da imagem !');
  end;

end;

initialization
 bridge_instance := TBridge.Create;

end.
