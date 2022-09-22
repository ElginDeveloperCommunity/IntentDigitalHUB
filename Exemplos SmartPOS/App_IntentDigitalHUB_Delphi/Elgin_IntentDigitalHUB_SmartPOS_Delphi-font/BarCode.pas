unit BarCode;

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

  TBarCode = class
    RetornoCallBack       : TCallBack;
    FMessageSubscriptionID: integer;

    private
      procedure HandleActivityMessage(const Sender: TObject;const msg: TMessage);
      procedure respostaBarCode(dados: JIntent);
    public
      procedure lerCodigo(Retorno: TCallBack);

  end;

var
  barcode_instance: TBarCode;

implementation

{ TBarCode }

procedure TBarCode.HandleActivityMessage(const Sender: TObject;
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
          respostaBarCode(TMessageResultNotification(msg).Value)
         else if TMessageResultNotification(msg).ResultCode =  TJActivity.JavaClass.RESULT_CANCELED then
              respostaBarCode(TMessageResultNotification(msg).Value)
         else
              ShowMessage('Erro resposta Sat');

      end;
    end;

  except
      on e: exception do
        ShowMessage('Erro resposta Sat: ' + e.Message);
  end;

end;

procedure TBarCode.lerCodigo(Retorno: TCallBack);
var
  IntentBalanca: JIntent;
begin
   RetornoCallBack := Retorno;

   IntentBalanca := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.SCANNER'));


   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBalanca, 4321);
end;

procedure TBarCode.respostaBarCode(dados: JIntent);
var
  msg : JJSONObject;
  res : JJSONArray;
begin
//  if Assigned(dados) then
//  begin
//      log.d('barcode: ' + JStringToString(dados.getStringExtra(StringToJString('retorno'))));
//      res:= TJJSONArray.JavaClass.init(dados.getStringExtra(StringToJString('retorno')));
//      msg:= res.getJSONObject(0);
//  end;

   RetornoCallBack(JStringToString(dados.getStringExtra(StringToJString('retorno'))));
end;

initialization
  barcode_instance := TBarCode.Create;

end.