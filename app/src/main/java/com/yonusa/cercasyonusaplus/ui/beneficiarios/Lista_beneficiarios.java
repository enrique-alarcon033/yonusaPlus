package com.yonusa.cercasyonusaplus.ui.beneficiarios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.adapter.beneficiarios_adapter;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.mocelo.beneficiario_model;
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

public class Lista_beneficiarios extends AppCompatActivity {

    FloatingActionButton add;

    private RecyclerView mRecyclerViewAceptadas;
    private RecyclerView.Adapter mAdapterco;
    private RecyclerView.LayoutManager mLayoutManagerco;

    private RecyclerView.Adapter mAdaptercoAcep;
    private RecyclerView.LayoutManager mLayoutManagercoAcep;
    //El dataset de tipo Photo
    private ArrayList<beneficiario_model> myDatasetCoAcep;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_beneficiarios2);

        add = (FloatingActionButton) findViewById(R.id.add_beneficiario);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);

                String limite = misPreferencias.getString("totalLimite", "1");
                String beneficiarios = misPreferencias.getString("totalBeneficiarios", "0");
                int lim = Integer.valueOf(limite);
                int ben = Integer.valueOf(beneficiarios);
                if (lim==ben){
                   Toast.makeText(getApplicationContext(), "Has llegado al limite máximo de beneficiarios", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), Nuevo_Beneficiario.class);
                    startActivity(intent);
                }

            }
        });

        mRecyclerViewAceptadas = (RecyclerView) findViewById(R.id.lista_beneficiarios);
        mRecyclerViewAceptadas.setHasFixedSize(true);
        mLayoutManagercoAcep = new GridLayoutManager(getApplication(), 1);
        mRecyclerViewAceptadas.setLayoutManager(mLayoutManagercoAcep);

        try {
            obtener_beneficiarios();
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
            mAdaptercoAcep = new beneficiarios_adapter(getApplication(), myDatasetCoAcep);
            mRecyclerViewAceptadas.setAdapter(mAdaptercoAcep);
        } else {
            mAdaptercoAcep.notifyDataSetChanged();
        }
    }
    public boolean obtener_beneficiarios() throws JSONException, UnsupportedEncodingException {
        myDatasetCoAcep = new ArrayList<beneficiario_model>();

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);

        String id_user =  misPreferencias.getString("usuarioId","0");
        //  String token =  misPreferencias.getString("accessToken","0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
     //   oJSONObject.put("cercaMAC",mac);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                "http://payonusa.com/usuarios/api/v1/ObtenerBeneficiarios?bindingUsuarioId="+id_user,(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                            //JSONObject obj = new JSONObject(content);
                            //String totalLimite = String.valueOf(obj.get("totalLimite"));
                            //String totalBeneficiarios = String.valueOf(obj.get("totalBeneficiarios"));
                            SharedPreferences sharedPref =getSharedPreferences("Datos_usuario",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("totalLimite", String.valueOf(obj.get("totalLimite")));
                            editor.putString("totalBeneficiarios", String.valueOf(obj.get("totalBeneficiarios")));

                            editor.commit();
                            JSONArray jsonArray = obj.getJSONArray("respuestaObtenerBeneficiarios");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject jsonObjectHijo = jsonArray.getJSONObject(i);
                                    if (jsonObjectHijo != null) {
                                        //Armamos un objeto Photo con el Title y la URL de cada JSONObject
                                        beneficiario_model photo = new beneficiario_model();


                                        if (jsonObjectHijo.has("IdBeneficiario"))
                                            photo.setIdBeneficiario(jsonObjectHijo.getString("IdBeneficiario"));

                                        if (jsonObjectHijo.has("Nombre"))
                                            photo.setNombre(jsonObjectHijo.getString("Nombre"));

                                        if (jsonObjectHijo.has("Apellidos"))
                                            photo.setApellidos(jsonObjectHijo.getString("Apellidos"));

                                        if (jsonObjectHijo.has("Email"))
                                            photo.setEmail(jsonObjectHijo.getString("Email"));

                                        if (jsonObjectHijo.has("RFC"))
                                            photo.setRfc(jsonObjectHijo.getString("RFC"));


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