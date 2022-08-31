unit Balanca;

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

  TBalanca = class
    RetornoCallBack       : TCallBack;
    FMessageSubscriptionID: integer;
    private
      { private declarations }
      procedure HandleActivityMessage(const Sender: TObject;const msg: TMessage);
      procedure longlong(str: string);
    procedure respostaBalanca(dados: JIntent);
    protected
      { protected declarations }
    public
    { public declarations }
      procedure ConfigurarModelo(modeloBalanca, protocoloComunicacao: integer; Retorno: TCallBack);
      procedure LerPeso(parity: Char;baudrate, length, stopbits, qtdLeituras: integer; Retorno: TCallBack);
  end;

var
  balanca_instance: TBalanca;

implementation

{ TSat }


procedure TBalanca.ConfigurarModelo(modeloBalanca, protocoloComunicacao: integer; Retorno: TCallBack);
var
  IntentBalanca: JIntent;
  comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBalanca := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BALANCA'));

   comando:='[{"funcao":"ConfigurarModeloBalanca",'+
                  '"parametros":{"modeloBalanca":'+ IntToStr(modeloBalanca) + '}},'+
              '{"funcao":"ConfigurarProtocoloComunicacao", '+
                  '"parametros":{"protocoloComunicacao": '+ IntToStr(protocoloComunicacao) +'}}]';

   IntentBalanca.putExtra(StringToJString('comando'), StringToJString(comando));

   log.d('comando: ' + comando);
   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBalanca, 4321);
end;

procedure TBalanca.LerPeso(parity: Char; baudrate, length,
  stopbits, qtdLeituras: integer; Retorno: TCallBack);
var
  IntentBalanca: JIntent;
  comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBalanca := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.BALANCA'));

   comando:='[{"funcao":"AbrirSerial",'+
                  '"parametros":{"baudrate": ' + IntToStr(baudrate) + ', '+
                  '"length": ' + IntToStr(length) + ', '+
                  '"parity": "'+ parity +'", '+
                  '"stopbits": '+ IntToStr(stopbits) +'}},'+
              '{"funcao":"LerPeso", '+
                  '"parametros":{"qtdLeituras": '+ IntToStr(qtdLeituras) +'}}, '+
              '{"funcao":"Fechar", '+
                  '"parametros": {}}]';

   IntentBalanca.putExtra(StringToJString('comando'), StringToJString(comando));

   log.d('comando: ' + comando);
   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBalanca, 4321);
end;


procedure TBalanca.HandleActivityMessage(const Sender: TObject;
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
          respostaBalanca(TMessageResultNotification(msg).Value)
         else if TMessageResultNotification(msg).ResultCode =  TJActivity.JavaClass.RESULT_CANCELED then
              respostaBalanca(TMessageResultNotification(msg).Value)
         else
              ShowMessage('Erro resposta Sat');

      end;
    end;

  except
      on e: exception do
        ShowMessage('Erro resposta Sat: ' + e.Message);
  end;

end;


procedure TBalanca.longlong(str: string);
begin
  if str.Length > 4000 then
  begin
    log.d(str.Substring(0,4000));
    longlong(str.Substring(4000));
  end
  else
    log.d(str);

end;

procedure TBalanca.respostaBalanca(dados: JIntent);
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

   RetornoCallBack(JStringToString(dados.getStringExtra(StringToJString('retorno'))));
//   RetornoCallBack(JStringToString(msg.getString(StringToJString('resultado'))));
end;


initialization
 balanca_instance := TBalanca.Create;
end.