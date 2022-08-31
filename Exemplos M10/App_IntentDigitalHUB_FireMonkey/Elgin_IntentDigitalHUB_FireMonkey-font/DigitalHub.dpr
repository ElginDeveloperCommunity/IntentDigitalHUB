program DigitalHub;

{$R *.dres}

uses
  System.StartUpCopy,
  FMX.Forms,
  MainForm in 'MainForm.pas' {FrmMain},
  Bridge in 'Bridge.pas',
  Bridge.Types in 'Bridge.Types.pas',
  BridgeForm in 'BridgeForm.pas' {FrmBridge},
  DialogConfigSenhaForm in 'DialogConfigSenhaForm.pas' {frmDialogConfigSenha},
  DialogCupomForm in 'DialogCupomForm.pas' {frmDialogCupom},
  DialogCuponForm in 'DialogCuponForm.pas' {Form1},
  DialogOpAdmForm in 'DialogOpAdmForm.pas' {frmDialogOpAdm},
  DialogTimeoutForm in 'DialogTimeoutForm.pas' {frmDialogTimeout},
  ImpressoraForm in 'ImpressoraForm.pas' {frmImpressora},
  Printer in 'Printer.pas',
  SatForm in 'SatForm.pas' {FrmSat},
  Sat in 'Sat.pas',
  ToastMessage in 'ToastMessage.pas',
  BalancaForm in 'BalancaForm.pas' {frmBalanca},
  Balanca in 'Balanca.pas';

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TFrmMain, FrmMain);
  Application.CreateForm(TFrmBridge, FrmBridge);
  Application.CreateForm(TfrmDialogConfigSenha, frmDialogConfigSenha);
  Application.CreateForm(TfrmDialogCupom, frmDialogCupom);
  Application.CreateForm(TfrmDialogOpAdm, frmDialogOpAdm);
  Application.CreateForm(TfrmDialogTimeout, frmDialogTimeout);
  Application.CreateForm(TFrmSat, FrmSat);
  Application.CreateForm(TfrmImpressora, frmImpressora);
  Application.CreateForm(TfrmBalanca, frmBalanca);
  Application.Run;
end.