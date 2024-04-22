package com.yonusa.cercasyonusaplus.ui.suscripciones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.login.view.Loguin_new;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CancelarSuscripcion extends AppCompatActivity {

    Button cancelar;
    LottieAnimationView loader;
    TextView plan,precio;
    Context context;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_suscripcion);

        this.context = this;
        plan= (TextView) findViewById(R.id.plan);
        precio = (TextView) findViewById(R.id.precio);

        String p1 = "price_1MvkRBCrSQWaJZnpMGdDvyAP"; //Plan Empresarial Mensual
        String p2 = "price_1MvkRBCrSQWaJZnpCiTTBVv9"; //Plan Empresarial Anual
        String p3 = "price_1MvOlOCrSQWaJZnpDnCHbGbg";//Plan Residencial Mensual
        String p4 = "price_1MvOlOCrSQWaJZnp3vhMMKmS";  //Plan Residencial Anual

        String pn1 = "400.00";
        String pn2 = "4400.00";
        String pn3 = "200.00";
        String pn4 = "200.00";

        SharedPreferences misPreferencias = getSharedPreferences("Suscripcion", Context.MODE_PRIVATE);
        String idPaquete = misPreferencias.getString("priceId", "0");

        if(idPaquete.equals(p1)){
            plan.setText("Plan Empresarial Mensual");
            precio.setText(pn1);
        }else if (idPaquete.equals(p2)){
            plan.setText("Plan Empresarial anual");
            precio.setText(pn2);
        } else if (idPaquete.equals(p3)){
            plan.setText("Plan Residencial Mensual");
            precio.setText(pn3);
        } else {
            plan.setText("Plan Residencial Anual");
            precio.setText(pn4);
        }

        cancelar= (Button) findViewById(R.id.btn_cancelar);
        loader = findViewById(R.id.loader_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Cuidado");
                alert.setMessage("Esta a punto de cancelar su suscripción al presionar continuar perderá los beneficios de su seguro. ¿Desea continuar?");
                alert.setPositiveButton("Continuar", (dialog, whichButton) -> {
                    // Toast.makeText(Tarjeta_pagar.this, itemNumero.getText().toString(), Toast.LENGTH_LONG).show();
                    try {
                        loader.setVisibility(View.VISIBLE);
                        cancelar.setVisibility(View.GONE);
                        cancelar_suscripcion();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
                alert.setNegativeButton("Cancelar",((dialog, which) ->{

                } ));
                alert.show();
            }
        });
    }

    public boolean cancelar_suscripcion() throws JSONException, UnsupportedEncodingException {

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser = misPreferencias.getString("usuarioId", "0");
        //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("userId",idUser);

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.delete(getApplicationContext(),
                "http://payonusa.com/usuarios/api/v1/suscriptions?userId="+idUser,(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                Toast.makeText(getApplicationContext(),"Tu suscripción ha sido cancelada", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CancelarSuscripcion.this, Loguin_new.class);
                                startActivity(intent);
                                finish();
                            }else{
                                loader.setVisibility(View.INVISIBLE);
                                Log.e("error",content);
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Error al procesar");
                                alert.setMessage("Por favor: "+obj.getString("mensaje"));
                                alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                                    // Toast.makeText(Tarjeta_pagar.this, itemNumero.getText().toString(), Toast.LENGTH_LONG).show();

                                });
                                alert.setNegativeButton("Cancelar",((dialog, which) ->{

                                } ));
                                alert.show();
                                // Toast.makeText(getApplicationContext(), "Intentalo mas tarde"+idUser+rfc+precioId+tarjeta+mesD+yearD+cvc+nombre, Toast.LENGTH_LONG).show();
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
                        loader.setVisibility(View.INVISIBLE);
                        cancelar.setVisibility(View.VISIBLE);
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