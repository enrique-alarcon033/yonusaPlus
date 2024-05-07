package com.yonusa.cercasyonusaplus.ui.homeScreen.view;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.api.ApiConstants;
import com.yonusa.cercasyonusaplus.api.ApiManager;
import com.yonusa.cercasyonusaplus.api.BaseResponse;
import com.yonusa.cercasyonusaplus.ui.add_devices.ethernet.agregar_ethernet;
import com.yonusa.cercasyonusaplus.ui.add_devices.list_of_devices.AddANewDeviceActivity;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.Lista_beneficiarios;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.llamada;
import com.yonusa.cercasyonusaplus.ui.device_control.view.DeviceControlActivity;
import com.yonusa.cercasyonusaplus.ui.homeScreen.adapters.Adapter_HomeScreen_Items;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.request.ChangeDeviceNameRequest;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.request.CloseSessionRequest;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.request.HomeScreenRequest;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.response.Cerca;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.response.HomeScreenResponse;
import com.yonusa.cercasyonusaplus.ui.login.view.LogInActivity;
import com.yonusa.cercasyonusaplus.ui.login.view.Loguin_new;
import com.yonusa.cercasyonusaplus.ui.perfil.MiCuenta;
import com.yonusa.cercasyonusaplus.ui.suscripciones.CancelarSuscripcion;
import com.yonusa.cercasyonusaplus.ui.suscripciones.MisTarjetas;
import com.yonusa.cercasyonusaplus.ui.suscripciones.Paquetes;
import com.yonusa.cercasyonusaplus.ui.usbSerial.Controles;
import com.yonusa.cercasyonusaplus.utilities.catalogs.Constants;
import com.yonusa.cercasyonusaplus.utilities.catalogs.ErrorCodes;
import com.yonusa.cercasyonusaplus.utilities.catalogs.SP_Dictionary;
import com.yonusa.cercasyonusaplus.utilities.helpers.GeneralHelpers;

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
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.yonusa.cercasyonusaplus.ui.login.view.LogInActivity.md5;

public class HomeActivity extends AppCompatActivity implements Adapter_HomeScreen_Items.onItemLongPress {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private final String FROM_FINISH_ADD = "com.yonusa.cercaspaniagua.ui.login.view.FROM_FINISH_ADD";
    private Adapter_HomeScreen_Items mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ApiManager apiManager;
    List<Cerca> mDeviceList = new ArrayList<>();
    FloatingActionButton agregar,whatsapp_btn;
    ImageView iv_CloseSession, iv_AddDevices,img_alerta;
    String fullName;
    String userId;
    Context context;
    android.app.AlertDialog alerta;
    Boolean mqttConnection = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView texto,correo,nombre,apellido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        this.context = this;
        Log.i(TAG, "On Create");
      //  setTitle("Home Screen Yonusa");

        iv_AddDevices = findViewById(R.id.iv_add_devices);
        iv_AddDevices.setVisibility(View.VISIBLE);
        iv_AddDevices.setOnClickListener(v -> toAddDevices());

        alerta = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Obteniendo Cercos")
                .setCancelable(false).build();

        alerta.show();


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




        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String emailt =  misPreferencias.getString("email","0");
        String nombre_t =  misPreferencias.getString("nombre","0");
        String apellido_t =  misPreferencias.getString("apellido","0");
        String pais_t = misPreferencias.getString("pais","");
        String estado_t = misPreferencias.getString("estado","");

        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String version = pInfo.versionName;
        String versionBack = misPreferencias.getString("versionApp","0");
        if (versionBack.equals(version)){
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Actualización disponible");
            alert.setMessage("Por favor actualiza tu aplicación para tener los cambios recientes y tengas las nuevas mejoras");
            alert.setPositiveButton("Actualizar", (dialog, whichButton) -> {
                String url = "https://play.google.com/store/apps/details?id=com.yonusa.cercasplus";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            });
            alert.show();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        correo = (TextView) header.findViewById(R.id.user_correo);
        nombre = (TextView) header.findViewById(R.id.nombre);
        apellido = (TextView) header.findViewById(R.id.apellido);
        correo.setText(emailt);
        nombre.setText(nombre_t);
        apellido.setText(apellido_t);
        img_alerta = (ImageView) header.findViewById(R.id.img_alerta);
        if (pais_t.equals("")|| estado_t.equals("")){
            img_alerta.setVisibility(View.VISIBLE);
        }

        img_alerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this,"Acompleta los datos de tu cuenta",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MiCuenta.class);
                startActivity(intent);
            }
        });
        drawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            SharedPreferences prefs = getSharedPreferences("Suscripcion", MODE_PRIVATE);
            String SusEst = prefs.getString("estatus", "NS");

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_add:
                    {
                    toAddDevices();
                        // Toast.makeText(HomeActivity.this,"Proximamente",Toast.LENGTH_LONG).show();
                        break;
                    }
                    case R.id.nav_ethernet:
                    {
                        Intent intent = new Intent(getApplicationContext(), agregar_ethernet.class);
                       startActivity(intent);
                        // Toast.makeText(HomeActivity.this,"Proximamente",Toast.LENGTH_LONG).show();
                        break;
                    }

                    case R.id.nav_usbserial:
                    {
                        Intent intent = new Intent(getApplicationContext(), Controles.class);
                        startActivity(intent);
                        // Toast.makeText(HomeActivity.this,"Proximamente",Toast.LENGTH_LONG).show();
                        break;
                    }


                    case R.id.nav_perfil:
                    {
                        if (pais_t.equals("")|| estado_t.equals("")){

                        }
                        Intent intent = new Intent(getApplicationContext(), MiCuenta.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.nav_beneficiarios:
                    {
                        if(SusEst.equals("NS")||SusEst.equals("past_due")||SusEst.equals("canceled")||SusEst.equals("incomplete_expired")||SusEst.equals("unpaid")||SusEst.equals("incomplete")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Sin Suscripción");
                            alert.setMessage("Este usuario no cuenta con una suscripción activa, por favor realiza tu suscripción en nuestro portal para disfrutar de los beneficios de potección");
                               alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                            });
                            alert.show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), Lista_beneficiarios.class);
                            startActivity(intent);
                        }
                       // Toast.makeText(HomeActivity.this,"Proximamente",Toast.LENGTH_LONG).show();
                        break;
                    }
                    case R.id.nav_ambulancia:
                    {
                        if(SusEst.equals("NS")||SusEst.equals("past_due")||SusEst.equals("canceled")||SusEst.equals("incomplete_expired")||SusEst.equals("unpaid")||SusEst.equals("incomplete")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Sin Suscripción");
                            alert.setMessage("Este usuario no cuenta con una suscripción activa, por favor realiza tu suscripción en nuestro portal para disfrutar de los beneficios de potección");
                            alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                            });
                            alert.show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), llamada.class);
                            startActivity(intent);
                        }
                        break;
                    }
                    case R.id.nav_asistencia_medica:
                    {
                        if(SusEst.equals("NS")||SusEst.equals("past_due")||SusEst.equals("canceled")||SusEst.equals("incomplete_expired")||SusEst.equals("unpaid")||SusEst.equals("incomplete")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Sin Suscripción");
                            alert.setMessage("Este usuario no cuenta con una suscripción activa, por favor realiza tu suscripción en nuestro portal para disfrutar de los beneficios de potección");
                            alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                            });
                            alert.show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), llamada.class);
                            startActivity(intent);
                        }
                        break;
                    }

                    case R.id.nav_psicologo:
                    {
                        if(SusEst.equals("NS")||SusEst.equals("past_due")||SusEst.equals("canceled")||SusEst.equals("incomplete_expired")||SusEst.equals("unpaid")||SusEst.equals("incomplete")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Sin Suscripción");
                            alert.setMessage("Este usuario no cuenta con una suscripción activa, por favor realiza tu suscripción en nuestro portal para disfrutar de los beneficios de potección");
                            alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                            });
                            alert.show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), llamada.class);
                            startActivity(intent);
                        }
                        break;
                    }

                    case R.id.nav_medicamento:
                    {
                        if(SusEst.equals("NS")||SusEst.equals("past_due")||SusEst.equals("canceled")||SusEst.equals("incomplete_expired")||SusEst.equals("unpaid")||SusEst.equals("incomplete")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Sin Suscripción");
                            alert.setMessage("Este usuario no cuenta con una suscripción activa, por favor realiza tu suscripción en nuestro portal para disfrutar de los beneficios de potección");
                            alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                            });
                            alert.show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), llamada.class);
                            startActivity(intent);
                        }
                        break;
                    }

                    case R.id.nav_nutriologo:
                    {
                        if(SusEst.equals("NS")||SusEst.equals("past_due")||SusEst.equals("canceled")||SusEst.equals("incomplete_expired")||SusEst.equals("unpaid")||SusEst.equals("incomplete")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Sin Suscripción");
                            alert.setMessage("Este usuario no cuenta con una suscripción activa, por favor realiza tu suscripción en nuestro portal para disfrutar de los beneficios de potección");
                            alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                            });
                            alert.show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), llamada.class);
                            startActivity(intent);
                        }
                        break;
                    }

                    case R.id.nav_paquetes:
                    {
                            Intent intent = new Intent(getApplicationContext(), Paquetes.class);
                            startActivity(intent);
                        break;
                    }

                    case R.id.nav_tarjetas:
                    {
                        Intent intent = new Intent(getApplicationContext(), MisTarjetas.class);
                        startActivity(intent);
                     //   Toast.makeText(HomeActivity.this,"Proximamente",Toast.LENGTH_LONG).show();
                        break;
                    }

                    case R.id.nav_cancelar:
                    {
                        Intent intent = new Intent(getApplicationContext(), CancelarSuscripcion.class);
                        startActivity(intent);
                        break;
                    }
                    case   R.id.nav_comentarios:
                    {
                        showDialog(getApplicationContext(),"hola");
                        break;
                    }

                    case R.id.nav_cerrar:
                    {
                        // Toast.makeText(Lista_cercas.this,"Cerrando Sesión...",Toast.LENGTH_LONG).show();
                        try {
                            CerrarSesion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });
        navigationView.setItemIconTintList(null);
        SharedPreferences prefs2 = getSharedPreferences("Suscripcion", MODE_PRIVATE);
        String SusEst = prefs2.getString("estatus", "NS");
        if (SusEst.equals("NS")){

        }else{
            //apartado para hacer visibles la partes de paquetes, y tarjetas
            //navigationView.getMenu().findItem(R.id.nav_tarjetas).setVisible(true);
            //navigationView.getMenu().findItem(R.id.nav_paquetes).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_cancelar).setVisible(true);
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
      //  getSupportActionBar().hide();

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

        whatsapp_btn = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        whatsapp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msj = "Hola, tengo una consulta.";
                String numeroTel = "+525625727490";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String uri = "whatsapp://send?phone=" + numeroTel + "&text=" + msj;
                intent.setData(Uri.parse(uri));
                startActivity(intent);

                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    Toast.makeText(HomeActivity.this, "La aplicación Whastapp no se encuentra instalada en el dispositivo.", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
        alerta.show();
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
                     //   alerta.dismiss();
                        int devices = response.body().getCercas().size();

                        if (devices != 0) {

                            mDeviceList = response.body().getCercas();
                            buildRecyclerView(mDeviceList);

                        } else {

                            Toast.makeText(HomeActivity.this, "No hay dispositivos", Toast.LENGTH_SHORT).show();
                            alerta.dismiss();

                        }

                        break;
                    case (ErrorCodes.FAILURE):
                        //TODO: Agregar alertas de error.
                        Toast.makeText(HomeActivity.this, "Algo salió mal...", Toast.LENGTH_SHORT).show();
                        alerta.dismiss();

                        break;

                    default:
                        Log.i("Peticion login", "Algo pasó");
                        Toast.makeText(HomeActivity.this, "Error de conexión a internet ...", Toast.LENGTH_SHORT).show();
                     //   alerta.dismiss();
                }

            alerta.dismiss();
            }

            @Override
            public void onFailure(Call<HomeScreenResponse> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                //TODO: Agregar alerta de error de conexión.
              //  alerta.dismiss();
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
                "https://fntyonusa.payonusa.com/usuarios/api/v1/CerrarSesion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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


        Intent deviceControlIntent = new Intent(HomeActivity.this, DeviceControlActivity.class);

        deviceControlIntent.putExtra("USER_ID", userId);
        deviceControlIntent.putExtra("USER_ROL", rol);
        deviceControlIntent.putExtra("MODEL_ID", model);
        deviceControlIntent.putExtra("DEVICE_ID", deviceId);
        deviceControlIntent.putExtra("DEVICE_NAME", deviceName);
        deviceControlIntent.putExtra("DEVICE_MAC", mac);
        deviceControlIntent.putExtra("DEVICE_STATUS", deviceStatus);

        startActivity(deviceControlIntent);

/*
        try {
            Consultar_Cercas_pagadas(rol,model,mac,deviceId,deviceName,deviceStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  */
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

    @SuppressLint("MissingInflatedId")
    public void showDialog(Context context, String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //builder.setTitle("Dejanos tus comentarios");

        View dialogLayout = inflater.inflate(R.layout.rate_us_dialog,
                null);
        final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingsBar);
        final TextView TvRating = dialogLayout.findViewById(R.id.rateTV);
        final  EditText comentario= dialogLayout.findViewById(R.id.reviewED);
        final Button btnGuardar = dialogLayout.findViewById(R.id.btn_guardar_comentario);
        final Button btnCancelar = dialogLayout.findViewById(R.id.btnDespues);
        final ImageView img_rating = dialogLayout.findViewById(R.id.img_rating);
        final android.app.AlertDialog mdialog = builder.show();
        builder.setView(dialogLayout);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                TvRating.setText(String.valueOf(rating));
                double doble = Double.parseDouble(TvRating.getText().toString());
                int entero = (int)doble;
                //int elEntero = Integer.parseInt(Double.toString(d));
                TvRating.setText(String.valueOf(entero));

                if (rating <=1){
                    img_rating.setImageResource(R.drawable.ic_triste2);
                }
                else if (rating <=2){
                    img_rating.setImageResource(R.drawable.ic_trsite1);
                }
                else if (rating <=3){
                    img_rating.setImageResource(R.drawable.ic_neutral);
                }
                else if (rating <=4){
                    img_rating.setImageResource(R.drawable.ic_feliz1);
                }
                else if (rating <=5){
                    img_rating.setImageResource(R.drawable.ic_feliz2);
                }
                animacion(img_rating);

            }
        });

        builder.show();

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mdialog.cancel();
                Toast.makeText(HomeActivity.this, "Gracias por tu calificación, seguiremos mejorando" +
                        ratingBar.getRating(), Toast.LENGTH_SHORT).show();


            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (comentario.getText().toString().isEmpty()){
                        Toast.makeText(HomeActivity.this, "Agrega un comentario por favor", Toast.LENGTH_SHORT).show();
                    }else{
                        Guardar_Comentario(Integer.parseInt(TvRating.getText().toString()),comentario.getText().toString());
                        builder.setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(HomeActivity.this, "Gracias por tu calificación, seguiremos mejorando" +
                                        ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                                Intent addDevicesIntent = new Intent(HomeActivity.this, HomeActivity.class);
                                startActivity(addDevicesIntent);
                                finish();
                            }
                        });
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void animacion (ImageView ratinImage){
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1f,0,1f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratinImage.startAnimation(scaleAnimation);
    }
    public boolean Guardar_Comentario(int cali, String coment) throws JSONException, UnsupportedEncodingException {
       // myDatasetCoAcep = new ArrayList<cercas_model_completo>();

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);

        String id_user = misPreferencias.getString("usuarioId", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioId", id_user);
        oJSONObject.put("calificacion", cali);
        oJSONObject.put("comentarios", coment);
        oJSONObject.put("plataforma", "Android");
        oJSONObject.put("version", "3.0");
        //valor 3 es para la aplicacion de yonusa plus
        //Valor 2 es para paniagua
        //valor uno es para instaladores
        //valor 0 es para 2.0
        oJSONObject.put("aplicacion", 3);

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
                "https://fntyonusa.payonusa.com/api/comentarios",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                        System.out.println(statusCode);
                        System.out.println(responseBody);
                        //     mMap = googleMap;d
                        Toast.makeText(getApplicationContext(), "Gracias por tu comentario", Toast.LENGTH_LONG).show();

                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject obj = new JSONObject(content);
                            String valor = String.valueOf(obj.get("codigo"));
                            Intent addDevicesIntent = new Intent(HomeActivity.this, HomeActivity.class);
                            startActivity(addDevicesIntent);
                            finish();

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
