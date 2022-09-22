program E1_IDH_POS;



{$R *.dres}

uses
  System.StartUpCopy,
  FMX.Forms,
  MainForm in 'MainForm.pas' {FrmMenu},
  BarCodeForm in 'BarCodeForm.pas' {FrmBarCode},
  ElginPayForm in 'ElginPayForm.pas' {FrmElginPay},
  ImpressoraForm in 'ImpressoraForm.pas' {FrmImpressora},
  BarCode in 'BarCode.pas',
  Printer in 'Printer.pas',
  ToastMessage in 'ToastMessage.pas',
  Elgin.Types in 'Elgin.Types.pas',
  ElginPayService in 'ElginPayService.pas';

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TFrmMenu, FrmMenu);
  Application.CreateForm(TFrmBarCode, FrmBarCode);
  Application.CreateForm(TFrmElginPay, FrmElginPay);
  Application.CreateForm(TFrmImpressora, FrmImpressora);
  Application.Run;
end.
