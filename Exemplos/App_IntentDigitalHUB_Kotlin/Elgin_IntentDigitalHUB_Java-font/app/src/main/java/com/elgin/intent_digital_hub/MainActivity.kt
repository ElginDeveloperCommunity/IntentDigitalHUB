package com.elgin.intent_digital_hub

import android.Manifest
import com.elgin.intent_digital_hub.ActivityUtils.startNewActivity
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.os.Bundle
import com.elgin.intent_digital_hub.Bridge.BridgeActivity
import com.elgin.intent_digital_hub.Printer.PrinterActivity
import com.elgin.intent_digital_hub.SAT.SATActivity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.widget.Toast
import android.os.Build
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var buttonBridge: LinearLayout
    private lateinit var buttonPrinter: LinearLayout
    private lateinit var buttonSat: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonBridge = findViewById(R.id.buttonBridge)
        buttonPrinter = findViewById(R.id.buttonPrinter)
        buttonSat = findViewById(R.id.buttonSAT)
        buttonBridge.setOnClickListener(View.OnClickListener { v: View? ->
            startNewActivity(
                this,
                BridgeActivity::class.java
            )
        })
        buttonPrinter.setOnClickListener(View.OnClickListener { v: View? ->
            startNewActivity(
                this,
                PrinterActivity::class.java
            )
        })
        buttonSat.setOnClickListener(View.OnClickListener { v: View? ->
            startNewActivity(
                this,
                SATActivity::class.java
            )
        })

        //Pede a permissão ao início da aplicação
        askWriteExternalStoragePermission()
    }

    //Pede a permissão de escrita no diretório externo
    private fun askWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Impede que a aplicação continue caso a permissão seja negada, uma vez que vários módulos dependem da permissão de acesso ao armazenamento
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(
                this,
                "É necessário conceder a permissão para várias funcionalidades da aplicação!",
                Toast.LENGTH_LONG
            ).show()
            closeApplication()
        }
    }

    //Força o fechamento da aplicação
    private fun closeApplication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) finishAffinity() else finish()
    }

    companion object {
        private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
    }
}