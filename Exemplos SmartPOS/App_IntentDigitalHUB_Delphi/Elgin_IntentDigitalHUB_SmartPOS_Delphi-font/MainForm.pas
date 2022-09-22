unit MainForm;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Objects,
  FMX.Layouts, FMX.Controls.Presentation, FMX.StdCtrls;

type
  TFrmMenu = class(TForm)
    layoutPrincipal: TLayout;
    img_logo: TImage;
    layoutBottom: TLayout;
    Label1: TLabel;
    GridPanelLayout1: TGridPanelLayout;
    rec_carteira_digital: TRectangle;
    Label4: TLabel;
    Image3: TImage;
    rec_barcode: TRectangle;
    Label5: TLabel;
    Image4: TImage;
    rec_elgin_pay: TRectangle;
    Label3: TLabel;
    Image2: TImage;
    rec_impressora: TRectangle;
    Label2: TLabel;
    Image1: TImage;
    Rectangle1: TRectangle;
    Label6: TLabel;
    Image5: TImage;
    procedure rec_barcodeClick(Sender: TObject);
    procedure rec_impressoraClick(Sender: TObject);
    procedure rec_elgin_payClick(Sender: TObject);
    procedure botaoEfeitoMouseDown(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  FrmMenu: TFrmMenu;

implementation

{$R *.fmx}

uses BarCodeForm, ElginPayForm, ImpressoraForm;

procedure TFrmMenu.botaoEfeitoMouseDown(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 0.7;
end;

procedure TFrmMenu.botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 1.0;
end;

procedure TFrmMenu.rec_barcodeClick(Sender: TObject);
begin
  FrmBarCode.Show;
end;

procedure TFrmMenu.rec_elgin_payClick(Sender: TObject);
begin
  FrmElginPay.Show;
end;

procedure TFrmMenu.rec_impressoraClick(Sender: TObject);
begin
  FrmImpressora.Show;
end;

end.
