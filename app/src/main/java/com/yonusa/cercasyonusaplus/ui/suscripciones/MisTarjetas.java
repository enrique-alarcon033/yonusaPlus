package com.yonusa.cercasyonusaplus.ui.suscripciones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.login.view.Loguin_new;
import com.yonusa.cercasyonusaplus.ui.suscripciones.adapter.paquetes_adapter;
import com.yonusa.cercasyonusaplus.ui.suscripciones.adapter.tarjetas_adapter;
import com.yonusa.cercasyonusaplus.ui.suscripciones.modelo.paquetes_model;
import com.yonusa.cercasyonusaplus.ui.suscripciones.modelo.tarjetas_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class MisTarjetas extends AppCompatActivity {

    private RecyclerView mRecyclerViewAceptadas;
    private RecyclerView.Adapter mAdapterco;
    private RecyclerView.LayoutManager mLayoutManagerco;
    private RecyclerView.Adapter mAdaptercoAcep;
    private RecyclerView.LayoutManager mLayoutManagercoAcep;
    //El dataset de tipo Photo
    private ArrayList<tarjetas_model> myDatasetCoAcep;
    FloatingActionButton agregar;
    LottieAnimationView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_tarjetas);

        loader = findViewById(R.id.loader_tarjetas);

        mRecyclerViewAceptadas = (RecyclerView) findViewById(R.id.lista_paquetes);
        mRecyclerViewAceptadas.setHasFixedSize(true);
        mLayoutManagercoAcep = new GridLayoutManager(getApplication(), 1);
        mRecyclerViewAceptadas.setLayoutManager(mLayoutManagercoAcep);

        agregar = (FloatingActionButton) findViewById(R.id.agregar_tarjeta);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MisTarjetas.this, Nueva_Tarjeta.class);
                startActivity(intent);
                finish();
            }
        });

        try {
            obtener_tarjetas();
            loader.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshDataset_aceptadas () {
        if (mRecyclerViewAceptadas == null)
            return;

        if (mAdaptercoAcep == null) {
            mAdaptercoAcep = new tarjetas_adapter(getApplication(), myDatasetCoAcep);
            mRecyclerViewAceptadas.setAdapter(mAdaptercoAcep);
        } else {
            mAdaptercoAcep.notifyDataSetChanged();
        }
    }

    public boolean obtener_tarjetas() throws JSONException, UnsupportedEncodingException {
        myDatasetCoAcep = new ArrayList<tarjetas_model>();

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser = misPreferencias.getString("usuarioId", "0");
        //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        // oJSONObject.put("usuarioId",mac);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible


        RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                "http://payonusa.com/usuarios/api/v1/cards/userId?userId="+idUser, (HttpEntity) oEntity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println(statusCode);
                        System.out.println(responseBody);
                        loader.setVisibility(View.GONE);
                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject obj = new JSONObject(content);
                            String valor = String.valueOf(obj.get("cardDefault"));

                           // Toast.makeText(getApplicationContext(), valor, Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPref2 =getSharedPreferences("Suscripcion",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPref2.edit();
                            editor2.putString("idTarjeta", valor);
                            editor2.commit();

                            JSONArray jsonArray = obj.getJSONArray("cards");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObjectHijo = jsonArray.getJSONObject(i);
                                    if (jsonObjectHijo != null) {
                                        //Armamos un objeto Photo con el Title y la URL de cada JSONObject
                                        tarjetas_model photo = new tarjetas_model();


                                        if (jsonObjectHijo.has("cardId"))
                                            photo.setCardId(jsonObjectHijo.getString("cardId"));

                                        if (jsonObjectHijo.has("cardExpMont"))
                                            photo.setMes(jsonObjectHijo.getString("cardExpMont"));

                                        if (jsonObjectHijo.has("cardExpYear"))
                                            photo.setYear(jsonObjectHijo.getString("cardExpYear"));

                                        if (jsonObjectHijo.has("cardName"))
                                            photo.setNombre(jsonObjectHijo.getString("cardName"));

                                        if (jsonObjectHijo.has("cardLastFour"))
                                            photo.setTerminacion(jsonObjectHijo.getString("cardLastFour"));

                                        if (jsonObjectHijo.has("cardBrand"))
                                            photo.setCard(jsonObjectHijo.getString("cardBrand"));


                                        //Agreagamos el objeto Photo al Dataset{

                                        myDatasetCoAcep.add(photo);


                                    }
                                    //   Toast.makeText(getApplicationContext(), String.valueOf(jsonObjectHijo), Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    Log.e("Parser JSON", e.toString());
                                } finally {
                                    //Finalmente si hemos cargado datos en el Dataset
                                    // entonces refrescamos
                                    //progressplanes.setVisibility(View.GONE);
                                    if (myDatasetCoAcep.size() > 0) {
                                        //  texto2.setVisibility(View.VISIBLE);
                                        //   aceptadas_s.setVisibility(View.GONE);
                                        refreshDataset_aceptadas();
                                        loader.setVisibility(View.GONE);

                                    } else {
                                        //  texto2.setVisibility(View.VISIBLE);
                                        mRecyclerViewAceptadas.setVisibility(View.GONE);
                                        //     aceptadas_s.setVisibility(View.GONE);
                                    }
                                }
                            }
                            //  Toast.makeText(getApplicationContext(), obj.getString("listConsents"), Toast.LENGTH_LONG).show();
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
                        loader.setVisibility(View.GONE);
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