unit ElginPayForm;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Edit,
  FMX.StdCtrls, FMX.Objects, FMX.Layouts, FMX.Controls.Presentation,
  Elgin.Types,
  ElginPayService,
  System.RegularExpressions;

type
  TFrmElginPay = class(TForm)
    layoutPrincipal: TLayout;
    layoutHeader: TLayout;
    lbl_titulo: TLabel;
    img_logo: TImage;
    layoutFooter: TLayout;
    Label1: TLabel;
    layoutBody: TLayout;
    GridPanelLayout1: TGridPanelLayout;
    GridPanelLayout6: TGridPanelLayout;
    rec_enviar: TRectangle;
    lbl_enviar: TLabel;
    rec_cancelar: TRectangle;
    lbl_cancelar: TLabel;
    rec_configuracao: TRectangle;
    lbl_configuracao: TLabel;
    cbPersonalizacao: TCheckBox;
    Layout1: TLayout;
    Label5: TLabel;
    Layout3: TLayout;
    Layout9: TLayout;
    rec_credito: TRectangle;
    Image4: TImage;
    Label9: TLabel;
    Layout10: TLayout;
    rec_debito: TRectangle;
    Image3: TImage;
    Label7: TLabel;
    Layout2: TLayout;
    Label2: TLabel;
    Layout4: TLayout;
    Layout5: TLayout;
    rec_a_vista: TRectangle;
    Image1: TImage;
    Label3: TLabel;
    Layout6: TLayout;
    rec_loja: TRectangle;
    Image5: TImage;
    Label4: TLabel;
    Layout7: TLayout;
    rec_adm: TRectangle;
    Image6: TImage;
    Label8: TLabel;
    GridPanelLayout3: TGridPanelLayout;
    Label10: TLabel;
    edtValor: TEdit;
    Label11: TLabel;
    edtParcelas: TEdit;
    chckCancelamento: TCheckBox;
    procedure FormShow(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure SelecionarFormaDePagamento(forma: FORMA_PAGAMENTO_TYPE);
    procedure SelecionarTipoDeParcelamento(forma: PARCELAMENTO_TYPE);
    procedure rec_enviarClick(Sender: TObject);
    procedure botaoEfeitoMouseDown(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Single);
    procedure rec_debitoClick(Sender: TObject);
    procedure rec_creditoClick(Sender: TObject);
    procedure rec_lojaClick(Sender: TObject);
    procedure rec_admClick(Sender: TObject);
    procedure chckCancelamentoChange(Sender: TObject);
    procedure rec_cancelarClick(Sender: TObject);
    procedure rec_configuracaoClick(Sender: TObject);
    procedure rec_a_vistaClick(Sender: TObject);
    procedure cbPersonalizacaoClick(Sender: TObject);
  private
    { Private declarations }
    procedure Retorno(msg: string = '');
  public
    { Public declarations }
  end;

var
  FrmElginPay: TFrmElginPay;
  FormaDePagamento: FORMA_PAGAMENTO_TYPE;
  TipoDeParcelamento : PARCELAMENTO_TYPE;
  TIPO_TRANSACAO : TRANSACAO_TYPE;
  persolizar: integer;

implementation

{$R *.fmx}



{ TFrmElginPay }

procedure TFrmElginPay.botaoEfeitoMouseDown(Sender: TObject;
  Button: TMouseButton; Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 0.7;
end;

procedure TFrmElginPay.botaoEfeitoMouseUp(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Single);
begin
  TRectangle(Sender).Opacity := 1.0;
end;


procedure TFrmElginPay.cbPersonalizacaoClick(Sender: TObject);
begin
  persolizar := 1;
  if cbPersonalizacao.IsChecked = True then
    elginpay_instance.NoPersonalizacao(Retorno)
  else elginpay_instance.Personalizacao(Retorno);
end;

procedure TFrmElginPay.chckCancelamentoChange(Sender: TObject);
begin
  if chckCancelamento.IsChecked = true then
  begin
      Label11.Text := 'C�DIGO:';
      edtParcelas.Text := '';
      edtParcelas.Enabled:= true;
      rec_cancelar.Enabled:= true;
  end
  else if chckCancelamento.IsChecked = false then
  begin
      Label11.Text := 'N� PARCELAS:';
      edtParcelas.Text := '1';
      edtParcelas.Enabled:= false;
      rec_cancelar.Enabled:= false;
  end;

end;

procedure TFrmElginPay.FormClose(Sender: TObject; var Action: TCloseAction);
begin
  rec_credito.Stroke.Color := TAlphaColors.Black;
  rec_debito.Stroke.Color := TAlphaColors.Black;

  rec_a_vista.Stroke.Color := TAlphaColors.Black;
  rec_loja.Stroke.Color := TAlphaColors.Black;
  rec_adm.Stroke.Color := TAlphaColors.Black;

  edtValor.Text := '';
  edtParcelas.Text := '';
end;

procedure TFrmElginPay.FormShow(Sender: TObject);
begin
  edtValor.Text := '2000.00';
  edtParcelas.Text := '1';
  edtParcelas.Enabled:= false;
  rec_credito.Stroke.Color := TAlphaColors.Greenyellow;
  rec_a_vista.Stroke.Color := TAlphaColors.Greenyellow;

  Label11.Text := 'N� PARCELAS:';
  edtParcelas.Text := '1';
  edtParcelas.Enabled:= false;
  rec_cancelar.Enabled:= false;

  SelecionarTipoDeParcelamento(VISTA);
  SelecionarFormaDePagamento(CREDITO);

  persolizar:=1;
  elginpay_instance.NoPersonalizacao(Retorno);

end;

procedure TFrmElginPay.rec_admClick(Sender: TObject);
begin
  edtParcelas.Enabled:= true;
  edtParcelas.Text := '2';
  SelecionarTipoDeParcelamento(ADM);
end;

procedure TFrmElginPay.rec_a_vistaClick(Sender: TObject);
begin
  edtParcelas.Text:='1';
  edtParcelas.Enabled:= false;
  SelecionarTipoDeParcelamento(VISTA);
end;

procedure TFrmElginPay.rec_cancelarClick(Sender: TObject);
var
  data:string;
begin
  data := DateToStr(Date);

  if TRegEx.IsMatch(edtValor.Text, '^([1-9]\d+)(\.\d{1,2})?$') then
    elginpay_instance.IniciarCancelamentoVenda(edtValor.Text, edtParcelas.Text, FormatDateTime('DD/MM/YYYY', Date), Retorno)
  else
    showmessage('Insira um valor v�lido - Ex: 18.99 ou 1899 / 1070.56 ou 107056');
end;

procedure TFrmElginPay.rec_configuracaoClick(Sender: TObject);
begin
  elginpay_instance.IniciarOperacaoAdministrativa(Retorno);
end;

procedure TFrmElginPay.rec_creditoClick(Sender: TObject);
begin
  rec_a_vista.Enabled:= true;
  rec_loja.Enabled:= true;
  rec_adm.Enabled:= true;
  SelecionarFormaDePagamento(CREDITO);
end;

procedure TFrmElginPay.rec_debitoClick(Sender: TObject);
begin
  rec_a_vista.Enabled:= false;
  rec_loja.Enabled:= false;
  rec_adm.Enabled:= false;
  SelecionarFormaDePagamento(DEBITO);
end;

procedure TFrmElginPay.rec_enviarClick(Sender: TObject);
var
  tipoParcela: integer;
begin
  if TRegEx.IsMatch(edtValor.Text, '^([1-9]\d+)(\.\d{1,2})?$') then
  begin
    if TRegEx.IsMatch(edtParcelas.Text,'^[1-9]\d*$') then
      begin
        if FormaDePagamento = CREDITO then
        begin
          case TipoDeParcelamento of
            VISTA: tipoParcela := 1;
            LOJA: tipoParcela := 3;
            ADM: tipoParcela := 2;
          end;

          elginpay_instance.IniciarPagamentoCredito(edtValor.Text, edtParcelas.Text, tipoParcela, cbPersonalizacao.IsChecked, Retorno);
        end
        else
          elginpay_instance.IniciarPagamentoDebito(edtValor.Text, cbPersonalizacao.IsChecked, Retorno)
      end
    else
      showmessage('Insira um n�mero v�lido para parcelas!');
  end
  else
    showmessage('Insira um valor v�lido - Ex: 18.99 ou 1899 / 1070.56 ou 107056');

end;

procedure TFrmElginPay.rec_lojaClick(Sender: TObject);
begin
  edtParcelas.Enabled:= true;
  edtParcelas.Text := '2';
  SelecionarTipoDeParcelamento(LOJA);
end;

procedure TFrmElginPay.Retorno(msg: string);
begin
  if persolizar<>1 then
    ShowMessage(msg);
  persolizar:= 0;
end;

procedure TFrmElginPay.SelecionarFormaDePagamento(forma: FORMA_PAGAMENTO_TYPE);
begin

  rec_credito.Stroke.Color := TAlphaColors.Black;
  rec_debito.Stroke.Color := TAlphaColors.Black;

  FormaDePagamento := forma;

  case forma of
    CREDITO: rec_credito.Stroke.Color := TAlphaColors.Greenyellow;
    DEBITO: rec_debito.Stroke.Color := TAlphaColors.Greenyellow;
  end;

end;

procedure TFrmElginPay.SelecionarTipoDeParcelamento(forma: PARCELAMENTO_TYPE);
begin

  rec_a_vista.Stroke.Color := TAlphaColors.Black;
  rec_loja.Stroke.Color := TAlphaColors.Black;
  rec_adm.Stroke.Color := TAlphaColors.Black;

  TipoDeParcelamento := forma;

  case forma of
    VISTA: rec_a_vista.Stroke.Color := TAlphaColors.Greenyellow;
    LOJA: rec_loja.Stroke.Color := TAlphaColors.Greenyellow;
    ADM: rec_adm.Stroke.Color := TAlphaColors.Greenyellow;
  end;

end;

end.
