package com.yonusa.cercasyonusaplus.ui.beneficiarios;



import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.yonusa.cercasyonusaplus.R;

public class llamada extends AppCompatActivity {
ImageView llamar;
    final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 10; // Puedes poner cualquier número, solo es para identificarlo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamada);

        llamar = (ImageView) findViewById(R.id.imgllamar);
        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ActivityCompat.checkSelfPermission(llamada.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    // Aquí ya está concedido, procede a realizar lo que tienes que hacer
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:5593031314"));
                    startActivity(i);
                }else{
                    // Aquí lanzamos un dialog para que el usuario confirme si permite o no el realizar llamadas
                    ActivityCompat.requestPermissions(llamada.this, new String[]{ Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }


            }
        });
    }
}