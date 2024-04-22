package com.yonusa.cercasyonusaplus.ui.rutinas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class  Rutina extends AppCompatActivity  implements CantidadListener{

    private static final String TAG = "Estado del dia";
    EditText hora,nombre;
    Button btn_hora,guardar;
    ImageView img_icon;
    TextView texto_control,comando_ejecutar;
    private int horar,minutos;
    RecyclerView recyclerView;
    CantidadAdapter adapter;

    Switch swl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        hora = (EditText) findViewById(R.id.hora);
        btn_hora =(Button) findViewById(R.id.btn_hora);
        img_icon = (ImageView) findViewById(R.id.img_icon);
        texto_control=(TextView)findViewById(R.id.texto_control);
        guardar =(Button) findViewById(R.id.guardar);
        nombre= (EditText)findViewById(R.id.nombre_rutina);
        comando_ejecutar = (TextView) findViewById(R.id.comando_ejecutar);

        btn_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c= Calendar.getInstance();
                horar=c.get(Calendar.HOUR_OF_DAY);
                minutos= c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Rutina.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hora.setText(hourOfDay+":"+minute);
                    }
                },horar,minutos,false);
                timePickerDialog.show();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.dias);
        setRecycler();
        String valor = getIntent().getStringExtra("comando");
        if (valor.equals("0")){
            comando_ejecutar.setText("@FENCE_OFF");
        }else if (valor.equals("1")){
            comando_ejecutar.setText("@PANIC_OFF");
        }else if (valor.equals("2")){
            comando_ejecutar.setText("@DOOR_OFF");
        }else if (valor.equals("3")){
            comando_ejecutar.setText("@LIGHT_OFF");
        }else if (valor.equals("4")){
            comando_ejecutar.setText("@AUX1_OFF");
        }else if (valor.equals("5")){
            comando_ejecutar.setText("@AUX2_OFF");
        }


        swl = (Switch) findViewById(R.id.switch1);
        swl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valor = getIntent().getStringExtra("comando");
                String comando = "";
                if (valor.equals("0")){
                    ValidarSwitch(v);
                }else if (valor.equals("1")){
                    ValidarSwitch2(v);
                }else if (valor.equals("2")){
                    ValidarSwitch3(v);
                    swl.setText("Cerrar Puerta");
                }else if (valor.equals("3")){
                    ValidarSwitch4(v);
                }else if (valor.equals("4")){
                    ValidarSwitch5(v);
                }else if (valor.equals("5")){
                    ValidarSwitch6(v);
                }
            }
        });

      // boolean estado_switch = getValuePreference(getApplicationContext());

//        swl.setChecked(estado_switch);
        SharedPreferences preferences = getSharedPreferences("lista", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences.edit();
        editor2.putString("comando", "0");
        editor2.commit();


        swl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                //guarda valor.
              //  saveValuePreference(getApplicationContext(), isChecked);
                if(isChecked){
                    SharedPreferences preferences = getSharedPreferences("lista", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = preferences.edit();
                    editor2.putString("comando", "1");
                    editor2.commit();
                    //Toast.makeText(getApplicationContext(), "Seleecionado", Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences preferences = getSharedPreferences("lista", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = preferences.edit();
                    editor2.putString("comando", "0");
                    editor2.commit();
                   // Toast.makeText(getApplicationContext(), "No seleccion", Toast.LENGTH_LONG).show();
                }
            }
        });

        //String valor = getIntent().getStringExtra("comando");
        String comando = "";
        if (valor.equals("0")){
             img_icon.setBackgroundResource(R.drawable.fence2_off);
             texto_control.setText("Cerco");
        }else if (valor.equals("1")){
            img_icon.setBackgroundResource(R.drawable.panico_off);
            texto_control.setText("Pá nico");
            comando.equals("@fenceON");

        }else if (valor.equals("2")){
            img_icon.setBackgroundResource(R.drawable.puerta_close);
            texto_control.setText("Puerta");
            swl.setText("Cerrar Puerta");
        }else if (valor.equals("3")){
            img_icon.setBackgroundResource(R.drawable.lights_off);
            texto_control.setText("Luces");
        }else if (valor.equals("4")){
            img_icon.setBackgroundResource(R.drawable.aux_disable);
            texto_control.setText("Aux 1");
        }else if (valor.equals("5")){
            img_icon.setBackgroundResource(R.drawable.aux_disable);
            texto_control.setText("Aux 2");
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Asigna un nombre a tu rutina", Toast.LENGTH_LONG).show();
                }else{
                    if(hora.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Asigna una hora de ejecución a tu rutina", Toast.LENGTH_LONG).show();
                    }else{
                    try {
                        crear_rutina(hora.getText().toString(),nombre.getText().toString(),comando_ejecutar.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    }
                }
            }
        });

      //  Toast.makeText(getApplicationContext(), valor, Toast.LENGTH_LONG).show();
    }

    private ArrayList<String> getCantidadDatos(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Monday");
        arrayList.add("Tuesday");
        arrayList.add("Wednesday");
        arrayList.add("Thursday");
        arrayList.add("Friday");
        arrayList.add("Saturday");
        arrayList.add("Sunday");
        return  arrayList;
    }
    private void setRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter= new CantidadAdapter(this,getCantidadDatos(),this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCantidadCambio(ArrayList<String> arrayList) {
       // Toast.makeText(this,arrayList.toString(),Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getSharedPreferences("lista", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> set = new HashSet<>();
        set.addAll(arrayList);
        editor.putStringSet("datos", set);
        editor.apply();


            Gson gson = new Gson();
            String list = gson.toJson(arrayList);
            editor.putString("savedList",list).apply();

        editor.apply();
    }

    public  void ValidarSwitch(View view){
        if (swl.isChecked()) {
            swl.setText("Encender");
            img_icon.setBackgroundResource(R.drawable.fence2_on);
            comando_ejecutar.setText("@FENCE_ON");
        }else{
            swl.setText("Apagar");
            img_icon.setBackgroundResource(R.drawable.fence2_off);
            comando_ejecutar.setText("@FENCE_OFF");
        }
    }

    public  void ValidarSwitch2(View view){
        if (swl.isChecked()) {
            swl.setText("Encender");
            img_icon.setBackgroundResource(R.drawable.panico_on);
            comando_ejecutar.setText("@PANIC_ON");
        }else{
            swl.setText("Apagar");
            img_icon.setBackgroundResource(R.drawable.panico_off);
            comando_ejecutar.setText("@PANIC_OFF");
        }
    }

    public  void ValidarSwitch3(View view){
        if (swl.isChecked()) {
            swl.setText("Encender");
            img_icon.setBackgroundResource(R.drawable.puerta_open);
            comando_ejecutar.setText("@DOOR_ON");
        }else{
            swl.setText("Apagar");
            img_icon.setBackgroundResource(R.drawable.puerta_close);
            comando_ejecutar.setText("@DOOR_OFF");
        }
    }

    public  void ValidarSwitch4(View view){
        if (swl.isChecked()) {
            swl.setText("Encender");
            img_icon.setBackgroundResource(R.drawable.lights_off);
            comando_ejecutar.setText("@LIGHT_ON");
        }else{
            swl.setText("Apagar");
            img_icon.setBackgroundResource(R.drawable.lights_on);
            comando_ejecutar.setText("@LIGHT_OFF");
        }
    }

    public  void ValidarSwitch5(View view){
        if (swl.isChecked()) {
            swl.setText("Encender");
            img_icon.setBackgroundResource(R.drawable.aux_on);
            comando_ejecutar.setText("@AUX1_ON");
        }else{
            swl.setText("Apagar");
            img_icon.setBackgroundResource(R.drawable.aux_disable);
            comando_ejecutar.setText("@AUX1_OFF");
        }
    }

    public  void ValidarSwitch6(View view){
        if (swl.isChecked()) {
            swl.setText("Encender");
            img_icon.setBackgroundResource(R.drawable.aux_on);
            comando_ejecutar.setText("@AUX2_ON");
        }else{
            swl.setText("Apagar");
            img_icon.setBackgroundResource(R.drawable.aux_disable);
            comando_ejecutar.setText("@AUX2_OFF");
        }
    }


    //me cago si funciona pasando dede el mismo arreglo de dias
    public boolean crear_rutina(String hora,String nombre,String comando) throws JSONException, UnsupportedEncodingException {

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);

        SharedPreferences preferences = getSharedPreferences("lista", Context.MODE_PRIVATE);
        Set<String> set = preferences.getStringSet("datos", null);
     //   String list = preferences.getString("savedList",null);
     Gson gson = new Gson();
     //   String list = gson.toJson(preferences.getString("savedList",null));
       // String list2 = gson.toJson(preferences.getString("datos",null));
        String mac = preferences.getString("mac", "0");
        String id_user = misPreferencias.getString("usuarioId", "0");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(comando);

        //meter todos los dias en un json array para verificar si funciona
        JSONArray diaa = new JSONArray();

        String jsonStr = preferences.getString("savedList", null);
        String[] lista = gson.fromJson(jsonStr, String[].class);

        Iterator<String> it= set.iterator();
        while(it.hasNext()) {
            diaa.put(it.next());
         //   System.out.println(it.next());
        }

    /*    diaa.put("Tuesday");
        diaa.put("Thursday");
        diaa.put("Friday");*/

        JSONArray dias = new JSONArray();
        //  dias.put(list);
   /*   //(  jsonArray.put((list));
       // jsonArray.get(0);

        //String str = list;
       // str.replace("\\", "''");
        Set<String> set = preferences.getStringSet("datos", null);
      //  list.clear();

        //list.addAll(set);

        //Convert HashSet to List.
       ArrayList<String> list = new ArrayList<String>(set);
        ArrayList<String> comando = new ArrayList<String>();
        comando.add("@FENCE_ON");*/
       // String str2 = list.replaceAll("\\s", "");

        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioId", id_user);
        oJSONObject.put("cercaMAC", mac);
        oJSONObject.put("comando", jsonArray);
        oJSONObject.put("zonaHoraria", "Central Standard Time");
        oJSONObject.put("nombreRutina", nombre);
        oJSONObject.put("dia",  diaa);
        oJSONObject.put("hora", hora);


      //  oJSONObject.put("usuarioInvitadoId", guestUserId);
       // oJSONObject.put("cercaId", deviceId);
        //   oJSONObject.put("coordenates",_contrasena);
        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  oEntity.setContentEncoding(new BasicHeader(HttpHeaders.AUTHORIZATION,  token));

       Toast.makeText(getApplicationContext(), "Creando Rutina", Toast.LENGTH_LONG).show();
        //    Toast.makeText(getApplicationContext(), oEntity.toString(), Toast.LENGTH_LONG).show();
        //      oEntity.setContentType("Authorization", "Bearer "+token);

        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/CalendarioNotificacion",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                Toast.makeText(getApplicationContext(), "Rutina Creada", Toast.LENGTH_LONG).show();
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

    public void Validar_comando (){
        SharedPreferences preferences = getSharedPreferences("lista", Context.MODE_PRIVATE);
        String estado_swt = preferences.getString("comando", "0");
      //  String comando = preferences.getString("comando", "0");
        String valor = getIntent().getStringExtra("comando");

        if (estado_swt.equals("1")){
            if (valor.equals("0")){

            }
        }else{

        }


    }
}