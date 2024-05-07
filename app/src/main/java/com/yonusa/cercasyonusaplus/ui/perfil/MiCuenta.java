package com.yonusa.cercasyonusaplus.ui.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.createAccount.create.CrearCuenta;
import com.yonusa.cercasyonusaplus.ui.homeScreen.view.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import dmax.dialog.SpotsDialog;

public class MiCuenta extends AppCompatActivity {

    TextView nombre, correo,telefono,pais,estado;
    ImageView alerta;

    Button modificar,actualizar,eliminar;

    AlertDialog carga,carga2;

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_cuenta);
        
        nombre = (TextView) findViewById(R.id.tvNombreP);
        correo = (TextView) findViewById(R.id.tvCorreoP);
        telefono = (TextView) findViewById(R.id.tvTelefonoP); 
        pais  = (TextView) findViewById(R.id.tvPaisP);
        estado = (TextView) findViewById(R.id.tvEstadoP);
        alerta = (ImageView) findViewById(R.id.img_alerta);

        modificar = (Button) findViewById(R.id.btn_modificar);
        actualizar = (Button) findViewById(R.id.btn_actualizar_pass);
        eliminar = (Button) findViewById(R.id.btn_BajaCuenta);

        carga = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Actualizando Contraseña")
                .setCancelable(false).build();

        carga2 = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espera un momento")
                .setCancelable(false).build();

        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String emailt =  misPreferencias.getString("email","0");
        String nombre_t =  misPreferencias.getString("nombre","0");
        String apellido_t =  misPreferencias.getString("apellido","0");
        String pais_t = misPreferencias.getString("pais","");
        String estado_t = misPreferencias.getString("estado","");
        String telefono_t= misPreferencias.getString("telefono","0");

        nombre.setText(nombre_t +" " +apellido_t);
        correo.setText(emailt);
        pais.setText(pais_t);
        estado.setText(estado_t);
        telefono.setText(telefono_t);
       // pais.setTextColor(R.color.narnaja);
       // estado.setTextColor(R.color.narnaja);
        if (pais_t.equals("")){
            pais.setText("Por definir");
            alerta.setVisibility(View.VISIBLE);
        }

        if (estado_t.equals("")){
            estado.setText("Por Definir");
            alerta.setVisibility(View.VISIBLE);
        }
        alerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MiCuenta.this,"Verifica que todos los datos de tu cuenta esten completos",Toast.LENGTH_LONG).show();
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActualizarDatos .class);
                startActivity(intent);
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getApplicationContext(),"Hola");
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBaja(getApplicationContext(),"Hola");
            }
        });


    }

    public void showDialog(Context context, String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //builder.setTitle("Dejanos tus comentarios");

        View dialogLayout = inflater.inflate(R.layout.dialog_password,
                null);
        final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingsBar);
        final TextView TvRating = dialogLayout.findViewById(R.id.rateTV);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final EditText passActual= dialogLayout.findViewById(R.id.et_pass_actual);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final EditText newPass= dialogLayout.findViewById(R.id.et_password_new);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final EditText repPass = dialogLayout.findViewById(R.id.et_rep_password);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnGuardar = dialogLayout.findViewById(R.id.btn_cambiar_pass);
        final Button btnCancelar = dialogLayout.findViewById(R.id.btnDespues);
        final ImageView img_rating = dialogLayout.findViewById(R.id.img_rating);
        final android.app.AlertDialog mdialog = builder.show();
        builder.setView(dialogLayout);
        builder.show();
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mdialog.cancel();
                Toast.makeText(MiCuenta.this, "Gracias por tu calificación, seguiremos mejorando" +
                        ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passActual.getText().length()>2){
                    if (newPass.getText().toString().equals((repPass.getText().toString()))){
                        try {
                            Actualizar_password(passActual.getText().toString(),newPass.getText().toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Las contraseñas nuevas no coinciden, favor de verificarlas", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Debes ingresar la contraseña Actual", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    public void showDialogBaja(Context context, String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //builder.setTitle("Dejanos tus comentarios");

        View dialogLayout = inflater.inflate(R.layout.dialog_baja_cuenta,
                null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnEliminar = dialogLayout.findViewById(R.id.btn_eliminar_cuenta);
        final Button btnCancelar = dialogLayout.findViewById(R.id.btnDespues);
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
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  //  Toast.makeText(getApplicationContext(), "Debes ingresar la contraseña Actual", Toast.LENGTH_LONG).show();
                try {
                    EliminarCuenta();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public boolean EliminarCuenta() throws JSONException, UnsupportedEncodingException {
        carga2.show();
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser_t = misPreferencias.getString("usuarioId","0");
        JSONObject oJSONObject = new JSONObject();

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.delete(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/PreliminacionUsuario/"+idUser_t,(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {
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
                                Toast.makeText(getApplicationContext(), "Tu cuenta se eliminará en 7 dias", Toast.LENGTH_LONG).show();
                                finish();
                                //  loader.setVisibility(View.GONE);
                            }
                            if (valor.equals("-1")){
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error"+valor, Toast.LENGTH_LONG).show();
                                // loader.setVisibility(View.GONE);
                            }
                            carga2.dismiss();
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
                        carga2.dismiss();
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
    public boolean Actualizar_password(String password_T,String newPassword) throws JSONException, UnsupportedEncodingException {
        carga.show();
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);
        String idUser_t = misPreferencias.getString("usuarioId","0");
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioId", idUser_t);
        oJSONObject.put("password", password_T);
        oJSONObject.put("passwordNew", newPassword);

        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(getApplicationContext(),
                "https://fntyonusa.payonusa.com/api/ChangePassword",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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

                                SharedPreferences sharedPref =getSharedPreferences("Datos_acceso",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("password", newPassword);
                                editor.commit();

                                Toast.makeText(getApplicationContext(), "Datos Actualizados"+password_T.toString(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                                //  loader.setVisibility(View.GONE);
                            }
                            if (valor.equals("-1")){
                                Toast.makeText(getApplicationContext(), "Verifica tu contraseña Actual  "+valor, Toast.LENGTH_LONG).show();
                                // loader.setVisibility(View.GONE);
                            }
                            carga.dismiss();
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
                        carga.dismiss();
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
    public void onBackPressed() {
        // Check if there are any fragments in the back stack
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            // Pop the last fragment from the back stack
            getFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, finish the activity
            super.onBackPressed();
        }
    }
}