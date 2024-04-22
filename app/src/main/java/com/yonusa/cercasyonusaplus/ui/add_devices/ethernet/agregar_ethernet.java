package com.yonusa.cercasyonusaplus.ui.add_devices.ethernet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.Lista_beneficiarios;
import com.yonusa.cercasyonusaplus.utilities.catalogs.SP_Dictionary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class agregar_ethernet extends AppCompatActivity {

    TextView idUser;
    EditText mac,alias;
    Button agregar;
    ImageView Scan;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ethernet);

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String id_user = misPreferencias.getString("usuarioId", "0");

        idUser = (TextView) findViewById(R.id.id_user_ethernet);
        mac = (EditText) findViewById(R.id.et_Mac_ethernet);
        alias = (EditText) findViewById(R.id.et_alias_ethernet);
        agregar= (Button) findViewById(R.id.btn_agregar_Ethernet);
        Scan = (ImageView) findViewById(R.id.imgScan);

        idUser.setText(id_user);

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(agregar_ethernet.this);
                        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        intentIntegrator.setPrompt("Escanea el Qr de tu modulo Ethernet");
                        intentIntegrator.setCameraId(0);
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setBarcodeImageEnabled(true);
                        intentIntegrator.initiateScan();
            }
        });



        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    agregar_ehetner(mac.getText().toString().trim(),id_user,alias.getText().toString().trim(),"","")     ;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requesCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requesCode,resultCode,data);

        IntentResult result = IntentIntegrator.parseActivityResult(requesCode,resultCode,data);
        String datos = result.getContents();
        mac.setText(datos);
    }

    public boolean agregar_ehetner(String mac,String userId, String alias,String lati,String longi) throws JSONException, UnsupportedEncodingException {
        SharedPreferences prefs = getSharedPreferences(SP_Dictionary.USER_INFO, MODE_PRIVATE);
        String uniqueId = prefs.getString("UniqueId_2", "No UniqueId defined");

        //String uniqueId = "TESTING-UNIQUEID080890";
        //  uniqueId = md5(uniqueId);
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String id_user = misPreferencias.getString("usuarioId", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("MAC", mac);
        oJSONObject.put("usuarioId", userId);

        oJSONObject.put("aliasCerca", alias);
        oJSONObject.put("latitud", lati);
        oJSONObject.put("longitud",longi);

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/FinalizarRegistroCercaEthernet", (HttpEntity) oEntity, "application/json", new AsyncHttpResponseHandler() {

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


                                Toast.makeText(getApplicationContext(), "Cerca Asignada", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Lista_beneficiarios.class);
                                startActivity(intent);
                                finish();
                            }else if(valor.equals("-1")) {
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error, intentalo nuevamente.", Toast.LENGTH_LONG).show();
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