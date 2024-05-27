package com.yonusa.cercasyonusaplus.ui.rutinas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.device_control.view.Botones;
import com.yonusa.cercasyonusaplus.ui.device_control.view.DeviceControlActivity;
import com.yonusa.cercasyonusaplus.ui.rutinas.adapters.rutinas_adapter;
import com.yonusa.cercasyonusaplus.ui.rutinas.modelo.rutinas_m;

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

public class lista_rutinas extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerViewAceptadas;
    private RecyclerView.Adapter mAdapterco;
    private RecyclerView.LayoutManager mLayoutManagerco;

    private RecyclerView.Adapter mAdaptercoAcep;
    private RecyclerView.LayoutManager mLayoutManagercoAcep;
    //El dataset de tipo Photo
    private ArrayList<rutinas_m> myDatasetCoAcep;

    FloatingActionButton add_rutina;

    LinearLayout layout_rutinas;
    private TextView rutinas;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rutinas);

        mRecyclerViewAceptadas = (RecyclerView) findViewById(R.id.lista_rutinas);
        mRecyclerViewAceptadas.setHasFixedSize(true);
        mLayoutManagercoAcep = new GridLayoutManager(getApplication(), 1);
        mRecyclerViewAceptadas.setLayoutManager(mLayoutManagercoAcep);

        add_rutina= (FloatingActionButton) findViewById(R.id.btn_add_rutina);
        layout_rutinas= (LinearLayout) findViewById(R.id.layout_rutinas);

        rutinas = (TextView) findViewById(R.id.textView49);

        try {
            obtener_rutinas();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        add_rutina.setOnClickListener(v -> addRutina());
        rutinas.setOnClickListener(v -> addRutina());

    }
    private void addRutina(){
        Intent intent4 = new Intent(lista_rutinas.this, Rutina.class);
        startActivity(intent4);
        finish();
    }
    private void refreshDataset_aceptadas () {
        if (mRecyclerViewAceptadas == null)
            return;

        if (mAdaptercoAcep == null) {
            mAdaptercoAcep = new rutinas_adapter(getApplication(), myDatasetCoAcep);
            mRecyclerViewAceptadas.setAdapter(mAdaptercoAcep);
        } else {
            mAdaptercoAcep.notifyDataSetChanged();
        }
    }
    public boolean obtener_rutinas() throws JSONException, UnsupportedEncodingException {
        myDatasetCoAcep = new ArrayList<rutinas_m>();

        SharedPreferences misPreferencias = getSharedPreferences("lista", Context.MODE_PRIVATE);

        String mac =  misPreferencias.getString("mac","0");
      //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("cercaMAC",mac);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/ObtenerCalendarioNotificacion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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

                            JSONArray jsonArray = obj.getJSONArray("Rutinas");
                            if (jsonArray.length()!=0){

                            }else{
                                layout_rutinas.setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject jsonObjectHijo = jsonArray.getJSONObject(i);
                                    if (jsonObjectHijo != null) {
                                        //Armamos un objeto Photo con el Title y la URL de cada JSONObject
                                        rutinas_m photo = new rutinas_m();


                                        if (jsonObjectHijo.has("id"))
                                            photo.setId(jsonObjectHijo.getString("id"));

                                        if (jsonObjectHijo.has("usuarioId"))
                                            photo.setUsuarioId(jsonObjectHijo.getString("usuarioId"));

                                        if (jsonObjectHijo.has("cercaMAC"))
                                            photo.setCercaMAC(jsonObjectHijo.getString("cercaMAC"));

                                        if (jsonObjectHijo.has("comando"))
                                            photo.setComando(jsonObjectHijo.getString("comando"));

                                        if (jsonObjectHijo.has("zonaHoraria"))
                                            photo.setZona(jsonObjectHijo.getString("zonaHoraria"));

                                        if (jsonObjectHijo.has("dia"))
                                            photo.setDia(jsonObjectHijo.getString("dia"));

                                        if (jsonObjectHijo.has("hora"))
                                            photo.setHora(jsonObjectHijo.getString("hora"));

                                        if (jsonObjectHijo.has("nombreRutina"))
                                            photo.setNombre(jsonObjectHijo.getString("nombreRutina"));

                                        if (jsonObjectHijo.has("status"))
                                            photo.setStatus(jsonObjectHijo.getString("status"));
                                        //Agreagamos el objeto Photo al Dataset
                                        //Agreagamos el objeto Photo al Dataset{

                                        myDatasetCoAcep.add(photo);


                                    }
                                    //   Toast.makeText(getApplicationContext(), String.valueOf(jsonObjectHijo), Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    Log.e("Parser JSON", e.toString());
                                }finally {
                                    //Finalmente si hemos cargado datos en el Dataset
                                    // entonces refrescamos
                                    //progressplanes.setVisibility(View.GONE);
                                    if (myDatasetCoAcep.size() > 0){
                                        //  texto2.setVisibility(View.VISIBLE);
                                        //   aceptadas_s.setVisibility(View.GONE);
                                        refreshDataset_aceptadas();


                                    }
                                    else{
                                        //  texto2.setVisibility(View.VISIBLE);
                                        mRecyclerViewAceptadas.setVisibility(View.GONE);
                                        //     aceptadas_s.setVisibility(View.GONE);

                                    }
                                }
                            }
                              Toast.makeText(getApplicationContext(), obj.getString("listConsents"), Toast.LENGTH_LONG).show();
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