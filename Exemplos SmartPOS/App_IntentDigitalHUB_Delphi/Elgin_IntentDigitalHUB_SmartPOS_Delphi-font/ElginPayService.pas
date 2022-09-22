unit ElginPayService;

interface
uses
  Androidapi.JNI.GraphicsContentViewText,
  Androidapi.Helpers,
  Androidapi.Jni.App,
  System.Messaging,
  System.SysUtils,
  System.IOUtils,
  System.Types,
  FMX.Dialogs,
  Elgin.Types,
  FMX.Helpers.Android,
  Androidapi.Jni.Os,
  FMX.Platform,
  FMX.Types,
  AndroidAPI.JNIBridge,
  Androidapi.JNI.JavaTypes;

type
//  THandlerCallback = class(TJavaLocal, JHandler_Callback)
//  public
//    function handleMessage(msg: JMessage): Boolean; cdecl;
//  end;
  TCallBack = reference to procedure(msg: string = '');

  TElgin = class

    RetornoCallBack : TCallBack;
    FMessageSubscriptionID : integer;
    context : JContext;

//    constructor Create(activity: JContext);
    procedure IniciarPagamentoCredito(valor, qtdeParcelar:string; tipoFinanciamento: integer; personalizacao:boolean; Retorno: TCallBack);
    procedure IniciarPagamentoDebito(valor: string; personalizacao:boolean; Retorno: TCallBack);
    procedure IniciarCancelamentoVenda(valor, cod, data: string; Retorno: TCallBack);
    procedure IniciarOperacaoAdministrativa(Retorno: TCallBack);
    procedure Personalizacao(Retorno: TCallBack);
    procedure NoPersonalizacao(Retorno: TCallBack);
  private
    function createFileFromInputStream(input: JInputStream): JFile;
    procedure HandleActivityMessage(const Sender: TObject;const msg: TMessage);
    procedure respostaElginPay(dados: JIntent);
    //function AppEvent(AAppEvent: TApplicationEvent; AContext: TObject): Boolean;
    //procedure Retorno;

end;

var
  elginpay_instance : TElgin;
//  elginpay_instance: TElginPay;
  handle: JHandler;
  //retorno_pendente : boolean;
//  FHandlerCallback: THandlerCallback;
  respostaOperacao: string;

implementation


//constructor TElgin.Create(activity: JContext);
//var
//AppEventSvc: IFMXApplicationEventService;
//begin
//   context := activity;
//   instance := TJElginPay.JavaClass.init;
//   FHandlerCallback := THandlerCallback.Create;
//   handle := TJHandler.JavaClass.init(TJLooper.JavaClass.getMainLooper, FHandlerCallback);
//
//   //   retorno_pendente := False;
//
////   if TPlatformServices.Current.SupportsPlatformService
////   (IFMXApplicationEventService, IInterface(AppEventSvc)) then
////   begin
////      AppEventSvc.SetApplicationEventHandler(AppEvent);
////   end;
//end;


procedure TElgin.IniciarPagamentoCredito(valor, qtdeParcelar:string; tipoFinanciamento: integer;
                                        personalizacao:boolean; Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin
  Log.d('Parametro Valor: ' + valor);
  Log.d('Parametro Tipo de Financiamento: ' + IntToStr(tipoFinanciamento));

   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.ELGINPAY'));

   comando:= '[{"funcao":"iniciaVendaCredito",'+
                  '"parametros":{"valorTransacao":"'+ valor +'",'+
                  '"tipoFinanciamento":'+ IntToStr(tipoFinanciamento) + ','+
                  '"numeroParcelas":'+ qtdeParcelar +'}}]';

   log.d('comando: ' + comando);

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);

end;

procedure TElgin.IniciarPagamentoDebito(valor:string; personalizacao:boolean; Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin

  Log.d('Parametro Valor: ' + valor);

   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.ELGINPAY'));

   comando:=  '[{"funcao":"iniciaVendaDebito",'+
                  '"parametros":{"valorTransacao":"'+ valor +'"}}]';

   log.d('comando: ' + comando);

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TElgin.respostaElginPay(dados: JIntent);
var
msg : string;
begin
  if Assigned(dados) then
      msg :=  JStringToString(dados.getStringExtra(StringToJString('retorno')));

   RetornoCallBack(msg);
end;

procedure TElgin.Personalizacao(Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.ELGINPAY'));

   comando:= '[{"funcao":"setPersonalizacao","parametros":'+
                '{"iconeToolbar":"","fonte":"",'+
                '"corFonte":"#FED20B","corFonteTeclado":"#050609",'+
                '"corFundoToolbar":"#FED20B","corFundoTela":"#050609",'+
                '"corTeclaLiberadaTeclado":"#FED20B","corFundoTeclado":"#050609",'+
                '"corTextoCaixaEdicao":"#FED20B","corSeparadorMenu":"#FED20B"}}]' ;

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

procedure TElgin.NoPersonalizacao(Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.ELGINPAY'));

    comando:= '[{"funcao":"setPersonalizacao","parametros":'+
                '{"iconeToolbar":"","fonte":"","corFonte":"#0864a4",'+
                '"corFonteTeclado":"#FFFFFF","corFundoToolbar":"#0864a4",'+
                '"corFundoTela":"#FFFFFF","corTeclaLiberadaTeclado":"#0864a4",'+
                '"corFundoTeclado":"#FFFFFF","corTextoCaixaEdicao":"#0864a4",'+
                '"corSeparadorMenu":"#0864a4"}}]';

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

function TElgin.createFileFromInputStream(input: JInputStream): JFile;
var
  dir: string;
  new_file: JFile;
  output: JOutputStream;
  buffer: TJavaArray<Byte>;
  length: integer;
begin
  dir:= 'batpay.png';
  dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + dir;
  try
    try
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

procedure TElgin.HandleActivityMessage(const Sender: TObject;
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
          respostaElginPay(TMessageResultNotification(msg).Value)
         else if TMessageResultNotification(msg).ResultCode =  TJActivity.JavaClass.RESULT_CANCELED then
              respostaElginPay(TMessageResultNotification(msg).Value)
         else
              ShowMessage('Erro resposta ElginPay');

      end;
    end;

  except
      on e: exception do
        ShowMessage('Erro resposta Bridge: ' + e.Message);
  end;

end;

procedure TElgin.IniciarCancelamentoVenda(valor, cod, data:string; Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin
//  instance.iniciaCancelamentoVenda(StringToJString(valor),
//                                  StringToJString(cod),
//                                  StringToJString(data),
//                                  context,
//                                  handle);

   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.ELGINPAY'));

   comando:=  '[{"funcao":"iniciaCancelamentoVenda",'+
                  '"parametros":{"valorTotal":"'+ valor +'",'+
                  '"ref":"' + cod + '",'+
                  '"data":"'+ data +'"}}]';

   log.d('comando: ' + comando);

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);

end;

procedure TElgin.IniciarOperacaoAdministrativa(Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin
//  instance.iniciaOperacaoAdministrativa(context,
//                                  handle);
   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.ELGINPAY'));

   comando:=  '[{"funcao":"iniciaOperacaoAdministrativa",'+
                  '"parametros":{}}]';

   log.d('comando: ' + comando);

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);

end;

//procedure TElgin.Retorno;
//var
//  res: JMessage;
//begin
//   try
//   res:= handle.obtainMessage;
//   showmessage('Iniciar Pagamento');
//
//   // showmessage(intTostr(res.what));
//   // showmessage(BoolToStr(handle.hasMessages(res.what)));
//   showmessage(JStringToString(res.toString));
//   showmessage(JStringToString((res.obj) as JString));
//
//
//
//   finally
//     retorno_pendente := False;
//   end;
//
//
//end;


//function THandlerCallback.handleMessage(msg: JMessage): Boolean;
//begin
//  Log.d('Handle: ' + JStringToString(TJString.Wrap(msg.obj)));
//  respostaOperacao:= JStringToString(TJString.Wrap(msg.obj));
//  showmessage('Handle: ' + respostaOperacao);
//  Result := True;
//end;

//function TElgin.AppEvent(AAppEvent: TApplicationEvent;
//AContext: TObject): Boolean;
//begin
//  case AAppEvent of
//    TApplicationEvent.BecameActive:
//    begin
//      Log.d('event: BecameActive');
//      // if retorno_pendente = True then Retorno();
//    end;
//  end;
//end;

initialization
  elginpay_instance := TElgin.Create;
end.