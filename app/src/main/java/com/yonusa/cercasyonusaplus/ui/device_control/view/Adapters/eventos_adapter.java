package com.yonusa.cercasyonusaplus.ui.device_control.view.Adapters;

import android.content.Context;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.device_control.models.eventos_model;

import java.util.ArrayList;


/**
 * Created by javiexpo on 26/7/16.
 */
public class eventos_adapter extends RecyclerView.Adapter<eventos_adapter.ViewHolder>  {
    private Context mContextCol;
    private ArrayList<eventos_model> mDatasetcol;
    String codigo;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder  {
        // each data item is just a string in this case
            TextView texto,nombre,apellido,fecha;
            ImageView tvControl;
        Context context;
        LinearLayout vista;
        CardView tarjeta;
        public Transition transition;
        ViewHolder(View v) {
            super(v);
            context = v.getContext();
           // contenido = (TextView) v.findViewById(R.id.Nombre_Categoria);
           // iduser =(TextView) v.findViewById(R.id.Virtual_Categoria_id);
            texto = (TextView) v.findViewById(R.id.tv_event);
            nombre = (TextView) v.findViewById(R.id.tv_event_owner);
            fecha = (TextView) v.findViewById(R.id.tv_event_date);
            tvControl = (ImageView) v.findViewById(R.id.iv_control);
           vista = (LinearLayout) v.findViewById(R.id.layout_tarjeta);
    //        tarjeta =(CardView) v.findViewById(R.id.tarjeta_vista);
        }

        void setOnClickListener()    {
//            tarjeta.setOnClickListener(this);
        }

    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public eventos_adapter(Context contextcol, ArrayList<eventos_model> myDatasetco) {
        mDatasetcol = myDatasetco;
        mContextCol= contextcol;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_items, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //   Glide.with(mContextCol).load(mDatasetcol.get(position).getImageUrlco()).into(holder.mImgtViewco);
        holder.texto.setText(mDatasetcol.get(position).getTextoMostrar());
        holder.nombre.setText(mDatasetcol.get(position).getNombre()+mDatasetcol.get(position).getAlarcon());
        holder.fecha.setText(mDatasetcol.get(position).getFechaRegistro());



  /*      if (holder.id_control.getText().equals("1") && holder.cerca_estatus.getText().equals("true")) {
            holder.tvControl.setImageResource(R.drawable.fence2_on);
        }else {holder.tvControl.setImageResource(R.drawable.fence2_off);
            if (holder.id_control.getText().equals("2") && holder.cerca_estatus.getText().equals("true")) {
                holder.tvControl.setImageResource(R.drawable.panico_on);
            } else { holder.tvControl.setImageResource(R.drawable.panico_off);
                if (holder.id_control.getText().equals("3") && holder.cerca_estatus.getText().equals("true")) {
                    holder.tvControl.setImageResource(R.drawable.puerta_close);
                } else {
                    if (holder.id_control.getText().equals("4") && holder.cerca_estatus.getText().equals("true")) {
                        holder.tvControl.setImageResource(R.drawable.lights_on);
                    } else {
                        if (holder.id_control.getText().equals("5") && holder.cerca_estatus.getText().equals("true")) {
                            holder.tvControl.setImageResource(R.drawable.aux_on);
                        } else {
                            if (holder.id_control.getText().equals("6") && holder.cerca_estatus.getText().equals("true")) {
                                holder.tvControl.setImageResource(R.drawable.aux_on);
                            }
                        }
                    }
                }
            }
        }  */
       // holder.setOnClickListener();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetcol.size();
    }


}
