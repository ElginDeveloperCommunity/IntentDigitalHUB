package com.elgin.intent_digital_hub.Printer.Fragments

import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter.startHubCommandActivity
import android.widget.CheckBox
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.elgin.intent_digital_hub.R
import com.elgin.intent_digital_hub.Printer.Fragments.PrinterImageFragment
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.elgin.intent_digital_hub.Printer.PrinterActivity
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommand
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.ImprimeImagem
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.AvancaPapel
import com.elgin.intent_digital_hub.IntentDigitalHubService.TERMICA.Corte
import com.elgin.intent_digital_hub.IntentDigitalHubService.IntentDigitalHubCommandStarter
import java.util.ArrayList

class PrinterImageFragment : Fragment() {
    private var pathOfLastSelectedImage: String? = null
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonPrintImage: Button
    private var checkBoxCutPaperImage: CheckBox? = null
    private var PrinterActivityReference: Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_printer_image, container, false)

        //Captura a referência da atividade onde o fragment é utilizado
        PrinterActivityReference = activity

        //Atribui o path da ultima imagem selecionada, sempre em Android/data/com.elgin.intent_digital_hub/files/ImageToPrint.jpg
        pathOfLastSelectedImage =
            "/Android/data/" + PrinterActivityReference!!.applicationContext.packageName + "/Files" + "/ImageToPrint.jpg"
        imageView = v.findViewById(R.id.previewImgDefault)
        buttonSelectImage = v.findViewById(R.id.buttonSelectImage)
        buttonPrintImage = v.findViewById(R.id.buttonPrintImage)
        checkBoxCutPaperImage = v.findViewById(R.id.checkBoxCutPaperPrintImage)
        buttonSelectImage.setOnClickListener(View.OnClickListener { v: View ->
            buttonSelectImageFunction(
                v
            )
        })
        buttonPrintImage.setOnClickListener(View.OnClickListener { v: View ->
            buttonPrintImageFunction(
                v
            )
        })
        return v
    }

    private fun buttonSelectImageFunction(v: View) {
        val cameraIntent = Intent(Intent.ACTION_PICK)
        cameraIntent.type = "image/*"
        PrinterActivityReference!!.startActivityForResult(
            cameraIntent,
            PrinterActivity.OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE
        )
    }

    private fun buttonPrintImageFunction(v: View) {
        val termicaCommands: MutableList<IntentDigitalHubCommand> = ArrayList()
        val path = pathOfLastSelectedImage
        val imprimeImagemCommand = ImprimeImagem(path!!)
        termicaCommands.add(imprimeImagemCommand)
        val avancaPapelCommand = AvancaPapel(10)
        termicaCommands.add(avancaPapelCommand)
        if (checkBoxCutPaperImage!!.isChecked) {
            val corteCommand = Corte(0)
            termicaCommands.add(corteCommand)
        }
        startHubCommandActivity(
            PrinterActivityReference!!,
            termicaCommands,
            PrinterActivity.IMPRIME_IMAGEM_REQUESTCODE
        )
    }

    companion object {
        @JvmField
        var imageView: ImageView? = null
    }
}