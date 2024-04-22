package com.yonusa.cercasyonusaplus.ui.usbSerial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yonusa.cercasyonusaplus.R;

public class ListaCerco extends AppCompatActivity {

    ConstraintLayout lista;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cerco);

        lista = (ConstraintLayout) findViewById(R.id.listacercasimg);

        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Controles.class);
                startActivity(intent);
            }
        });
    }
}