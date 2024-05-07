package com.yonusa.cercasyonusaplus.ui.add_devices.set_up_devices;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Update;

import com.airbnb.lottie.LottieAnimationView;
import com.gusakov.library.PulseCountDown;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.Lista_beneficiarios;
import com.yonusa.cercasyonusaplus.ui.homeScreen.view.HomeActivity;
import com.yonusa.cercasyonusaplus.utilities.catalogs.SP_Dictionary;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import dmax.dialog.SpotsDialog;

public class FinishAddingDeviceActivity extends AppCompatActivity {
    private static final String TAG = "variable";
    private final String FROM_FINISH_ADD = "com.yonusa.instala.ui.login.view.FROM_FINISH_ADD";
    Context context;
    TextView contador,texto,ubicacion_x,ubicacion_y;
    Button btnFinish;
    LottieAnimationView loader,success;

    double longitudeBest, latitudeBest;
    double longitudeGPS, latitudeGPS;
    double longitudeNetwork, latitudeNetwork;
    TextView longitudeValueBest, latitudeValueBest;
    TextView longitudeValueGPS, latitudeValueGPS;
    TextView longitudeValueNetwork, latitudeValueNetwork;

   // private MiContador timer;
    private long lastCountDown = 30000; //Milliseconds for view ad
    private Boolean isCountDown = false;

    private Button buttonStart;
    private PulseCountDown pulseCountDown;

    AlertDialog alerta;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_step4);
        getSupportActionBar().hide();

        alerta = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Terminando configuracion")
                .setCancelable(false).build();

        this.context = this;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        loader = findViewById(R.id.loader_step4);
        success = findViewById(R.id.succes_lottie);
        btnFinish = findViewById(R.id.btn_finish);

        contador= (TextView) findViewById(R.id.contador);
        texto = (TextView) findViewById(R.id.texto_conteo);
        ubicacion_x = (TextView) findViewById(R.id.tvLatitud);
        ubicacion_y = (TextView) findViewById(R.id.tvLongitud);

        longitudeValueBest = (TextView) findViewById(R.id.longitudeValueBest);
        latitudeValueBest = (TextView) findViewById(R.id.latitudeValueBest);

        btnFinish.setOnClickListener(v -> {
            try {
                guardar_coordenadas(ubicacion_x.getText().toString(),ubicacion_y.getText().toString());
                alerta.show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else if (ubicacion_x.getText().equals("")){
            locationStart();

        }

        // start countdown and add OnCountdownCompleted

        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {

                // Used for formatting digit to be in 2 digits only

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                contador.setText( f.format(sec));
            }

            // When the task is over it will print 00:00:00 there

            public void onFinish() {

                contador.setText("00");

            }

        }.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
             //   mProgressDialog.dismiss();
           btnFinish.setVisibility(View.VISIBLE);
           texto.setText("Si tu Wifi no se conecto, favor de verificacar la contraseña del wifi que ingresaste, será necesario repetir el proceso de instalación");
                //finish();
            }
        }, 19000);


    }

    private void toHomeScreen() {
        btnFinish.setText("Espera...");
        runOnUiThread(()-> {
            try {
                Thread.sleep(2000);
                Intent i = new Intent(FinishAddingDeviceActivity.this, HomeActivity.class);
                i.putExtra(FROM_FINISH_ADD,true);
                startActivity(i);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        latitudeValueBest.setText("Localización agregada");
        longitudeValueBest.setText("");
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    longitudeValueBest.setText("Mi direccion es: \n"
                            + DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Localizacion implements LocationListener {
        FinishAddingDeviceActivity mainActivity;
        public FinishAddingDeviceActivity getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(FinishAddingDeviceActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            ubicacion_x.setText(String.valueOf(loc.getLongitude()));
            ubicacion_y.setText(String.valueOf(loc.getLatitude()));

            latitudeValueBest.setText(Text);
            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            latitudeValueBest.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            latitudeValueBest.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }


    public boolean guardar_coordenadas(String lat,String lon) throws JSONException, UnsupportedEncodingException {
        SharedPreferences prefs = getSharedPreferences(SP_Dictionary.USER_INFO, MODE_PRIVATE);
        String uniqueId = prefs.getString("UniqueId_2", "No UniqueId defined");

        //String uniqueId = "TESTING-UNIQUEID080890";
        //  uniqueId = md5(uniqueId);
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String id_user = misPreferencias.getString("usuarioId", "0");

        SharedPreferences misPreferencias2 = getSharedPreferences("Datos_Tarjeta", Context.MODE_PRIVATE);
        String mac = misPreferencias2.getString("MAC", "0");

        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("MAC", mac);
        oJSONObject.put("latitud", lat);
        oJSONObject.put("longitud", lon);
        oJSONObject.put("usuarioAdministradorId", id_user);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

        //  Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/ActualizarUbicacionCerca", (HttpEntity) oEntity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println(statusCode);
                        System.out.println(responseBody);
                        //     mMap = googleMap;d
                        alerta.dismiss();
                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject obj = new JSONObject(content);
                            String valor = String.valueOf(obj.get("codigo"));

                            if (valor.equals("0")||valor.equals("6")) {
                                Toast.makeText(getApplicationContext(), "Ubicacion guardada", Toast.LENGTH_LONG).show();
                                toHomeScreen();

                            }else if(valor.equals("-1")) {
                                Toast.makeText(getApplicationContext(), "Ocurrio un error al guardar la ubicacion.", Toast.LENGTH_LONG).show();
                            }else if(valor.equals("-4")) {
                                androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                                alert.setTitle("Error de configuracion");
                                alert.setMessage("Si tu equipo no aparece en la lista de cercos, posiblemente la contraseña ingresada fue incorrecta, necesitas volver a inciar el proceso de configuracion");
                                alert.setPositiveButton("Siguiente", (dialog, whichButton) -> {
                                  toHomeScreen();
                                });
                                alert.show();
                               // Toast.makeText(getApplicationContext(), "El dispositivo no se configuro, posiblemente la contraseña es incorrecta.", Toast.LENGTH_LONG).show();
                            }else if(valor.equals("-3")) {
                                androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                                alert.setTitle("Dispositivo no Registrado");
                                alert.setMessage("Al parecer este equipo esta previamente configurado con otro correo, comunicate con soporte para poder reiniciarlo a modo fabrica");
                                alert.setPositiveButton("Entendido", (dialog, whichButton) -> {
                                 toHomeScreen();
                                });
                                alert.show();
                              //  Toast.makeText(getApplicationContext(), "Dispositivo Registrado con otro usuario.", Toast.LENGTH_LONG).show();
                            }else
                            {
                                Toast.makeText(getApplicationContext(), "Error en el servidor intentalo mas tarde", Toast.LENGTH_LONG).show();
                                toHomeScreen();
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