package com.yonusa.cercaspaniagua.ui.device_control.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.yonusa.cercaspaniagua.R;
import com.yonusa.cercaspaniagua.ui.rutinas.lista_rutinas;

public class Botones extends AppCompatActivity {

    Switch puerta,aux1,aux2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_botones);

    puerta = (Switch) findViewById(R.id.switch2);
    aux1=(Switch) findViewById(R.id.switch3);
    aux2= (Switch) findViewById(R.id.switch4);

    SharedPreferences prefs = getSharedPreferences("Botones", MODE_PRIVATE);
    String valor1 = prefs.getString("puerta", "0");
    String valor2 = prefs.getString("aux1", "0");
    String valor3 = prefs.getString("aux2", "0");

    if (valor1.equals("0")){
        puerta.setChecked(false);
    }else{
        puerta.setChecked(true);
    }

    if (valor2.equals("0")){
        aux1.setChecked(false);
    }else{
        aux1.setChecked(true);
    }

    if (valor3.equals("0")){
        aux2.setChecked(false);
    }else{
        aux2.setChecked(true);
    }

        //obtiene valor.
       // boolean estado_switch = getValuePreference(getApplicationContext());
       // puerta.setChecked(estado_switch);


        puerta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                //guarda valor.
                //saveValuePreference(getApplicationContext(), isChecked);

                if(isChecked){
                   // Intent intent = new Intent(Botones.this, lista_rutinas.class);
                   // startActivity(intent);
                    SharedPreferences sharedPref2 =getSharedPreferences("Botones",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putString("puerta", "1");
                    editor2.commit();
                }else
                {
                    SharedPreferences sharedPref2 =getSharedPreferences("Botones",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putString("puerta", "0");
                    editor2.commit();
                }

            }
        });

        aux1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                //guarda valor.
                //saveValuePreference(getApplicationContext(), isChecked);

                if(isChecked){
                    // Intent intent = new Intent(Botones.this, lista_rutinas.class);
                    // startActivity(intent);
                    SharedPreferences sharedPref2 =getSharedPreferences("Botones",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putString("aux1", "1");
                    editor2.commit();
                }else
                {
                    SharedPreferences sharedPref2 =getSharedPreferences("Botones",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putString("aux1", "0");
                    editor2.commit();
                }

            }
        });

        aux2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                //guarda valor.
                //saveValuePreference(getApplicationContext(), isChecked);

                if(isChecked){
                    // Intent intent = new Intent(Botones.this, lista_rutinas.class);
                    // startActivity(intent);
                    SharedPreferences sharedPref2 =getSharedPreferences("Botones",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putString("aux2", "1");
                    editor2.commit();
                }else
                {
                    SharedPreferences sharedPref2 =getSharedPreferences("Botones",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putString("aux2", "0");
                    editor2.commit();
                }

            }
        });


    }
}