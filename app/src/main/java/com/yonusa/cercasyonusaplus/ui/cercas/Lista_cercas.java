package com.yonusa.cercasyonusaplus.ui.cercas;

import static com.yonusa.cercasyonusaplus.ui.login.view.LogInActivity.md5;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.api.ApiConstants;
import com.yonusa.cercasyonusaplus.api.ApiManager;
import com.yonusa.cercasyonusaplus.api.BaseResponse;
import com.yonusa.cercasyonusaplus.ui.add_devices.list_of_devices.AddANewDeviceActivity;
import com.yonusa.cercasyonusaplus.ui.cercas.adapter.cercas_adapter_completo;
import com.yonusa.cercasyonusaplus.ui.cercas.modelo.cercas_model_completo;
import com.yonusa.cercasyonusaplus.ui.device_control.view.DeviceControlActivity;
import com.yonusa.cercasyonusaplus.ui.homeScreen.adapters.Adapter_HomeScreen_Items;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.request.ChangeDeviceNameRequest;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.request.CloseSessionRequest;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.request.HomeScreenRequest;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.response.Cerca;
import com.yonusa.cercasyonusaplus.ui.homeScreen.models.response.HomeScreenResponse;

import com.yonusa.cercasyonusaplus.ui.login.view.LogInActivity;
import com.yonusa.cercasyonusaplus.ui.login.view.Loguin_new;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Lista_cercas extends AppCompatActivity  {



    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView texto, correo, nombre, apellido;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cercas);




       // iv_AddDevices = findViewById(R.id.iv_add_devices);
       // iv_AddDevices.setVisibility(View.VISIBLE);
       // iv_AddDevices.setOnClickListener(v -> toAddDevices());

       // iv_CloseSession = findViewById(R.id.iv_close_session);



        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String emailt = misPreferencias.getString("email", "0");
        String nombre_t = misPreferencias.getString("nombre", "0");
        String apellido_t = misPreferencias.getString("apellido", "0");
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        correo = (TextView) header.findViewById(R.id.user_correo);
        nombre = (TextView) header.findViewById(R.id.nombre);
        apellido = (TextView) header.findViewById(R.id.apellido);
        correo.setText(emailt);
        nombre.setText(nombre_t);
        apellido.setText(apellido_t);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_beneficiarios: {
                        Toast.makeText(Lista_cercas.this, "Proximamente", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case R.id.nav_ambulancia: {
                        Toast.makeText(Lista_cercas.this, "Proximamente", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case R.id.nav_asistencia_medica: {
                        Toast.makeText(Lista_cercas.this, "Proximamente", Toast.LENGTH_LONG).show();
                        break;
                    }

                    case R.id.nav_cerrar: {
                        // Toast.makeText(Lista_cercas.this,"Cerrando Sesión...",Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
        navigationView.setItemIconTintList(null);
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




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}