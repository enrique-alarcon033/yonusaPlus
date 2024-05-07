package com.yonusa.cercasyonusaplus.ui.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.createAccount.create.CrearCuenta;
import com.yonusa.cercasyonusaplus.ui.createAccount.view.Activar_cuenta;
import com.yonusa.cercasyonusaplus.ui.createAccount.view.Create_Account_Activity;
import com.yonusa.cercasyonusaplus.ui.homeScreen.view.HomeActivity;
import com.yonusa.cercasyonusaplus.utilities.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import dmax.dialog.SpotsDialog;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class ActualizarDatos extends AppCompatActivity {
    private static final String TAG = ActualizarDatos.class.getSimpleName();
    private EditText name, lastName, email, passwrd, confPassword, cellPhone,codigo_pais;

    public EditText paises,localidades;
    private Button btn_actualizar, mostrarPswrd, mostrarConfirmPswrd, btnShowPrivacy,btn_paises,btn_localidad;
    AlertDialog alerta,alerta_paises,alerta3;
    SpinnerDialog spinner,spinner_localidades;

    int count1 = 0;
    int count0= 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_datos);

        alerta = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Buscando Estados / Localidades")
                .setCancelable(false).build();

        alerta3 = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Actualizando Datos")
                .setCancelable(false).build();


        alerta_paises = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando Paises")
                .setCancelable(false).build();

        try {
            Obtener_paises();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        name = findViewById(R.id.nombre_editText_cc);
        lastName = findViewById(R.id.apellidos_editText_cc);
        email = findViewById(R.id.email_editText_cc);
        passwrd = findViewById(R.id.passwrd_editText_cc);
        confPassword = findViewById(R.id.passwrd_confirm_editText_cc);
        cellPhone = findViewById(R.id.celphone_editText_cc);
        btn_actualizar = findViewById(R.id.create_account_btn);
//        btnShowPrivacy = findViewById(R.id.btn_privacy);
        btn_paises = findViewById(R.id.btn_paises);
        btn_localidad = findViewById(R.id.btn_localidad);
        codigo_pais = findViewById(R.id.codigo_pais);

        paises = findViewById(R.id.spinner_basic);
        localidades = findViewById(R.id.spinner_localidad);


        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser_t = misPreferencias.getString("usuarioId","0");
        String telefono_t =  misPreferencias.getString("telefono","0");
        String nombre_t =  misPreferencias.getString("nombre","0");
        String apellido_t =  misPreferencias.getString("apellido","0");
        String pais_t = misPreferencias.getString("pais","");
        String estado_t = misPreferencias.getString("estado","");
        String lada_t = misPreferencias.getString("lada","");

        name.setText(nombre_t);
        lastName.setText(apellido_t);
        cellPhone.setText(telefono_t);
        codigo_pais.setText(lada_t);

        paises.setText(pais_t);
        localidades.setText(estado_t);


        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean complete = validarDatos(name.getText().toString(), lastName.getText().toString(),
                            cellPhone.getText().toString(),paises.getText().toString(),localidades.getText().toString());
                    if (complete){
                        //   loader.setVisibility(View.VISIBLE);
                        alerta3.show();
                        Actualizar_perfil(idUser_t.toString(), name.getText().toString(), lastName.getText().toString(),codigo_pais.getText().toString(), cellPhone.getText().toString(),
                                paises.getText().toString(), localidades.getText().toString());
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btn_paises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count0>=1){
                    spinner.showSpinerDialog();
                }else{
                    Toast.makeText(getApplicationContext(), "Hubo un error al cargar los paises", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_localidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count1>=1){
                    if (paises.getText().length()>3){
                        spinner_localidades.showSpinerDialog();
                    }else{
                        Toast.makeText(getApplicationContext(), "Selecciona primero un Pais", Toast.LENGTH_SHORT).show();
                    }
                }else{
                        Toast.makeText(getApplicationContext(), "Selecciona primero un Pais", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public boolean validarDatos(String name, String lastName, String celPhone, String pais, String estado){
        String text;
        if (name.equals("")){
            Log.e(TAG, "Empty name error");
            text = getResources().getString(R.string.empty_name);
            Toast.makeText(ActualizarDatos.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (lastName.equals("")){
            Log.e(TAG, "Empty lastName error");
            text = getResources().getString(R.string.empty_lastname);
            Toast.makeText(ActualizarDatos.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (celPhone.equals("")){
            Log.e(TAG, "Empty phone number error");
            text = getResources().getString(R.string.empty_phone);
            Toast.makeText(ActualizarDatos.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (pais.equals("")){
            Log.e(TAG, "Empty phone number error");
            text = getResources().getString(R.string.empty_pais);
            Toast.makeText(ActualizarDatos.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (estado.equals("")){
            Log.e(TAG, "Empty phone number error");
            text = getResources().getString(R.string.empty_estado);
            Toast.makeText(ActualizarDatos.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    public boolean Actualizar_perfil(String userid,String nombre,String apellidos,String lada, String telefono,String pais,String estado) throws JSONException, UnsupportedEncodingException {
        alerta3.show();
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioId", userid);
        oJSONObject.put("nombre", nombre);
        oJSONObject.put("apellidos", apellidos);
        oJSONObject.put("telefono",lada+" "+ telefono);
        oJSONObject.put("pais", pais);
        oJSONObject.put("estado", estado);

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/ActualizarPerfil",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                        System.out.println(statusCode);
                        System.out.println(responseBody);
                        //     mMap = googleMap;d

                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject obj = new JSONObject(content);
                            String valor = String.valueOf(obj.get("codigo"));
                            if (valor.equals("0")){
                                SharedPreferences sharedPref =getSharedPreferences("Datos_usuario",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("nombre", nombre);
                                editor.putString("apellido", apellidos);
                                editor.putString("pais", pais);
                                editor.putString("estado", estado);
                                editor.putString("telefono",telefono);
                                editor.putString("lada",lada);
                                editor.commit();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                              //  loader.setVisibility(View.GONE);
                            }
                            if (valor.equals("-1")){
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error al actualizar los datos", Toast.LENGTH_LONG).show();
                               // loader.setVisibility(View.GONE);
                            }
                            alerta3.dismiss();
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
                    alerta3.dismiss();
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
    public boolean Obtener_paises() throws JSONException, UnsupportedEncodingException {
        alerta_paises.show();
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("nombre", "nombre");

        SharedPreferences sharedPref =getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token_pais", "0");
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                "https://www.universal-tutorial.com/api/countries",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                        System.out.println(statusCode);
                        System.out.println(responseBody);
                        //     mMap = googleMap;d
                        count0 = count0+1;
                        try {
                            String content = new String(responseBody, "UTF-8");
                            //JSONObject obj = new JSONObject(content);
                            JSONArray array = new JSONArray(content);

                            //       String valor = String.valueOf(obj.get("country_name"));
                            String val = "uno";
                            String val2 = "dos";
                            String val3= "tres";

                            ArrayList<String> auxiliar = new ArrayList<>();
                            List<String> pais = new ArrayList<>();
                            List<String> code_pais = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                // Acceder al objeto en el índice `i` usando `contenido.getJSONObject(i)`
                                String val4= "tres"+i;
                                //  Toast.makeText(getApplicationContext(),  String.valueOf(array.getJSONObject(1)), Toast.LENGTH_LONG).show();
                                // Realizar operaciones en el objeto
                                try {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    if (jsonObject.has("country_name")) {
                                        String valor = jsonObject.getString("country_name");
                                        pais.add(valor);
                                    }
                                    if (jsonObject.has("country_phone_code")) {
                                        String code = jsonObject.getString("country_phone_code");
                                        code_pais.add(code);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            alerta_paises.dismiss();
                            //ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, (List) pais);
                            //paises.setAdapter(adapter);
                            // Toast.makeText(getApplicationContext(), String.valueOf(names), Toast.LENGTH_LONG).show();
                            spinner = new SpinnerDialog(ActualizarDatos.this, (ArrayList<String>) pais,"selecciona tu pais");
                            spinner.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String pais, int i) {
                                    paises.setText(pais);
                                    localidades.setText("");
                                    try {
                                        Obtener_localidades(pais);
                                        codigo_pais.setText(code_pais.get(i));
                                        count1= count1+1;
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "404 !", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "500 ! Error al cargar paises", Toast.LENGTH_LONG).show();
                            //sin_tarjetas();
                        } else if (statusCode == 403) {
                            Toast.makeText(getApplicationContext(), "403 !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Reintentar" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                        alerta_paises.dismiss();
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
    public boolean Obtener_localidades(String pais) throws JSONException, UnsupportedEncodingException {
        alerta.show();
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("nombre", "nombre");
        SharedPreferences sharedPref =getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token_pais", "0");

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                "https://www.universal-tutorial.com/api/states/"+pais,(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                        System.out.println(statusCode);
                        System.out.println(responseBody);
                        //     mMap = googleMap;d

                        try {
                            String content = new String(responseBody, "UTF-8");
                            //JSONObject obj = new JSONObject(content);
                            JSONArray array = new JSONArray(content);

                            //       String valor = String.valueOf(obj.get("country_name"));
                            String val = "uno";
                            String val2 = "dos";
                            String val3= "tres";

                            ArrayList<String> auxiliar = new ArrayList<>();
                            List<String> pais = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                // Acceder al objeto en el índice `i` usando `contenido.getJSONObject(i)`
                                String val4= "tres"+i;
                                //  Toast.makeText(getApplicationContext(),  String.valueOf(array.getJSONObject(1)), Toast.LENGTH_LONG).show();
                                // Realizar operaciones en el objeto
                                try {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    if (jsonObject.has("state_name")) {
                                        String valor = jsonObject.getString("state_name");
                                        // Aquí hacer algo con el valor
                                        // Toast.makeText(getApplicationContext(),  valor, Toast.LENGTH_LONG).show();
                                        // auxiliar.add(valor);
                                        pais.add(valor);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            alerta.dismiss();
                            //ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, (List) pais);
                            //paises.setAdapter(adapter);
                            // Toast.makeText(getApplicationContext(), String.valueOf(names), Toast.LENGTH_LONG).show();
                            spinner_localidades = new SpinnerDialog(ActualizarDatos.this, (ArrayList<String>) pais,"selecciona tu estado / localidad");
                            spinner_localidades.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String pais, int i) {
                                    localidades.setText(pais);
                                   // Toast.makeText(getApplicationContext(), pais, Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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
                        alerta.dismiss();
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