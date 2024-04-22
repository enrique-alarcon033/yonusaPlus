package com.yonusa.cercasyonusaplus.ui.rutinas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class eliminar_rutina extends AppCompatActivity {

    TextView id,usuario,mac,comando,zona,dia,hora,nombre,status;
    Button borrar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_rutina);

        id= (TextView) findViewById(R.id.id_Rutina);

        usuario =(TextView) findViewById(R.id.textView11);
        mac =(TextView) findViewById(R.id.textView12);
        comando =(TextView) findViewById(R.id.textView14);
        zona =(TextView) findViewById(R.id.textView15);
        dia =(TextView) findViewById(R.id.textView16);
        hora =(TextView) findViewById(R.id.textView17);
        nombre =(TextView) findViewById(R.id.textView18);
        status =(TextView) findViewById(R.id.textView19);
        borrar = (Button) findViewById(R.id.eliminar_rutina);


        String v1 = getIntent().getStringExtra("id");
        String v2 = getIntent().getStringExtra("id_user");
        String v3 = getIntent().getStringExtra("mac");
        String v4 = getIntent().getStringExtra("comando");
        String v5 = getIntent().getStringExtra("zona");
        String v6 = getIntent().getStringExtra("dia");
        String v7 = getIntent().getStringExtra("hora");
        String v8 = getIntent().getStringExtra("nombre");
        String v9 = getIntent().getStringExtra("status");

        id.setText(v1);
        usuario.setText(v2);
        mac.setText(v3);
        comando.setText(v4);
        zona.setText(v5);
        dia.setText(v6);
        hora.setText(v7);
        nombre.setText(v8);
        status.setText(v9);



        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar_rutina(id.getText().toString(), nombre.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean eliminar_rutina(String id,String nombre) throws JSONException, UnsupportedEncodingException {

        SharedPreferences misPreferencias = getSharedPreferences("lista", Context.MODE_PRIVATE);

        String mac =  misPreferencias.getString("mac","0");
        //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("id",id);
        oJSONObject.put("cercaMAC",mac);
          oJSONObject.put("nombreRutina",nombre);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/EliminarCalendarioNotificacion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                        System.out.println(statusCode);
                        System.out.println(responseBody);

                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject obj = new JSONObject(content);
                            if ( obj.getString("codigo").equals("0")){
                                Toast.makeText(getApplicationContext(),"Se ha borrado la rutina", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(eliminar_rutina.this, lista_rutinas.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Intentalo mas tarde", Toast.LENGTH_LONG).show();
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "404 !", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "500 !", Toast.LENGTH_LONG).show();
                            //sin_tarjetas();
                        } else if (statusCode == 403) {
                            Toast.makeText(getApplicationContext(), "403 !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public boolean getUseSynchronousMode() {
                        return false;
                    }
                    @Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                        System.out.println(retryNo);
                    }
                });

        return false;
    }

}