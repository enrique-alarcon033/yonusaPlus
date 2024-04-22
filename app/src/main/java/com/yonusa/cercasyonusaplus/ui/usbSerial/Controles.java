package com.yonusa.cercasyonusaplus.ui.usbSerial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yonusa.cercasyonusaplus.R;

import java.lang.ref.WeakReference;
import java.util.Set;

import dmax.dialog.SpotsDialog;

public class Controles extends AppCompatActivity {

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();

                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private TextView display;
    private EditText editText;
    private MyHandler mHandler;
    int Icerca = 0;
    int Ipanico = 0;
    int Ipuerta = 0;
    int Iluces = 0;
    int Iaux = 0;
    int Iaux2 = 0;
    ImageButton cerca, panico, puerta, luces, aux, aux2;
    TextView rutinas;
    ImageView imgcerca, imgpanico, imgpuerta, imgluces, imgaux, imgaux2, historial;

    static AlertDialog alerta;
    private MediaPlayer mediaPlayer,panic;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);

        mHandler = new MyHandler(this);

        alerta = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Buscando Estados / Localidades")
                .setCancelable(false).build();


        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        cerca = (ImageButton) findViewById(R.id.ib_control1);
        panico = (ImageButton) findViewById(R.id.ib_control2);
        puerta = (ImageButton) findViewById(R.id.ib_control3);
        luces = (ImageButton) findViewById(R.id.ib_control4);
        aux = (ImageButton) findViewById(R.id.ib_control5);
        aux2 = (ImageButton) findViewById(R.id.ib_control6);

        imgcerca = (ImageView) findViewById(R.id.iv_control1);
        imgpanico = (ImageView) findViewById(R.id.iv_control2);
        imgpuerta = (ImageView) findViewById(R.id.iv_control3);
        imgluces = (ImageView) findViewById(R.id.iv_control4);
        imgaux = (ImageView) findViewById(R.id.iv_control5);
        imgaux2 = (ImageView) findViewById(R.id.iv_control6);

        historial = (ImageView) findViewById(R.id.historial);
        rutinas = (TextView) findViewById(R.id.tv_rutinas);

        mediaPlayer = MediaPlayer.create(this, R.raw.opendoor);
        panic = MediaPlayer.create(this,R.raw.alarma);


        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Historial.class);
                startActivity(intent);
            }
        });

        rutinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Rutinas.class);
                startActivity(intent);
            }
        });


        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    String data = editText.getText().toString();

                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                    }
                }
            }
        });


        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Icerca == 0) {
                    String data = "A";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());

                        Icerca = Icerca + 1;
                        imgcerca.setImageResource(R.drawable.btn_fence_on);
                        notificacion("Cerca Encendida","Samuel Nurko, Cerca Encendida 16/04/2024 ");
                        mediaPlayer.start();
                    }
                } else {
                    String data = "a";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());

                        Icerca = Icerca - 1;
                        imgcerca.setImageResource(R.drawable.btn_fence_off);
                        notificacion("Cerca Apagada","Samuel Nurko, Cerca Apagada 16/04/2024 ");
                        mediaPlayer.start();
                    }
                }

            }
        });

        panico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Ipanico == 0) {
                    String data = "B";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Ipanico = Ipanico + 1;
                        imgpanico.setImageResource(R.drawable.btn_panico_on);
                        notificacion("Panico Encendido","Samuel Nurko, Panico Encendido 16/04/2024 ");
                        panic.start();
                    }
                } else {
                    String data = "b";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Ipanico = Ipanico - 1;
                        imgpanico.setImageResource(R.drawable.btn_panico_off);
                        notificacion("Panico Apagado","Samuel Nurko, Panico Apagado 16/04/2024 ");
                      //  panic.start();
                    }
                }

            }
        });

        puerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Ipuerta == 0) {
                    String data = "C";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Ipuerta = Ipuerta + 1;
                        imgpuerta.setImageResource(R.drawable.btn_puerta_off);
                        notificacion("Puerta Abierta","Samuel Nurko, Puerta Abierta 16/04/2024 ");
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "La puerta cerrara en 3 segundos", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //   mProgressDialog.dismiss();
                               // btnFinish.setVisibility(View.VISIBLE);
                                String data = "c";
                                if (usbService != null) { // if UsbService was correctly binded, Send data
                                    usbService.write(data.getBytes());
                                    Ipuerta = Ipuerta - 1;
                                    imgpuerta.setImageResource(R.drawable.btn_puerta_on);
                                    notificacion("Puerta Cerrada","Samuel Nurko, Puerta Cerrada 16/04/2024 ");
                                    mediaPlayer.start();
                                  }
                                //texto.setText("Si tu Wifi no se conecto, favor de verificacar la contraseña del wifi que ingresaste, será necesario repetir el proceso de instalación");
                                //finish();
                            }
                        }, 3000);
                    }
                } else {
                    String data = "c";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Ipuerta = Ipuerta - 1;
                        imgpuerta.setImageResource(R.drawable.btn_puerta_on);
                        notificacion("Puerta Cerrada","Samuel Nurko, Puerta Cerrada 16/04/2024 ");
                        mediaPlayer.start();
                    }
                }

            }
        });

        luces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Iluces == 0) {
                    String data = "D";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Iluces = Iluces + 1;
                        imgluces.setImageResource(R.drawable.btn_luces_on);
                        notificacion("Luces Encendidas","Samuel Nurko, Luces Encendidas 16/04/2024 ");
                        mediaPlayer.start();
                    }
                } else {
                    String data = "d";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Iluces = Iluces - 1;
                        imgluces.setImageResource(R.drawable.btn_luces_off);
                        notificacion("Luces Apagadas","Samuel Nurko, Luces Apagadas 16/04/2024 ");
                        mediaPlayer.start();
                    }
                }

            }
        });

        aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Iaux == 0) {
                    String data = "E";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Iaux = Iaux + 1;
                        imgaux.setImageResource(R.drawable.btn_aux_on);
                        notificacion("Aux 1 Encendido","Samuel Nurko, Aux 1 Encendido 16/04/2024 ");
                        mediaPlayer.start();
                    }
                } else {
                    String data = "e";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Iaux = Iaux - 1;
                        imgaux.setImageResource(R.drawable.btn_aux_off);
                        notificacion("Aux 1 Apagado","Samuel Nurko, Aux 1 Apagado 16/04/2024 ");
                        mediaPlayer.start();
                    }
                }

            }
        });

        aux2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Iaux2 == 0) {
                    String data = "F";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Iaux2 = Iaux2 + 1;
                        imgaux2.setImageResource(R.drawable.btn_aux_on);
                        notificacion("Aux 2 Encendido","Samuel Nurko, Aux 2 Encendido 16/04/2024 ");
                        mediaPlayer.start();
                    }
                } else {
                    String data = "f";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                        Iaux2 = Iaux2 - 1;
                        imgaux2.setImageResource(R.drawable.btn_aux_off);
                        notificacion("Aux 2 Apagado","Samuel Nurko, Aux 2 Apagado 16/04/2024 ");
                        mediaPlayer.start();
                    }
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }

            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel for my notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);

            // Configura otros atributos del canal aquí

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void notificacion (String titulo, String contenido) {
        // Crear el canal de notificación (solo necesario para Android 8.0 y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_alerta)
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }
    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<Controles> mActivity;

        public MyHandler(Controles activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;

                    Toast.makeText(mActivity.get(), "CTS_CHANGE"+ data.toString(),Toast.LENGTH_LONG).show();
                    mActivity.get().display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }
}