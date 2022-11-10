package com.yonusa.cercaspaniagua.ui.device_control.view.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import com.yonusa.cercaspaniagua.R;
import com.yonusa.cercaspaniagua.ui.device_control.models.response.Controls;
import com.yonusa.cercaspaniagua.ui.device_control.models.response.GetDeviceControlsResponse;
import com.yonusa.cercaspaniagua.ui.rutinas.Rutina;
import com.yonusa.cercaspaniagua.ui.view.view.user_permissions.User_Edit_Permissions;

import java.util.ArrayList;

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
        public ImageView ivControl,calendarizar,reloj;
        public CheckBox reloj2;
        public TextView tvControl;
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
            calendarizar = itemView.findViewById(R.id.calendariazar);
            reloj = itemView.findViewById(R.id.reloj);
            reloj2 = itemView.findViewById(R.id.reloj2);

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


        holder.calendarizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Toast.makeText(mContextCol,"click", Toast.LENGTH_LONG).show();
                Intent i = new Intent(mContextCol, Rutina.class);
                i.putExtra("comando",String.valueOf(holder.getAdapterPosition()));
                mContextCol.startActivity(i);
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
                        holder.ivControl.setImageResource(R.drawable.fence2_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.fence2_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.fence_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());

                break;
            case 2: //panic
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.panico_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.panico_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.panic_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                break;
            case 3: //door
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.puerta_open);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.puerta_close);

                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.door_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                break;
            case 4: //lights
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.lights_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.lights_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.lights_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
                break;
            case 5: //aux1
            case 6: //aux2
                if (control.getEstadoPermiso() && status) {
                    if (control.getEstadoControl()) {
                        holder.ivControl.setImageResource(R.drawable.aux_on);
                    } else {
                        holder.ivControl.setImageResource(R.drawable.aux_off);
                    }
                } else {
                    holder.ivControl.setImageResource(R.drawable.aux_disable);
                    holder.ibControl.setClickable(false);
                    holder.ibControl.setLongClickable(false);
                }

                holder.tvControl.setText(control.getAliasControl());
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

}