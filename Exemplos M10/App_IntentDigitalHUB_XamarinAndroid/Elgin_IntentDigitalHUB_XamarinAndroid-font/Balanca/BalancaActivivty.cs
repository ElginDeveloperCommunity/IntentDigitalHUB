using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;
using GoogleGson;
using Java.Util;
using Newtonsoft.Json;
using Org.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Xamarin_Android_Intent_Digital_Hub.IntentServices;
using Xamarin_Android_Intent_Digital_Hub.IntentServices.BALANCA.Commands;

namespace Xamarin_Android_Intent_Digital_Hub.Balanca
{
    [Activity(Label = "BalancaActivivty")]
    public class BalancaActivivty : Activity
    {
        private static Context mContext;

        Button buttonConfigurarBalanca, buttonLerPeso;
        TextView textReturnValueBalanca;

        //Constantes utilizadas para o filtro do retorno da intent em @OnActivityResult
        private const  int CONFIGURAR_BALANCA_REQUESTCODE = 1;
        private const int LER_PESO_REQUESTCODE = 2;

        //Group de seleção de modelos de balança
        private RadioGroup radioGroupBalanceModels;
        //Spinner de dropdown de seleção dos protocolos de comunicação com a balança
        private Spinner spinnerProtocols;

        //Modelo e protocolo selecionados, iniciados com a configuração DP3005 e protocolo 0
        private BalanceModel selectedBalanceModel = BalanceModel.DP3005;
        private BalanceProtocol selectedBalanceProtocol = BalanceProtocol.PROTOCOL_0;

        string typeProtocol;
        string selectedNumProtocl = "";
        string typeModel = "DP30CK";
        int protocol;

        //Criando um novo spinner de teste

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_balanca);
            // Create your application here

            textReturnValueBalanca = FindViewById<TextView>(Resource.Id.textReturnValueBalanca);
            buttonConfigurarBalanca = FindViewById<Button>(Resource.Id.buttonConfigurarBalanca);
            buttonLerPeso = FindViewById<Button>(Resource.Id.buttonLerPeso);            
            radioGroupBalanceModels = FindViewById<RadioGroup>(Resource.Id.radioGroupBalanceModels);
            spinnerProtocols = FindViewById<Spinner>(Resource.Id.spinnerProtocols);

            buttonConfigurarBalanca.Click += buttonConfigurarBalancaFunction;
            buttonLerPeso.Click += ButtonLerPeso_Click;

            radioGroupBalanceModels.CheckedChange += (s, e) =>
            {
                switch (e.CheckedId)
                {
                    case Resource.Id.radioButtonDP3005:
                        selectedBalanceModel = BalanceModel.DP3005;
                        break;
                    case Resource.Id.radioButtonSA110:
                        selectedBalanceModel = BalanceModel.SA110;
                        break;
                    case Resource.Id.radioButtonDPSC:
                        selectedBalanceModel = BalanceModel.DPSC;
                        break;
                    case Resource.Id.radioButtonDP30CK:
                        selectedBalanceModel = BalanceModel.DP30CK;
                        break;
                }
            };

            spinnerProtocols.ItemSelected += new EventHandler<AdapterView.ItemSelectedEventArgs>(spinner_ItemSelected);
            var adapter = ArrayAdapter.CreateFromResource(
            this, Resource.Array.protocols, Android.Resource.Layout.SimpleSpinnerItem);
            adapter.SetDropDownViewResource(Android.Resource.Layout.SimpleSpinnerDropDownItem);
            spinnerProtocols.Adapter = adapter;

            
        }

        private void spinner_ItemSelected(object sender, AdapterView.ItemSelectedEventArgs e)
        {
            Spinner spinner = (Spinner)sender;
            selectedNumProtocl = (string)spinner.GetItemAtPosition(e.Position);

            switch (selectedNumProtocl)
            {
                case "PROTOCOL 0":
                    protocol = 0;
                    break;
                case "PROTOCOL 1":
                    protocol = 1;
                    break;
                case "PROTOCOL 2":
                    protocol = 2;
                    break;
                case "PROTOCOL 3":
                    protocol = 3;
                    break;
                case "PROTOCOL 4":
                    protocol = 4;
                    break;
                case "PROTOCOL 5":
                    protocol = 5;
                    break;
                case "PROTOCOL 6":
                    protocol = 6;
                    break;
                case "PROTOCOL 7":
                    protocol = 7;
                    break;
                default:
                    protocol = 0;
                    break;
            }
        }

        private void buttonConfigurarBalancaFunction(object v, EventArgs ev)
        {

            //const ConfigurarModeloBalanca configurarModeloBalancaCommand = new ConfigurarModeloBalanca(selectedBalanceModel.GetBalanceCode)
            ConfigurarModeloBalanca configurarModeloBalancaCommand = new ConfigurarModeloBalanca(selectedBalanceModel.getBalanceCode());
            Log.Debug("balanceModel", selectedBalanceModel.getBalanceCode().ToString());

            ConfigurarProtocoloComunicacao configurarProtocoloComunicacaoCommand = new ConfigurarProtocoloComunicacao(protocol);
            Log.Debug("protocol", protocol.ToString());

            List<IntentDigitalHubCommand> balancaCommandList = new List<IntentDigitalHubCommand>();

            balancaCommandList.Add(configurarModeloBalancaCommand);
            balancaCommandList.Add(configurarProtocoloComunicacaoCommand);

            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, balancaCommandList, CONFIGURAR_BALANCA_REQUESTCODE);
        }

        private void ButtonLerPeso_Click(object sender, EventArgs e)
        {
            AbrirSerial abrirSerialCommand = new AbrirSerial(2400, 8, 'N', 1);

            LerPeso lerPesoCommand = new LerPeso(1);

            Fechar fecharCommand = new Fechar();

            List<IntentDigitalHubCommand> balancaCommandList = new List<IntentDigitalHubCommand>();

            balancaCommandList.Add(abrirSerialCommand);
            balancaCommandList.Add(lerPesoCommand);
            balancaCommandList.Add(fecharCommand);
                        
            IntentDigitalHubCommandStarter.StartHubCommandActivity(this, balancaCommandList, LER_PESO_REQUESTCODE);
        }

        protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);
            //Log.Debug("resulCode", resultCode.ToString());

            if(resultCode == Result.Ok)
            {
                string retorno = data.GetStringExtra("retorno");
                Log.Debug("resultCode", retorno.ToString());

                /**
                * O retorno das operações Intent Digital Hub será sempre um ArrayJSO; mo módulo balança é mostrado num toast o retorno de todos os comandos realizados em cada botão
                */

                try
                {
                    JSONArray jsonArray = new JSONArray(retorno);
                    JSONObject jsonObjectReturn = new JSONObject();


                    switch (requestCode)
                    {
                        case CONFIGURAR_BALANCA_REQUESTCODE:
                            JSONObject configurarModeloBalancaJson = jsonArray.GetJSONObject(0);

                            JSONObject configurarProtocoloComunicacaoJson = jsonArray.GetJSONObject(1);

                            ConfigurarModeloBalanca configurarModeloBalancaReturn = JsonConvert.DeserializeObject<ConfigurarModeloBalanca>(configurarModeloBalancaJson.ToString());
                            //IniciaVendaDebito iniciaVendaDebitoReturn = JsonConvert.DeserializeObject<IniciaVendaDebito>(jsonObjectReturn.ToString());

                            ConfigurarProtocoloComunicacao configurarProtocoloComunicacaoReturn = JsonConvert.DeserializeObject<ConfigurarProtocoloComunicacao>(configurarProtocoloComunicacaoJson.ToString());

                            Toast.MakeText(this, string.Format("ConfigurarModeloBalanca: {0}\nConfigurarProtocoloComunicacao: {1}", 
                                configurarModeloBalancaReturn.GetResultado(), 
                                configurarProtocoloComunicacaoReturn.GetResultado()), ToastLength.Short).Show();

                            break;
                        case LER_PESO_REQUESTCODE:
                            JSONObject abrirSerialJson = jsonArray.GetJSONObject(0);
                            JSONObject lerPesoJson = jsonArray.GetJSONObject(1);
                            JSONObject fecharJson = jsonArray.GetJSONObject(2);

                            AbrirSerial abrirSerialReturn = JsonConvert.DeserializeObject<AbrirSerial>(abrirSerialJson.ToString());
                            LerPeso lerPesoReturn = JsonConvert.DeserializeObject<LerPeso>(lerPesoJson.ToString());
                            Fechar fecharReturn = JsonConvert.DeserializeObject<Fechar>(fecharJson.ToString());

                            Toast.MakeText(this, string.Format("AbrirSerial: {0}\nLerPeso: {1}\nFechar: {2}", 
                                abrirSerialReturn.GetResultado(), lerPesoReturn.GetResultado(), 
                                fecharReturn.GetResultado()), ToastLength.Short).Show();

                            Double weightRead = Convert.ToDouble(lerPesoReturn.GetResultado());
                                                        
                            if (weightRead > 0.00)
                            {
                                string result = Convert.ToString(weightRead / 1000);
                                textReturnValueBalanca.Text = result;
                            }

                            break;

                    }

                }catch(JSONException e)
                {
                    e.PrintStackTrace();
                }
            }
            else
            {
                ActivityUtils.ShowAlertMessage(this, "Alerta", "O comando não foi bem sucedido!");
            }
        }
    }
}