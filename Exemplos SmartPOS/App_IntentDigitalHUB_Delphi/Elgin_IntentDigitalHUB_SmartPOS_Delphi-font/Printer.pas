unit Printer;

interface
  uses
   Androidapi.Helpers,
   Androidapi.JNI.JavaTypes,
   Androidapi.JNI.GraphicsContentViewText,
   FMX.Graphics,
   FMX.Surfaces,
   FMX.Helpers.Android,
   System.SysUtils,
   FMX.Types,
   System.Messaging,
   Androidapi.Jni.App,
   System.IOUtils,
   System.StrUtils,
   AndroidAPI.JNIBridge,
   FMX.Dialogs;


  const
    QTD_LINHAS_I9 = 5;
    QTD_LINHAS_INTERNA = 10;


  type

      BARCODE_TYPE = (UPC_A,UPC_E,EAN_13,JAN_13, EAN_8, JAN_8,CODE_39,ITF, CODE_BAR, CODE_93, CODE_128,QR_CODE);
      TCallBack = reference to procedure(msg: string = ''; response: integer = -1);
      TPrinter = class
      RetornoCallBack: TCallBack;

      FMessageSubscriptionID : integer;
      private

      context : JContext;
      qtd_linhas : integer;
//      USBInitializer : JConUSB;

      function codeOfBarCode(barCodeName: BARCODE_TYPE): integer;
    function createFileFromInputStream(input: JInputStream; xml: string): JFile;

      public

      procedure HandleActivityMessage(const Sender: TObject;const msg: TMessage);
      procedure respostaBridge(dados: JIntent);

      procedure PrinterExternalImpStart(modelo, conexao: string; tipo, port: integer; Retorno: TCallBack);
      procedure printerInternalImpStart(Retorno: TCallBack);
      procedure PrinterStop(Retorno: TCallBack);
      function StatusSensorPapel(Retorno: TCallBack): integer;
      function StatusGaveta() : integer;
      function abrirGaveta() : integer;
      function AvancaLinhas(rbImpInterna:boolean): string;
      function CutPaper(quantLinhas : integer; isCutPaper:boolean) : string;
      procedure ImprimeTexto(text, align, font: string; fontSize: integer;
             isCutPaper, isUnderline, isBold, rbImpInterna: boolean; Retorno: TCallBack);
      procedure ImprimeBarCode(barcodetype: BARCODE_TYPE; text, align: string; height,
  width: integer; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
      procedure ImprimeQR_CODE(text, align : string; qrSize: integer; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
      procedure ImprimeImagem(bitmap : TBitmap; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
      procedure ImprimeXMLNFCe(xmlNFCe, csc : string; indexcsc, param : integer; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack );
      procedure ImprimeXMLSAT(xmlSAT : string; param : integer; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
      function IImprimeCupomTEF(viaCliente: string): integer;


      procedure longlong(str: string);

    end;

var
  printer_instance : TPrinter;
implementation

{ TPrinter }

function TPrinter.AvancaLinhas(rbImpInterna:boolean): string;
begin
  Result:= '{"funcao":"AvancaPapel",'+
        '"parametros":{"linhas": '+ IntToStr(10) +'}}';

end;

function TPrinter.codeOfBarCode(barCodeName: BARCODE_TYPE): integer;
begin
  case barCodeName of
    UPC_A: Result := 0;
    UPC_E: Result := 1;
    EAN_13: Result := 2;
    JAN_13: Result := 2;
    EAN_8: Result := 3;
    JAN_8: Result := 3;
    CODE_39: Result := 4;
    ITF: Result := 5;
    CODE_BAR: Result := 6;
    CODE_93: Result := 7;
    CODE_128: Result := 8;
    else
        Result := 0;
  end;
end;

function TPrinter.CutPaper(quantLinhas: integer; isCutPaper:boolean): string;
begin
   if isCutPaper then
   begin
    Result:= ',{"funcao":"Corte", '+
    '"parametros":{"avanco":'+ IntToStr(qtd_linhas) +'}}]'
   end
   else begin
    Result:= ']';
   end;
end;

procedure TPrinter.HandleActivityMessage(const Sender: TObject;
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
              ShowMessage('Erro resposta Printer');

      end;
    end;

  except
      on e: exception do
        ShowMessage('Erro resposta Printer: ' + e.Message);
  end;

end;

procedure TPrinter.imprimeBarCode(barcodetype: BARCODE_TYPE; text, align: string; height,
  width: integer; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
var
  hri,alignValue : integer;
  IntentTermica: JIntent;
  comando: string;
begin
 hri := 4; //NO PRINT

 alignValue := 0;

   if align = 'Esquerda' then
      alignValue := 0
   else if align = 'Centralizado' then
      alignValue := 1
   else
     alignValue := 2;

   RetornoCallBack := Retorno;

   IntentTermica := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));
   comando := '[{"funcao":"DefinePosicao","parametros":{"posicao":'+ IntToStr(alignValue) +'}}';
   comando:=comando + ',{"funcao":"ImpressaoCodigoBarras",'+
                  '"parametros":{"tipo":'+ IntToStr(codeOfBarCode(barCodeType)) +','+
                  '"dados":"'+ text +'",'+
                  '"altura":'+ IntToStr(height) +','+
                  '"largura ":'+ IntToStr(width)+','+
                  '"HRI ":'+ IntToStr(hri) +'}},';


    comando := comando + AvancaLinhas(rbImpInterna);
    comando := comando + CutPaper(qtd_linhas, isCutPaper);

   IntentTermica.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentTermica, 4321);

end;

// converte delphi bitmap para java bitmap
function BitmapToJBitmap(const ABitmap: TBitmap): JBitmap;
var
  LSurface: TBitmapSurface;
begin
  Result := TJBitmap.JavaClass.createBitmap(ABitmap.Width, ABitmap.Height, TJBitmap_Config.JavaClass.ARGB_8888);
  LSurface := TBitmapSurface.Create;
  try
    LSurface.Assign(ABitmap);
    SurfaceToJBitmap(LSurface, Result);
  finally
    LSurface.Free;
  end;
end;

procedure TPrinter.imprimeImagem(bitmap: TBitmap; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
var
  scale, a: Double;
  dir, path: string;
  IntentBridge: JIntent;
  comando: string;
begin
//  if rbImpInterna=True then
//  begin
    dir:= 'print_img.png';
    dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + dir;

    if bitmap.Width > 600 then
    begin
      scale:= (bitmap.Width-600)/bitmap.Width;
      bitmap.Width:= Round(bitmap.Width * (1.0 - scale));
      bitmap.Height:= Round(bitmap.Height * (1.0 - scale));
    end;
    // ShowMessage(inttostr(bitmap.Width));
    bitmap.SaveToFile(dir);
    dir := ReplaceStr(dir,'/storage/emulated/0','');

    RetornoCallBack := Retorno;
    IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

    comando:='[{"funcao":"ImprimeImagem",'+
                  '"parametros":{"path":"'+ dir +'"}},';

    comando := comando + AvancaLinhas(rbImpInterna);
    comando := comando + CutPaper(qtd_linhas, isCutPaper);

    log.d('---comando: ' + comando);

    IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

    FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
    TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);

//    Result := TJTermica.JavaClass.ImprimeBitmap(BitmapToJBitmap(bitmap))
//  end
//  else ShowMessage('Fun��o em manuten��o!!');

  //Result := TJTermica.JavaClass.ImprimeImagemMemoria(stringtojstring(System.IOUtils.TPath.GetDocumentsPath + PathDelim  + 'barcode_icon.png'),0);
//  AvancaLinhas(rbImpInterna);
//
//  if isCutPaper then
//         CutPaper(qtd_linhas);
end;

procedure TPrinter.imprimeQR_CODE(text, align: string; qrSize: integer;
                                isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
var
  alignValue : integer;
  IntentBridge: JIntent;
  comando: string;
begin

    alignValue := 0;

   if align = 'Esquerda' then
      alignValue := 0
   else if align = 'Centralizado' then
      alignValue := 1
   else
     alignValue := 2;


   RetornoCallBack := Retorno;

   IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

   comando:= '[{"funcao":"DefinePosicao","parametros":{"posicao":'+ IntToStr(alignValue) +'}}';
   comando:= comando + ',{"funcao":"ImpressaoQRCode",'+
                  '"parametros":{"dados":"'+ text +'",'+
                  '"tamanho":'+ IntToStr(qrSize) +','+
                  '"nivelCorrecao ":'+ IntToStr(2) +'}},';

    comando := comando + AvancaLinhas(rbImpInterna);
    comando := comando + CutPaper(qtd_linhas, isCutPaper);

   IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);

//   TJTermica.JavaClass.DefinePosicao(alignValue);
//
//  Result := TJTermica.JavaClass.ImpressaoQRCode(StringToJString(text), qrSize, 2);

//  AvancaLinhas(rbImpInterna);
//
//  if isCutPaper then
//         CutPaper(qtd_linhas);
end;

procedure TPrinter.ImprimeTexto(text, align, font: string; fontSize: integer;
  isCutPaper, isUnderline, isBold, rbImpInterna: boolean; Retorno: TCallBack);
var
  alignValue,styleValue : integer;
  IntentTermica: JIntent;
  comando: string;
begin
   alignValue := 0;
   styleValue := 0;

   if align = 'Esquerda' then
      alignValue := 0
   else if align = 'Centralizado' then
      alignValue := 1
   else
     alignValue := 2;

   if font = 'FONT B' then
      styleValue := 1;

   if isUnderline then
      styleValue := styleValue + 2;

   if isBold then
      styleValue := styleValue + 8;

   RetornoCallBack := Retorno;

   IntentTermica := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

   comando:='[{"funcao":"ImpressaoTexto",'+
                  '"parametros":{"dados":"'+ text +'",'+
                  '"posicao":'+ IntToStr(alignValue) +','+
                  '"stilo":'+ IntToStr(styleValue) +','+
                  '"tamanho ":'+ IntToStr(fontSize)+'}},';

    comando := comando + AvancaLinhas(rbImpInterna);
    comando := comando + CutPaper(qtd_linhas, isCutPaper);

   IntentTermica.putExtra(StringToJString('comando'), StringToJString(comando));



   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentTermica, 4321);

end;

procedure TPrinter.imprimeXMLNFCe(xmlNFCe, csc: string; indexcsc,
  param: integer; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
var
  IntentTermica: JIntent;
  comando: string;
begin
   RetornoCallBack := Retorno;

   IntentTermica := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

   comando:='[{"funcao":"ImprimeXMLNFCe",'+
                  '"parametros":{"dados":"'+ xmlNFCe +'",'+
                  '"indexcsc":'+ IntToStr(indexcsc) +','+
                  '"csc":"'+ csc +'",'+
                  '"int":'+ IntToStr(param)+'}}';

    comando := comando + CutPaper(qtd_linhas, isCutPaper);
    longlong(comando);

   IntentTermica.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentTermica, 4321);

end;

procedure TPrinter.imprimeXMLSAT(xmlSAT: string; param: integer; isCutPaper, rbImpInterna: boolean; Retorno: TCallBack);
var
  IntentTermica: JIntent;
  comando: string;
begin
   RetornoCallBack := Retorno;

   IntentTermica := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

   comando:='[{"funcao":"ImprimeXMLSAT",'+
                  '"parametros":{"dados":"'+ xmlSAT +'",'+
                  '"int":'+ IntToStr(param)+'}}';

   comando := comando + CutPaper(qtd_linhas, isCutPaper);

//   log.d('path= ' + comando);
   longlong(comando)   ;

   IntentTermica.putExtra(StringToJString('comando'), StringToJString(comando));

   FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
   TAndroidHelper.Activity.startActivityForResult(IntentTermica, 4321);

end;

procedure TPrinter.longlong(str: string);
begin
  if str.Length > 4000 then
  begin
    log.d(str.Substring(0,4000));
    longlong(str.Substring(4000));
  end
  else
    log.d(str);
end;

procedure TPrinter.printerExternalImpStart(modelo, conexao: string; tipo, port: integer; Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin
//    printerStop(Retorno);
    qtd_linhas := QTD_LINHAS_I9;

    try
       RetornoCallBack := Retorno;

       IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

        comando:= '[{"funcao":"FechaConexaoImpressora",'+
                      '"parametros":{}},';
        comando:= comando + '{"funcao":"AbreConexaoImpressora",'+
                      '"parametros":{"tipo":'+ IntToStr(tipo) +
                      ',"modelo":"'+ modelo +'",'+
                      '"conexao ":"'+ conexao +'", '+
                      '"parametro ":'+ IntToStr(port) +'}}]';

       IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

       FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
       TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
//        Result := TJTermica.JavaClass.AbreConexaoImpressora(tipo, StringToJString(modelo), StringToJString(conexao), port);
//        Log.d('Result: ' + inttostr(Result));
    except
      on e: exception do
         printerInternalImpStart(Retorno);
    end;

end;

procedure TPrinter.printerInternalImpStart(Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin
//    printerStop(Retorno);
    qtd_linhas := QTD_LINHAS_INTERNA;

    RetornoCallBack := Retorno;

    IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));
    comando:= '[{"funcao":"FechaConexaoImpressora",'+
                      '"parametros":{}},';
    comando:= comando + '{"funcao":"AbreConexaoImpressora",'+
                      '"parametros":{"tipo":'+ IntToStr(6) +
                      ',"modelo":"'+ 'M8' +'",'+
                      '"conexao ":"'+ 'USB' +'", '+
                      '"parametro ":'+ IntToStr(0) +'}}]';

    IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

    FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
    TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);

end;

procedure TPrinter.printerStop(Retorno: TCallBack);
var
  IntentBridge: JIntent;
  comando: string;
begin

    RetornoCallBack := Retorno;

    IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

    comando:= '[{"funcao":"FechaConexaoImpressora",'+
                      '"parametros":{}}]';

    IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

    FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
    TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
//   TJTermica.JavaClass.FechaConexaoImpressora();
end;

procedure TPrinter.respostaBridge(dados: JIntent);
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

   RetornoCallBack(JStringToString(dados.getStringExtra(StringToJString('retorno'))), StrToInt(JStringToString(msg.getString(StringToJString('resultado')))));
end;

function TPrinter.abrirGaveta: integer;

begin
//  Result := TJTermica.JavaClass.AbreGavetaElgin();
end;

function TPrinter.StatusGaveta: integer;

begin
//  Result := TJTermica.JavaClass.StatusImpressora(1);
end;

function TPrinter.IImprimeCupomTEF(viaCliente: string): integer;

begin
//    Result := TJTermica.JavaClass.ImprimeCupomTEF(StringToJString(viaCliente));
end;

function TPrinter.statusSensorPapel(Retorno: TCallBack): integer;
var
  IntentBridge: JIntent;
  comando: string;
begin

    RetornoCallBack := Retorno;

    IntentBridge := TJIntent.JavaClass.init(StringToJString('com.elgin.e1.digitalhub.TERMICA'));

    comando:= '[{"funcao":"StatusImpressora",'+
                      '"parametros":{"param":3}}]';

    IntentBridge.putExtra(StringToJString('comando'), StringToJString(comando));

    FMessageSubscriptionID := TMessageManager.DefaultManager.SubscribeToMessage(TMessageResultNotification, HandleActivityMessage);
    TAndroidHelper.Activity.startActivityForResult(IntentBridge, 4321);
end;

function TPrinter.createFileFromInputStream(input: JInputStream; xml: string): JFile;
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

 printer_instance := TPrinter.Create;

end.

