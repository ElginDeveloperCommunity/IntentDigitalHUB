unit MainForm;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, FMX.Objects, FMX.Layouts,
  System.Permissions,
  Androidapi.JNI.OS,
  Androidapi.Helpers;

type
  TFrmMain = class(TForm)
    Layout1: TLayout;
    Image1: TImage;
    GridPanelLayout1: TGridPanelLayout;
    LayoutGP_01: TLayout;
    GridPanelLayout3: TGridPanelLayout;
    recBridge: TRectangle;
    Image7: TImage;
    BRIDGE: TLabel;
    recNFCe: TRectangle;
    Image8: TImage;
    Label7: TLabel;
    LayoutGP_02: TLayout;
    GridPanelLayoutGP_2: TGridPanelLayout;
    recBalanca: TRectangle;
    Image5: TImage;
    Label5: TLabel;
    rectMenuImpressora: TRectangle;
    icon: TImage;
    text: TLabel;
    LayoutGP_03: TLayout;
    GridPanelLayout2: TGridPanelLayout;
    rectMenuTEF: TRectangle;
    Image3: TImage;
    Label3: TLabel;
    redCarteiraDigital: TRectangle;
    Image6: TImage;
    Label6: TLabel;
    LayoutGP_04: TLayout;
    GridPanelLayout4: TGridPanelLayout;
    rectMenuSAT: TRectangle;
    Image4: TImage;
    Label4: TLabel;
    rectMenuBarCode: TRectangle;
    Image2: TImage;
    Label2: TLabel;
    Layout2: TLayout;
    Label1: TLabel;
    Timer1: TTimer;
    Layout3: TLayout;
    GridPanelLayout5: TGridPanelLayout;
    Rectangle2: TRectangle;
    Image10: TImage;
    Label9: TLabel;
    Rectangle3: TRectangle;
    Image11: TImage;
    Label10: TLabel;
    procedure recBridgeClick(Sender: TObject);
    procedure rectMenuSATClick(Sender: TObject);
    procedure rectMenuImpressoraClick(Sender: TObject);
    procedure FormShow(Sender: TObject);
    procedure recBalancaClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  FrmMain: TFrmMain;

implementation

{$R *.fmx}

uses BridgeForm, SatForm, ImpressoraForm, ToastMessage, BalancaForm;



procedure TFrmMain.FormShow(Sender: TObject);
begin

  log.d('teste \\"com aspas\\"');
  PermissionsService.RequestPermissions([JStringToString(TJManifest_permission.JavaClass.READ_EXTERNAL_STORAGE),
                                           JStringToString(TJManifest_permission.JavaClass.WRITE_EXTERNAL_STORAGE)],
    procedure(const APermissions: TArray<string>; const AGrantResults: TArray<TPermissionStatus>)
    begin
      if (Length(AGrantResults) = 2)
      and (AGrantResults[0] = TPermissionStatus.Granted)
      and (AGrantResults[1] = TPermissionStatus.Granted) then
      else
        begin
          messagedlg('� necess�rio permitir!!!',TMsgDlgType.mtConfirmation, mbOKCancel,0,
              procedure(const AResult: System.UITypes.TModalResult)
              begin
                SharedActivity.finish;
              end);

        end;
    end)
end;

procedure TFrmMain.recBalancaClick(Sender: TObject);
begin
  frmBalanca.Show;
end;

procedure TFrmMain.recBridgeClick(Sender: TObject);
begin
  FrmBridge.Show;
end;

procedure TFrmMain.rectMenuImpressoraClick(Sender: TObject);
begin
  frmImpressora.Show;
end;

procedure TFrmMain.rectMenuSATClick(Sender: TObject);
begin
  FrmSat.Show;
end;

end.