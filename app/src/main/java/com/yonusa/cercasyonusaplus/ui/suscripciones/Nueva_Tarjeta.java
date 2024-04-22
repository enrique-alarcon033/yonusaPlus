package com.yonusa.cercasyonusaplus.ui.suscripciones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class Nueva_Tarjeta extends AppCompatActivity {
    TextView idProducto, nombre,moneda,periodo,precio,item_mes,item_ano;
    TextView tarjeta1,tarjeta2,tarjeta3,tarjeta4;

    Context context;
    TextView itemRFC, itemNombre,itemCVV, itemNumero;
    Button suscribir;
    Spinner mes, year;

    LottieAnimationView loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_tarjeta);

        this.context = this;
        loader = findViewById(R.id.loader_pagar);
        suscribir = (Button) findViewById(R.id.btn_agregar_tarjeta);


        nombre = (TextView) findViewById(R.id.tv_nombre);
        moneda = (TextView) findViewById(R.id.tv_moneda);
        periodo = (TextView) findViewById(R.id.tv_periodo);
        precio = (TextView) findViewById(R.id.tv_precio);
        item_mes = (TextView) findViewById(R.id.item_mes);
        item_ano = (TextView) findViewById(R.id.item_ano);

        tarjeta1 = (TextView) findViewById(R.id.number1);
        tarjeta2 = (TextView) findViewById(R.id.number2);
        tarjeta3 = (TextView) findViewById(R.id.number3);
        tarjeta4 = (TextView) findViewById(R.id.number4);

        itemCVV = (TextView) findViewById(R.id.item_cvv);

        itemNombre = (TextView) findViewById(R.id.et_nombre);
        itemNumero = (TextView) findViewById(R.id.item_numero);

        mes = (Spinner) findViewById(R.id.spinnermes);
        year = (Spinner) findViewById(R.id.spinnerano);


        setupSpinnerBasic();
        setupSpinneryears();
        mes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                item_mes.setText(item);
                //   itemNumero.setText(tarjeta1.getText().toString()+tarjeta2.getText().toString()+tarjeta3.getText().toString()+tarjeta4.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                item_ano.setText(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        suscribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemNombre.getText().toString().isEmpty()||tarjeta1.getText().toString().isEmpty()||tarjeta2.getText().toString().isEmpty()
                        ||tarjeta3.getText().toString().isEmpty() ||tarjeta4.getText().toString().isEmpty() ||item_mes.getText().toString().isEmpty()
                        ||item_ano.getText().toString().isEmpty()
                        ||itemCVV.getText().toString().isEmpty()){
                    Toast.makeText(Nueva_Tarjeta.this, "Campos vacios favor de verificar", Toast.LENGTH_LONG).show();
                }else{
                    itemNumero.setText(tarjeta1.getText().toString()+tarjeta2.getText().toString()+tarjeta3.getText().toString()+tarjeta4.getText().toString());
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Registrar Tarjeta Nueva " );
                    alert.setMessage("Al registrar esta tarjeta se convertira en su metodo de pago predeterminado");
                    alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                        // Toast.makeText(Tarjeta_pagar.this, itemNumero.getText().toString(), Toast.LENGTH_LONG).show();
                        try {
                            suscribir.setVisibility(View.GONE);
                            loader.setVisibility(View.VISIBLE);
                            crear_tarjeta(itemNombre.getText().toString(),itemNumero.getText().toString(),item_mes.getText().toString(),item_ano.getText().toString(),itemCVV.getText().toString());
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

            }
        });

    }

    private void setupSpinnerBasic() {
        Spinner spinner = findViewById(R.id.spinnermes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void setupSpinneryears() {
        Spinner spinner = findViewById(R.id.spinnerano);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.anos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public boolean crear_tarjeta(String nombre,String tarjeta, String mesD, String yearD, String cvc) throws JSONException, UnsupportedEncodingException {

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser = misPreferencias.getString("usuarioId", "0");
        //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("userId",idUser);
        oJSONObject.put("numCard",tarjeta);
        oJSONObject.put("cardExpMonth",mesD);
        oJSONObject.put("cardExpYear",yearD);
        oJSONObject.put("cardCvc",cvc);
        oJSONObject.put("cardName",nombre);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.put(getApplicationContext(),
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
                                Toast.makeText(getApplicationContext(),"Tu tarjeta se ha agregado exitosamente", Toast.LENGTH_LONG).show();
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
                                suscribir.setVisibility(View.VISIBLE);
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
                        suscribir.setVisibility(View.VISIBLE);
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