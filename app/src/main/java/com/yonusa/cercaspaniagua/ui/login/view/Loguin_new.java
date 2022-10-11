package com.yonusa.cercaspaniagua.ui.login.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercaspaniagua.R;
import com.yonusa.cercaspaniagua.ui.cercas.Lista_cercas;
import com.yonusa.cercaspaniagua.ui.createAccount.create.CrearCuenta;
import com.yonusa.cercaspaniagua.ui.homeScreen.view.HomeActivity;
import com.yonusa.cercaspaniagua.ui.password_recovery.Recovery_one;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Loguin_new extends AppCompatActivity {


    private EditText edtEmail;
    private EditText edtPass;
    private Button btnShow;
    private Button btnLogin;
    private Button btnRecoveryPassword;
    private Button btnCreateAccount;
    LottieAnimationView loader;
    private String email;
    private String pass;
    CheckBox recordar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin_new);

        edtEmail = findViewById(R.id.email_editText);
        edtPass = findViewById(R.id.pass_editText);
        loader = findViewById(R.id.loader_loguin);
        btnLogin = findViewById(R.id.login_button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loguin(edtEmail.getText().toString(),edtPass.getText().toString());
                    loader.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCreateAccount = findViewById(R.id.btn_create_account);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CrearCuenta.class);
                startActivity(intent);
            }
        });

        btnShow = findViewById(R.id.btn_show_pass);
        btnShow.setTag("hide");
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showPassword();
            }
        });

        btnRecoveryPassword = findViewById(R.id.btn_recovery_pass);
        btnRecoveryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Recovery_one.class);
                startActivity(intent);
            }
        });

        recordar = (CheckBox) findViewById(R.id.checkBox_loguin);

        SharedPreferences prefs = getSharedPreferences("Datos_acceso", MODE_PRIVATE);
        String restoredText = prefs.getString("recordar", "0");
        if (restoredText.equals("1")) {
            String name = prefs.getString("correo", "");
            String password = prefs.getString("password", "");
            edtEmail.setText(name);
            edtPass.setText(password);
            recordar.setEnabled(true);
            recordar.setChecked(true);
        }

    }

    public boolean loguin(String correo, String password) throws JSONException, UnsupportedEncodingException {

        SharedPreferences misPreferencias = getSharedPreferences("User_info", Context.MODE_PRIVATE);
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("email",correo);
        oJSONObject.put("password",password);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
         //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "http://payonusa.com/paniagua/usuario/api/v1/IniciarSesion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                editor.putString("usuarioId", String.valueOf(obj.get("usuarioId")));
                                editor.putString("email", String.valueOf(obj.get("email")));
                                editor.putString("nombre", String.valueOf(obj.get("nombre")));
                                editor.putString("apellido", String.valueOf(obj.get("apellido")));
                                editor.putString("telefono", String.valueOf(obj.get("telefono")));
                                editor.putString("MQTT_SERVER", String.valueOf(obj.get("MQTT_SERVER")));
                                editor.putString("MQTT_PORT", String.valueOf(obj.get("MQTT_PORT")));
                                editor.putString("MQTT_USER",String.valueOf(obj.get("MQTT_USER")));
                                editor.putString("MQTT_PWD", String.valueOf(obj.get("MQTT_PWD")));
                                editor.putString("accessToken", String.valueOf(obj.get("accessToken")));
                                editor.commit();


                                if(recordar.isChecked()){
                                    SharedPreferences sharedPref2 =getSharedPreferences("Datos_acceso",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                                    editor2.putString("correo", edtEmail.getText().toString());
                                    editor2.putString("password", edtPass.getText().toString());
                                    editor2.putString("recordar","1");
                                    editor2.commit();
                                }else{
                                    SharedPreferences sharedPref2 =getSharedPreferences("Datos_acceso",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                                    editor2.putString("correo", "");
                                    editor2.putString("password", "");
                                    editor2.putString("recordar","0");
                                    editor2.commit();
                                }
                                Toast.makeText(getApplicationContext(), "Loguin Correcto", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"Verifica tu correo y/o Contraseña", Toast.LENGTH_LONG).show();
                                loader.setVisibility(View.GONE);
                            }

                            // Toast.makeText(getApplicationContext(), String.valueOf(names), Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            loader.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loader.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "404 !", Toast.LENGTH_LONG).show();
                            loader.setVisibility(View.GONE);
                        } else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "500 !", Toast.LENGTH_LONG).show();
                            loader.setVisibility(View.GONE);
                            //sin_tarjetas();
                        } else if (statusCode == 403) {
                            Toast.makeText(getApplicationContext(), "403 !", Toast.LENGTH_LONG).show();
                            loader.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "No ha respondido el servidor, verifica tu conexión a internet", Toast.LENGTH_LONG).show();
                            loader.setVisibility(View.GONE);
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


    public void showPassword(){

        int pass_length = edtPass.getText().length();

        if(btnShow.getTag() == "hide"){
            edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            btnShow.setTag("show");
            edtPass.setSelection(pass_length);
        } else {
            edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnShow.setTag("hide");
            edtPass.setSelection(pass_length);
        }

    }
}