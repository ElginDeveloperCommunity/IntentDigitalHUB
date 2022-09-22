package com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterPages

import ActivityUtils.getRootDirectoryPATH
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterMenuActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.AvancaPapel
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.Corte
import com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImprimeImagem
import com.elgin.kotlin_intent_digital_hub_smartpos.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class PrinterImageActivity() : AppCompatActivity() {

    //Imagem de pré-visualização.
    private lateinit var imageView: ImageView

    //Botões de seleção de imagem e de impressão.
    private lateinit var buttonSelectImage: Button

    //Botões de seleção de imagem e de impressão.
    private lateinit var buttonPrintImage: Button

    //Checkbox de corte de papel, só será disponibilizada caso o método de impressão escolhido seja por impressora externa.
    private lateinit var checkBoxIsCutPaperImage: CheckBox

    //PATH da imagem a ser impressa.
    private lateinit var pathOfLastSelectedImage: String

    //Int usado para inicio da atividade;
    private val OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE = 1
    private val IMPRIME_IMAGEM_REQUESTCODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer_image)

        //Atribui path onde a imagem será salva. (Android/data/pacoteDaAplicacao/Files/ImageToPrint.jpg
        pathOfLastSelectedImage =
            "/Android/data/" + this.applicationContext.packageName + "/Files" + "/ImageToPrint.jpg"

        //Atribui as views ao iniciar da tela.
        viewsAssignment()

        //Estado inicial da tela.
        initialState()

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment()
    }

    //Atribuição das views.
    private fun viewsAssignment() {
        imageView = findViewById(R.id.previewImgDefault)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)
        buttonPrintImage = findViewById(R.id.buttonPrintImage)
        checkBoxIsCutPaperImage = findViewById(R.id.checkBoxIsCutPaperImage)
    }

    //Aplica algumas configurações iniciais de tela.
    private fun initialState() {
        //O corte de papel só esta disponível em impressões por impressora externa, caso a opção escolhida no menu de impressora não tenha sido impressora externa, a checkbox de corte de papel deve sumir.
        if (PrinterMenuActivity.selectedPrinterConnectionType !== PrinterMenuActivity.PrinterConnectionMethod.EXTERN) checkBoxIsCutPaperImage!!.visibility =
            View.INVISIBLE

        //Para que ao abrir a tela, exista uma imagem pré salva no diretório da aplicação para que a impressão seja feita via PATH, é feito o salvamento da imagem padrão na abertura da tela.
        val elgin_logo_default_print_image =
            BitmapFactory.decodeResource(this.resources, R.drawable.elgin_logo_default_print_image)
        storeImage(elgin_logo_default_print_image)
    }

    //Atribuição das funcionalidades das views.
    private fun viewsFunctionalityAssignment() {
        buttonSelectImage!!.setOnClickListener { v: View? -> startGallery() }
        buttonPrintImage!!.setOnClickListener { v: View? -> printImage() }
    }

    //Abre a galeria para a selação de uma imagem para impressão.
    private fun startGallery() {
        Toast.makeText(
            this,
            "Selecione uma imagem com no máximo 400 pixels de largura!",
            Toast.LENGTH_LONG
        ).show()
        val cameraIntent = Intent(Intent.ACTION_PICK)
        cameraIntent.type = "image/*"
        startActivityForResult(cameraIntent, OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE)
    }

    //Realiza a impressão da imagem por PATH.
    private fun printImage() {
        val termicaCommands: MutableList<IntentDigitalHubCommand> =
            ArrayList<IntentDigitalHubCommand>()
        val imprimeImagemCommand = ImprimeImagem(pathOfLastSelectedImage!!)
        termicaCommands.add(imprimeImagemCommand)
        val avancaPapelCommand = AvancaPapel(10)
        termicaCommands.add(avancaPapelCommand)
        if (checkBoxIsCutPaperImage!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommands.add(corteCommand)
        }
        IntentDigitalHubCommandStarter.startIDHCommandForResult(
            this,
            termicaCommands,
            IMPRIME_IMAGEM_REQUESTCODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE) {
                // Cria um bitmap através do URI da imagem selecionada da galeria, e através dele cria e salva uma imagem em Android/data/applicationPackage/files/ImageToPrint.jpg, que será utilizada na impressão de imagem por PATH
                val returnUri = data?.data
                var bitmapImage: Bitmap? = null
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.contentResolver, returnUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //Atualiza a view pela imagem selecionada na galeria
                imageView!!.setImageBitmap(bitmapImage)

                //Salva a imagem dentro do diretório da aplicação
                storeImage(bitmapImage)
            }
        } else {
            Toast.makeText(this, "Você não escolheu uma imagem!", Toast.LENGTH_LONG).show()
        }
    }


    //Salva uma copia da imagem enviada como bitmap por parametro dentro do diretorio do dispostivo, para a impressao via comando ImprimeImagem
    private fun storeImage(image: Bitmap?) {
        val pictureFile = getCreatedImage()

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
    }

    //Cria a imagem que será salva no diretório da aplicação.
    private fun getCreatedImage(): File {
        val rootDirectoryPATH: String = getRootDirectoryPATH(this)

        // A imagem a ser impressa sempre tera o mesmo nome para que a impressão ache o ultimo arquivo salvo
        val mediaFile: File
        val mImageName = "ImageToPrint.jpg"
        mediaFile = File(rootDirectoryPATH + File.separator + mImageName)
        return mediaFile
    }
}