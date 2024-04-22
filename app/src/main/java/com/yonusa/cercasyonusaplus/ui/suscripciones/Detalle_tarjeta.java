package com.yonusa.cercasyonusaplus.ui.suscripciones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Detalle_tarjeta extends AppCompatActivity {

    TextView nombre,numero, fecha;
    Button eliminar, predeter;
    Context context;
    LottieAnimationView loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_tarjeta);
        this.context = this;
        loader = findViewById(R.id.loader_tarjeta);

        nombre = (TextView) findViewById(R.id.tv_nombre_card);
        numero = (TextView) findViewById(R.id.textView42);
        fecha = (TextView)  findViewById(R.id.tv_fecha);

        String nombreD = getIntent().getStringExtra("nombre");
        String numeroD = getIntent().getStringExtra("numero");
        String fechaD = getIntent().getStringExtra("fecha");
        String idCard = getIntent().getStringExtra("id_card");

        SharedPreferences misPreferencias = getSharedPreferences("Suscripcion", Context.MODE_PRIVATE);
        String idTarjeta = misPreferencias.getString("idTarjeta", "0");
        nombre.setText(nombreD);
        numero.setText(numeroD);
        fecha.setText(fechaD);

        eliminar = (Button) findViewById(R.id.btn_eliminar_tarjeta);
        predeter = (Button) findViewById(R.id.btn_predeterminada);

        if (idTarjeta.equals(idCard)){
            predeter.setVisibility(View.GONE);
        }

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Cuidado");
                alert.setMessage("Esta a punto de eliminar esta tarjeta, al eliminarla asegurese de tener otro metodo de pago para continuar con su sucripción y sus beneficios" +
                        "");
                alert.setPositiveButton("Continuar", (dialog, whichButton) -> {
                            // Toast.makeText(Tarjeta_pagar.this, itemNumero.getText().toString(), Toast.LENGTH_LONG).show();
                    try {
                        Eliminar_tarjeta(idCard);
                        eliminar.setVisibility(View.GONE);
                        predeter.setVisibility(View.GONE);
                        loader.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
                    alert.show();
                    }
        });

        predeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Cambiar tarjeta predeterminada");
                alert.setMessage("¿Esta seguro de seleccionar esta tarjeta como predeterminada?" +
                        "");
                alert.setPositiveButton("Continuar", (dialog, whichButton) -> {
                    // Toast.makeText(Tarjeta_pagar.this, itemNumero.getText().toString(), Toast.LENGTH_LONG).show();
                    try {
                        crear_tarjeta_predeter(idCard);
                        eliminar.setVisibility(View.GONE);
                        predeter.setVisibility(View.GONE);
                        loader.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });

                alert.setNegativeButton("Cacelar", (dialog, whichButton) -> {
                    // Toast.makeText(Tarjeta_pagar.this, itemNumero.getText().toString(), Toast.LENGTH_LONG).show();

                });
                alert.show();
            }
        });

    }


    public boolean crear_tarjeta_predeter(String tarjeta) throws JSONException, UnsupportedEncodingException {

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser = misPreferencias.getString("usuarioId", "0");
        //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("userId",idUser);
        oJSONObject.put("cardId",tarjeta);

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.put(getApplicationContext(),
                "http://payonusa.com/usuarios/api/v1/cards/default",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                Toast.makeText(getApplicationContext(),"Nueva tarjeta predeterminada", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                loader.setVisibility(View.INVISIBLE);
                                eliminar.setVisibility(View.VISIBLE);
                                predeter.setVisibility(View.VISIBLE);
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
                             //   suscribir.setVisibility(View.VISIBLE);
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
                        eliminar.setVisibility(View.VISIBLE);
                        predeter.setVisibility(View.VISIBLE);
                       // suscribir.setVisibility(View.VISIBLE);
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


    public boolean Eliminar_tarjeta(String tarjeta) throws JSONException, UnsupportedEncodingException {

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser = misPreferencias.getString("usuarioId", "0");
        //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("userId",idUser);
        oJSONObject.put("cardId",tarjeta);

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.delete(getApplicationContext(),
                "http://payonusa.com/usuarios/api/v1/cards",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                Toast.makeText(getApplicationContext(),"Tarjeta Eliminada", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                loader.setVisibility(View.INVISIBLE);
                                eliminar.setVisibility(View.VISIBLE);
                                predeter.setVisibility(View.VISIBLE);
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
                                //   suscribir.setVisibility(View.VISIBLE);
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
                        eliminar.setVisibility(View.VISIBLE);
                        predeter.setVisibility(View.VISIBLE);
                        // suscribir.setVisibility(View.VISIBLE);
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