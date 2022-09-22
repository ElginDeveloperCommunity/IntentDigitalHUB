package com.elgin.kotlin_intent_digital_hub_smartpos

import ActivityUtils.startNewActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.BarCodeReader.BarCodeReaderActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.ElginPay.ElginPayActivity
import com.elgin.kotlin_intent_digital_hub_smartpos.Activities.Printer.PrinterMenuActivity

open class MainActivity : AppCompatActivity() {
    private var buttonElginPay: LinearLayout? = null
    private var buttonPrinterMenu: LinearLayout? = null
    private var buttonBarCodeReader: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonElginPay = findViewById<LinearLayout>(R.id.buttonElginPay)
        buttonPrinterMenu = findViewById<LinearLayout>(R.id.buttonPrinterMenu)
        buttonBarCodeReader = findViewById<LinearLayout>(R.id.buttonBarCodeReader)
        buttonElginPay!!.setOnClickListener { v: View? ->
            startNewActivity(
                this,
                ElginPayActivity::class.java
            )
        }
        buttonPrinterMenu!!.setOnClickListener { v: View? ->
            startNewActivity(
                this,
                PrinterMenuActivity::class.java
            )
        }
        buttonBarCodeReader!!.setOnClickListener { v: View? ->
            startNewActivity(
                this,
                BarCodeReaderActivity::class.java
            )
        }
    }
}