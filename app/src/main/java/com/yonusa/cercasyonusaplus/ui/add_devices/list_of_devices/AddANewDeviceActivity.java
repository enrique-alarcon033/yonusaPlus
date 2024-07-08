package com.yonusa.cercasyonusaplus.ui.add_devices.list_of_devices;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.api.ApiConstants;
import com.yonusa.cercasyonusaplus.api.ApiManager;
import com.yonusa.cercasyonusaplus.ui.add_devices.ethernet.agregar_ethernet;
import com.yonusa.cercasyonusaplus.ui.add_devices.list_of_devices.request.GetConfigParamsRequest;
import com.yonusa.cercasyonusaplus.ui.add_devices.list_of_devices.response.GetConfigParamsResponse;
import com.yonusa.cercasyonusaplus.ui.add_devices.set_up_devices.GetYonusaWifiActivity;
import com.yonusa.cercasyonusaplus.utilities.catalogs.Constants;
import com.yonusa.cercasyonusaplus.utilities.catalogs.ErrorCodes;
import com.yonusa.cercasyonusaplus.utilities.catalogs.SP_Dictionary;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddANewDeviceActivity extends AppCompatActivity {

    ApiManager apiManager;
    Button btnWifi,btn_ethernet;
    Button btnMonitor;
    AlertDialog dialogo;
    Context context;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_devices);
        getSupportActionBar().hide();

        dialogo = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Obteniendo parametros de configuracion")
                .setCancelable(false).build();

        btnWifi = findViewById(R.id.btn_to_wifi);
        btnWifi.setOnClickListener(v -> toWifi());

        btn_ethernet = findViewById(R.id.btn_to_wifi2);
        btn_ethernet.setOnClickListener(v -> toEthernet());

        btnMonitor = findViewById(R.id.btn_to_monitor);
        btnMonitor.setOnClickListener(v -> toMonitor());

    }

    private void toWifi() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            // Location is not enabled, prompt the user to enable it
            // ...
            Toast.makeText(AddANewDeviceActivity.this, "Activa la ubicacion de este dispositivo para continuar", Toast.LENGTH_SHORT).show();
        } else {
            // Location is enabled, proceed with your location-based tasks
            // ...
            // Toast.makeText(AddANewDeviceActivity.this, "Activa", Toast.LENGTH_SHORT).show();
            dialogo.show();
            //loader.setVisibility(View.VISIBLE);
            isNetworkAvailable(AddANewDeviceActivity.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //  loader.setVisibility(View.GONE);
                }
            }, 1000);
            getDeviceConfigParameters(Constants.WI_FI_MODEL_03);
        }
    }

    private void toEthernet(){
        Intent intent = new Intent(getApplicationContext(), agregar_ethernet.class);
        startActivity(intent);
    }

    private void toMonitor() {

        getDeviceConfigParameters(Constants.MONITOR_MODEL);

    }

    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_CELLULAR");
                    //  Toast.makeText(AddANewDeviceActivity.this, "Conectado con Datos", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_WIFI");
                    // Toast.makeText(AddANewDeviceActivity.this, "Conectado con Wifi", Toast.LENGTH_SHORT).show();
                    return true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_ETHERNET");
                    return true;
                }
            }else {
                Toast.makeText(AddANewDeviceActivity.this, "Esta función requiere internet", Toast.LENGTH_SHORT).show();
            }
        }

        return false;

    }
    public void getDeviceConfigParameters(String model){
        //FIXME
        String mac = "00:00:00:00:00:00";
        dialogo.show();
        GetConfigParamsRequest getConfigParamsRequest = new GetConfigParamsRequest();

        getConfigParamsRequest.setMAC(mac);
        getConfigParamsRequest.setModeloId(model);

        apiManager = ApiConstants.getAPIManager();

        Call<GetConfigParamsResponse> call = apiManager.getDeviceConfigParameters(getConfigParamsRequest);

        call.enqueue(new Callback<GetConfigParamsResponse>() {
            @Override
            public void onResponse(Call<GetConfigParamsResponse> call, Response<GetConfigParamsResponse> response) {

                int typeCode = response.body().getCodigo();
                switch (typeCode) {
                    case (ErrorCodes.SUCCESS):

                        String UUID = response.body().getUUID();
                        Intent intentAddDeviceStep1 = new Intent(AddANewDeviceActivity.this, GetYonusaWifiActivity.class);

                        intentAddDeviceStep1.putExtra("UUID", UUID);

                        String preferenceContainer = SP_Dictionary.USER_INFO;
                        SharedPreferences.Editor editor = getSharedPreferences(preferenceContainer, MODE_PRIVATE).edit();
                        editor.putString("UUID", UUID);

                        editor.apply();
                        intentAddDeviceStep1.putExtra("MODEL", model);
                        startActivity(intentAddDeviceStep1);
                        dialogo.dismiss();
                        finish();
                        break;

                    case (ErrorCodes.FAILURE):
                        //TODO: Agregar alertas de error.
                        Toast.makeText(AddANewDeviceActivity.this, "Ocurrio un error.", Toast.LENGTH_SHORT).show();
                        dialogo.dismiss();
                        break;

                    default:
                        Log.i("Peticion login", "Algo paso");
                }
            }
            @Override
            public void onFailure(Call<GetConfigParamsResponse> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                //TODO: Agregar alerta de error de conexión.
            }
        });
    }

}
