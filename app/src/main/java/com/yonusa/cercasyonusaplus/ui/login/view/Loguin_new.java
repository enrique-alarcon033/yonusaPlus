package com.yonusa.cercasyonusaplus.ui.login.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.api.ApiConstants;
import com.yonusa.cercasyonusaplus.api.ApiManager;
import com.yonusa.cercasyonusaplus.api.BaseResponse;
import com.yonusa.cercasyonusaplus.ui.createAccount.create.CrearCuenta;
import com.yonusa.cercasyonusaplus.ui.homeScreen.view.HomeActivity;
import com.yonusa.cercasyonusaplus.ui.login.models.RegisterNotificationsRequest;
import com.yonusa.cercasyonusaplus.ui.password_recovery.Recovery_one;
import com.yonusa.cercasyonusaplus.ui.usbSerial.Controles;
import com.yonusa.cercasyonusaplus.ui.usbSerial.ListaCerco;
import com.yonusa.cercasyonusaplus.utilities.catalogs.Constants;
import com.yonusa.cercasyonusaplus.utilities.catalogs.ErrorCodes;
import com.yonusa.cercasyonusaplus.utilities.catalogs.SP_Dictionary;
import com.yonusa.cercasyonusaplus.utilities.firebaseService.FirebaseCloudMessagingService;
import com.yonusa.cercasyonusaplus.utilities.helpers.SP_Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import de.mateware.snacky.Snacky;
import dmax.dialog.SpotsDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class Loguin_new extends AppCompatActivity implements Connectable, Disconnectable, Bindable {


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

    AlertDialog alerta;
    private Merlin merlin;
    ConstraintLayout snack_layout;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG = LogInActivity.class.getSimpleName();

    private static String TAG1 = "xxxxxx";
    private static final int RQC = 1;
    private String[] grupoPermisos = new String[]{Manifest.permission.POST_NOTIFICATIONS};
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 100;

    ImageView img_usb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin_new);

        Log.i(TAG, "On ");
        edtEmail = findViewById(R.id.email_editText);
        edtPass = findViewById(R.id.pass_editText);
        loader = findViewById(R.id.loader_loguin);
        btnLogin = findViewById(R.id.login_button);
        snack_layout = (ConstraintLayout) findViewById(R.id.snack_loguin);
        img_usb = (ImageView) findViewById(R.id.img_usb);

        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);

        alerta = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();



      //  new GetTask().execute();
        try {
            ObtenerTokenPaises();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        SP_Helper sp_helper = new SP_Helper();
        createNotificationChannel();
        compruebaPermiso();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loguin(edtEmail.getText().toString(),edtPass.getText().toString());
                    //loader.setVisibility(View.VISIBLE);
                    alerta.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        getVersionActual(context);

// Solicitar el permiso al usuario
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                REQUEST_CODE_SCHEDULE_EXACT_ALARM);


        img_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListaCerco.class);
                startActivity(intent);
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

            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                if (!TextUtils.isEmpty(token)) {
                    Log.d(TAG, "retrieve token successful : " + token);

                    SharedPreferences sharedPref2 =getSharedPreferences("User_info",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putString("FCMtoken", token);
                    editor2.commit();
                } else{
                    Log.w(TAG, "token should not be null...");
                }
            }).addOnFailureListener(e -> {
                //handle e
            }).addOnCanceledListener(() -> {
                //handle cancel
            }).addOnCompleteListener(task -> Log.v(TAG, "This is the token : " + task.getResult()));

            SharedPreferences prefs2 = getSharedPreferences("Datos_usuario", MODE_PRIVATE);
            String userId = prefs2.getString("usuarioId", "No userId defined");

            SharedPreferences prefs1 = getSharedPreferences("User_info", MODE_PRIVATE);
            String fcm_token = prefs2.getString("FCMtoken", "NoToken");



            if (userId == "No userId defined"){
                //Si no existe userId se borra la informacion del usuario si existe.
                sp_helper.cleanUserInfo(context);
            } else {
                //Si existe userId guardado se registra el dispositivo para recibir notificaciones y despues se envía directo a Home screen.

                String notificationId = prefs1.getString("FCMtoken", "No notificationId defined");
                String uniqueId = prefs.getString("UniqueId: ", "No UniqueId defined");

                Log.i(TAG, notificationId);
                //String uniqueId = "TESTING-UNIQUEID080890";
                uniqueId = md5(uniqueId);
                Log.i(TAG, "Md5 UniqueId" + uniqueId);
                String registrationID = prefs.getString("registrationID", "No notificationId defined");

                if (!notificationId.equals("No notificationId defined")) {

                    registerUserWithDeviceToNotifications(userId, notificationId, uniqueId, Constants.HUB_NOTIFICATION_ID);
                    //goToHomeScreen();

                } else {
                    //registerWithNotificationHubs();
                    //    FirebaseCloudMessagingService.createChannelAndHandleNotifications(getApplicationContext());

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
        public boolean ObtenerTokenPaises() throws JSONException, UnsupportedEncodingException {
            JSONObject oJSONObject = new JSONObject();
            String token = "_wYcvMzK5nvKfyZf_OInEhD0FfhCa_NMRZcQN1EAeZIJkpk0ACJ69-u6vASYrR2XF50";
            ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
            oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            AsyncHttpClient oHttpClient = new AsyncHttpClient();
                    oHttpClient.addHeader("user-email", "enrique.alarcon03@gmail.com");
                    oHttpClient.addHeader("api-token", token);
            //cambiar varible
            RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                    "https://www.universal-tutorial.com/api/getaccesstoken", (HttpEntity) oEntity, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            // called before request is starte
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.println(statusCode);
                            System.out.println(responseBody);
                            //     mMap = googleMap;d

                            try {
                                String content = new String(responseBody, "UTF-8");
                                JSONObject obj = new JSONObject(content);
                                String valor = String.valueOf(obj.get("auth_token"));
                                SharedPreferences sharedPref =getSharedPreferences("Datos_usuario",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("token_pais", valor);
                                editor.commit();
                            // Toast.makeText(getApplicationContext(), String.valueOf(valor), Toast.LENGTH_LONG).show();
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

    // Manejar la respuesta del usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario ha concedido el permiso
                // Puedes configurar alarmas exactas
            } else {
                // El usuario ha denegado el permiso
                // No puedes configurar alarmas exactas
            }
        }
    }
    public String getVersionActual(Context ctx){
        try {
            return ctx.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
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
                "https://fntyonusa.payonusa.com/usuarios/api/v1/IniciarSesion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                editor.putString("pais", String.valueOf(obj.get("pais")));
                                editor.putString("estado", String.valueOf(obj.get("estado")));
                                editor.putString("MQTT_SERVER", String.valueOf(obj.get("MQTT_SERVER")));
                                editor.putString("MQTT_PORT", String.valueOf(obj.get("MQTT_PORT")));
                                editor.putString("MQTT_USER",String.valueOf(obj.get("MQTT_USER")));
                                editor.putString("MQTT_PWD", String.valueOf(obj.get("MQTT_PWD")));
                                editor.putString("versionApp", String.valueOf(obj.get("versionApp")));
                               // editor.putString("accessToken", String.valueOf(obj.get("accessToken")));
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
                                Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_LONG).show();

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
                                        String Hub ="Yon3-NotHub002";

                                        SharedPreferences.Editor editor2 = prefs.edit();
                                        editor2.putString("UniqueId_2", uniqueId);
                                        editor2.commit();

                                        if (!notificationId.equals("No notificationId defined")) {

                                            registerUserWithDeviceToNotifications(userId, notificationId, uniqueId, Hub);
                                          //  goToHomeScreen();
                                       //     Toast.makeText(Loguin_new.this, Constants.HUB_NOTIFICATION_ID, Toast.LENGTH_SHORT).show();

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
                             //   Consultar_suscripcion();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"Verifica tu correo y/o Contraseña", Toast.LENGTH_LONG).show();
                                alerta.dismiss();
                                //loader.setVisibility(View.GONE);
                            }

                            // Toast.makeText(getApplicationContext(), String.valueOf(names), Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            alerta.dismiss();
                            //loader.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            alerta.dismiss();
                            //loader.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "404 !", Toast.LENGTH_LONG).show();
                            //loader.setVisibility(View.GONE);
                        } else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "500 !", Toast.LENGTH_LONG).show();
                           // loader.setVisibility(View.GONE);
                            //sin_tarjetas();
                        } else if (statusCode == 403) {
                            Toast.makeText(getApplicationContext(), "403 !", Toast.LENGTH_LONG).show();
                            //loader.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "No ha respondido el servidor, verifica tu conexión a internet", Toast.LENGTH_LONG).show();
                          //  loader.setVisibility(View.GONE);
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
        Log.i(TAG, "NOTIFICATION HUB: ------>" + registerNotificationsRequest.getHubNotificationId());

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
       //     Intent intent = new Intent(this, RegistrationIntentService.class);
         //   startService(intent);
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


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Yonusa Plus";
            String description = "Descripcion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Yonusa Plus", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean Consultar_suscripcion() throws JSONException, UnsupportedEncodingException {
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String id_user = misPreferencias.getString("usuarioId", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioId", id_user);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                "http://payonusa.com/usuarios/api/v1/prices/"+id_user,(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {
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
                            String valor = String.valueOf(obj.get("active"));
                            if (valor.equals("false")){
                                SharedPreferences sharedPref2 =getSharedPreferences("Suscripcion",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = sharedPref2.edit();
                                editor2.putString("estatus", (String) obj.get("statusSuscription"));
                                editor2.putString("active", String.valueOf((Boolean) obj.get("active")));
                                editor2.putString("priceId",(String) obj.get("priceIdSubscribed"));
                                editor2.commit();
                                //   Toast.makeText(getApplicationContext(), "No Cuentas con suscripción Activa", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                SharedPreferences sharedPref2 =getSharedPreferences("Suscripcion",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = sharedPref2.edit();
                                editor2.putString("estatus", (String) obj.get("statusSuscription"));
                                editor2.putString("active",String.valueOf((Boolean) obj.get("active")));
                                editor2.putString("priceId",(String) obj.get("priceIdSubscribed"));
                                editor2.commit();
                                // Toast.makeText(getApplicationContext(), "Cuentas con suscripción Activa", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
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

    private void compruebaPermiso() {
        for (String cad: grupoPermisos){
            if (ContextCompat.checkSelfPermission(this,cad) == PackageManager.PERMISSION_DENIED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    //requestPermissions(new String[]{cad}, RQC);
                    ActivityCompat.requestPermissions(
                            Loguin_new.this,
                            new String[] {cad},
                            RQC);

                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (merlin!= null){
            merlin.bind();
        }
    }
    @Override
    protected void  onDestroy(){
        super.onDestroy();
        if(merlin != null){
            merlin.unbind();
        }
    }
    @Override
    public void onBind(NetworkStatus networkStatus) {
        onDisconnect();
       // Toast.makeText(getApplicationContext(), "Con acceso a internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnect() {
        snack_conectado();
       // Toast.makeText(getApplicationContext(), "Sin acceso a internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //networkStatusDisplayer.displayNegativeMessage("Conexion a Intenert inestable",Loguin_new.this);
                // Toast.makeText(getApplicationContext(), "Sin acceso a internet", Toast.LENGTH_SHORT).show();
                snack_wifi_off();
            }
        });
    }
    private void snack_error() {
        Snacky.builder()
                .setBackgroundColor(Color.parseColor("#FF600A"))
                .setTextSize(18)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(
                        "Tu Conexión de internet es inestable, conectate a otra red")
                .setMaxLines(4)
                .centerText()
                .setActionText("Ok")
                .setActionTextColor(Color.parseColor("#66FFFFFF"))
                .setActionTextSize(20)
                .setActionTextTypefaceStyle(Typeface.BOLD)
                .setIcon(R.drawable.ic_no_wifi)
                .setActivity(Loguin_new.this)
                .setDuration(Snacky.LENGTH_INDEFINITE)
                .build()
                .show();

    }
    private void snack_wifi_off(){
        Snackbar warningSnackBar = Snacky.builder()
                .setActivity(Loguin_new.this)
                .setText("En este momento no tienes Conexión")
                .centerText()
                .setMaxLines(4)
                .setActionText("Ok")
                .setActionTextSize(20)
                .setActionTextColor(Color.parseColor("#000000"))
                .setDuration(Snacky.LENGTH_INDEFINITE)
                .warning();
        warningSnackBar.show();
    }

    private void snack_conectado() {
        Snacky.builder()
                .setActivity(Loguin_new.this)
                .setText("Conexión Estable")
                .setDuration(1000)
                .success()
                .show();

    }
}