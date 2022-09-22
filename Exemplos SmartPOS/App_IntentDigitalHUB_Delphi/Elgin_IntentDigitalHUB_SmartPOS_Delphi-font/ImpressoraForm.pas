unit ImpressoraForm;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Actions,
  FMX.ActnList, FMX.StdActns, FMX.MediaLibrary.Actions, FMX.ListBox,
  FMX.StdCtrls, FMX.Edit, FMX.Objects, FMX.Layouts, FMX.TabControl,
  FMX.Controls.Presentation,
  System.Permissions,
  Androidapi.Helpers,
  Androidapi.JNI.OS,
  System.RegularExpressions,
  System.IOUtils;

type
  TFrmImpressora = class(TForm)
    layoutHeader: TLayout;
    lbl_titulo: TLabel;
    img_logo: TImage;
    imgBack: TImage;
    LayoutBody: TLayout;
    TabNavegacao: TTabControl;
    tabMenu: TTabItem;
    Layout6: TLayout;
    GridPanelLayout2: TGridPanelLayout;
    menuImpressaoTexto: TRectangle;
    Image1: TImage;
    btnLabel: TLabel;
    menuImpressaoBarcode: TRectangle;
    Image2: TImage;
    Label2: TLabel;
    menuImpressaoImagem: TRectangle;
    Image3: TImage;
    Label3: TLabel;
    menuStatusImpressora: TRectangle;
    Image4: TImage;
    Label4: TLabel;
    LayoutConectar: TLayout;
    edtIP: TEdit;
    tabImpressaoTexto: TTabItem;
    Rectangle2: TRectangle;
    Label5: TLabel;
    LayoutContainer: TLayout;
    cbNegrito: TCheckBox;
    cbSublinhado: TCheckBox;
    Label12: TLabel;
    Label13: TLabel;
    Label14: TLabel;
    edtMensagem: TEdit;
    rbEsquerda: TRadioButton;
    rbCentralizado: TRadioButton;
    rbDireita: TRadioButton;
    Label15: TLabel;
    Label16: TLabel;
    cbFontSize: TComboBox;
    cbItem17: TListBoxItem;
    cbItem34: TListBoxItem;
    cbItem51: TListBoxItem;
    cbItem68: TListBoxItem;
    cbFontFamily: TComboBox;
    cbItemFontA: TListBoxItem;
    cbItemFontB: TListBoxItem;
    Layout3: TLayout;
    GridPanelLayout1: TGridPanelLayout;
    btnImprimirSAT: TRectangle;
    Label11: TLabel;
    btnImprimirNFCE: TRectangle;
    Label10: TLabel;
    Layout5: TLayout;
    btnImprimirTexto: TRectangle;
    Label9: TLabel;
    tabImpressaoBarcode: TTabItem;
    Rectangle1: TRectangle;
    Label7: TLabel;
    Layout1: TLayout;
    btnImprimirCodigoBarras: TRectangle;
    Label17: TLabel;
    Label20: TLabel;
    Label22: TLabel;
    edtCodigo: TEdit;
    labelHeightBarcode: TLabel;
    labelWidthBarcode: TLabel;
    cbWidthCodigoBarras: TComboBox;
    ListBoxItemW1: TListBoxItem;
    ListBoxItemW2: TListBoxItem;
    ListBoxItemW3: TListBoxItem;
    ListBoxItemW4: TListBoxItem;
    ListBoxItemW5: TListBoxItem;
    ListBoxItemW6: TListBoxItem;
    cbHeightCodigoBarras: TComboBox;
    ListBoxItemH20: TListBoxItem;
    ListBoxItemH60: TListBoxItem;
    ListBoxItemH120: TListBoxItem;
    ListBoxItemH200: TListBoxItem;
    Label21: TLabel;
    cbTipoCodigoBarras: TComboBox;
    ListBoxEAN8: TListBoxItem;
    ListBoxEAN13: TListBoxItem;
    ListBoxQRCODE: TListBoxItem;
    ListBoxUPCA: TListBoxItem;
    ListBoxCODE39: TListBoxItem;
    ListBoxITF: TListBoxItem;
    ListBoxCODEBAR: TListBoxItem;
    ListBoxCODE93: TListBoxItem;
    ListBoxCODE128: TListBoxItem;
    rbCBDireita: TRadioButton;
    rbCBCentralizado: TRadioButton;
    rbCBEsquerda: TRadioButton;
    Label27: TLabel;
    tabImpressaoImagem: TTabItem;
    Rectangle3: TRectangle;
    Label6: TLabel;
    Layout2: TLayout;
    Label25: TLabel;
    ImagePreVisualizacao: TImage;
    Layout4: TLayout;
    btnSelecionar: TRectangle;
    Label18: TLabel;
    btnImprimir: TRectangle;
    Label26: TLabel;
    layoutFooter: TLayout;
    Label1: TLabel;
    ActionList1: TActionList;
    TakePhotoFromLibraryAction1: TTakePhotoFromLibraryAction;
    Layout7: TLayout;
    Layout8: TLayout;
    Label23: TLabel;
    cbImpressora: TComboBox;
    listI9: TListBoxItem;
    listI8: TListBoxItem;
    cbCutPaper: TCheckBox;
    rbImpInterna: TRadioButton;
    rbImpExterna: TRadioButton;
    cbImageCutPaper: TCheckBox;
    cbCodigoBarrasCutPaper: TCheckBox;
    procedure botaoEfeitoMouseDown(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure FormActivate(Sender: TObject);
    procedure menuImpressaoTextoClick(Sender: TObject);
    procedure btnImprimirTextoClick(Sender: TObject);
    procedure Retorno(msg: string = ''; response: integer=0);
    procedure rbImpInternaChange(Sender: TObject);
    procedure imgBackClick(Sender: TObject);
    procedure menuImpressaoBarcodeClick(Sender: TObject);
    procedure menuImpressaoImagemClick(Sender: TObject);
    procedure btnImprimirNFCEClick(Sender: TObject);
    procedure btnImprimirSATClick(Sender: TObject);
    procedure btnImprimirCodigoBarrasClick(Sender: TObject);
    procedure rbImpExternaClick(Sender: TObject);
    procedure rbImpInternaClick(Sender: TObject);
    procedure btnSelecionarClick(Sender: TObject);
    procedure btnImprimirClick(Sender: TObject);
    procedure rbImpExternaChange(Sender: TObject);
    procedure cbImpressoraChange(Sender: TObject);
    procedure menuStatusImpressoraClick(Sender: TObject);
    procedure menuStatusMenuGavetaClick(Sender: TObject);
    procedure cbTipoCodigoBarrasChange(Sender: TObject);
    procedure TakePhotoFromLibraryAction1DidFinishTaking(Image: TBitmap);
  private
    { Private declarations }
    procedure MudarCorBotao(codigo: integer);
    function FileToString(arquivo: string): string;
    procedure longlong(str: string);

    public
    { Public declarations }
  end;

var
  FrmImpressora: TFrmImpressora;
  modelo, type_impressora: string;
  res, status: integer;

implementation

{$R *.fmx}

uses Printer, ToastMessage;

{ TFrmImpressora }

procedure TFrmImpressora.botaoEfeitoMouseDown(Sender: TObject;
  Button: TMouseButton; Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 0.7;
end;

procedure TFrmImpressora.botaoEfeitoMouseUp(Sender: TObject;
  Button: TMouseButton; Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 1.0;
end;

procedure TFrmImpressora.btnImprimirClick(Sender: TObject);
begin
  printer_instance.imprimeImagem(ImagePreVisualizacao.Bitmap,cbImageCutPaper.IsChecked, rbImpInterna.IsChecked, Retorno);
end;

procedure TFrmImpressora.btnImprimirCodigoBarrasClick(Sender: TObject);
var
Alinhamento : string;
barcodeType : BARCODE_TYPE;
width, height : integer;
begin

   Alinhamento := 'Centralizado';

  if rbCBEsquerda.IsChecked then
      Alinhamento := 'Esquerda'
  else if rbCBCentralizado.IsChecked then
      Alinhamento := 'Centralizado'
  else
      Alinhamento := 'Direita';

   case cbTipoCodigoBarras.ItemIndex of
       0 : barcodeType := EAN_8;
       1 : barcodeType := EAN_13;
       2 : barcodeType := QR_CODE;
       3 : barcodeType := UPC_A;
       4 : barcodeType := CODE_39;
       5 : barcodeType := ITF;
       6 : barcodeType := CODE_BAR;
       7 : barcodeType := CODE_93;
       8 : barcodeType := CODE_128;

   end;

   width := cbWidthCodigoBarras.ItemIndex + 1;

   case cbHeightCodigoBarras.ItemIndex of
       0 : height := 20;
       1 : height := 60;
       2 : height := 120;
       3 : height := 200;
   end;

  if not (barcodeType = QR_CODE) then
      printer_instance.imprimeBarCode(barcodeType,edtCodigo.Text,Alinhamento,height,width, cbCodigoBarrasCutPaper.IsChecked, rbImpInterna.IsChecked, Retorno)
   else
      printer_instance.imprimeQR_CODE(edtCodigo.Text,Alinhamento,width,cbCodigoBarrasCutPaper.IsChecked, rbImpInterna.IsChecked, Retorno);
end;


procedure TFrmImpressora.btnImprimirNFCEClick(Sender: TObject);
var
xml : string;
begin
   xml := FileToString('xmlnfce.xml');
   longlong(xml);
   xml := StringReplace(xml,'"','\"',[rfReplaceAll, rfIgnoreCase]);
   printer_instance.ImprimeXMLNFCe(xml,'CODIGO-CSC-CONTRIBUINTE-36-CARACTERES',1,0,cbCutPaper.IsChecked, rbImpInterna.IsChecked, Retorno);

end;

procedure TFrmImpressora.btnImprimirSATClick(Sender: TObject);
var
xml : string;
begin
   xml := FileToString('xmlsat.xml');
   longlong(xml);
   xml := StringReplace(xml,'"','\"',[rfReplaceAll, rfIgnoreCase]);
   printer_instance.imprimeXMLSAT(xml,0,cbCutPaper.IsChecked, rbImpInterna.IsChecked, Retorno);

end;

procedure TFrmImpressora.btnImprimirTextoClick(Sender: TObject);
var
Alinhamento, Font, Texto : string;
FontSize : integer;
begin
  Alinhamento := 'Centralizado';

  if rbEsquerda.IsChecked then
      Alinhamento := 'Esquerda'
  else if rbCentralizado.IsChecked then
      Alinhamento := 'Centralizado'
  else
      Alinhamento := 'Direita';

  case cbFontFamily.ItemIndex of
     0 : Font := 'FONT A';
     1 : Font := 'FONT B';
  end;

  case cbFontSize.ItemIndex of
    0 : FontSize := 17;
    1 : FontSize := 34;
    2 : FontSize := 51;
    3 : FontSize := 68;
  end;

  if edtMensagem.Text = '' then
  begin
    ShowMessage('Digite uma Mensagem!');
    exit;
  end;
   printer_instance.ImprimeTexto(edtMensagem.Text,Alinhamento,Font,FontSize,cbCutPaper.IsChecked,cbSublinhado.IsChecked,cbNegrito.IsChecked, rbImpInterna.IsChecked, Retorno);
end;

procedure TFrmImpressora.btnSelecionarClick(Sender: TObject);
begin
  MessageDlg('Imagens devem possuir no m�ximo 400 pixels de largura!',
    System.UITypes.TMsgDlgType.mtInformation,
    [System.UITypes.TMsgDlgBtn.mbOK], 0,
      procedure(const AResult: System.UITypes.TModalResult)
        begin
          case AResult of
          mrOk: TAction(ActionList1.Actions[0]).Execute;
        end;
      end);

end;

procedure TFrmImpressora.cbImpressoraChange(Sender: TObject);
begin
  case cbImpressora.ItemIndex of
      0: modelo := 'i9';
      1: modelo := 'i8';
  end;

  if rbImpExterna.IsChecked then
    rbImpExternaClick(Sender)
  else if rbImpInterna.IsChecked then
    rbImpInternaClick(Sender);
end;

procedure TFrmImpressora.cbTipoCodigoBarrasChange(Sender: TObject);
begin
   if cbTipoCodigoBarras.ItemIndex = 2 then
   begin
      labelHeightBarcode.Visible := False;
      cbHeightCodigoBarras.Visible := False;

      labelWidthBarcode.Visible := True;
      cbWidthCodigoBarras.Visible := True;
   end
   else if cbTipoCodigoBarras.ItemIndex = 8 then
   begin
      labelHeightBarcode.Visible := True;
      cbHeightCodigoBarras.Visible := True;

      labelWidthBarcode.Visible := True;
      cbWidthCodigoBarras.Visible := True;
   end
   else
   begin
      labelHeightBarcode.Visible := False;
      cbHeightCodigoBarras.Visible := False;

      labelWidthBarcode.Visible := False;
      cbWidthCodigoBarras.Visible := False;
   end;

    case cbTipoCodigoBarras.ItemIndex of
       0 : edtCodigo.Text := '40170725';
       1 : edtCodigo.Text := '0123456789012';
       2 : edtCodigo.Text := 'ELGIN DEVELOPERS COMMUNITY';
       3 : edtCodigo.Text := '123601057072';
       //4 : edtCodigo.Text := 'UPC_E'; // lib nao funciona
       4 : edtCodigo.Text := 'CODE39';
       5 : edtCodigo.Text := '05012345678900';
       6 : edtCodigo.Text := 'A3419500A';
       7 : edtCodigo.Text := 'CODE93';
       8 : edtCodigo.Text := '{C1233';
   end;
end;

function TFrmImpressora.FileToString(arquivo: string): string;
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

procedure TFrmImpressora.FormActivate(Sender: TObject);
begin
    type_impressora:= 'interna';
    status:= 0;
    printer_instance.printerInternalImpStart(Retorno);
    rbImpInterna.IsChecked := True;
    cbImpressora.ItemIndex := 0;
    modelo:= 'i9';
    cbCutPaper.Visible:= False;
    cbCodigoBarrasCutPaper.Visible:=False;
    cbImageCutPaper.Visible:=False;

    rbEsquerda.IsChecked:= True;
    cbFontFamily.ItemIndex := 0;
    cbFontSize.ItemIndex := 2;
    edtMensagem.Text := 'ELGIN DEVELOPER COMMUNITY';
    rbCentralizado.IsChecked := True;
    rbCBCentralizado.IsChecked := True;

    cbTipoCodigoBarras.ItemIndex := 0;
    cbHeightCodigoBarras.ItemIndex := 2;
    cbWidthCodigoBarras.ItemIndex := 2;

    tabnavegacao.ActiveTab := tabImpressaoTexto;
    MudarCorBotao(0);

    tabnavegacao.ActiveTab := tabMenu;


    PermissionsService.RequestPermissions([JStringToString(TJManifest_permission.JavaClass.READ_EXTERNAL_STORAGE),
                                           JStringToString(TJManifest_permission.JavaClass.WRITE_EXTERNAL_STORAGE)],
    procedure(const APermissions: TArray<string>; const AGrantResults: TArray<TPermissionStatus>)
    begin
      if (Length(AGrantResults) = 2)
      and (AGrantResults[0] = TPermissionStatus.Granted)
      and (AGrantResults[1] = TPermissionStatus.Granted) then
      else
        begin
          ShowMessage('Permiss�es para acesso a Biblioteca n�o concedida!');
        end;
    end)


end;

procedure TFrmImpressora.imgBackClick(Sender: TObject);
begin
  imgBack.Visible:= False;
  tabnavegacao.ActiveTab := tabMenu;

   rbImpInterna.IsChecked := True;

   cbCutPaper.Visible:= False;
   cbCodigoBarrasCutPaper.Visible:=False;
   cbImageCutPaper.Visible:=False;
end;

procedure TFrmImpressora.longlong(str: string);
begin
  if str.Length > 4000 then
  begin
    log.d(str.Substring(0,4000));
    longlong(str.Substring(4000));
  end
  else
    log.d(str);
end;

procedure TFrmImpressora.menuImpressaoBarcodeClick(Sender: TObject);
begin
  imgBack.Visible:= True;
  tabnavegacao.ActiveTab := tabImpressaoBarcode;
end;

procedure TFrmImpressora.menuImpressaoImagemClick(Sender: TObject);
begin
  imgBack.Visible:= True;
  tabnavegacao.ActiveTab := tabImpressaoImagem;
end;

procedure TFrmImpressora.menuImpressaoTextoClick(Sender: TObject);
begin
  imgBack.Visible:= True;
  tabnavegacao.ActiveTab := tabImpressaoTexto;
end;

procedure TFrmImpressora.menuStatusImpressoraClick(Sender: TObject);
var
 msg : string;
begin
   status:=1;
   printer_instance.statusSensorPapel(Retorno);

end;

procedure TFrmImpressora.menuStatusMenuGavetaClick(Sender: TObject);
var msgStatusGaveta : string;

begin
//   case printer_instance.StatusGaveta of
//      1 : msgStatusGaveta := 'Gaveta Aberta';
//      2 : msgStatusGaveta := 'Gaveta Fechada';
//      else
//        msgStatusGaveta := 'Status Desconhecido!'
//   end;
//
//   ShowMessage('Status: ' + msgStatusGaveta);
end;


procedure TFrmImpressora.MudarCorBotao(codigo: integer);
var
 cor_ativo: TAlphaColorRec;
begin
  cor_ativo.R := 0;
  cor_ativo.G := 105;
  cor_ativo.B := 165;
  cor_ativo.A := 255;


  menuImpressaoTexto.Stroke.Color := TAlphaColors.Black;
  menuImpressaoBarcode.Stroke.Color := TAlphaColors.Black;
  menuImpressaoImagem.Stroke.Color := TAlphaColors.Black;

  case codigo of
     0 : menuImpressaoTexto.Stroke.Color := TAlphaColor(cor_ativo);
     1 : menuImpressaoBarcode.Stroke.Color := TAlphaColor(cor_ativo);
     2 : menuImpressaoImagem.Stroke.Color := TAlphaColor(cor_ativo);
  end;
end;

procedure TFrmImpressora.rbImpExternaChange(Sender: TObject);
begin
  case cbImpressora.ItemIndex of
      0: modelo := 'i9';
      1: modelo := 'i8';
  end;

  if rbImpExterna.IsChecked then
    rbImpExternaClick(Sender)
  else if rbImpInterna.IsChecked then
    rbImpInternaClick(Sender);

end;

procedure TFrmImpressora.rbImpExternaClick(Sender: TObject);
var
config : TStringList;
tipoImp, Result: integer;
begin
  if TRegEx.IsMatch(edtIP.Text,'^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$') then
  begin
    type_impressora:= 'externa';
    config := TStringList.Create;
    config.StrictDelimiter:= True;
    config.Delimiter := ':';
    config.DelimitedText := edtIP.Text;
    tipoImp := 3;

    Log.d('Imp: \nmodelo: ' + modelo + '\t ip : ' + config[0] + '\t conx�o: ' + inttostr(tipoImp) + '\t port: ' + config[1]);

    printer_instance.PrinterExternalImpStart(modelo, config[0], tipoImp, strtoint(config[1]), Retorno);
    if (res <> 0) then
    begin
      res:= -1;
      type_impressora:= 'interna';
      rbImpInterna.IsChecked := True;
      printer_instance.printerInternalImpStart(Retorno);
      showmessage('Impressora IP n�o conectada');
    end;
  end else
  begin
    ShowMessage('Ip Inv�lido!');
    rbImpInterna.IsChecked := True;
  end;
end;

procedure TFrmImpressora.rbImpInternaChange(Sender: TObject);
var
config : TStringList;
begin
//    if rbImpExterna.IsChecked then
//    begin
//       if TRegEx.IsMatch(edtIP.Text,'^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])-[0-9]+$') then
//       begin
//        config := TStringList.Create;
//        config.StrictDelimiter:=True;
//        config.Delimiter := '-';
//        config.DelimitedText := edtIP.Text;
//        cbCutPaper.Visible:= True;
//        cbCodigoBarrasCutPaper.Visible:=True;
//        cbImageCutPaper.Visible:=True;
//
//        printer_instance.PrinterExternalImpStart(modelo, config[0], tipoImp, strtoint(config[1]), Retorno);
//       end
//       else
//       begin
//         ShowMessage('Ip Inv�lido!');
//         edtIP.Text := '';
//         rbImpInterna.IsChecked := True;
//       end;
//
//    end;

end;
procedure TFrmImpressora.rbImpInternaClick(Sender: TObject);
begin
  rbImpInterna.IsChecked := True;
  printer_instance.printerInternalImpStart(Retorno)
end;

procedure TFrmImpressora.Retorno(msg: string; response: integer);
begin
  log.d('printer: ' + msg);
  log.d('type: ' + type_impressora);
  log.d('res: ' + inttostr(response));
  if (type_impressora = 'externa') then
  begin
    res:= response;
    log.d('resssss' + inttostr(res));
  end;

  // Status Impressora
  if status=1 then
  begin
    log.d('---status---');
    log.d(intToStr(status));
    status:=0;
    log.d('---status---');
    log.d(intToStr(status));

      case response of
          5 : msg := 'Papel est� presente e n�o est� pr�ximo!';
          6 : msg := 'Papel est� pr�ximo do fim!';
          7 : msg := 'Papel ausente!';
          else
             msg := 'Status Desconhecido!'
       end;
       ShowMessage('Status: ' + msg);
  end;

end;


procedure TFrmImpressora.TakePhotoFromLibraryAction1DidFinishTaking(
  Image: TBitmap);
begin
  ImagePreVisualizacao.Bitmap:= Image;
end;

end.