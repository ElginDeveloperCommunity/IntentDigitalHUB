package com.elgin.intent_digital_hub.Printer

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter.startHubCommandActivity
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.EditText
import android.os.Bundle
import com.elgin.intent_digital_hub.R
import androidx.appcompat.content.res.AppCompatResources
import com.elgin.intent_digital_hub.Printer.Fragments.PrinterTextFragment
import com.elgin.intent_digital_hub.Printer.Fragments.PrinterBarCodeFragment
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.elgin.intent_digital_hub.Printer.Fragments.PrinterImageFragment
import android.content.DialogInterface
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.AbreConexaoImpressora
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.StatusImpressora
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.AbreGavetaElgin
import android.content.Intent
import android.app.AlertDialog
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import com.elgin.intent_digital_hub.ActivityUtils
import org.json.JSONArray
import com.google.gson.Gson
import org.json.JSONException
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.FechaConexaoImpressora
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Pattern

/*
    Nas funções da impressora é recomendado que utilize a concatenção dos JSON de comandos, enviando um só comundo, por se tratar de processamento em hardware o processo assíncrono deve ser levado em consideração.
 */
class PrinterActivity : AppCompatActivity() {
    private val EXTERNAL_PRINTER_MODEL_I9 = "i9"
    private val EXTERNAL_PRINTER_MODEL_I8 = "i8"
    private lateinit var selectedPrinterModel: String
    private lateinit var buttonPrinterTextSelected: Button
    private lateinit var buttonPrinterBarCodeSelected: Button
    private lateinit var buttonPrinterImageSelected: Button
    private lateinit var buttonStatusPrinter: Button
    private lateinit var buttonStatusGaveta: Button
    private lateinit var buttonAbrirGaveta: Button
    private lateinit var radioGroupConnectPrinterIE: RadioGroup
    private lateinit var radioButtonConnectPrinterIntern: RadioButton
    private lateinit var editTextInputIP: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer)

        //Inicia a impressora interna ao abrir da tela
        connectInternPrinter()

        //Atualiza Fragment
        switchToPrinterTextFragment()
        editTextInputIP = findViewById(R.id.editTextInputIP)
        buttonPrinterTextSelected = findViewById(R.id.buttonPrinterTextSelect)
        buttonPrinterImageSelected = findViewById(R.id.buttonPrinterImageSelect)
        buttonPrinterBarCodeSelected = findViewById(R.id.buttonPrinterBarCodeSelect)
        buttonStatusPrinter = findViewById(R.id.buttonStatus)
        buttonStatusGaveta = findViewById(R.id.buttonStatusGaveta)
        buttonAbrirGaveta = findViewById(R.id.buttonAbrirGaveta)
        radioButtonConnectPrinterIntern = findViewById(R.id.radioButtonConnectPrinterIntern)
        radioGroupConnectPrinterIE = findViewById(R.id.radioGroupConnectPrinterIE)

        //Atualiza a borda selecionada inicialmente
        updateSelectedScreenButtonBorder("Text")
        radioButtonConnectPrinterIntern.setChecked(true)
        editTextInputIP.setText("192.168.0.103:9100")
        buttonPrinterTextSelected.setOnClickListener(View.OnClickListener { v: View? ->
            updateSelectedScreenButtonBorder("Text")
            switchToPrinterTextFragment()
        })
        buttonPrinterBarCodeSelected.setOnClickListener(View.OnClickListener { v: View? ->
            updateSelectedScreenButtonBorder("Barcode")
            switchToPrinterBarCodeFragment()
        })
        buttonPrinterImageSelected.setOnClickListener(View.OnClickListener { v: View? ->
            updateSelectedScreenButtonBorder("Image")
            switchToPrinterImageFragment()
        })
        radioGroupConnectPrinterIE.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { grouo: RadioGroup, checkedId: Int ->
            onRadioConnectPrinterIEChanged(
                grouo,
                checkedId
            )
        })
        buttonStatusPrinter.setOnClickListener(View.OnClickListener { v: View? -> statusPrinter() })
        buttonStatusGaveta.setOnClickListener(View.OnClickListener { v: View? -> statusDrawer() })
        buttonAbrirGaveta.setOnClickListener(View.OnClickListener { v: View? -> openDrawer() })
    }

    private fun updateSelectedScreenButtonBorder(screenSelected: String) {
        buttonPrinterTextSelected!!.backgroundTintList = AppCompatResources.getColorStateList(
            this,
            if (screenSelected == "Text") R.color.azul else R.color.black
        )
        buttonPrinterBarCodeSelected!!.backgroundTintList = AppCompatResources.getColorStateList(
            this,
            if (screenSelected == "Barcode") R.color.azul else R.color.black
        )
        buttonPrinterImageSelected!!.backgroundTintList = AppCompatResources.getColorStateList(
            this,
            if (screenSelected == "Image") R.color.azul else R.color.black
        )
    }

    private fun switchToPrinterTextFragment() {
        val printerTextFragment = PrinterTextFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerFragments, printerTextFragment)
        transaction.commit()
    }

    private fun switchToPrinterBarCodeFragment() {
        val printerBarCodeFragment = PrinterBarCodeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerFragments, printerBarCodeFragment)
        transaction.commit()
    }

    private fun switchToPrinterImageFragment() {
        //Cria a logo que será impressa dentro do diretório da aplicação
        val elgin_logo_default_print_image =
            BitmapFactory.decodeResource(this.resources, R.drawable.elgin_logo_default_print_image)
        storeImage(elgin_logo_default_print_image)
        val printerImageFragment = PrinterImageFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerFragments, printerImageFragment)
        transaction.commit()
    }

    //Validação de IP
    private fun isIpValid(ip: String): Boolean {
        val pattern =
            Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$")
        val matcher = pattern.matcher(ip)
        return matcher.matches()
    }

    private fun onRadioConnectPrinterIEChanged(grouo: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.radioButtonConnectPrinterIntern -> connectInternPrinter()
            R.id.radioButtonConnectPrinterExternByIP -> if (isIpValid(editTextInputIP!!.text.toString())) {
                //Invoca o alertDialog que permite a escolha do modelo de impressora antes da tentativa de iniciar a conexão por IP
                alertDialogSetSelectedPrinterModelThenConnect(EXTERNAL_CONNECTION_METHOD_IP)
            } else {
                //Se não foi possível validar o ip antes da chamada da função, retorne para a conexão com impressora interna
                radioButtonConnectPrinterIntern!!.isChecked = true
                connectInternPrinter()
            }
            R.id.radioButtonConnectPrinterExternByUSB ->                 //Invoca o alertDialog que permite a escolha do modelo de impressora antes da tentativa de iniciar a conexão por IP
                alertDialogSetSelectedPrinterModelThenConnect(EXTERNAL_CONNECTION_METHOD_USB)
        }
    }

    //Dialogo usado para escolher definir o modelo de impressora externa que sera estabelecida a conexao
    fun alertDialogSetSelectedPrinterModelThenConnect(externalConnectionMethod: String) {
        val operations = arrayOf(EXTERNAL_PRINTER_MODEL_I9, EXTERNAL_PRINTER_MODEL_I8)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecione o modelo de impressora a ser conectado")

        //Tornando o dialógo não-cancelável
        builder.setCancelable(false)
        builder.setNegativeButton("CANCELAR") { dialog: DialogInterface, which: Int ->
            //Se a opção de cancelamento tiver sido escolhida, retorne sempre à opção de impressão por impressora interna
            radioButtonConnectPrinterIntern!!.isChecked = true
            val abreConexaoImpressoraCommand = AbreConexaoImpressora(6, "M8", "", 0)
            startHubCommandActivity(
                this,
                abreConexaoImpressoraCommand,
                ABRE_CONEXAO_IMPRESSORA_REQUESTCODE
            )
            dialog.dismiss()
        }
        builder.setItems(operations) { dialog: DialogInterface?, which: Int ->
            //Envia o parâmetro escolhido para a função que atualiza o modelo de impressora selecionado
            setSelectedPrinterModel(which)

            //inicializa depois da seleção do modelo a conexão de impressora, levando em conta o parâmetro que define se a conexão deve ser via IP ou USB
            if (externalConnectionMethod == "USB") connectExternPrinterByUSB() else connectExternPrinterByIP()
        }
        builder.show()
    }

    private fun setSelectedPrinterModel(whichSelected: Int) {
        selectedPrinterModel =
            if (whichSelected == 0) EXTERNAL_PRINTER_MODEL_I9 else EXTERNAL_PRINTER_MODEL_I8
    }

    private fun connectInternPrinter() {
        val abreConexaoImpressoraCommand = AbreConexaoImpressora(6, "M8", "", 0)
        startHubCommandActivity(
            this,
            abreConexaoImpressoraCommand,
            ABRE_CONEXAO_IMPRESSORA_REQUESTCODE
        )
    }

    private fun connectExternPrinterByIP() {
        val ip = editTextInputIP!!.text.toString()
        val ipAndPort = ip.split(":").toTypedArray()
        val abreConexaoImpressoraCommand =
            AbreConexaoImpressora(3, selectedPrinterModel!!, ipAndPort[0], ipAndPort[1].toInt())
        startHubCommandActivity(
            this,
            abreConexaoImpressoraCommand,
            ABRE_CONEXAO_IMPRESSORA_IP_REQUESTCODE
        )
    }

    private fun connectExternPrinterByUSB() {
        val abreConexaoImpressoraCommand =
            AbreConexaoImpressora(1, selectedPrinterModel!!, "USB", 0)
        startHubCommandActivity(
            this,
            abreConexaoImpressoraCommand,
            ABRE_CONEXAO_IMPRESSORA_USB_REQUESTCODE
        )
    }

    private fun statusPrinter() {
        val statusImpressoraCommand = StatusImpressora(3)
        startHubCommandActivity(this, statusImpressoraCommand, STATUS_IMPRESSORA_REQUESTCODE)
    }

    private fun statusDrawer() {
        val statusImpressoraCommand = StatusImpressora(1)
        startHubCommandActivity(
            this,
            statusImpressoraCommand,
            STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE
        )
    }

    private fun openDrawer() {
        val abreGavetaElginCommand = AbreGavetaElgin()
        startHubCommandActivity(this, abreGavetaElginCommand, ABRE_GAVETA_ELGIN_REQUESTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Se o resultado for OK
        Log.d("resultCode", resultCode.toString())
        if (resultCode == RESULT_OK) {
            //Resultado da intent de seleção de imagem
            if (requestCode == OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE) {
                val returnUri = data!!.data
                var bitmapImage: Bitmap? = null
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.contentResolver, returnUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //Atualiza a view pela imagem selecionada na galeria
                PrinterImageFragment.imageView!!.setImageBitmap(bitmapImage)

                //Salva a imagem dentro do diretório da aplicação
                storeImage(bitmapImage)
            } else {
                val retorno = data!!.getStringExtra("retorno")
                Log.d("retorno", retorno!!)
                //O retorno é sempre um JSONArray, no App_Experience apenas um comando é dado por vez, portanto o JSONArray de retorno sempre terá somente um JSON.
                try {
                    val jsonArray = JSONArray(retorno)
                    val jsonObjectReturn = jsonArray.getJSONObject(0)
                    when (requestCode) {
                        ABRE_CONEXAO_IMPRESSORA_REQUESTCODE -> {
                            val abreConexaoImpressoraReturn: AbreConexaoImpressora =
                                Gson().fromJson(
                                    jsonObjectReturn.toString(),
                                    AbreConexaoImpressora::class.java
                                )
                            Log.d(
                                "result printerIntern=",
                                java.lang.String.valueOf(abreConexaoImpressoraReturn.getResultado())
                            )
                        }
                        ABRE_CONEXAO_IMPRESSORA_USB_REQUESTCODE, ABRE_CONEXAO_IMPRESSORA_IP_REQUESTCODE -> {
                            var abreConexaoImpressoraReturn = Gson().fromJson(
                                jsonObjectReturn.toString(),
                                AbreConexaoImpressora::class.java
                            )

                            //Se a conexão não obtiver sucesso, retorne a impressora interna
                            if (abreConexaoImpressoraReturn.getResultado() !== 0) {
                                ActivityUtils.showAlertMessage(
                                    this,
                                    "Alerta",
                                    "A tentativa de conexão por USB não foi bem sucedida"
                                )
                                radioButtonConnectPrinterIntern!!.isChecked = true
                                connectInternPrinter()
                            }
                        }
                        FECHA_CONEXAO_IMPRESSORA_REQUESTCDOE, IMPRESSAO_TEXTO_REQUESTCODE, IMPRIME_XML_NFCE_REQUESTCODE, IMPRIME_XML_SAT_REQUESTCODE, IMPRESSAO_CODIGO_BARRAS_REQUESTCODE, IMPRIME_IMAGEM_REQUESTCODE -> {}
                        STATUS_IMPRESSORA_REQUESTCODE -> {
                            val statusImpressoraReturn: StatusImpressora = Gson().fromJson(
                                jsonObjectReturn.toString(),
                                StatusImpressora::class.java
                            )
                            var statusPrinter = ""
                            statusPrinter = when (statusImpressoraReturn.getResultado()) {
                                5 -> "Papel está presente e não está próximo do fim!"
                                6 -> "Papel próximo do fim!"
                                7 -> "Papel ausente!"
                                else -> "Status Desconhecido!"
                            }
                            ActivityUtils.showAlertMessage(this, "Alert", statusPrinter)
                        }
                        STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE -> {
                            var statusImpressoraReturn = Gson().fromJson(
                                jsonObjectReturn.toString(),
                                StatusImpressora::class.java
                            )
                            var statusGaveta = ""
                            statusGaveta = when (statusImpressoraReturn.getResultado()) {
                                1 -> "Gaveta aberta!"
                                2 -> "Gaveta fechada"
                                else -> "Status Desconhecido!"
                            }
                            ActivityUtils.showAlertMessage(this, "Alert", statusGaveta)
                        }
                        ABRE_GAVETA_ELGIN_REQUESTCODE -> {}
                        else -> ActivityUtils.showAlertMessage(
                            this,
                            "Alerta",
                            "O comando $requestCode não foi encontrado!"
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    ActivityUtils.showAlertMessage(
                        this,
                        "Alerta",
                        "O retorno não está no formato esperado!"
                    )
                }
            }
        } else {
            ActivityUtils.showAlertMessage(this, "Alerta", "O comando não foi bem sucedido!")
        }
    }

    //Desliga a impressora após sair da página
    override fun onDestroy() {
        super.onDestroy()
        val fechaConexaoImpressoraCommand = FechaConexaoImpressora()
        startHubCommandActivity(
            this,
            fechaConexaoImpressoraCommand,
            FECHA_CONEXAO_IMPRESSORA_REQUESTCDOE
        )
    }

    /**
     * Salva uma copia da imagem enviada como bitmap por parametro dentro do diretorio do dispostivo, para a impressao via comando ImprimeImagem
     */
    private fun storeImage(image: Bitmap?) {
        val pictureFile = createdImage

        //Salva a imagem
        try {
            val fos = FileOutputStream(pictureFile)
            image!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("Error", "Arquivo não encontrado: " + e.message)
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e("Error", "Erro ao acessar o arquivo: " + e.message)
            e.printStackTrace()
        }
    }// A imagem a ser impressa sempre tera o mesmo nome para que a impressão ache o ultimo arquivo salvo

    /**
     * Cria a imagem que será salva no diretório da aplicação
     */
    private val createdImage: File
        private get() {
            val rootDirectoryPATH = ActivityUtils.getRootDirectoryPATH(this)

            // A imagem a ser impressa sempre tera o mesmo nome para que a impressão ache o ultimo arquivo salvo
            val mediaFile: File
            val mImageName = "ImageToPrint.jpg"
            mediaFile = File(rootDirectoryPATH + File.separator + mImageName)
            return mediaFile
        }

    companion object {
        const val IMPRESSAO_TEXTO_REQUESTCODE = 8
        const val IMPRIME_XML_NFCE_REQUESTCODE = 9
        const val IMPRIME_XML_SAT_REQUESTCODE = 10
        const val IMPRESSAO_CODIGO_BARRAS_REQUESTCODE = 11
        const val OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE = 12
        const val IMPRIME_IMAGEM_REQUESTCODE = 13
        private const val ABRE_CONEXAO_IMPRESSORA_REQUESTCODE = 1
        private const val ABRE_CONEXAO_IMPRESSORA_USB_REQUESTCODE = 2
        private const val ABRE_CONEXAO_IMPRESSORA_IP_REQUESTCODE = 3
        private const val FECHA_CONEXAO_IMPRESSORA_REQUESTCDOE = 4
        private const val STATUS_IMPRESSORA_REQUESTCODE = 5
        private const val STATUS_IMPRESSORA_STATUS_GAVETA_REQUESTCODE = 6
        private const val ABRE_GAVETA_ELGIN_REQUESTCODE = 7
        private const val EXTERNAL_CONNECTION_METHOD_USB = "USB"
        private const val EXTERNAL_CONNECTION_METHOD_IP = "IP"
    }
}