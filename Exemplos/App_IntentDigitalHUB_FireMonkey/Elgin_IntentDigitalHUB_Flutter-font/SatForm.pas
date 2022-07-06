unit SatForm;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Memo.Types,
  FMX.Edit, FMX.StdCtrls, FMX.ScrollBox, FMX.Memo, FMX.Objects, FMX.Layouts,
  FMX.Controls.Presentation,
  System.IOUtils,
  Androidapi.Helpers,
  Androidapi.JNI.JavaTypes,
  System.NetEncoding;

type
  TFrmSat = class(TForm)
    LayoutMain: TLayout;
    LayoutHeading: TLayout;
    Titulo: TLabel;
    Logo: TImage;
    LayoutContent: TLayout;
    GridPanelLayout1: TGridPanelLayout;
    LayoutLeft: TLayout;
    Rectangle1: TRectangle;
    Label3: TLabel;
    Memo1: TMemo;
    LayoutRight: TLayout;
    GridPanelLayout2: TGridPanelLayout;
    rd_smart: TRadioButton;
    rd_satgo: TRadioButton;
    Label2: TLabel;
    LayoutLine: TLayout;
    Edit1: TEdit;
    rec_consultar: TRectangle;
    lbl_consultar: TLabel;
    rec_cancelar: TRectangle;
    lbl_cancelamento: TLabel;
    rec_status: TRectangle;
    lbl_status: TLabel;
    rec_ativar: TRectangle;
    lbl_ativar: TLabel;
    rec_venda: TRectangle;
    lbl_realiza_venda: TLabel;
    rec_associar: TRectangle;
    lbl_associar: TLabel;
    rec_extrair_logs: TRectangle;
    Label4: TLabel;
    LayoutFooter: TLayout;
    Label1: TLabel;

    procedure botaoEfeitoMouseDown(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure Retorno(msg: string);
    procedure rec_consultarClick(Sender: TObject);
    procedure rec_cancelarClick(Sender: TObject);
    procedure rec_statusClick(Sender: TObject);
    procedure rec_ativarClick(Sender: TObject);
    procedure rec_vendaClick(Sender: TObject);
    procedure rec_associarClick(Sender: TObject);
    procedure rec_extrair_logsClick(Sender: TObject);
    procedure FormShow(Sender: TObject);
    procedure rd_satgoChange(Sender: TObject);
  private
    function FileToString(arquivo: string): string;
    { Private declarations }
  public

    { Public declarations }


  end;

var
  FrmSat: TFrmSat;
  xmlEnviaDadosVenda:string;
  xmlCancelamento: string;
  cfeCancelamento:string;
  op: string='';

implementation

{$R *.fmx}

uses Sat;

{ TFrmSat }

procedure TFrmSat.botaoEfeitoMouseDown(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Single);
begin
   TRectangle(Sender).Opacity := 0.7;
end;

procedure TFrmSat.botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Single);
begin
   TRectangle(Sender).Opacity := 1.0;
end;

procedure TFrmSat.rd_satgoChange(Sender: TObject);
begin
  if rd_smart.IsChecked=true then
  begin
    xmlEnviaDadosVenda:= 'xmlenviadadosvendasat.xml';
    xmlCancelamento:= 'sat_cancelamento.xml';
//    xmlCancelamento:= 'cupomSatCancelamento.xml';
  end
  else
  begin
    xmlEnviaDadosVenda:= 'satgo3.xml';
    xmlCancelamento:='cancelamentosatgo.xml';
  end;

end;

procedure TFrmSat.rec_associarClick(Sender: TObject);
begin
  sat_instance.AssociarAssinatura(Edit1.Text, '16716114000172', 'SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT', Retorno);
end;

procedure TFrmSat.rec_ativarClick(Sender: TObject);
begin
  sat_instance.AtivarSat('2', Edit1.Text, '14200166000166', '15', Retorno);
end;

procedure TFrmSat.rec_cancelarClick(Sender: TObject);
var
  res, xml, mimo: string;
  i: integer;
begin
  xml := FileToString(xmlCancelamento);
//  xml := xmlCancelamento;
  xml := StringReplace(xml,'novoCFe',cfeCancelamento,[rfReplaceAll, rfIgnoreCase]);
  xml := StringReplace(xml,'"','\"',[rfReplaceAll, rfIgnoreCase]);
  sat_instance.CancelarUltimaVenda(Edit1.TExt, cfeCancelamento, xml, Retorno);
end;

procedure TFrmSat.rec_consultarClick(Sender: TObject);
begin
  sat_instance.ConsultarSat(Retorno);
end;

procedure TFrmSat.rec_extrair_logsClick(Sender: TObject);
begin
  sat_instance.ExtrairLogs(Edit1.Text, Retorno);
  op:= 'extrair';
end;

procedure TFrmSat.rec_statusClick(Sender: TObject);
begin
  sat_instance.ConsultarStatusOperacional(Edit1.Text, Retorno);
end;

procedure TFrmSat.rec_vendaClick(Sender: TObject);
var
  xml: string;
begin
  op:= 'venda' ;
  xml:= xmlEnviaDadosVenda;
  sat_instance.EnviarDadosVenda(Edit1.Text, xml, Retorno);
end;

procedure TFrmSat.Retorno(msg: string);
var
  mimo, dir, retornoCfe: string;
  i: integer;
  TextFile: TStringList;
  charArray : Array[0..0] of Char;
  cfe : TArray<string>;
begin
//  ShowMessage(msg);
  Log.d('RETORNO: ' + msg);

  Memo1.Lines.Clear;
  if op = 'extrair' then
  begin
    op:= '';


//    dir:= 'logs_SAT.txt';
//    dir:= TPath.GetDownloadsPath + TPath.DirectorySeparatorChar + dir;
//  //  dir:= TPath.GetTempPath +TPath.DirectorySeparatorChar + dir;
//
//    charArray[0] := '|';
//    msg:= (msg.Split(charArray))[5];
//    msg:= TNetEncoding.Base64.Decode(msg);
//    TextFile := TStringList.Create;
//    TextFile.Text:= msg;
//    TextFile.SaveToFile(dir);

    if msg <> 'DeviceNotFound' then
      Memo1.Lines.Add('Log Sat salvo em ');

  end;
  if op='venda' then
  begin
    op:='';
    cfe:= msg.Split(['|']);

    if Length(cfe)>8 then
      cfeCancelamento:= cfe[8];


  end;

  for i := 0 to length(msg)- 1 do
  begin
    mimo:= mimo + msg[i];
    if i mod 40 = 0 then mimo:= mimo + #13#10;
  end;
  Memo1.Lines.Add(mimo);


end;

function TFrmSat.FileToString(arquivo: string): string;
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

procedure TFrmSat.FormShow(Sender: TObject);
begin
  xmlEnviaDadosVenda:= 'xmlenviadadosvendasat.xml';
  xmlCancelamento:= 'sat_cancelamento.xml';
  cfeCancelamento:= '';

  rd_smart.IsChecked:= true;
end;

end.
