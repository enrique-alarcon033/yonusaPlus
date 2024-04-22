package com.yonusa.cercasyonusaplus.ui.createAccount.create;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.api.ApiManager;
import com.yonusa.cercasyonusaplus.ui.createAccount.view.Activar_cuenta;
import com.yonusa.cercasyonusaplus.ui.createAccount.view.Create_Account_Activity;
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

public class CrearCuenta extends AppCompatActivity {
    int count1 = 0;
    int count0= 0;
    private EditText name, lastName, email, passwrd, confPassword, cellPhone,codigo_pais;
    private CountryCodePicker countryCode;
    private Button createAcBtn, mostrarPswrd, mostrarConfirmPswrd, btnShowPrivacy,btn_paises,btn_localidad;
    private CheckBox chkPrivacy;
   // private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7InVzZXJfZW1haWwiOiJlbnJpcXVlLmFsYXJjb24wM0BnbWFpbC5jb20iLCJhcGlfdG9rZW4iOiJfd1ljdk16SzVudktmeVpmX09JbkVoRDBGZmhDYV9OTVJaY1FOMUVBZVpJSmtwazBBQ0o2OS11NnZBU1lyUjJYRjUwIn0sImV4cCI6MTcxMjY3MDU1NX0.OYc0iMde_uUxKiMyEThNVbEQUtsncBWCgOIKhRl7B38";

    ApiManager apiManager;

    private static final String TAG = Create_Account_Activity.class.getSimpleName();
    LottieAnimationView loader;
    private String url;
     public EditText paises,localidades;
     SpinnerDialog spinner,spinner_localidades;

     AlertDialog alerta,alerta_paises;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);
        alerta = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Buscando Estados / Localidades")
                .setCancelable(false).build();

        alerta_paises = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando Paises")
                .setCancelable(false).build();


        loader = findViewById(R.id.loader_create_form);

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
        countryCode = findViewById(R.id.phoneCodeInput);
        createAcBtn = findViewById(R.id.create_account_btn);
        chkPrivacy = findViewById(R.id.check_privacy);
        btnShowPrivacy = findViewById(R.id.btn_privacy);
        btn_paises = findViewById(R.id.btn_paises);
        btn_localidad = findViewById(R.id.btn_localidad);
        codigo_pais = findViewById(R.id.codigo_pais);

        paises = findViewById(R.id.spinner_basic);
        localidades = findViewById(R.id.spinner_localidad);



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


        mostrarPswrd = findViewById(R.id.btn_show_pass_cc);
        mostrarPswrd.setTag("hide");
        mostrarPswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword();
            }
        });

        url="https://yonusa.com/documentos/terminosycondiciones.pdf";
        btnShowPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse(url);
                Intent intent= new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

       /* paises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                //itemSelect.setText(item);
                Toast.makeText(getApplicationContext(), "Pais seleccionado "+item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        mostrarConfirmPswrd = findViewById(R.id.btn_show_pass_confirm_cc);
        mostrarConfirmPswrd.setTag("hide");
        mostrarConfirmPswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordConfirm();
            }
        });

        createAcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean complete = validarDatos(name.getText().toString(), lastName.getText().toString(),email.getText().toString(),passwrd.getText().toString(), confPassword.getText().toString(), cellPhone.getText().toString());
                    if (complete){
                        loader.setVisibility(View.VISIBLE);
                        Crear_cuenta(name.getText().toString().trim(),lastName.getText().toString(),email.getText().toString().trim(),passwrd.getText().toString(),
                                cellPhone.getText().toString(),codigo_pais.getText().toString(),paises.getText().toString(),localidades.getText().toString());
                    }
                 } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public boolean validarDatos(String name, String lastName, String email, String password, String confPassword, String celPhone){
        String text;
        if (name.equals("")){
            Log.e(TAG, "Empty name error");
            text = getResources().getString(R.string.empty_name);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (lastName.equals("")){
            Log.e(TAG, "Empty lastName error");
            text = getResources().getString(R.string.empty_lastname);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (email.equals("") || !Validations.isValidEmail(email)){
            Log.e(TAG, "Email not valid error");
            text = getResources().getString(R.string.enter_valid_email);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (password.equals("") || !Validations.isValidPassword(password)){
            Log.e(TAG, "Password not valid error");
            text = getResources().getString(R.string.invalid_password_lenght);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_LONG).show();
            return false;
        }else if (confPassword.equals("")){
            Log.e(TAG, "Password confirm is empty valid error");
            text = getResources().getString(R.string.empty_password_confirm);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (!confPassword.equals(password)){
            Log.e(TAG, "Passwords are not equals error");
            text = getResources().getString(R.string.passwords_not_equals);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (celPhone.equals("")){
            Log.e(TAG, "Empty phone number error");
            text = getResources().getString(R.string.empty_phone);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else if (!chkPrivacy.isChecked()){
            Log.e(TAG, "No privacy policy accept error");
            text = getResources().getString(R.string.privacy_policy_not_checked);
            Toast.makeText(CrearCuenta.this,text, Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    public boolean Crear_cuenta(String nombre,String apellidos, String email,String password, String telefono,String codigoP,String pais,String estado) throws JSONException, UnsupportedEncodingException {

        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("nombre", nombre);
        oJSONObject.put("apellidos", apellidos);
        oJSONObject.put("pais",pais);
        oJSONObject.put("estado",estado);
        oJSONObject.put("email", email);
        oJSONObject.put("password", password);
        oJSONObject.put("telefono", codigoP+" "+telefono);

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/RegistrarUsuario",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                Enviar_codigo(String.valueOf(obj.get("usuarioId")),email);
                            }

                            if (valor.equals("-1")){

                                Toast.makeText(getApplicationContext(), "El correo proporcionado ya se encuentra registrado", Toast.LENGTH_LONG).show();
                                loader.setVisibility(View.GONE);
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
    public boolean Enviar_codigo(String userid,String emailt) throws JSONException, UnsupportedEncodingException {

        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioId", userid);
        oJSONObject.put("email", emailt);


        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/SolicitarCodigoActivacion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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

                                Intent intent = new Intent(getApplicationContext(), Activar_cuenta.class);
                                intent.putExtra("uuid",userid);
                                startActivity(intent);
                                finish();

                                loader.setVisibility(View.GONE);

                            }

                            if (valor.equals("-1")){

                                Toast.makeText(getApplicationContext(), "El correo proporcionado ya se encuentra registrado", Toast.LENGTH_LONG).show();
                                loader.setVisibility(View.GONE);
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
                             spinner = new SpinnerDialog(CrearCuenta.this, (ArrayList<String>) pais,"selecciona tu pais");
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
                            spinner_localidades = new SpinnerDialog(CrearCuenta.this, (ArrayList<String>) pais,"selecciona tu estado / localidad");
                            spinner_localidades.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String pais, int i) {
                                    localidades.setText(pais);
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

    public void showPassword(){
        int pass_length = passwrd.getText().length();

        if(mostrarPswrd.getTag() == "hide"){
            passwrd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mostrarPswrd.setTag("show");
            passwrd.setSelection(pass_length);
        } else {
            passwrd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mostrarPswrd.setTag("hide");
            passwrd.setSelection(pass_length);
        }
    }

    public void showPasswordConfirm(){
        int pass_length = confPassword.getText().length();

        if(mostrarConfirmPswrd.getTag() == "hide"){
            confPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mostrarConfirmPswrd.setTag("show");
            confPassword.setSelection(pass_length);
        } else {
            confPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mostrarConfirmPswrd.setTag("hide");
            confPassword.setSelection(pass_length);
        }
    }
}