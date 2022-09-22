unit BarCodeForm;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Objects,
  FMX.Controls.Presentation, FMX.StdCtrls, FMX.Layouts,
  Androidapi.JNI.GraphicsContentViewText,
  Androidapi.Helpers,
  Androidapi.JNI.JavaTypes,
  AndroidAPI.JNIBridge;

type
  TFrmBarCode = class(TForm)
    layoutPrincipal: TLayout;
    layoutHeader: TLayout;
    lbl_titulo: TLabel;
    img_logo: TImage;
    layoutFooter: TLayout;
    Label1: TLabel;
    layoutBody: TLayout;
    LayoutButton: TLayout;
    Rectangle1: TRectangle;
    Label2: TLabel;
    Rectangle4: TRectangle;
    Label4: TLabel;
    LayoutArea: TLayout;
    Rectangle2: TRectangle;
    Label3: TLabel;
    Label5: TLabel;
    Rectangle3: TRectangle;
    txtTipo: TLabel;
    Rectangle5: TRectangle;
    txtCodigo: TLabel;
    procedure Rectangle4Click(Sender: TObject);
    procedure Rectangle1Click(Sender: TObject);
    procedure botaoEfeitoMouseDown(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
  private
    { Private declarations }
    procedure Retorno(msg: string);
  public
    { Public declarations }
  end;

var
  FrmBarCode: TFrmBarCode;

implementation

{$R *.fmx}

uses BarCode;

procedure TFrmBarCode.botaoEfeitoMouseDown(Sender: TObject;
  Button: TMouseButton; Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 0.7;
end;

procedure TFrmBarCode.botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 1.0;
end;

procedure TFrmBarCode.Rectangle1Click(Sender: TObject);
begin
  barcode_instance.lerCodigo(Retorno);
//  ShowMessage('asdfasdf');
end;

procedure TFrmBarCode.Rectangle4Click(Sender: TObject);
begin
  txtCodigo.Text:= '';
  txtTipo.Text:= '';
end;

procedure TFrmBarCode.Retorno(msg: string);
var
  text : JJSONObject;
  res: JJSONArray;
begin
  res:= TJJSONArray.JavaClass.init(StringToJString(msg));    // transforma em array
  text:= res.getJSONObject(0); // pegar o primeiro elemento do array e transforma em json

  log.d('Retorno: ' + msg);

  txtCodigo.Text:= JStringToString(text.getString(StringToJString('resultado'))).Split(['"'])[5];
  txtTipo.Text:= JStringToString(text.getString(StringToJString('resultado'))).Split(['"'])[7];
//  log.d(JStringToString(text.getString(StringToJString('resultado'))).Split(['"'])[7]);

end;

end.