package com.yonusa.cercasyonusaplus.ui.view.view.user_permissions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.api.ApiConstants;
import com.yonusa.cercasyonusaplus.api.ApiManager;
import com.yonusa.cercasyonusaplus.api.BaseResponse;
import com.yonusa.cercasyonusaplus.ui.view.adapter.Adapter_UserPermissions;
import com.yonusa.cercasyonusaplus.ui.view.models.request.get_users_permissions.GetUserPermissionsRequest;
import com.yonusa.cercasyonusaplus.ui.view.models.request.update_permissions.PermisoControles;
import com.yonusa.cercasyonusaplus.ui.view.models.request.update_permissions.UpdateUserPermissionsRequest;
import com.yonusa.cercasyonusaplus.ui.view.models.response.Controle;
import com.yonusa.cercasyonusaplus.ui.view.models.response.GetUserPermissionsResponse;
import com.yonusa.cercasyonusaplus.utilities.catalogs.ErrorCodes;

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

public class User_Edit_Permissions extends AppCompatActivity {

    private static final String TAG = User_Edit_Permissions.class.getSimpleName();

    private Adapter_UserPermissions mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Controle> mControlList = new ArrayList<>();
    ApiManager apiManager;

    TextView guestUserName;
    String userId;
    String userAlias;
    String deviceId;
    String guestUserId;
    Button eliminar;
    AlertDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_edit_permissions);

        Log.i(TAG, "On Create");
        setTitle("Add Users");
        getSupportActionBar().hide();

        dialogo = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Eliminando Usuario")
                .setCancelable(false).build();

        guestUserName = findViewById(R.id.tv_guestName);

        SharedPreferences prefs = getSharedPreferences("Datos_usuario", MODE_PRIVATE);
        userId = prefs.getString("usuarioId", "No user id definded");

        deviceId = getIntent().getStringExtra("DEVICE_ID");
        guestUserId = getIntent().getStringExtra("GUEST_USER_ID");
        userAlias = getIntent().getStringExtra("GUEST_USER_NAME");
        eliminar = (Button) findViewById(R.id.eliminar_user);

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Eliminar_usuario();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        guestUserName.setText(userAlias);

    }

    @Override
    protected void onResume() {
        super.onResume();

        toGetUserPermissions();

    }

    public void buildRecyclerView(List<Controle> controlList){

        Log.i("Devices: ------------>",controlList.toString());
        RecyclerView recyclerView = findViewById(R.id.rv_user_permissions);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(User_Edit_Permissions.this);
        mAdapter = new Adapter_UserPermissions((ArrayList<Controle>) controlList);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> toUpdatePermissions(controlList, position));

    }

    private void toUpdatePermissions(List<Controle> controlList, Integer position){

        Log.i("To User Permissions", "User Selected");

        Integer controlId = controlList.get(position).getControlId();
        Boolean permissionState = controlList.get(position).getEstadoPermiso();

        UpdateUserPermissionsRequest updateUserPermissionsRequest = new UpdateUserPermissionsRequest();

        updateUserPermissionsRequest.setUsuarioAdministradorId(userId);
        updateUserPermissionsRequest.setUsuarioInvitadoId(guestUserId);
        updateUserPermissionsRequest.setCercaId(deviceId);

        List<PermisoControles> listPermissions = new ArrayList<>();

        PermisoControles permisoControles = new PermisoControles();
        permisoControles.setControlId(controlId);
        permisoControles.setEstadoPermiso(!permissionState);

        listPermissions.add(permisoControles);

        updateUserPermissionsRequest.setPermisoControles(listPermissions);

        Log.i("Update Construction", updateUserPermissionsRequest.toString());
        updatePermissions(updateUserPermissionsRequest);

    }

    private void updatePermissions(UpdateUserPermissionsRequest updateUserPermissionsRequest){

        //Inicializa la construcción de la petición para que "apiManager" traiga la URL Base
        apiManager = ApiConstants.getAPIManager();

        //Una vez que apiManager es asignado, se crea el "call" y se ocupa el api "login" contenido en ApiManager que es la interface de retrofit.
        Call<BaseResponse> call = apiManager.updateUserPermissions(updateUserPermissionsRequest);

        //Se ejecuta la petición y se asigna el objeto LogIn_Response para recibir el Callback del servicio.
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                int typeCode = response.body().getCodigo();
                switch (typeCode){
                    case (ErrorCodes.SUCCESS):
                        toGetUserPermissions();
                        Toast.makeText(User_Edit_Permissions.this, "Cambió el estado del permiso", Toast.LENGTH_LONG).show();
                        break;
                    case (ErrorCodes.FAILURE):
                        //TODO: Agregar alertas de error.
                        Toast.makeText(User_Edit_Permissions.this, "Ocurrió un error", Toast.LENGTH_SHORT).show();

                        break;

                    default:
                        Log.i("Peticion update", "Algo pasó");
                }


            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(User_Edit_Permissions.this, "Ocurrió un error", Toast.LENGTH_SHORT).show();
                //TODO: Agregar alerta de error de conexión.
            }
        });


    }



    private void toGetUserPermissions(){

        GetUserPermissionsRequest getUserPermissionsRequest = new GetUserPermissionsRequest();
        getUserPermissionsRequest.setUsuarioAdministradorId(userId);
        getUserPermissionsRequest.setUsuarioInvitadoId(guestUserId);
        getUserPermissionsRequest.setCercaId(deviceId);

        getUserPermissions(getUserPermissionsRequest);

    }

    private void getUserPermissions(GetUserPermissionsRequest getUserPermissionsRequest){


        //Inicializa la construcción de la petición para que "apiManager" traiga la URL Base
        apiManager = ApiConstants.getAPIManager();

        //Una vez que apiManager es asignado, se crea el "call" y se ocupa el api "login" contenido en ApiManager que es la interface de retrofit.
        Call<GetUserPermissionsResponse> call = apiManager.getUserPermissions(getUserPermissionsRequest);

        //Se ejecuta la petición y se asigna el objeto LogIn_Response para recibir el Callback del servicio.
        call.enqueue(new Callback<GetUserPermissionsResponse>() {
            @Override
            public void onResponse(Call<GetUserPermissionsResponse> call, Response<GetUserPermissionsResponse> response) {

                int typeCode = response.body().getCodigo();
                switch (typeCode){
                    case (ErrorCodes.SUCCESS):

                        int controls = response.body().getControles().size();

                        if (controls != 0) {

                            mControlList = response.body().getControles();
                            buildRecyclerView(mControlList);

                        } else {

                            Toast.makeText(User_Edit_Permissions.this, "No hay dispositivos", Toast.LENGTH_SHORT).show();

                        }

                        break;
                    case (ErrorCodes.FAILURE):
                        //TODO: Agregar alertas de error.
                        Toast.makeText(User_Edit_Permissions.this, "Revisa tus credenciales", Toast.LENGTH_SHORT).show();

                        break;

                    default:
                        Log.i("Peticion login", "Algo pasó");
                }


            }

            @Override
            public void onFailure(Call<GetUserPermissionsResponse> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                //TODO: Agregar alerta de error de conexión.
            }
        });


    }

    public boolean Eliminar_usuario() throws JSONException, UnsupportedEncodingException {
        dialogo.show();
        SharedPreferences misPreferencias = getSharedPreferences("Datos_usuario", Context.MODE_PRIVATE);

        String id_user = misPreferencias.getString("usuarioId", "0");
       // String id_invitado = misPreferencias.getString("usuarioId", "0");
       // String id_cerca = misPreferencias.getString("usuarioId", "0");
        String aplicacion = "application/json";
        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("usuarioAdministradorId", id_user);
        oJSONObject.put("usuarioInvitadoId", guestUserId);
        oJSONObject.put("cercaId", deviceId);
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
                "https://fntyonusa.payonusa.com/api/EliminarUsuarioCerca",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                                dialogo.dismiss();
                                Toast.makeText(getApplicationContext(), "Invitado eliminado", Toast.LENGTH_LONG).show();
                                finish();
                            }


                            // Toast.makeText(getApplicationContext(), String.valueOf(names), Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
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
                        dialogo.dismiss();
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
