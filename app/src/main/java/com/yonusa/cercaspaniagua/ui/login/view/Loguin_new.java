package com.yonusa.cercaspaniagua.ui.login.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercaspaniagua.R;
import com.yonusa.cercaspaniagua.api.ApiConstants;
import com.yonusa.cercaspaniagua.api.ApiManager;
import com.yonusa.cercaspaniagua.api.BaseResponse;
import com.yonusa.cercaspaniagua.ui.cercas.Lista_cercas;
import com.yonusa.cercaspaniagua.ui.createAccount.create.CrearCuenta;
import com.yonusa.cercaspaniagua.ui.homeScreen.view.HomeActivity;
import com.yonusa.cercaspaniagua.ui.login.models.RegisterNotificationsRequest;
import com.yonusa.cercaspaniagua.ui.password_recovery.Recovery_one;
import com.yonusa.cercaspaniagua.utilities.catalogs.Constants;
import com.yonusa.cercaspaniagua.utilities.catalogs.ErrorCodes;
import com.yonusa.cercaspaniagua.utilities.catalogs.SP_Dictionary;
import com.yonusa.cercaspaniagua.utilities.firebaseService.FirebaseCloudMessagingService;
import com.yonusa.cercaspaniagua.utilities.firebaseService.RegistrationIntentService;
import com.yonusa.cercaspaniagua.utilities.helpers.SP_Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



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
    ApiManager apiManager;
    Context context = this;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG = LogInActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin_new);

        Log.i(TAG, "On ");
        edtEmail = findViewById(R.id.email_editText);
        edtPass = findViewById(R.id.pass_editText);
        loader = findViewById(R.id.loader_loguin);
        btnLogin = findViewById(R.id.login_button);

        SP_Helper sp_helper = new SP_Helper();


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

        //SP_Helper sp_helper = new SP_Helper();

        //Persistencia de sesión
        try {

            SharedPreferences prefs2 = getSharedPreferences(SP_Dictionary.USER_INFO, MODE_PRIVATE);
            String userId = prefs2.getString(SP_Dictionary.USER_ID, "No userId defined");

            if (userId == "No userId defined"){
                //Si no existe userId se borra la informacion del usuario si existe.
                sp_helper.cleanUserInfo(context);
            } else {
                //Si existe userId guardado se registra el dispositivo para recibir notificaciones y despues se envía directo a Home screen.

                String notificationId = prefs2.getString("FCMtoken", "No notificationId defined");
              //  String uniqueId = prefs2.getString("UniqueId: ", "No UniqueId defined");

                Log.i(TAG, notificationId);
                //String uniqueId = "TESTING-UNIQUEID080890";
              //  uniqueId = md5(uniqueId);
                String uniqueId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i(TAG, "Md5 UniqueId" + uniqueId);
                String registrationID = prefs2.getString("registrationID", "No notificationId defined");

                if (!notificationId.equals("No notificationId defined")) {

                    registerUserWithDeviceToNotifications(userId, notificationId, uniqueId, Constants.HUB_NOTIFICATION_ID);
                    //goToHomeScreen();

                } else {
                    registerWithNotificationHubs();
                    FirebaseCloudMessagingService.createChannelAndHandleNotifications(getApplicationContext());

                    Thread.sleep(400);

                    registerUserWithDeviceToNotifications(userId, notificationId, uniqueId, Constants.HUB_NOTIFICATION_ID);
                    // goToHomeScreen();
                    //Toast.makeText(LogInActivity.this, "Restart your application", Toast.LENGTH_SHORT).show();

                }

            }

        } catch(Exception e){
            //Si algo falla en la carga del shared preference no se muestra nada y se permanece en log in.
            Toast.makeText(Loguin_new.this, "User id inexistente", Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getApplicationContext(), "Log in Correcto", Toast.LENGTH_LONG).show();

                                SP_Helper sp_helper = new SP_Helper();
                                //Persistencia de sesión
                                try {

                                    SharedPreferences prefs = getSharedPreferences(SP_Dictionary.USER_INFO, MODE_PRIVATE);
                                   // String userId = prefs.getString(SP_Dictionary.USER_ID, "No userId defined");

                                    SharedPreferences prefs2 = getSharedPreferences("Datos_usuario", MODE_PRIVATE);
                                    String userId = prefs2.getString("usuarioId", "No userId defined");

                                    if (userId == "No userId defined"){
                                        //Si no existe userId se borra la informacion del usuario si existe.
                                        sp_helper.cleanUserInfo(context);
                                    } else {
                                        //Si existe userId guardado se registra el dispositivo para recibir notificaciones y despues se envía directo a Home screen.

                                        String notificationId = prefs.getString("FCMtoken", "No notificationId defined");
                                   //     String uniqueId = prefs.getString("UniqueId: ", "No UniqueId defined");

                                        Log.i(TAG, notificationId);
                                        //String uniqueId = "TESTING-UNIQUEID080890";
                                       // uniqueId = md5(uniqueId);
                                        String uniqueId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                        Log.i(TAG, "Md5 UniqueId" + uniqueId);
                                        String registrationID = prefs.getString("registrationID", "No notificationId defined");


                                        SharedPreferences.Editor editor2 = prefs.edit();
                                        editor2.putString("UniqueId_2", uniqueId);
                                        editor2.commit();

                                        if (!notificationId.equals("No notificationId defined")) {

                                            registerUserWithDeviceToNotifications(userId, notificationId, uniqueId, Constants.HUB_NOTIFICATION_ID);
                                          //  goToHomeScreen();
                                           // Toast.makeText(Loguin_new.this, "User 1", Toast.LENGTH_SHORT).show();

                                        } else {
                                            registerWithNotificationHubs();
                                            FirebaseCloudMessagingService.createChannelAndHandleNotifications(getApplicationContext());
                                           // Toast.makeText(Loguin_new.this, "User 2", Toast.LENGTH_SHORT).show();
                                            Thread.sleep(400);

                                            registerUserWithDeviceToNotifications(userId, notificationId, uniqueId, Constants.HUB_NOTIFICATION_ID);
                                           // goToHomeScreen();
                                            //Toast.makeText(LogInActivity.this, "Restart your application", Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                } catch(Exception e){
                                    //Si algo falla en la carga del shared preference no se muestra nada y se permanece en log in.
                                    Toast.makeText(Loguin_new.this, "User id inexistente", Toast.LENGTH_SHORT).show();

                                }
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

    public void registerUserWithDeviceToNotifications(String userId, String notificationsId, String deviceId, String hubNotificationId){

        RegisterNotificationsRequest registerNotificationsRequest= new RegisterNotificationsRequest();
        registerNotificationsRequest.setUsuarioId(userId);
        registerNotificationsRequest.setDispositivoId(deviceId);
        registerNotificationsRequest.setNotificacionesId(notificationsId);
        registerNotificationsRequest.setHubNotificationId(hubNotificationId);


        Log.i(TAG, "NOTIFICATION ID: ------>" + registerNotificationsRequest.getNotificacionesId());
        Log.i(TAG, "NOTIFICATION DEVICE ID: ------>" + registerNotificationsRequest.getDispositivoId());

        apiManager = ApiConstants.getAPIManager();

        Call<BaseResponse> call = apiManager.postRegisterIdNotification(registerNotificationsRequest);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {


                int typeCode = response.body().getCodigo();

                switch (typeCode) {
                    case (ErrorCodes.SUCCESS):
                    //    Toast.makeText(Loguin_new.this, "Dispositivo registrado para notificaciones"+hubNotificationId, Toast.LENGTH_SHORT).show();
                        Log.i(TAG,"Dispositivo registrado para notificaciones.");
                        Log.i(TAG, response.body().getMensaje());
                        break;
                    case (ErrorCodes.FAILURE):
                      //  Toast.makeText(Loguin_new.this, "Dispositivo NO registrado para notificaciones", Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"Dispositivo NO registrado para notificaciones.");
                        Log.e(TAG, response.body().getMensaje());
                        break;
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });

    }

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();

            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                Toast.makeText(Loguin_new.this, "This device is not supported by Google Play Services.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }
}