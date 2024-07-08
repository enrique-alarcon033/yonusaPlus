package com.yonusa.cercasyonusaplus.ui.device_control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.yonusa.cercasyonusaplus.MainActivity;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.api.ApiConstants;
import com.yonusa.cercasyonusaplus.api.ApiManager;
import com.yonusa.cercasyonusaplus.api.BaseResponse;
import com.yonusa.cercasyonusaplus.databinding.DeviceControlMainBinding;
import com.yonusa.cercasyonusaplus.mqtt.Publisher;
import com.yonusa.cercasyonusaplus.ui.add_devices.ethernet.agregar_ethernet;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.Lista_beneficiarios;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.llamada;
import com.yonusa.cercasyonusaplus.ui.device_control.models.eventos_model;
import com.yonusa.cercasyonusaplus.ui.device_control.models.request.GetDeviceControlsRequest;
import com.yonusa.cercasyonusaplus.ui.device_control.models.request.GetEventsByDateRequest;
import com.yonusa.cercasyonusaplus.ui.device_control.models.request.SetNewControlNameRequest;
import com.yonusa.cercasyonusaplus.ui.device_control.models.response.Controls;
import com.yonusa.cercasyonusaplus.ui.device_control.models.response.GetDeviceControlsResponse;
import com.yonusa.cercasyonusaplus.ui.device_control.models.response.GetEventsByDateResponse;
import com.yonusa.cercasyonusaplus.ui.device_control.models.response.Historial;
import com.yonusa.cercasyonusaplus.ui.device_control.view.Adapters.DeviceControlAdapter;
import com.yonusa.cercasyonusaplus.ui.device_control.view.Adapters.eventos_adapter;
import com.yonusa.cercasyonusaplus.ui.homeScreen.view.HomeActivity;
import com.yonusa.cercasyonusaplus.ui.login.view.Loguin_new;
import com.yonusa.cercasyonusaplus.ui.perfil.MiCuenta;
import com.yonusa.cercasyonusaplus.ui.rutinas.lista_rutinas;
import com.yonusa.cercasyonusaplus.ui.suscripciones.CancelarSuscripcion;
import com.yonusa.cercasyonusaplus.ui.suscripciones.MisTarjetas;
import com.yonusa.cercasyonusaplus.ui.suscripciones.Paquetes;
import com.yonusa.cercasyonusaplus.ui.usbSerial.Controles;
import com.yonusa.cercasyonusaplus.ui.view.view.userList.userAdministration;
import com.yonusa.cercasyonusaplus.utilities.catalogs.Constants;
import com.yonusa.cercasyonusaplus.utilities.catalogs.ErrorCodes;
import com.yonusa.cercasyonusaplus.utilities.catalogs.Mqtt_CMD;
import com.yonusa.cercasyonusaplus.utilities.catalogs.SP_Dictionary;
import com.yonusa.cercasyonusaplus.utilities.helpers.GeneralHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import de.mateware.snacky.Snacky;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceControlActivity extends AppCompatActivity
        implements View.OnClickListener, eventDate.OnFragmentInteractionListener, DeviceControlAdapter.onDataControlListener,
        Connectable, Disconnectable, Bindable {

    private static final String TAG = DeviceControlActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private GetDeviceControlsResponse deviceControlsResponse;
    private ApiManager apiManager;
    private ImageView userAdm;
    private TextView tvEventDesc, tvEventName, tvDate, rutinas, botones;
    private RelativeLayout rlControlDescription;
    private SharedPreferences prefs;
    private MediaPlayer mediaPlayer;
    private int lyControlHeight;
    private ArrayList<Controls> controlsResp = new ArrayList<>();
    private DeviceControlMainBinding binding;
    private String uuid;
    private Integer rol;
    private String cercaId,fechaD;
    private String deviceName;
    private String model;
    private String mac;
    private Boolean status,statusReset, corriente, statusAlarma, isLongPress = false;
    private SimpleDateFormat sdfYMDTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy / MM / dd");
    private Context context;
    BottomNavigationView barraNav;

    private ArrayList<eventos_model> myDatasetCoAcep2;
    private RecyclerView mRecyclerViewAceptadas2;
    private RecyclerView.Adapter mAdaptercoAcep2;
    private RecyclerView.LayoutManager mLayoutManagercoAcep2;

    Toolbar toolbar;

    private Merlin merlin;
    TextView Tv_wifi, Tv_corriente, Tv_alarma, Nombre_cerco, Tv_mac, Tv_preguntas,estado_wifi,estado_alarma,Tv_der,Tv_der1,TvFecha,TvFechaActual;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    android.app.AlertDialog dialog_controles, dialog_comando;


    enum ControlOptions {
        SET_DATE(9);
        public final int option;

        ControlOptions(int option) {
            this.option = option;
        }

        int getValue() {
            return option;
        }
    }


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_main);
        this.context = this;

        binding = DataBindingUtil.setContentView(this, R.layout.device_control_main);
        prefs = this.getSharedPreferences(SP_Dictionary.USER_INFO, Context.MODE_PRIVATE);
        Log.i(TAG, "On Create");
        //  setTitle("Device control");
        //  getSupportActionBar().hide();


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        //actionBar.setSubtitle("   Design a custom Action Bar");
        actionBar.setDisplayShowHomeEnabled(true); // Enable home button
        actionBar.setDisplayHomeAsUpEnabled(true); // Enable back button (if needed)
        //  actionBar.setIcon(R.drawable.logo_yonusa_blanco); // Set the icon
        actionBar.setIcon(R.drawable.logo_yonusa_blanco);
        // methods to display the icon in the ActionBar
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);


        uuid = getIntent().getStringExtra("USER_ID");
        rol = getIntent().getIntExtra("USER_ROL", 1);
        model = getIntent().getStringExtra("MODEL_ID");
        cercaId = getIntent().getStringExtra("DEVICE_ID");
        deviceName = getIntent().getStringExtra("DEVICE_NAME");
        mac = getIntent().getStringExtra("DEVICE_MAC");
        status = getIntent().getBooleanExtra("DEVICE_STATUS", false);
        statusReset=getIntent().getBooleanExtra("DEVICE_STATUS_RESET", false);
        corriente = getIntent().getBooleanExtra("DEVICE_CORRIENTE", false);
        statusAlarma = getIntent().getBooleanExtra("STATUS_ALARMA", false);
        fechaD = getIntent().getStringExtra("FECHA");

        estado_wifi = (TextView) findViewById(R.id.tv_estado_wifi);
        estado_alarma = (TextView) findViewById(R.id.tv_estado_alarma);
        Tv_wifi = (TextView) findViewById(R.id.tv_wifi);
        Tv_corriente = (TextView) findViewById(R.id.tv_corriente);
        Tv_alarma = (TextView) findViewById(R.id.tv_alarma);
        Nombre_cerco = (TextView) findViewById(R.id.tv_nombre_cerco);
        Tv_mac = (TextView) findViewById(R.id.tv_MAC);
        Tv_preguntas = (TextView) findViewById(R.id.tv_pregunas);
        Tv_der = (TextView) findViewById(R.id.textView53);
        Tv_der1 = (TextView) findViewById(R.id.textView57);
        TvFecha = (TextView) findViewById(R.id.tv_fecha_alta);
        TvFechaActual = (TextView) findViewById(R.id.tv_fecha_Actual);

        obtenerFecha();

        String Dfecha = fechaD.substring(0,10);
        TvFecha.setText(Dfecha);

        mRecyclerViewAceptadas2 = (RecyclerView) findViewById(R.id.lista_eventos);
        mRecyclerViewAceptadas2.setHasFixedSize(true);
        mLayoutManagercoAcep2 = new GridLayoutManager(getApplication(), 1);
        mRecyclerViewAceptadas2.setLayoutManager(mLayoutManagercoAcep2);

        calcularFechas(Dfecha,TvFechaActual.getText().toString());

        Tv_preguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPreguntas(getApplicationContext(), "Hola");
            }
        });

        Nombre_cerco.setText(deviceName);
        Tv_mac.setText(mac);

        if (status.equals(true)) {
            Tv_wifi.setBackgroundResource(R.drawable.ic_wifi_on);
            estado_wifi.setText("Conectado");
            Tv_der.setBackgroundResource(R.drawable.img_cerco_der_verde);
            Tv_der1.setBackgroundResource(R.drawable.img_cerco_der1_verde);
        }
        if (corriente.equals(true)) {
            Tv_corriente.setBackgroundResource(R.drawable.ic_sistema_on);
        }
        if (statusAlarma.equals(true)) {
            Tv_alarma.setBackgroundResource(R.drawable.ic_sirena_on);
            estado_alarma.setText("Activada / Sonando");
        }

        rutinas = (TextView) findViewById(R.id.tv_rutinas);
        botones = (TextView) findViewById(R.id.tv_botones);

        SharedPreferences preferences = getSharedPreferences("lista", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("mac", mac);
        editor.apply();

        mediaPlayer = MediaPlayer.create(this, R.raw.opendoor);

        dialog_controles = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando Controles")
                .setCancelable(false).build();

        dialog_comando = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Enviando Comando al modulo Wifi")
                .setCancelable(false).build();

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        lyControlHeight = (point.y / 5);

        getControls();

        userAdm = findViewById(R.id.iv_user_configuration);
        userAdm.setOnClickListener(this);

        rlControlDescription = findViewById(R.id.relative_control_description);
        rlControlDescription.setOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_device_control);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 3);
        adapter = new DeviceControlAdapter(DeviceControlActivity.this, controlsResp, status, this, lyControlHeight);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        try {
            obtener_eventos5(cercaId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

  /*      rutinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceControlActivity.this, lista_rutinas.class);
                startActivity(intent);
            }
        });*/

      /*  botones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceControlActivity.this, Botones.class);
                startActivity(intent);
            }
        });*/

    }

    private void refreshDataset_aceptadas2() {
        if (mRecyclerViewAceptadas2 == null)
            return;

        if (mAdaptercoAcep2 == null) {
            mAdaptercoAcep2 = new eventos_adapter(getApplication(), myDatasetCoAcep2);
            mRecyclerViewAceptadas2.setAdapter(mAdaptercoAcep2);
        } else {
            mAdaptercoAcep2.notifyDataSetChanged();
        }
    }

    private void obtenerFecha()
    {
        try
        {
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = df.format(currentDate);

            TvFechaActual.setText(date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void calcularFechas(String fecha1,String fechaActual){


        String A1 = fecha1.substring(0,4);
        String M1 = fecha1.substring(5,7);
        String D1 = fecha1.substring(8,10);

        int a1= Integer.parseInt(A1);
        int m1 = Integer.parseInt(M1);
        int d1 = Integer.parseInt(D1);

        String AA1 = fechaActual.substring(0,4);
        String MA1 = fechaActual.substring(5,7);
        String DA1 = fechaActual.substring(8,10);

        int aa1= Integer.parseInt(AA1);
        int ma1 = Integer.parseInt(MA1);
        int da1 = Integer.parseInt(DA1);

    //    Toast.makeText(this, A1+"-"+ M1+"-"+D1, Toast.LENGTH_SHORT).show();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(a1, m1, d1); // Fecha  de registro

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(aa1, ma1, da1); // Fecha actual

// Establecer la diferencia de tiempo en milisegundos
        long diffInMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();

// Convertir milisegundos a días
        int days = (int) (diffInMillis / (1000 * 60 * 60 * 24));

// Imprimir la diferencia en días
        System.out.println("Diferencia de días: " + days);

        if (days > 180){
            SharedPreferences preferencesRead = getSharedPreferences("Alert_mantenimiento", Context.MODE_PRIVATE);
            String verificar_mac = preferencesRead.getString(mac, "0");
            if (verificar_mac != null && verificar_mac.equals("Recordar Despues")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Mantenimiento Sugerido");
                    alert.setMessage("Te recomendamos realizar un mantenimiento preventivo a tu cerco electrico ya que tiene mas de 6 meses trabajando continuamente comunicate con tu instalador");
                    alert.setPositiveButton("Recordarmelo mas tarde", (dialog, whichButton) -> {
                        SharedPreferences preferences = getSharedPreferences("Alert_mantenimiento", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(mac, "Recordar Despues");
                        editor.apply();
                    });

                    alert.setNegativeButton("No volver a recordar",((dialog, which) ->
                    {
                        //    ArrayList<String> myArray = new ArrayList<String>(Arrays.asList("No recordar"));
                        //   Gson gson = new Gson();
                        //    String jsonString = gson.toJson(myArray);

                        SharedPreferences preferences = getSharedPreferences("Alert_mantenimiento", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(mac, "No Recordar");
                        editor.apply();
// Recuperar y convertir el array de String

                        String savedString = preferences.getString("Alert_mantenimiento", null);
                        if (savedString != null) {
                            // ArrayList<String> restoredArray = gson.fromJson(savedString, new TypeToken<ArrayList<String>>() {}.getType());
                            // Usar el array restaurado
                        }
                    }));
                    alert.show();
            }else if (verificar_mac.equals("0")){
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Mantenimiento Sugerido");
                alert.setMessage("Te recomendamos realizar un mantenimiento preventivo a tu cerco electrico ya que tiene mas de 6 meses trabajando continuamente comunicate con tu instalador");
                alert.setPositiveButton("Recordarmelo mas tarde", (dialog, whichButton) -> {
                    SharedPreferences preferences = getSharedPreferences("Alert_mantenimiento", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(mac, "Recordar Despues");
                    editor.apply();
                });

                alert.setNegativeButton("No volver a recordar",((dialog, which) ->
                {
                    //    ArrayList<String> myArray = new ArrayList<String>(Arrays.asList("No recordar"));
                    //   Gson gson = new Gson();
                    //    String jsonString = gson.toJson(myArray);

                    SharedPreferences preferences = getSharedPreferences("Alert_mantenimiento", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(mac, "No Recordar");
                    editor.apply();
// Recuperar y convertir el array de String

                    String savedString = preferences.getString("Alert_mantenimiento", null);
                    if (savedString != null) {
                        // ArrayList<String> restoredArray = gson.fromJson(savedString, new TypeToken<ArrayList<String>>() {}.getType());
                        // Usar el array restaurado
                    }
                }));
                alert.show();
            }
        }
        //Toast.makeText(this, String.valueOf(days), Toast.LENGTH_SHORT).show();
    }
    public void showDialogPreguntas(Context context, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //builder.setTitle("Dejanos tus comentarios");

        View dialogLayout = inflater.inflate(R.layout.dialog_preguntas,
                null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnCancelar = dialogLayout.findViewById(R.id.btn_despues);
        //final ImageView img_rating = dialogLayout.findViewById(R.id.img_rating);
        final android.app.AlertDialog mdialog = builder.show();
        builder.setView(dialogLayout);
        builder.show();

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

    }

    public void showDialogResetWifi(Context context, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //builder.setTitle("Dejanos tus comentarios");

        View dialogLayout = inflater.inflate(R.layout.dialog_reset_wifi,
                null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnReset = dialogLayout.findViewById(R.id.btn_reset_wifi);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final EditText passActual= dialogLayout.findViewById(R.id.et_pass_actual);
        //final ImageView img_rating = dialogLayout.findViewById(R.id.img_rating);
        final android.app.AlertDialog mdialog = builder.show();
        builder.setView(dialogLayout);
        builder.show();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("Datos_acceso", MODE_PRIVATE);
                String password = prefs.getString("password", "0");

                String PasswordActual = passActual.getText().toString();

                if (PasswordActual.equals(password)) {
                    String msg = "";
                    msg = Mqtt_CMD.RESET_WIFI;
                    msg = msg.concat("|").concat(mac).concat("|").concat(uuid);

                    if (msg.contains("DATE")) {
                        String currentDay = getCurrenDateWithTime();
                        msg = msg.concat("|").concat(getCurrenDateWithTime());
                        Log.d(TAG, "mensaje enviado: " + msg);
                    }

                    Publisher publisher = new Publisher();
                    publisher.SendMessage(DeviceControlActivity.this, msg, mac);

                    finish();

                }else{
                    Toast.makeText(getApplicationContext(), "Debes ingresar la contraseña Actual", Toast.LENGTH_LONG).show();
                }
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        if (rol == 1) {
            menu.getItem(2).setVisible(true);
            menu.getItem(4).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.historial:
                //Toast.makeText(this, "Historial", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeviceControlActivity.this, EventsByDate.class);
                intent.putExtra("DEVICE_ID", cercaId);
                startActivity(intent);
                break;
            case R.id.rutinas:
                //  Toast.makeText(this, "Rutinas", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(DeviceControlActivity.this, lista_rutinas.class);
                startActivity(intent2);
                break;
            case R.id.invitados:
                Intent userAdmin = new Intent(this, userAdministration.class);
                userAdmin.putExtra("DEVICE_ID", cercaId);
                startActivity(userAdmin);

                // overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
                break;
            case R.id.botones:
                //Toast.makeText(this, "Botones", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(DeviceControlActivity.this, Botones.class);
                startActivity(intent4);
                break;

            case R.id.wifi:

               showDialogResetWifi(getApplicationContext(),"");
                //Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (merlin != null) {
            merlin.bind();
        }
        statusCheck();
    }

    @Override
    public void onClick(View v) {
        if ((deviceControlsResponse.getControles() != null || !deviceControlsResponse.getControles().isEmpty()) && !isLongPress) {
            switch (v.getId()) {
                case R.id.iv_user_configuration:
                    Intent userAdmin = new Intent(this, userAdministration.class);
                    userAdmin.putExtra("DEVICE_ID", cercaId);
                    startActivity(userAdmin);
                    break;
                case R.id.relative_control_description:
                    Intent intent = new Intent(this, EventsByDate.class);
                    intent.putExtra("DEVICE_ID", cercaId);
                    startActivity(intent);
                    break;
            }
        } else {
            Toast.makeText(this, "No se pueden mostrar dispositivos", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeStatusControl(int controld) {
        if ((deviceControlsResponse.getControles() != null || !deviceControlsResponse.getControles().isEmpty()) && !isLongPress) {
            mediaPlayer.start();
            if (deviceControlsResponse.getControles().get(controld).getEstadoPermiso()) {
                changeStatusControl(deviceControlsResponse.getControles().get(controld));
            } else {
                Toast.makeText(this, getString(R.string.action_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeNameControl(String aliasControl, int controlId) {
        final EditText edittext = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.rename_control_title).concat(" ").concat(aliasControl));
        alert.setMessage(getString(R.string.new_control_name_description));

        alert.setView(edittext);

        alert.setPositiveButton(getString(R.string.save), (dialog, whichButton) -> {
            String newNameControl = edittext.getText().toString();
            if (newNameControl.equals("")) {
                Toast.makeText(this, getString(R.string.invalid_control_name), Toast.LENGTH_SHORT).show();
            } else {
                setNewControlName(newNameControl, controlId);
            }

        });

        alert.setNegativeButton(getString(R.string.back), (dialog, whichButton) -> {
        });

        alert.show();

        isLongPress = false;
    }

    public void setNewControlName(String name, int controlId) {
        SetNewControlNameRequest newNamecontrol = new SetNewControlNameRequest();
        newNamecontrol.setCercaId(cercaId);
        newNamecontrol.setControlId(controlId);
        newNamecontrol.setNuevoAliasControl(name);
        newNamecontrol.setUsuarioId(uuid);

        apiManager = ApiConstants.getAPIManager();
        Call<BaseResponse> call = apiManager.setControlName(newNamecontrol);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCodigo() == ErrorCodes.SUCCESS) {
                        Toast.makeText(DeviceControlActivity.this, getString(R.string.success_rename_control), Toast.LENGTH_SHORT).show();
                        getControls();
                    } else {
                        GeneralHelpers.singleMakeAlert(DeviceControlActivity.this, getString(R.string.error_title), response.body().getMensaje());
                    }
                } else {
                    GeneralHelpers.singleMakeAlert(DeviceControlActivity.this, getString(R.string.error_title), getString(R.string.an_error_ocurred_text));
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                GeneralHelpers.singleMakeAlert(DeviceControlActivity.this, getString(R.string.error_title), getString(R.string.an_error_ocurred_text));
            }
        });

    }

    public void changeStatusControl(Controls control) {
        // dialog_comando.show();
        sendMqttMessage(control.getControlId(), control.getEstadoControl());
        SharedPreferences prefs = getSharedPreferences("Botones", MODE_PRIVATE);
        String puerta = prefs.getString("puerta", "0");
        String aux1 = prefs.getString("aux1", "0");
        String aux2 = prefs.getString("aux2", "0");

        if (control.getEstadoControl()) {
            control.setEstadoControl(false);
            updateView();
        } else {
            control.setEstadoControl(true);
            updateView();
            if (control.getControlId() == Constants.DOOR_ID) {
                Toast.makeText(DeviceControlActivity.this, getString(R.string.door_on_message_seconds), Toast.LENGTH_LONG).show();
                new doorInteraction(control).start();
            } else if (control.getControlId() == Constants.AUX1_ID && aux1.equals("1")) {
                Toast.makeText(DeviceControlActivity.this, "el aux1 se apagar en 5 seg", Toast.LENGTH_LONG).show();
                new aux1Interaction(control).start();
            } else if (control.getControlId() == Constants.AUX2_ID && aux2.equals("1")) {
                Toast.makeText(DeviceControlActivity.this, "el aux2 se apagar en 5 seg", Toast.LENGTH_LONG).show();
                new aux2Interaction(control).start();
            }
        }
    }

    public void getControls() {
        dialog_controles.show();
        GetDeviceControlsRequest getDiv = new GetDeviceControlsRequest();
        getDiv.setCercaId(cercaId);
        getDiv.setUsuarioId(uuid);

        apiManager = ApiConstants.getAPIManager();
        Call<GetDeviceControlsResponse> call = apiManager.getDeviceControls(getDiv);
        call.enqueue(new Callback<GetDeviceControlsResponse>() {
            @Override
            public void onResponse(Call<GetDeviceControlsResponse> call, Response<GetDeviceControlsResponse> response) {
                if (response.body().getCodigo() == ErrorCodes.SUCCESS) {
                    deviceControlsResponse = response.body();
                    showControlsView(deviceControlsResponse);
                    dialog_controles.dismiss();
                } else if (response.body().getCodigo() == ErrorCodes.FAILURE) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_get_controls), Toast.LENGTH_LONG).show();
                    dialog_controles.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetDeviceControlsResponse> call, Throwable t) {
                Log.e("User reg. error", t.getMessage());
                dialog_controles.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.error_get_controls), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showControlsView(GetDeviceControlsResponse controlsResponse) {

        if (controlsResponse.getUltimoEvento().getTextoMostrar() != null && controlsResponse.getUltimoEvento().getNombre() != null
                && controlsResponse.getUltimoEvento().getApellidos() != null && controlsResponse.getUltimoEvento().getFechaRegistroDato() != null) {

            tvEventDesc = findViewById(R.id.tv_control_description);
            tvEventDesc.setText(controlsResponse.getUltimoEvento().getTextoMostrar());

            String nombreEvento = controlsResponse.getUltimoEvento().getNombre().concat(" " + controlsResponse.getUltimoEvento().getApellidos());
            tvEventName = findViewById(R.id.tv_control_name);
            tvEventName.setText(nombreEvento);


            SimpleDateFormat output = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            Date d = null;
            try {
                // TODO: 16/06/2020 change time source
                d = sdfYMDTime.parse(controlsResponse.getUltimoEvento().getFechaRegistroDato());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String formattedTime = output.format(d);

            tvDate = findViewById(R.id.tv_control_date);
            tvDate.setText(formattedTime);
        }
        controlsResp.clear();
        if (controlsResponse.getControles() != null) {
            for (Controls control : controlsResponse.getControles()) {
                controlsResp.add(control);
            }
            updateView();
        } else {
            Toast.makeText(this, getString(R.string.empty_control), Toast.LENGTH_LONG).show();
        }
    }

    public void updateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    class doorInteraction extends Thread {

        Controls control;

        public doorInteraction(Controls control) {
            this.control = control;
        }

        @Override
        public void run() {
            try {
                //TODO
                Thread.sleep(5000);
                runOnUiThread(() -> {
                    mediaPlayer.start();
                    sendMqttMessage(control.getControlId(), control.getEstadoControl());
                    control.setEstadoControl(false);
                    updateView();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class aux1Interaction extends Thread {

        Controls control;

        public aux1Interaction(Controls control) {
            this.control = control;
        }

        @Override
        public void run() {
            try {
                //TODO
                Thread.sleep(5000);
                runOnUiThread(() -> {
                    mediaPlayer.start();
                    sendMqttMessage(control.getControlId(), control.getEstadoControl());
                    control.setEstadoControl(false);
                    updateView();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class aux2Interaction extends Thread {

        Controls control;

        public aux2Interaction(Controls control) {
            this.control = control;
        }

        @Override
        public void run() {
            try {
                //TODO
                Thread.sleep(5000);
                runOnUiThread(() -> {
                    mediaPlayer.start();
                    sendMqttMessage(control.getControlId(), control.getEstadoControl());
                    control.setEstadoControl(false);
                    updateView();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dataJunit(String mac, String uuid) {
        this.mac = mac;
        this.uuid = uuid;


    }

    public void sendMqttMessage(int controlId, boolean isOn) { //CMD|MacAddress|userId
        dialog_comando.show();
        String msg = "";
        switch (controlId) {
            case 1:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_FENCE_ON;
                } else {
                    msg = Mqtt_CMD.CMD_FENCE_OFF;
                }
                break;
            case 2:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_PANIC_ON;
                } else {
                    msg = Mqtt_CMD.CMD_PANIC_OFF;
                }
                break;
            case 3:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_DOOR_OPEN;
                } else {
                    msg = Mqtt_CMD.CMD_DOOR_CLOSE;
                }
                break;
            case 4:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_LIGHT_ON;
                } else {
                    msg = Mqtt_CMD.CMD_LIGHT_OFF;
                }
                break;
            case 5:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_AUX_1_ON;
                } else {
                    msg = Mqtt_CMD.CMD_AUX_1_OFF;
                }
                break;
            case 6:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_AUX_2_ON;
                } else {
                    msg = Mqtt_CMD.CMD_AUX_2_OFF;
                }
                break;
            case 7:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_PANEL_ON;
                } else {
                    msg = Mqtt_CMD.CMD_PANEL_OFF;
                }
                break;
            case 8:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_ZONE_ON;
                } else {
                    msg = Mqtt_CMD.CMD_ZONE_OFF;
                }
                break;
            case 9:
                if (!isOn) {
                    msg = Mqtt_CMD.CMD_DATE_SET;
                } else {
                    msg = Mqtt_CMD.CMD_DATE_ERASE;
                }
                break;
        }

        msg = msg.concat("|").concat(mac).concat("|").concat(uuid);

        if (msg.contains("DATE")) {
            String currentDay = getCurrenDateWithTime();
            msg = msg.concat("|").concat(getCurrenDateWithTime());
            Log.d(TAG, "mensaje enviado: " + msg);
        }

        Publisher publisher = new Publisher();
        publisher.SendMessage(this, msg, mac);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog_comando.dismiss();
            }
        }, 1500);
        //dialog_comando.dismiss();
        //   Publicar publicar = new Publicar();
        // publicar.SendMessage(this,msg,mac);
    }

    private String getCurrenDateWithTime() {
        return sdfYMDTime.format(new Date());
    }

    private String getCurrentDate() {
        return sdfYMD.format(new Date());
    }

    public void statusCheck() {
        if (status.equals(false)&&statusReset.equals(true)) {
            GeneralHelpers.singleMakeAlert(this, "WIFI Modo configuración", "Este dispositivo se reinicio a modo de fabrica, para seguir controlando su cerco desde la app requiere configurar nuevamente una red wifi, puede hacerlo agregando nuevo dispositivo desde la opcion configurar modulo.");
        }else{
            if (status.equals(false)){
                GeneralHelpers.singleMakeAlert(this, getString(R.string.conection_title), getString(R.string.conection_message).concat(" ").concat(deviceName));
            }
            //Conectado a intenert
        }

        if (rol == 1) {
            userAdm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDataControlClick(int position, boolean isLongPressAction) {
        if (isLongPressAction) {
            //Toast.makeText(this, "LongPress al control: " + this.controlsResp.get(position).getAliasControl(), Toast.LENGTH_SHORT).show();
            changeNameControl(this.controlsResp.get(position).getAliasControl(), this.controlsResp.get(position).getControlId());
        } else {
            String nextStatus = "";
            if (this.controlsResp.get(position).getEstadoControl()) {
                nextStatus = "OFF";
            } else {
                nextStatus = "ON";
            }
            Toast.makeText(this, this.controlsResp.get(position).getAliasControl().concat(" : ").concat(nextStatus), Toast.LENGTH_SHORT).show();
            changeStatusControl(position);
        }

    }

    public void onClickSetCurrentDate(View v) {
        final ControlOptions controlOptions = ControlOptions.SET_DATE;

        Log.d(TAG, "onClickGetCurrentDate: ");
        getEvents(controlOptions);
    }

    public void getEvents(ControlOptions controlOptions) {

        new AlertDialog.Builder(context)
                .setTitle(R.string.dev_control_event_dialog_title)
                .setMessage(R.string.dev_control_event_dialog_msg)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ok_alert_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendMqttMessage(controlOptions.getValue(), true);
                        GetEventsByDateRequest eventsRequest = new GetEventsByDateRequest();
                        eventsRequest.setCercaId(cercaId);
                        eventsRequest.setFechaInicial(getCurrentDate());
                        eventsRequest.setFechaFinal(getCurrentDate());
                        eventsRequest.setUsuarioId(uuid);
                        apiManager = ApiConstants.getAPIManager();
                        Call<GetEventsByDateResponse> call = apiManager.getEventsByDate(eventsRequest);
                        call.enqueue(new Callback<GetEventsByDateResponse>() {
                            @Override
                            public void onResponse(Call<GetEventsByDateResponse> call, Response<GetEventsByDateResponse> response) {
                                if (response.body().getCodigo() == ErrorCodes.SUCCESS) {
                                    //TODO get current
                                    String date = response.body()
                                            .getHistorial()
                                            .get(response.body()
                                                    .getHistorial().size() - 1)
                                            .getFechaRegistroDato();
                                    Log.d(TAG, "onResponse: " + date);
                                    tvDate.setText(date);
                                    GeneralHelpers.singleMakeAlert(context, getString(R.string.set_data_title), "");
                                } else if (response.body().getCodigo() == ErrorCodes.FAILURE) {

                                    GeneralHelpers.singleMakeAlert(context,
                                            getString(R.string.error_title),
                                            getString(R.string.error_getting_events).concat(" ")
                                                    .concat(response.body().getMensaje() != null ? response.body().getMensaje() : "No se pudo conectar"));
                                }
                            }

                            @Override
                            public void onFailure(Call<GetEventsByDateResponse> call, Throwable t) {
                                Log.e("User reg. error", t.getMessage());
                                GeneralHelpers.singleMakeAlert(context, getString(R.string.error_title), getString(R.string.error_getting_events));
                            }
                        });


                    }

                })
                .setNegativeButton(context.getString(R.string.cancel_alert_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GeneralHelpers.singleMakeAlert(context, getString(R.string.date_not_updated_title), "");
                    }
                })
                .show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (merlin != null) {
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
                .setActivity(DeviceControlActivity.this)
                .setDuration(Snacky.LENGTH_INDEFINITE)
                .build()
                .show();

    }

    private void snack_wifi_off() {
        Snackbar warningSnackBar = Snacky.builder()
                .setActivity(DeviceControlActivity.this)
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
                .setActivity(DeviceControlActivity.this)
                .setText("Conexión Estable")
                .setDuration(1000)
                .success()
                .show();

    }

    public boolean obtener_eventos5(String cerca) throws JSONException, UnsupportedEncodingException {
        myDatasetCoAcep2 = new ArrayList<eventos_model>();

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String id_user = misPreferencias.getString("usuarioId", "0");
        String token = misPreferencias.getString("accessToken", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);
        oJSONObject.put("fechaInicial", "2023-03-01");
        oJSONObject.put("fechaFinal", fecha);
        oJSONObject.put("usuarioId", id_user);
        oJSONObject.put("cercaId", cerca);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        oHttpClient.addHeader(
                "Authorization",
                token);
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/ObtenerHistorialPorFecha", (HttpEntity) oEntity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println(statusCode);
                        System.out.println(responseBody);

                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject obj = new JSONObject(content);

                            JSONArray jsonArray = obj.getJSONArray("Historial");
                            for (int i = 0; i < 4; i++) {
                                try {
                                    JSONObject jsonObjectHijo = jsonArray.getJSONObject(i);
                                    if (jsonObjectHijo != null) {
                                        //Armamos un objeto Photo con el Title y la URL de cada JSONObject
                                        eventos_model photo = new eventos_model();


                                        if (jsonObjectHijo.has("TextoMostrar"))
                                            photo.setTextoMostrar(jsonObjectHijo.getString("TextoMostrar"));

                                        if (jsonObjectHijo.has("Nombre"))
                                            photo.setNombre(jsonObjectHijo.getString("Nombre"));

                                        if (jsonObjectHijo.has("Apellidos"))
                                            photo.setAlarcon(jsonObjectHijo.getString("Apellidos"));

                                        if (jsonObjectHijo.has("FechaRegistroDato"))
                                            photo.setFechaRegistro(jsonObjectHijo.getString("FechaRegistroDato"));


                                        myDatasetCoAcep2.add(photo);


                                    }
                                    //   Toast.makeText(getApplicationContext(), String.valueOf(jsonObjectHijo), Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    Log.e("Parser JSON", e.toString());
                                } finally {
                                    //Finalmente si hemos cargado datos en el Dataset
                                    // entonces refrescamos
                                    //progressplanes.setVisibility(View.GONE);
                                    if (myDatasetCoAcep2.size() > 0) {
                                        //  texto2.setVisibility(View.VISIBLE);
                                        //   aceptadas_s.setVisibility(View.GONE);
                                        refreshDataset_aceptadas2();
                                    } else {

                                        //  texto2.setVisibility(View.VISIBLE);
                                        mRecyclerViewAceptadas2.setVisibility(View.GONE);
                                        //     aceptadas_s.setVisibility(View.GONE);
                                    }
                                }
                            }
                            if (jsonArray.length() > 0) {

                            } else {
//                                texto.setVisibility(View.VISIBLE);
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
