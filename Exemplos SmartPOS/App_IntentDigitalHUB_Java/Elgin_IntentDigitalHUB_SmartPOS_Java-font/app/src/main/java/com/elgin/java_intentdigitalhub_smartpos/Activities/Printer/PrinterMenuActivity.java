package com.elgin.java_intentdigitalhub_smartpos.Activities.Printer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.elgin.java_intentdigitalhub_smartpos.Activities.ActivityUtils;
import com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterPages.PrinterBarCodeActivity;
import com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterPages.PrinterImageActivity;
import com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterPages.PrinterTextActivity;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.AbreConexaoImpressora;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.FechaConexaoImpressora;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.StatusImpressora;
import com.elgin.java_intentdigitalhub_smartpos.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrinterMenuActivity extends AppCompatActivity {

    //RadioGroup de seleção de conexão de impressora interna OU externa.
    private RadioGroup radioGroupPrinterConnection;

    //RadioButton referente à conexão de impressora interna.
    private RadioButton radioButtonConnectPrinterIntern;

    //Campo de IP.
    private EditText editTextInputIP;

    //Botões.
    private Button buttonPrinterText, buttonPrinterBarCode, buttonPrinterImage, buttonStatusPrinter;

    //Método de conexão de impressora, externa e interna.
    public enum PrinterConnectionMethod {
        INTERN,
        EXTERN
    }

    //Modelos de impressora externa disponíveis
    private enum ExternalPrinterModel {
        i8,
        i9
    }

    //Método de conexão com impressora selecionado inicialmente.
    public static PrinterConnectionMethod selectedPrinterConnectionType = PrinterConnectionMethod.INTERN;

    //Códigos utilizados para filtros dos comandos, necessário para o ínicio de um intent e para que seu resultado possa ser capturado em @onActivityResult.
    private static class REQUEST_CODE {
        final static int ABRE_CONEXAO_IMPRESSORA = 1;
        final static int FECHA_CONEXAO_IMPRESSORA = 2;
        final static int STATUS_IMPRESSORA = 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_menu);

        //Atribui as views ao iniciar da tela.
        viewsAssignment();

        //Ao iniciar da tela, a conexão com a impressora interna deve ser estabelecida.
        connectPrinterIntern();

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment();

        //Um exemplo de IP já estará presente no campo de IP.
        editTextInputIP.setText("192.168.0.100:9100");
    }

    //Ao sair da página, a impressora deve ser corretamente desligada.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        FechaConexaoImpressora fechaConexaoImpressoraCommand = new FechaConexaoImpressora();

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, fechaConexaoImpressoraCommand, REQUEST_CODE.FECHA_CONEXAO_IMPRESSORA);
    }

    //Atribuição das views.
    private void viewsAssignment() {
        radioGroupPrinterConnection = findViewById(R.id.radioGroupPrinterConnection);

        radioButtonConnectPrinterIntern = findViewById(R.id.radioButtonConnectPrinterIntern);

        editTextInputIP = findViewById(R.id.editTextInputIP);

        buttonPrinterText = findViewById(R.id.buttonPrinterText);
        buttonPrinterBarCode = findViewById(R.id.buttonPrinterBarCode);
        buttonPrinterImage = findViewById(R.id.buttonPrinterImage);
        buttonStatusPrinter = findViewById(R.id.buttonStatusPrinter);
    }

    //Atribuição das funcionalidades das views.
    private void viewsFunctionalityAssignment() {
        radioGroupPrinterConnection.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                //Caso o botão de impressora interna tenha sido escolhido, inicia a conexão com a impressora interna imediatamente e atualiza a variável de controle.
                case R.id.radioButtonConnectPrinterIntern:
                    //Se a impressora selecionada já for a interna, não é necessário conectar com a impressora interna novamente, isso evita recursão.
                    if (selectedPrinterConnectionType != PrinterConnectionMethod.INTERN)
                        connectPrinterIntern();
                    break;
                case R.id.radioButtonConnectPrinterExtern:
                    //Caso o botão de impressora externa seja selecionado, é necessário validar o ip.
                    if (isIpValid(editTextInputIP.getText().toString())) {
                        //Invoca o dialog que permitirá a escolha do modelo de impressora externa a ser conectado, e posteirormente tentará a conexão.
                        invokeDialogForPrinterModelSelection();
                    }
                    //Caso o ip não seja válido, será avisado em tela e retornará a conexão por impressora interna.
                    else {
                        ActivityUtils.showAlertMessage(this, "Alerta", "O IP inserido não é valido!");
                        connectPrinterIntern();
                    }
            }
        });


        buttonPrinterText.setOnClickListener(v -> ActivityUtils.startNewActivity(this, PrinterTextActivity.class));
        buttonPrinterBarCode.setOnClickListener(v -> ActivityUtils.startNewActivity(this, PrinterBarCodeActivity.class));
        buttonPrinterImage.setOnClickListener(v -> ActivityUtils.startNewActivity(this, PrinterImageActivity.class));

        buttonStatusPrinter.setOnClickListener(v -> checkPrinterStatus());
    }

    //Inicia a conexão com a impressora interna.
    private void connectPrinterIntern() {
        //Atualiza a váriavel de controle.
        selectedPrinterConnectionType = PrinterConnectionMethod.INTERN;
        //Atualiza o radioButton de seleção.
        radioButtonConnectPrinterIntern.setChecked(true);

        AbreConexaoImpressora abreConexaImpressoraCommand = new AbreConexaoImpressora(5, "SMARTPOS", "", 0);

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, abreConexaImpressoraCommand, REQUEST_CODE.ABRE_CONEXAO_IMPRESSORA);
    }

    //Invoca um dialog que permite a opção de escolha entre os modelos de impressora, assim que um modelo for escolhido a conexão com aquele modelo tentará ser estabelecida
    //caso não obtenha sucesso, a conexão com a impressora interna será retorna e uma alerta será joga na tela.
    private void invokeDialogForPrinterModelSelection() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o modelo de impressora a ser conectado");

        //Tornando o dialógo não-cancelável
        builder.setCancelable(false);

        builder.setNegativeButton("CANCELAR", (dialog, which) -> {
            //Se a opção de cancelamento tiver sido escolhida, retorne sempre à opção de impressão por impressora interna
            connectPrinterIntern();

            dialog.dismiss();
        });

        //Cria o vetor de modelos de impressora disponíveis.
        String[] externalPrinterModels = {ExternalPrinterModel.i8.toString(), ExternalPrinterModel.i9.toString()};

        builder.setItems(externalPrinterModels, (dialog, which) -> {
            //A opção 1 no array se refere a impressora i8, consequentemente a opção 2 se refere a impressora i9.
            if (which == 1) connectPrinterExtern(ExternalPrinterModel.i8);
            else connectPrinterExtern(ExternalPrinterModel.i9);
        });

        builder.show();
    }

    //Tenta iniciar conexão com impressora externa, é necessário prover um modelo.
    private void connectPrinterExtern(ExternalPrinterModel externalPrinterModelSelected) {
        //Atualiza variável de controle.
        selectedPrinterConnectionType = PrinterConnectionMethod.EXTERN;

        //O ip deve ser enviado separadamente com a porta.
        final String[] ipAndPort = editTextInputIP.getText().toString().split(":");

        //O ip deve ser passado como string.
        final String ip = ipAndPort[0];
        //A porta deve ser passada como inteiro.
        final int port = Integer.parseInt(ipAndPort[1]);

        AbreConexaoImpressora abreConexaImpressoraCommand = new AbreConexaoImpressora(3, externalPrinterModelSelected.toString(), ip, port);

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, abreConexaImpressoraCommand, REQUEST_CODE.ABRE_CONEXAO_IMPRESSORA);
    }

    //Inicia o comando que verifica o estado da impressora e seu papel, em @onActivityResult, onde é manejado o retorno do comando, será jogado na tela o resultado.
    private void checkPrinterStatus() {
        //A mesma função é utilizada para diversas checagem de status, para o papel da impressora o param a ser enviado é 3;
        final int param = 3;

        StatusImpressora statusImpressoraCommand = new StatusImpressora(param);

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, statusImpressoraCommand, REQUEST_CODE.STATUS_IMPRESSORA);
    }

    //Capturado o resultado dos comandos.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //O retorno dos comandos no IDH, está sempre sob a chave "retorno", no extra da intent de retorno.
            final String retorno = data.getStringExtra("retorno");
            try {
                //Os retornos dos comando do Intent Digital Hub, estão sempre em um Array de Json, neste módulo, apenas alguns retornos terão seus resultados manipulados
                //e estes, se referem apenas a comandos únicos enviados, portanto, o retorno será capturado do único json que o array de retorno terá.

                JSONArray jsonArray = new JSONArray(retorno);
                JSONObject jsonObjectReturn = jsonArray.getJSONObject(0);

                switch (requestCode) {
                    case REQUEST_CODE.ABRE_CONEXAO_IMPRESSORA:

                        Log.d("LOG", jsonObjectReturn.toString());
                        //Se o comando tentou iniciar a conexão com impressora externa, e não obteve sucesso (resultado != 0).
                        if (selectedPrinterConnectionType == PrinterConnectionMethod.EXTERN) {
                            AbreConexaoImpressora abreConexaImpressoraReturn = new Gson().fromJson(jsonObjectReturn.toString(), AbreConexaoImpressora.class);

                            if (abreConexaImpressoraReturn.getResultado() != 0) {
                                ActivityUtils.showAlertMessage(this, "Alerta", "Não foi possível conectar a impressora externa!");
                                //Volta a conexão com a impressora interna.
                                connectPrinterIntern();
                            }
                        }
                        break;
                    case REQUEST_CODE.STATUS_IMPRESSORA:
                        //Lida com o status retornado pelo comando.
                        StatusImpressora statusImpressoraReturn = new Gson().fromJson(jsonObjectReturn.toString(), StatusImpressora.class);

                        String statusMessage = "";

                        switch (statusImpressoraReturn.getResultado()) {
                            case 5:
                                statusMessage = "Papel está presente e não está próximo do fim!";
                                break;
                            case 6:
                                statusMessage = "Papel está próximo do fim!";
                                break;
                            case 7:
                                statusMessage = "Papel ausente!";
                                break;
                            default:
                                statusMessage = "Status desconhecido!";
                        }

                        ActivityUtils.showAlertMessage(this, "Alerta", statusMessage);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Validações

    //Validação de IP
    private boolean isIpValid(String ip) {
        Pattern pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");

        Matcher matcher = pattern.matcher(ip);

        return matcher.matches();
    }
}