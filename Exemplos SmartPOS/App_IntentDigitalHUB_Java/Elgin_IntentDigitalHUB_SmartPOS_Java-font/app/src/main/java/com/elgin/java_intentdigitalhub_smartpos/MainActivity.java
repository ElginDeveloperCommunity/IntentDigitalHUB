package com.elgin.java_intentdigitalhub_smartpos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.elgin.java_intentdigitalhub_smartpos.Activities.ActivityUtils;
import com.elgin.java_intentdigitalhub_smartpos.Activities.BarCodeReader.BarCodeReaderActivity;
import com.elgin.java_intentdigitalhub_smartpos.Activities.ElginPay.ElginPayActivity;
import com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterMenuActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout buttonElginPay;
    private LinearLayout buttonPrinterMenu;
    private LinearLayout buttonBarCodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonElginPay = findViewById(R.id.buttonElginPay);
        buttonPrinterMenu = findViewById(R.id.buttonPrinterMenu);
        buttonBarCodeReader = findViewById(R.id.buttonBarCodeReader);

        buttonElginPay.setOnClickListener(v -> ActivityUtils.startNewActivity(this, ElginPayActivity.class));

        buttonPrinterMenu.setOnClickListener(v -> ActivityUtils.startNewActivity(this, PrinterMenuActivity.class));

        buttonBarCodeReader.setOnClickListener(v -> ActivityUtils.startNewActivity(this, BarCodeReaderActivity.class));
    }
}