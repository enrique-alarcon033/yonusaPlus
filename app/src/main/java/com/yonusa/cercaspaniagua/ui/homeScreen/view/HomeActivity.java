package com.yonusa.cercaspaniagua.ui.homeScreen.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercaspaniagua.R;
import com.yonusa.cercaspaniagua.api.ApiConstants;
import com.yonusa.cercaspaniagua.api.ApiManager;
import com.yonusa.cercaspaniagua.api.BaseResponse;
import com.yonusa.cercaspaniagua.ui.add_devices.list_of_devices.AddANewDeviceActivity;
import com.yonusa.cercaspaniagua.ui.cercas.modelo.cercas_model_completo;
import com.yonusa.cercaspaniagua.ui.device_control.view.DeviceControlActivity;
import com.yonusa.cercaspaniagua.ui.homeScreen.adapters.Adapter_HomeScreen_Items;
import com.yonusa.cercaspaniagua.ui.homeScreen.models.request.ChangeDeviceNameRequest;
import com.yonusa.cercaspaniagua.ui.homeScreen.models.request.CloseSessionRequest;
import com.yonusa.cercaspaniagua.ui.homeScreen.models.request.HomeScreenRequest;
import com.yonusa.cercaspaniagua.ui.homeScreen.models.response.Cerca;
import com.yonusa.cercaspaniagua.ui.homeScreen.models.response.HomeScreenResponse;
import com.yonusa.cercaspaniagua.ui.login.view.LogInActivity;
import com.yonusa.cercaspaniagua.ui.login.view.Loguin_new;
import com.yonusa.cercaspaniagua.utilities.catalogs.Constants;
import com.yonusa.cercaspaniagua.utilities.catalogs.ErrorCodes;
import com.yonusa.cercaspaniagua.utilities.catalogs.SP_Dictionary;
import com.yonusa.cercaspaniagua.utilities.helpers.GeneralHelpers;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.yonusa.cercaspaniagua.ui.login.view.LogInActivity.md5;

public class HomeActivity extends AppCompatActivity implements Adapter_HomeScreen_Items.onItemLongPress {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private final String FROM_FINISH_ADD = "com.yonusa.cercaspaniagua.ui.login.view.FROM_FINISH_ADD";
    private Adapter_HomeScreen_Items mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ApiManager apiManager;
    List<Cerca> mDeviceList = new ArrayList<>();

    ImageView iv_CloseSession, iv_AddDevices;
    String fullName;
    String userId;
    Context context;
    Boolean mqttConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        this.context = this;
        Log.i(TAG, "On Create");
        setTitle("Home Screen Yonusa");

        iv_AddDevices = findViewById(R.id.iv_add_devices);
        iv_AddDevices.setVisibility(View.GONE);
        iv_AddDevices.setOnClickListener(v -> toAddDevices());

        iv_CloseSession = findViewById(R.id.iv_close_session);
        iv_CloseSession.setVisibility(View.VISIBLE);
        iv_CloseSession.setOnClickListener(v -> {
            try {
                CerrarSesion();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        try {
            Consultar_suscripcion();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
/*
        Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.pseudo_white));
*/
        getSupportActionBar().hide();

        SharedPreferences prefs = getSharedPreferences("Datos_usuario", MODE_PRIVATE);
        String userName = prefs.getString("nombre", "No username defined");
        String userLastName = prefs.getString("apellido", "No userlastName defined");

        fullName = userName + " " + userLastName;

        TextView tvFullName = findViewById(R.id.tv_fullName);
        tvFullName.setTypeface(null, Typeface.BOLD);
        tvFullName.setText(fullName);

        String email = prefs.getString("email", "No email defined");

        TextView tvEmail = findViewById(R.id.tv_userEmail);
        tvEmail.setTypeface(null, Typeface.NORMAL);
        tvEmail.setText(email);

        userId = prefs.getString("usuarioId", "No user id definded");


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent fromFinishAdd = getIntent();
        if (fromFinishAdd.getBooleanExtra(FROM_FINISH_ADD,false)) {
           GeneralHelpers.singleMakeAlert(context,"Mensaje para el usuario","Favor de cerrar seción y esperar a que la cerca se conecte");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toGetDevices();
    }


    public void toGetDevices() {
        HomeScreenRequest mHomeScreenRequest = new HomeScreenRequest();
        mHomeScreenRequest.setUsuarioId(userId);

        getDevices(mHomeScreenRequest);
    }

    public void getDevices(HomeScreenRequest homeScreenRequest) {

        //Inicializa la construcción de la petición para que "apiManager" traiga la URL Base
        apiManager = ApiConstants.getAPIManager();

        //Una vez que apiManager es asignado, se crea el "call" y se ocupa el api "login" contenido en ApiManager que es la interface de retrofit.
        Call<HomeScreenResponse> call = apiManager.getDevices(homeScreenRequest);

        //Se ejecuta la petición y se asigna el objeto LogIn_Response para recibir el Callback del servicio.
        call.enqueue(new Callback<HomeScreenResponse>() {
            @Override
            public void onResponse(Call<HomeScreenResponse> call, Response<HomeScreenResponse> response) {

                int typeCode = response.body().getCodigo();
                switch (typeCode) {
                    case (ErrorCodes.SUCCESS):

                        int devices = response.body().getCercas().size();

                        if (devices != 0) {

                            mDeviceList = response.body().getCercas();
                            buildRecyclerView(mDeviceList);

                        } else {

                            Toast.makeText(HomeActivity.this, "No hay dispositivos", Toast.LENGTH_SHORT).show();

                        }

                        break;
                    case (ErrorCodes.FAILURE):
                        //TODO: Agregar alertas de error.
                        Toast.makeText(HomeActivity.this, "Algo salió mal...", Toast.LENGTH_SHORT).show();

                        break;

                    default:
                        Log.i("Peticion login", "Algo pasó");
                        Toast.makeText(HomeActivity.this, "Error de conexión a internet ...", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<HomeScreenResponse> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                //TODO: Agregar alerta de error de conexión.
            }
        });

    }

    public void buildRecyclerView(List<Cerca> deviceList) {

        Log.i("Devices: ------------>", deviceList.toString());
        RecyclerView recyclerView = findViewById(R.id.device_list);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(HomeActivity.this);
        mAdapter = new Adapter_HomeScreen_Items((ArrayList<Cerca>) deviceList, this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> goToDeviceControl(deviceList, position));

        SharedPreferences prefs = getSharedPreferences("Datos_usuario", MODE_PRIVATE);

        String mqttHost = prefs.getString("MQTT_SERVER", "");
        String mqttPort = prefs.getString("MQTT_PORT", "");
        String mqttPass = prefs.getString("MQTT_PWD", "");
        String mqttUser = prefs.getString("MQTT_USER", "");

       // Toast.makeText(HomeActivity.this, "host:"+mqttHost+"puerto:"+mqttPort+"pass:"+mqttPass+"user:"+mqttUser, Toast.LENGTH_SHORT).show();
        if (mqttHost.isEmpty() || mqttPort.isEmpty() || mqttPass.isEmpty() || mqttUser.isEmpty())
            return;
        setMqttClient(mqttHost, mqttPort, mqttUser, mqttPass, userId);

    }

    @Override
    public void onBackPressed() {


        Log.d(this.getClass().getName(), "back button pressed and do nothing");

    }

    private void toCloseSession() {

        SharedPreferences prefs = getSharedPreferences(SP_Dictionary.USER_INFO, MODE_PRIVATE);
        String uniqueId = prefs.getString("UniqueId", "No UniqueId defined");

        //String uniqueId = "TESTING-UNIQUEID080890";
        uniqueId = md5(uniqueId);
        String userId = prefs.getString(SP_Dictionary.USER_ID, "No userId defined");

        Log.i(TAG, uniqueId);
        Log.i(TAG, userId);

        CloseSessionRequest closeSessionRequest = new CloseSessionRequest();

        closeSessionRequest.setUsuarioId(userId);
        closeSessionRequest.setDispositivoId(uniqueId);

        String preferenceContainer = SP_Dictionary.USER_INFO;
        SharedPreferences preferences = getSharedPreferences(preferenceContainer, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        apiManager = ApiConstants.getAPIManager();

        //Una vez que apiManager es asignado, se crea el "call" y se ocupa el api "login" contenido en ApiManager que es la interface de retrofit.
        Call<BaseResponse> call = apiManager.postCloseSession(closeSessionRequest);

        //Se ejecuta la petición y se asigna el objeto LogIn_Response para recibir el Callback del servicio.
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                int typeCode = response.body().getCodigo();
                Intent login = new Intent(context, LogInActivity.class);
                switch (typeCode) {
                    case (ErrorCodes.SUCCESS):

                        Log.i(TAG, "Close session successfully");
                        editor.clear();
                        editor.apply();


                        startActivity(login);
                        finish();

                        break;
                    case (ErrorCodes.FAILURE):
                        Log.e(TAG, "Close session failure");
                        editor.clear();
                        editor.apply();

                        startActivity(login);
                        finish();

                        //TODO: Agregar alertas de error.
                        Toast.makeText(HomeActivity.this, "Algo salió mal...", Toast.LENGTH_SHORT).show();

                        break;

                    default:
                        Log.i("Peticion login", "Algo pasó");
                        Toast.makeText(HomeActivity.this, "Error de conexión a internet ...", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                //TODO: Agregar alerta de error de conexión.
            }
        });


        //FIXME: Arreglar la petición para cerrar sesión y para regresar al Home Screen
    }

    public boolean CerrarSesion() throws JSONException, UnsupportedEncodingException {


        SharedPreferences prefs = getSharedPreferences(SP_Dictionary.USER_INFO, MODE_PRIVATE);
        String uniqueId = prefs.getString("UniqueId_2", "No UniqueId defined");

        //String uniqueId = "TESTING-UNIQUEID080890";
      //  uniqueId = md5(uniqueId);
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);

        String id_user = misPreferencias.getString("usuarioId", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioId", id_user);
        oJSONObject.put("dispositivoId",uniqueId);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "http://payonusa.com/paniagua/usuario/api/v1/CerrarSesion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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

                                SharedPreferences sharedPref2 =getSharedPreferences("Datos_acceso",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = sharedPref2.edit();
                                editor2.putString("correo", "");
                                editor2.putString("password", "");
                                editor2.putString("recordar","0");
                                editor2.commit();
                                Toast.makeText(getApplicationContext(), "Sesión Cerrada", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Loguin_new.class);
                                startActivity(intent);
                                finish();
                            }

                            if (valor.equals("-2")){

                                Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_LONG).show();
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

    private void toAddDevices() {
        Intent addDevicesIntent = new Intent(HomeActivity.this, AddANewDeviceActivity.class);
        startActivity(addDevicesIntent);

    }

    public void goToDeviceControl(List<Cerca> deviceList, int position) {

        String model = deviceList.get(position).getModelo();
        String deviceId = deviceList.get(position).getCercaId();
        Integer rol = deviceList.get(position).getRol();
        String deviceName = deviceList.get(position).getAliasCerca();
        String mac = deviceList.get(position).getMAC();
        Boolean deviceStatus = deviceList.get(position).getEstadoConexionAlSistema();

        mqttDisconnect();





        try {
            Consultar_Cercas_pagadas(rol,model,mac,deviceId,deviceName,deviceStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    /*    SharedPreferences misPreferencias = getSharedPreferences("Suscripcion", Context.MODE_PRIVATE);
        String active = misPreferencias.getString("active", "0");

        if (active.equals("false")){
            Toast.makeText(this, "No Cuentas con una suscripción Activa", Toast.LENGTH_SHORT).show();
        }else{

            String model = deviceList.get(position).getModelo();
            String deviceId = deviceList.get(position).getCercaId();
            Integer rol = deviceList.get(position).getRol();
            String deviceName = deviceList.get(position).getAliasCerca();
            String mac = deviceList.get(position).getMAC();
            Boolean deviceStatus = deviceList.get(position).getEstadoConexionAlSistema();

            mqttDisconnect();

            Intent deviceControlIntent = new Intent(HomeActivity.this, DeviceControlActivity.class);

            deviceControlIntent.putExtra("USER_ID", userId);
            deviceControlIntent.putExtra("USER_ROL", rol);
            deviceControlIntent.putExtra("MODEL_ID", model);
            deviceControlIntent.putExtra("DEVICE_ID", deviceId);
            deviceControlIntent.putExtra("DEVICE_NAME", deviceName);
            deviceControlIntent.putExtra("DEVICE_MAC", mac);
            deviceControlIntent.putExtra("DEVICE_STATUS", deviceStatus);

            startActivity(deviceControlIntent);
        } */

    }

    private volatile IMqttAsyncClient mqttClient;

    public void mqttDisconnect() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                Log.i("MQTT:", "Getting disconnected");
                mqttClient.disconnect();
                mqttConnection = false;
            } catch (MqttException e) {
                e.printStackTrace();
                mqttConnection = true;
            }
        }
    }

    private void setMqttClient(String mqttHost, String mqttPort, String mqttUser, String mqttPass, String userId) {

        String topic = Constants.MQTT_LISTENER_HOMESCREEN + userId;

        MqttConnectOptions options = new MqttConnectOptions();

        options.setCleanSession(false);
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(60);
        options.setUserName(mqttUser);
        options.setPassword(mqttPass.toCharArray());

        String clientId = MqttClient.generateClientId();
        Log.i("Subscribed to: ----->", topic);

        try {

            mqttClient = new MqttAsyncClient("tcp://" + mqttHost + ":" + mqttPort + "", clientId, new MemoryPersistence());

            IMqttToken token = mqttClient.connect(options);
            token.waitForCompletion(3500);

            mqttClient.setCallback(new MqttEventCallback());

            token = mqttClient.subscribe(topic, 1);
            token.waitForCompletion(5000);

        } catch (MqttSecurityException e) {

            e.printStackTrace();

        } catch (MqttException e) {

            switch (e.getReasonCode()) {

                case MqttException.REASON_CODE_BROKER_UNAVAILABLE:
                    Log.e("UNAVAILABLE MQTT SERVER", e.getMessage());
                    e.printStackTrace();
                    break;
                case MqttException.REASON_CODE_CLIENT_TIMEOUT:
                    Log.e("CLIENT_TIMEOUT MQTT", e.getMessage());
                    e.printStackTrace();
                    break;
                case MqttException.REASON_CODE_CONNECTION_LOST:
                    Log.e("MQTT CONNECTION LOST", e.getMessage());
                    e.printStackTrace();
                    break;
                case MqttException.REASON_CODE_SERVER_CONNECT_ERROR:
                    Log.e("MQTT SERVER CONNECT ERR", e.getMessage());
                    e.printStackTrace();
                    break;
                case MqttException.REASON_CODE_FAILED_AUTHENTICATION:
                    Log.e("MQTT FAILED AUTH", e.getMessage());
                    break;
                default:
                    Log.e("MQTT CONNECTION ERROR ", e.getMessage());
                    e.printStackTrace();

            }

        }

    }

    @Override
    public void onLongPressItem(int position) {
        //Toast.makeText(this, "LongPressItem", Toast.LENGTH_SHORT).show();
        changeDeviceName(position);
    }

    public void changeDeviceName(int position) {
        final EditText edittext = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.rename_device_title).concat(" ").concat(this.mDeviceList.get(position).getAliasCerca()));
        alert.setMessage(getString(R.string.new_control_name_description));

        alert.setView(edittext);

        alert.setPositiveButton(getString(R.string.save), (dialog, whichButton) -> {
            String newNameDevice = edittext.getText().toString();
            if (newNameDevice.equals("")) {
                Toast.makeText(this, getString(R.string.invalid_control_name), Toast.LENGTH_SHORT).show();
            } else {
                setNewDeviceName(newNameDevice, this.mDeviceList.get(position).getCercaId(), position);
            }

        });

        alert.setNegativeButton(getString(R.string.back), (dialog, whichButton) -> {
        });

        alert.show();
    }

    public void setNewDeviceName(String newDeviceName, String deviceId, int position) {
        ChangeDeviceNameRequest changeName = new ChangeDeviceNameRequest();
        changeName.setCercaId(deviceId);
        changeName.setNuevoAliasCerca(newDeviceName);
        changeName.setUsuarioId(this.userId);
        apiManager = ApiConstants.getAPIManager();
        Call<BaseResponse> call = apiManager.changeDeviceName(changeName);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCodigo() == ErrorCodes.SUCCESS) {
                        Toast.makeText(HomeActivity.this, getString(R.string.device_name_success), Toast.LENGTH_SHORT).show();
                        mDeviceList.get(position).setAliasCerca(newDeviceName);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        GeneralHelpers.singleMakeAlert(HomeActivity.this, getString(R.string.error_title), response.body().getMensaje());
                    }
                } else {
                    GeneralHelpers.singleMakeAlert(HomeActivity.this, getString(R.string.error_title), getString(R.string.an_error_ocurred_text));
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                GeneralHelpers.singleMakeAlert(HomeActivity.this, getString(R.string.error_title), getString(R.string.an_error_ocurred_text));
            }
        });
    }

    private class MqttEventCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable arg0) {
            Log.e("MQTT: ", "Connection Lost");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i("MQTT:", "Callback deliveryComplete");

        }

        @Override
        @SuppressLint("NewApi")
        public void messageArrived(String topic, final MqttMessage msg) {

            Handler h = new Handler(Looper.getMainLooper());
            h.post(() -> messageArrive(msg));

        }

    }

    public void messageArrive(MqttMessage message) {
        String messageReceived = message.toString();
        Log.i("Message received", messageReceived);
        toGetDevices();
        if (messageReceived.contains("REFRESH") || messageReceived.contains("CONFIG")) {
            toGetDevices();

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
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                "http://payonusa.com/paniagua/usuario/api/v1/prices/"+id_user,(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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

                            }else{
                                SharedPreferences sharedPref2 =getSharedPreferences("Suscripcion",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = sharedPref2.edit();
                                editor2.putString("estatus", (String) obj.get("statusSuscription"));
                                editor2.putString("active",String.valueOf((Boolean) obj.get("active")));
                                editor2.putString("priceId",(String) obj.get("priceIdSubscribed"));
                                editor2.commit();
                               // Toast.makeText(getApplicationContext(), "Cuentas con suscripción Activa", Toast.LENGTH_SHORT).show();
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

    public boolean Consultar_Cercas_pagadas(Integer rol, String model, String mac, String deviceId, String deviceName, Boolean deviceStatus) throws JSONException, UnsupportedEncodingException {


        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);

        String id_user = misPreferencias.getString("usuarioId", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
     //   oJSONObject.put("usuarioId", id_user);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
         RequestHandle requestHandle = oHttpClient.get(getApplicationContext(),
                "http://payonusa.com/api/ListaCercasPagadas/"+id_user,(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                          //  String valor = String.valueOf(obj.get("active"));

                            JSONArray jsonArray = obj.getJSONArray("ListaCercaPagadas");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject jsonObjectHijo = jsonArray.getJSONObject(i);
                                    //  Toast.makeText(getApplicationContext(), String.valueOf(jsonObjectHijo), Toast.LENGTH_SHORT).show();
                                    //  Toast.makeText(getApplicationContext(), String.valueOf(jsonArray2), Toast.LENGTH_SHORT).show();
                                    JSONObject obj2 =new JSONObject(String.valueOf(jsonObjectHijo));
                                    String cercaId = obj2.getString("cercaId");
                                    String status = obj2.getString("status");
                                    if (cercaId.equals(deviceId)&& status.equals("active")){
                                        Intent deviceControlIntent = new Intent(HomeActivity.this, DeviceControlActivity.class);
                                        deviceControlIntent.putExtra("USER_ID", userId);
                                        deviceControlIntent.putExtra("USER_ROL", rol);
                                        deviceControlIntent.putExtra("MODEL_ID", model);
                                        deviceControlIntent.putExtra("DEVICE_ID", deviceId);
                                        deviceControlIntent.putExtra("DEVICE_NAME", deviceName);
                                        deviceControlIntent.putExtra("DEVICE_MAC", mac);
                                        deviceControlIntent.putExtra("DEVICE_STATUS", deviceStatus);
                                        startActivity(deviceControlIntent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Esta cerca no cuenta con ninguna suscripción", Toast.LENGTH_SHORT).show();
                                    }
                                 // SONArray jsonArray2 = obj2.getJSONArray("coordenates");

                                } catch (JSONException e) {
                                    Log.e("Parser JSON", e.toString());
                                }
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


}
