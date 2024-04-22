package com.yonusa.cercasyonusaplus.ui.beneficiarios;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.login.view.Loguin_new;
import com.yonusa.cercasyonusaplus.utilities.catalogs.SP_Dictionary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Nuevo_Beneficiario extends AppCompatActivity {

    EditText nombre,apellido,correo,rfc,telefono;
    Button guardar;
    Context context;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_beneficiario);
        nombre = (EditText) findViewById(R.id.nombre_ben);
        apellido = (EditText)  findViewById(R.id.apellido_ben);
        correo= (EditText) findViewById(R.id.correo_ben);
        rfc = (EditText) findViewById(R.id.rfc_ben);
        telefono = (EditText) findViewById(R.id.telefono_ben);

        guardar= (Button) findViewById(R.id.guardar_ben);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (nombre.getText().toString().isEmpty()||apellido.getText().toString().isEmpty()||
                            correo.getText().toString().isEmpty()||rfc.getText().toString().isEmpty()||telefono.getText().toString().isEmpty())
                    {
                        Toast.makeText(Nuevo_Beneficiario.this,"Verifica que tus datos sean correctos",Toast.LENGTH_LONG).show();
                    }else{
                        guardar_ben(nombre.getText().toString(),apellido.getText().toString(),correo.getText().toString(),rfc.getText().toString(),telefono.getText().toString());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }



    public boolean guardar_ben(String nomb,String apell, String emai,String rfc,String telefono) throws JSONException, UnsupportedEncodingException {
        SharedPreferences prefs = getSharedPreferences(SP_Dictionary.USER_INFO, MODE_PRIVATE);
        String uniqueId = prefs.getString("UniqueId_2", "No UniqueId defined");

        //String uniqueId = "TESTING-UNIQUEID080890";
        //  uniqueId = md5(uniqueId);
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String id_user = misPreferencias.getString("usuarioId", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("nombre", nomb);
        oJSONObject.put("apellidos", apell);
        oJSONObject.put("email", emai);
        oJSONObject.put("rfc", rfc);
        oJSONObject.put("telefono",telefono);
        oJSONObject.put("idUsuario", id_user);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "http://payonusa.com/usuarios/api/v1/RegistrarUsuarioBeneficiario", (HttpEntity) oEntity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println(statusCode);
                        System.out.println(responseBody);
                        //     mMap = googleMap;d

                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject obj = new JSONObject(content);
                            String valor = String.valueOf(obj.get("codigo"));

                            if (valor.equals("0")) {


                                Toast.makeText(getApplicationContext(), "Beneficiario Creado", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Lista_beneficiarios.class);
                                startActivity(intent);
                                finish();
                            }else if(valor.equals("-15")) {
                                Toast.makeText(getApplicationContext(), "El correo ingresado ya fue asignado, intenta con otro correo.", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Error en el servidor intentalo mas tarde", Toast.LENGTH_LONG).show();
                            }


                            // Toast.makeText(getApplicationContext(), String.valueOf(names), Toast.LENGTH_LONG).show();
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