package com.yonusa.cercasyonusaplus.ui.device_control.view.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.device_control.models.response.Controls;
import com.yonusa.cercasyonusaplus.ui.homeScreen.view.HomeActivity;
import com.yonusa.cercasyonusaplus.ui.rutinas.Rutina;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class DeviceControlAdapter extends RecyclerView.Adapter<DeviceControlAdapter.DeviceControlViewHolder> {
    private ArrayList<Controls> dataControl;
    private onDataControlListener listener;
    private int heigthItem;
    private boolean status;
    private Context mContextCol;

    public interface onDataControlListener {
        void onDataControlClick(int position, boolean isLong);
    }

    public DeviceControlAdapter(Context mContextCol, ArrayList<Controls> datacontrol, boolean status, onDataControlListener listener, int heigthSize) {
        this.dataControl = datacontrol;
        this.status = status;
        this.listener = listener;
        this.heigthItem = heigthSize;
        this.mContextCol= mContextCol;
    }

    public static class DeviceControlViewHolder extends RecyclerView.ViewHolder {
        public ImageButton ibControl;
        public ImageView ivControl,calendarizar,reloj, favoritos;
        public CheckBox reloj2;
        public TextView tvControl,tvFavorito,tvControlId;
        public CardView cardView;
        Context context;

        public DeviceControlViewHolder(@NonNull View itemView, onDataControlListener listener, int heigthZise) {
            super(itemView);

            itemView.getLayoutParams().height = heigthZise;
            context = itemView.getContext();
            cardView = itemView.findViewById(R.id.control_card_view);
            ibControl = itemView.findViewById(R.id.ib_control);
            ivControl = itemView.findViewById(R.id.iv_control);
            tvControl = itemView.findViewById(R.id.tv_control);
            tvFavorito = itemView.findViewById(R.id.tvFavorito);
            calendarizar = itemView.findViewById(R.id.calendariazar);
            reloj = itemView.findViewById(R.id.reloj);
            reloj2 = itemView.findViewById(R.id.reloj2);
            favoritos = itemView.findViewById(R.id.img_favorito);
            tvControlId = itemView.findViewById(R.id.tv_control_id);

            ibControl.setOnClickListener(v -> {
                        goToControl(listener, this.getAdapterPosition(), false);
                    }
            );
            ibControl.setOnLongClickListener(v -> {
                goToControl(listener, this.getAdapterPosition(), true);
                return true;
            });



        }

        public static void goToControl(onDataControlListener listener, int position, boolean isLongPress) {
            if (listener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    Log.isLoggable("Position------>", position);
                    listener.onDataControlClick(position, isLongPress);
                }
            }
        }
    }

    @NonNull
    @Override
    public DeviceControlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.decive_control_item, parent, false);
        DeviceControlViewHolder deviceControlViewHolder = new DeviceControlViewHolder(v, this.listener, this.heigthItem);
        return deviceControlViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull DeviceControlViewHolder holder, int position) {
        Controls control = dataControl.get(position);
        Context context ;
        holder.cardView.setBackgroundResource(R.drawable.item_control_shape);
      /*  holder.calendarizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Toast.makeText(mContextCol,"click", Toast.LENGTH_LONG).show();
                Intent i = new Intent(mContextCol, Rutina.class);
                i.putExtra("comando",String.valueOf(holder.getAdapterPosition()));
                mContextCol.startActivity(i);
            }
        }); */

        if (control.getEstadoFavorito().equals(false)){
            holder.tvFavorito.setText("false");
            holder.favoritos.setImageResource(R.drawable.ic_estrella_off);
        }else{
            holder.tvFavorito.setText("true");
            holder.favoritos.setImageResource(R.drawable.ic_estrella);
        }

      holder.favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(mContextCol.getApplicationContext(),holder.tvControlId.getText().toString()+"  "+ holder.tvFavorito.getText().toString(),Toast.LENGTH_SHORT).show();
                if (holder.tvFavorito.getText().toString().equals("false")){
                    holder.favoritos.setImageResource(R.drawable.ic_estrella);
                    holder.tvFavorito.setText("true");
                }else {
                    holder.favoritos.setImageResource(R.drawable.ic_estrella_off);
                    holder.tvFavorito.setText("false");
                }
                try {
                    ActuliazarFav(holder.tvControlId.getText().toString(), holder.tvFavorito.getText().toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        holder.reloj2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    holder.reloj2.setBackgroundResource(R.drawable.reloj_on);
                }else{
                    holder.reloj2.setBackgroundResource(R.drawable.reloj_of);
                }
            }
        });

        holder.reloj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reloj.setImageResource(R.drawable.reloj_on);
            }
        });
        switch (control.getControlId()) {
            case 1: //fence
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.btn_fence_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.btn_fence_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.fence_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }


                holder.tvControl.setText(control.getAliasControl());
                holder.tvControlId.setText(String.valueOf(control.getControlId()));

                break;
            case 2: //panic
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.btn_panico_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.btn_panico_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.panic_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                holder.tvControlId.setText(String.valueOf(control.getControlId()));

                break;
            case 3: //door
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.btn_puerta_off);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.btn_puerta_on);

                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.door_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                holder.tvControlId.setText(String.valueOf(control.getControlId()));

                break;
            case 4: //lights
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.btn_luces_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.btn_luces_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.lights_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                holder.tvControlId.setText(String.valueOf(control.getControlId()));

                break;
            case 5: //aux1
            case 6: //aux2
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.btn_aux_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.btn_aux_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.aux_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                holder.tvControlId.setText(String.valueOf(control.getControlId()));

                break;

            case 7: //panel
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.panel_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.panel_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.panel_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                holder.tvControlId.setText(String.valueOf(control.getControlId()));

                break;
            case 8: //zone
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.zone_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.zone_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.zone_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return dataControl.size();
    }

    public boolean ActuliazarFav(String control, String  est) throws JSONException, UnsupportedEncodingException {
        //alerta3.show();
        int controlId = Integer.parseInt(control);
        boolean estado = Boolean.parseBoolean(est);
            Boolean valor= false;
        if (est.equals("false")){
             valor = false;
        }else {
            valor= true;
        }

        JSONObject oJSONObject = new JSONObject();
        oJSONObject.put("cercaId", "EYW-9607444d33b74c51a423d44847badbc6");
        oJSONObject.put("usuarioId","660ed98403244d55b78993bf320e7896");
        oJSONObject.put("controlId", controlId);
        oJSONObject.put("estadoFavorito",valor);


        ByteArrayEntity oEntity = new ByteArrayEntity(oJSONObject.toString().getBytes("UTF-8"));
        oEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        AsyncHttpClient oHttpClient = new AsyncHttpClient();
        //cambiar varible
        RequestHandle requestHandle = oHttpClient.post(mContextCol,
                "https://fntyonusa.payonusa.com/api/CambiarFavoritoControlUsuarioCerca",(HttpEntity) oEntity, "application/json" ,new AsyncHttpResponseHandler() {

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
                          //    Toast.makeText(mContextCol.getApplicationContext(), "Boton agregado a favoritos", Toast.LENGTH_LONG).show();
                                //  loader.setVisibility(View.GONE);
                            }
                            if (valor.equals("-1")){
                          //      Toast.makeText(mContextCol.getApplicationContext(), "Ha ocurrido un error al actualizar los datos", Toast.LENGTH_LONG).show();
                                // loader.setVisibility(View.GONE);
                            }
                           // alerta3.dismiss();
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
                            Toast.makeText(mContextCol.getApplicationContext(), "404 !", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(mContextCol.getApplicationContext(), "500 !", Toast.LENGTH_LONG).show();
                            //sin_tarjetas();
                        } else if (statusCode == 403) {
                            Toast.makeText(mContextCol.getApplicationContext(), "403 !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContextCol.getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                      //  alerta3.dismiss();
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