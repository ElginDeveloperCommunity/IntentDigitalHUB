unit Sat;

interface

uses
  Androidapi.JNI.GraphicsContentViewText,
  Androidapi.Helpers,
  Androidapi.JNI.JavaTypes,
  System.Classes,
  System.IOUtils,
  System.Messaging,
  Androidapi.Jni.App,
  FMX.Types,
  System.Math,
  AndroidAPI.JNIBridge,
  System.SysUtils,
  FMX.Dialogs;

type
  TCallBack = reference to procedure(msg: string = '');

  TSat = class
    RetornoCallBack       : TCallBack;
    FMessageSubscriptionID: integer;
    private
      { private declarations }
      function FileToString(arquivo: string): string;
      procedure HandleActivityMessage(const Sender: TObject;const msg: TMessage);
      procedure respostaBridge(dados: JIntent);
      procedure longlong(str: string);
    function createFileFromInputStream(input: JInputStream; xml: string): JFile;
    protected
      { protected declarations }
    public
    { public declarations }
    procedure ConsultarSat(Retorno: TCallBack);
    procedure CancelarUltimaVenda(codAtivacao, numeroCFe,dadosCancelamento: string;
      Retorno: TCallBack);
    procedure ConsultarStatusOperacional(codAtivacao: String; Retorno: TCallBack);
    procedure AtivarSat(subComando, codAtivacao, cnpj, cUF: string; Retorno: TCallBack);
    procedure EnviarDadosVenda(codAtivacao, dadosVenda: String; Retorno: TCallBack);
    procedure AssociarAssinatura(codAtivacao, cnpjSH, assinaturaAC: String; Retorno: TCallBack);
    procedure ExtrairLogs(codAtivacao: String; Retorno: TCallBack);
//      function trocarCodigoDeAtivacao(numSessao: Integer; codAtivacao: String; opcao: Integer; novoCodigo: String; confNovoCodigo: String): String;
  end;

var
  sat_instance: TSat;

implementation

{ TSat }

function TSat.FileToString(arquivo: string): string;
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

    on E:Exception do ShowMessage('N�o foi poss�vel abrir o arquivo!');
  end;
end;

procedure TSat.AssociarAssinatura(codAtivacao, cnpjSH, assinaturaAC: String;
  Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
  numSessao: integer;
begin
   RetornoCallBack := Retorno;
   numSessao := RandomRange(1,1000);

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SAT'));

   comando:='[{"funcao":"AssociarAssinatura",'+
                  '"parametros":{"numSessao":'+ IntToStr(numSessao) +','+
                  '"codAtivacao":"'+ codAtivacao +'",'+
                  '"cnpjSH":"'+ cnpjSH +'",'+
                  '"assinaturaAC":"'+ assinaturaAC +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));
   
   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TSat.AtivarSat(subComando, codAtivacao, cnpj, cUF: string;
  Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
  numSessao: integer;
begin
   RetornoCallBack := Retorno;
   numSessao := RandomRange(1,1000);

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SAT'));

   comando:='[{"funcao":"AtivarSAT",'+
                  '"parametros":{"numSessao":'+ IntToStr(numSessao) +','+
                  '"subComando":'+ subComando +','+
                  '"codAtivacao":"'+ codAtivacao +'",'+
                  '"cnpj":"'+ cnpj +'",'+
                  '"cUF":'+ cUF +'}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TSat.CancelarUltimaVenda(codAtivacao, numeroCFe,
  dadosCancelamento: string; Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando, dir, xml: string;
  numSessao: integer;
  file_output:JFile;
  input: JInputStream;
begin
//   dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + dadosCancelamento;
//   dir := StringReplace(dir,'/storage/emulated/0','', [rfReplaceAll, rfIgnoreCase]);
//   log.d('path ' + dir);
//
//   file_output:=  TJFile.JavaClass.init(StringToJString(System.IOUtils.TPath.GetDocumentsPath + PathDelim + dadosCancelamento));
//   input := TJFileInputStream.JavaClass.init(file_output);
//   file_output:= createFileFromInputStream(input, dadosCancelamento);

   RetornoCallBack := Retorno;
   numSessao := RandomRange(1,1000);

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SAT'));

   comando:='[{"funcao":"CancelarUltimaVenda",'+
                  '"parametros":{"numSessao":'+ IntToStr(numSessao) +','+
                  '"codAtivacao":"'+ codAtivacao +'",'+
                  '"numeroCFe":"'+ numeroCFe +'",'+
                  '"dadosCancelamento":"'+ dadosCancelamento +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   log.d('cancelar: ' + comando);

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TSat.ConsultarSat(Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
  numSessao: integer;
begin
   RetornoCallBack := Retorno;
   numSessao := RandomRange(1,1000);

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SAT'));

   comando:='[{"funcao":"ConsultarSat",'+
                  '"parametros":{"numSessao":'+ IntToStr(numSessao) +'}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TSat.ConsultarStatusOperacional(codAtivacao: String;
  Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
  numSessao: integer;
begin
   RetornoCallBack := Retorno;
   numSessao := RandomRange(1,1000);

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SAT'));

   comando:='[{"funcao":"ConsultarStatusOperacional",'+
                  '"parametros":{"numSessao":'+ IntToStr(numSessao) +','+
                  '"codAtivacao":"'+ codAtivacao +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TSat.EnviarDadosVenda(codAtivacao, dadosVenda: String;
  Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando, dir: string;
  numSessao: integer;
  file_output:JFile;
  input: JInputStream;
begin
   dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + dadosVenda;
   dir := StringReplace(dir,'/storage/emulated/0','', [rfReplaceAll, rfIgnoreCase]);

   file_output:=  TJFile.JavaClass.init(StringToJString(System.IOUtils.TPath.GetDocumentsPath + PathDelim + dadosVenda));
   input := TJFileInputStream.JavaClass.init(file_output);
   file_output:= createFileFromInputStream(input, dadosVenda);
   RetornoCallBack := Retorno;


   RetornoCallBack := Retorno;
   numSessao := RandomRange(1,1000);

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SAT'));

   comando:='[{"funcao":"EnviarDadosVenda",'+
                  '"parametros":{"numSessao":'+ IntToStr(numSessao) +','+
                  '"codAtivacao":"'+ codAtivacao +'",'+
                  '"dadosVenda":"path='+ dir +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));


   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TSat.ExtrairLogs(codAtivacao: String; Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
  numSessao: integer;
begin
   RetornoCallBack := Retorno;
   numSessao := RandomRange(1,1000);

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SAT'));

   comando:='[{"funcao":"ExtrairLogs",'+
                  '"parametros":{"numSessao":'+ IntToStr(numSessao) +','+
                  '"codAtivacao":"'+ codAtivacao +'"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TSat.HandleActivityMessage(const Sender: TObject;
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
              ShowMessage('Erro resposta Sat');

      end;
    end;

  except
      on e: exception do
        ShowMessage('Erro resposta Sat: ' + e.Message);
  end;

end;

procedure TSat.longlong(str: string);
begin
  if str.Length > 4000 then
  begin
    log.d(str.Substring(0,4000));
    longlong(str.Substring(4000));
  end
  else
    log.d(str);

end;

procedure TSat.respostaBridge(dados: JIntent);
var
  msg : JJSONObject;
  res : JJSONArray;
begin
  if Assigned(dados) then
  begin
      log.d('----------------------');
      log.d(JStringToString(dados.getStringExtra(StringToJString('retorno'))));
      res:= TJJSONArray.JavaClass.init(dados.getStringExtra(StringToJString('retorno')));
      msg:= res.getJSONObject(0);
  end;

   RetornoCallBack(JStringToString(msg.getString(StringToJString('resultado'))));
end;

function TSat.createFileFromInputStream(input: JInputStream; xml: string): JFile;
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
 sat_instance := TSat.Create;
end.