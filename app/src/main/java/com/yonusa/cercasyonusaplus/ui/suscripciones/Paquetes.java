package com.yonusa.cercasyonusaplus.ui.suscripciones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.rutinas.adapters.rutinas_adapter;
import com.yonusa.cercasyonusaplus.ui.rutinas.modelo.rutinas_m;
import com.yonusa.cercasyonusaplus.ui.suscripciones.adapter.paquetes_adapter;
import com.yonusa.cercasyonusaplus.ui.suscripciones.modelo.paquetes_model;

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

public class Paquetes extends AppCompatActivity {

    private RecyclerView mRecyclerViewAceptadas;
    private RecyclerView.Adapter mAdapterco;
    private RecyclerView.LayoutManager mLayoutManagerco;
    private RecyclerView.Adapter mAdaptercoAcep;
    private RecyclerView.LayoutManager mLayoutManagercoAcep;
    //El dataset de tipo Photo
    private ArrayList<paquetes_model> myDatasetCoAcep;
    LottieAnimationView loader;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paquetes);


        loader = findViewById(R.id.loader_paquetes);

        mRecyclerViewAceptadas = (RecyclerView) findViewById(R.id.lista_paquetes);
        mRecyclerViewAceptadas.setHasFixedSize(true);
        mLayoutManagercoAcep = new GridLayoutManager(getApplication(), 1);
        mRecyclerViewAceptadas.setLayoutManager(mLayoutManagercoAcep);
        try {
            obtener_paquetes();
            loader.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void refreshDataset_aceptadas () {
        if (mRecyclerViewAceptadas == null)
            return;

        if (mAdaptercoAcep == null) {
            mAdaptercoAcep = new paquetes_adapter(getApplication(), myDatasetCoAcep);
            mRecyclerViewAceptadas.setAdapter(mAdaptercoAcep);
        } else {
            mAdaptercoAcep.notifyDataSetChanged();
        }
    }

    public boolean obtener_paquetes() throws JSONException, UnsupportedEncodingException {
        myDatasetCoAcep = new ArrayList<paquetes_model>();

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
                "http://payonusa.com/usuarios/api/v1/prices/"+idUser, (HttpEntity) oEntity, "application/json", new AsyncHttpResponseHandler() {

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

                            JSONArray jsonArray = obj.getJSONArray("lstPrices");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObjectHijo = jsonArray.getJSONObject(i);
                                    if (jsonObjectHijo != null) {
                                        //Armamos un objeto Photo con el Title y la URL de cada JSONObject
                                        paquetes_model photo = new paquetes_model();


                                        if (jsonObjectHijo.has("priceId"))
                                            photo.setPriceId(jsonObjectHijo.getString("priceId"));

                                        if (jsonObjectHijo.has("name"))
                                            photo.setName(jsonObjectHijo.getString("name"));

                                        if (jsonObjectHijo.has("currency"))
                                            photo.setCurrency(jsonObjectHijo.getString("currency"));

                                        if (jsonObjectHijo.has("recurring"))
                                            photo.setRecurrency(jsonObjectHijo.getString("recurring"));

                                        if (jsonObjectHijo.has("unitAmount"))
                                            photo.setMonto(jsonObjectHijo.getString("unitAmount"));


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